package com.zpj.downloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.zpj.downloader.constant.DefaultConstant;
import com.zpj.downloader.constant.Error;
import com.zpj.downloader.constant.ErrorCode;
import com.zpj.downloader.constant.ResponseCode;
import com.zpj.downloader.util.FileUtils;
import com.zpj.downloader.util.FormatUtils;
import com.zpj.http.ZHttp;
import com.zpj.http.core.HttpHeader;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Z-P-J
 */
@Keep
public class BaseMission<T extends BaseMission<T>> extends BaseConfig<T> {
    private static final String TAG = BaseMission.class.getSimpleName();

    public interface MissionListener {
//        HashMap<MissionListener, Handler> HANDLER_STORE = new HashMap<>();

        void onInit();

        void onStart();

        void onPause();

        void onWaiting();

        void onRetry();

        void onProgress(ProgressInfo update);

        void onFinish();

        void onError(Error e);

        void onDelete();

        void onClear();
    }

    @Keep
    public enum MissionStatus {
        INITING("准备中"),
        START("已开始"),
        RUNNING("下载中"),
        WAITING("等待中"),
        PAUSE("已暂停"),
        FINISHED("已完成"),
        ERROR("出错了"),
        RETRY("重试中");

        private final String statusName;

        MissionStatus(String name) {
            statusName = name;
        }

        @NonNull
        @Override
        public String toString() {
            return statusName;
        }
    }

    private final ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Long> finished = new ConcurrentLinkedQueue<>();
    private final List<Long> speedHistoryList = new ArrayList<>();

    protected String uuid = "";
    protected String name = "";
    protected String url = "";
    protected String redirectUrl = "";
    protected String originUrl = "";
    protected long createTime = 0;
    protected long finishTime = 0;
//    protected int notifyId = 0;
    protected long blocks = 1;
    protected long length = 0;
    //    protected long done = 0;
    protected AtomicLong done = new AtomicLong(0);
    protected MissionStatus missionStatus = MissionStatus.INITING;
    protected boolean fallback = false;
    protected int errCode = -1;
    protected boolean hasInit = false;

    //-----------------------------------------------------transient---------------------------------------------------------------

    private transient int currentRetryCount = DefaultConstant.RETRY_COUNT;

    protected transient AtomicInteger finishCount = new AtomicInteger(0);
    private final transient AtomicInteger aliveThreadCount = new AtomicInteger(0);

    private transient int threadCount = DefaultConstant.THREAD_COUNT;

    private final transient ArrayList<WeakReference<MissionListener>> mListeners = new ArrayList<>();

    private transient int errorCount = 0;
    private transient long lastDone = -1;
    private transient String tempSpeed = "0 KB/s";
    private transient ProgressInfo progressInfo;
    private transient Handler handler;
    private transient Gson gson;
    private transient ConcurrentLinkedQueue<DownloadBlock> blockQueue;


    //------------------------------------------------------runnables---------------------------------------------

    protected ConcurrentLinkedQueue<DownloadBlock> getBlockQueue() {
        if (blockQueue == null) {
            synchronized (BaseMission.class) {
                if (blockQueue == null) {
                    blockQueue = new ConcurrentLinkedQueue<>();
                }
            }
        }
        return blockQueue;
    }

    protected Handler getHandler() {
        if (handler == null) {
            synchronized (BaseMission.class) {
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return handler;
    }

    private Gson getGson() {
        if (gson == null) {
            synchronized (BaseMission.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    public ProgressInfo getProgressInfo() {
        if (progressInfo == null) {
            synchronized (BaseMission.class) {
                if (progressInfo == null) {
                    progressInfo = new ProgressInfo();
                }
            }
        }
        return progressInfo;
    }

    protected void initMission() {
        notifyStatus(missionStatus);
        Log.d(TAG, "init url=" + url);
        ZHttp.head(url)
                .proxy(Proxy.NO_PROXY)
                .userAgent(getUserAgent())
                .cookie(getCookie())
                .accept("*/*")
                .referer(url)
                .headers(getHeaders())
//                .connection("close")
//                .range("bytes=0-")
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")
                .acceptEncoding("identity")
                .connectTimeout(getConnectOutTime())
                .readTimeout(getReadOutTime())
                .ignoreContentType(true)
                .ignoreHttpErrors(false)
                .maxBodySize(0)
                .execute()
                .onNext(new HttpObserver.OnNextListener<IHttp.Response, IHttp.Response>() {
                    @Override
                    public HttpObserver<IHttp.Response> onNext(IHttp.Response res) {
                        if (handleResponse(res, BaseMission.this)) {
                            Log.d(TAG, "handleResponse--111");
                            return null;
                        }
//                        Log.d(TAG, "init onNext--new Task");
                        return ZHttp.get(url)
                                .proxy(Proxy.NO_PROXY)
                                .userAgent(getUserAgent())
                                .cookie(getCookie())
//                                .accept("*/*")
                                .referer(url)
//                                .range("bytes=0-")
                                .header(HttpHeader.RANGE, "bytes=0-")
                                .header("Pragma", "no-cache")
                                .header("Cache-Control", "no-cache")
                                .header("Access-Control-Expose-Headers", "Content-Disposition")
                                .acceptEncoding("identity")
                                .headers(getHeaders())
//                                .connection("close")
                                .connectTimeout(getConnectOutTime())
                                .readTimeout(getReadOutTime())
                                .ignoreContentType(true)
                                .ignoreHttpErrors(false)
                                .maxBodySize(0)
                                .execute();
                    }
                })
                .onSuccess(new IHttp.OnSuccessListener<IHttp.Response>() {
                    @Override
                    public void onSuccess(IHttp.Response res) throws Exception {
                        if (handleResponse(res, BaseMission.this)) {
                            Log.d(TAG, "handleResponse--222");
                            return;
                        }

                        if (res.statusCode() != ResponseCode.RESPONSE_206) {
                            // Fallback to single thread if no partial content support
                            fallback = true;

                            Log.d(TAG, "falling back");
                        }

                        Log.d("mission.name", "mission.name444=" + name);
                        if (TextUtils.isEmpty(name)) {
                            Log.d("Initializer", "getMissionNameFromUrl--url=" + url);
                            name = getMissionNameFromUrl(BaseMission.this, url);
                        }

                        Log.d("mission.name", "mission.name555=" + name);

                        for (BaseMission<?> downloadMission : DownloadManagerImpl.getInstance().getMissions()) {
                            if (!TextUtils.equals(uuid, downloadMission.uuid) && TextUtils.equals(name, downloadMission.name) && // !downloadMission.isIniting() &&
                                    (TextUtils.equals(downloadMission.originUrl.trim(), url.trim()) ||
                                            TextUtils.equals(downloadMission.redirectUrl.trim(), url.trim()))) {
                                Log.d(TAG, "has mission---url=" + downloadMission.url);
                                downloadMission.start();
                                return;
                            }
                        }

                        if (fallback) {
                            blocks = 1;
                        } else {
                            blocks = length / getBlockSize();
                            if (blocks * getBlockSize() < length) {
                                blocks++;
                            }
                        }
                        Log.d(TAG, "blocks=" + blocks);

                        queue.clear();
                        for (long position = 0; position < blocks; position++) {
                            Log.d(TAG, "initQueue add position=" + position);
                            queue.add(position);
                        }


                        File loacation = new File(getDownloadPath());
                        if (!loacation.exists()) {
                            loacation.mkdirs();
                        }
                        File file = new File(getFilePath());
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        Log.d(TAG, "storage=" + FileUtils.getAvailableSize());
                        hasInit = true;

                        start();
                    }
                })
                .onError(new IHttp.OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        notifyError(new Error(throwable.getMessage()));
                    }
                })
                .subscribe();
    }

    private final transient Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "progressRunnable--start isRunning=" + isRunning() + " missionStatus=" + missionStatus);
            if (isFinished() || errCode != -1 || aliveThreadCount.get() < 1 || !isRunning()) {
                getHandler().removeCallbacks(progressRunnable);
                notifyStatus(missionStatus);
                return;
            }
            getHandler().postDelayed(progressRunnable, getProgressInterval());
            long downloaded = done.get();
            long delta = downloaded - lastDone;
            Log.d(TAG, "progressRunnable--delta=" + delta);
            speedHistoryList.add(delta);
            if (delta > 0) {
                lastDone = downloaded;
                double speed = delta * (getProgressInterval() / 1000f);
                tempSpeed = FormatUtils.formatSpeed(speed);
            }
            String downloadedSizeStr = FormatUtils.formatSize(downloaded);
            float progress = getProgress(downloaded, length);
            Log.d(TAG, "progressRunnable--tempSpeed=" + tempSpeed);
            ProgressInfo progressInfo = getProgressInfo();
            progressInfo.setDone(downloaded);
            progressInfo.setSize(length);
            progressInfo.setProgress(progress);
            progressInfo.setFileSizeStr(getFileSizeStr());
            progressInfo.setDownloadedSizeStr(downloadedSizeStr);
            progressInfo.setProgressStr(String.format(Locale.US, "%.2f%%", progress));
            progressInfo.setSpeedStr(tempSpeed);
            writeMissionInfo();
            notifyStatus(MissionStatus.RUNNING);
            if (getEnableNotification() && getNotificationInterceptor() != null) {
                getNotificationInterceptor().onProgress(getContext(), BaseMission.this, getProgress(), false);
            }
        }
    };

    protected BaseMission() {

    }

    //-------------------------下载任务状态-----------------------------------
    public boolean isIniting() {
        return missionStatus == MissionStatus.INITING;
    }

    public boolean isRunning() {
        return missionStatus == MissionStatus.RUNNING;
    }

    public boolean isWaiting() {
        return missionStatus == MissionStatus.WAITING;
    }

    public boolean isPause() {
        return missionStatus == MissionStatus.PAUSE;
    }

    public boolean isFinished() {
        return missionStatus == MissionStatus.FINISHED;
    }

    public boolean isError() {
        return missionStatus == MissionStatus.ERROR;
    }

    public boolean canPause() {
        return isRunning() || isWaiting() || isIniting();
    }

    public boolean canStart() {
        return isPause() || isError(); //  || isIniting()
    }


    //----------------------------------------------------------operation------------------------------------------------------------
    void init() {
        currentRetryCount = getRetryCount();
        threadCount = getProducerThreadCount();
        lastDone = done.get();
        if (isFinished()) {
            return;
        }
        pause();
        if (hasInit) {
            for (long position = 0; position < getBlocks(); position++) {
                if (!queue.contains(position) && !finished.contains(position)) {
                    queue.add(position);
                }
            }
            pause();
        }
    }

    public void start() {
        if (!hasInit) {
            currentRetryCount = getRetryCount();
            threadCount = getProducerThreadCount();
            DownloadManagerImpl.getInstance().insertMission(this);
//            init();
            writeMissionInfo();
            initMission();
            return;
        }
        errorCount = 0;
        if (!isRunning() && !isFinished()) {
            initCurrentRetryCount();
            if (DownloadManagerImpl.getInstance().shouldMissionWaiting()) {
                waiting();
                return;
            }

            if (fallback) {
                if (isPause() || isError()) {
                    missionStatus = MissionStatus.INITING;
                    redirectUrl = "";
                    url = originUrl;
//                    threadPoolExecutor.submit(initRunnable);
                    initMission();
                    return;
                }
                // In fallback mode, resuming is not supported.
//                missionConfig.getThreadPoolConfig().setCorePoolSize(1);
                threadCount = 1;
                setProducerThreadCount(1);
//                done = 0;
                done.set(0);
                blocks = 1;
                queue.clear();
                queue.add(0L);
            }

            // TODO
//            setConsumerThreadCount(getProducerThreadCount() * 3);
//            threadCount = 1;
//            setProducerThreadCount(1);


            DownloadManagerImpl.increaseDownloadingCount();

            missionStatus = MissionStatus.RUNNING;

            aliveThreadCount.set(threadCount);
            finishCount.set(0);

            writeMissionInfo();

            for (int i = 0; i < threadCount; i++) {
                Observable.create(new DownloadBlockProducer2(this))
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                int count = aliveThreadCount.decrementAndGet();
                                if (count == 0 && isRunning()) {
                                    Log.d(TAG, "doOnComplete length=" + length + " doneLen.get()=" + done.get());
                                    if (isFallback() || done.get() == length) {
                                        onFinish();
                                    } else {
                                        pause();
                                        for (long position = 0; position < getBlocks(); position++) {
                                            if (!queue.contains(position) && !finished.contains(position)) {
                                                queue.add(position);
                                            }
                                        }
                                        start();
                                    }
                                }
                            }
                        })
                        .subscribe();
            }



//            for (int i = 0; i < threadCount; i++) {
//                Observable.create(new DownloadBlockProducer(this, getBlockQueue()))
//                        .subscribeOn(Schedulers.io())
//                        .doOnComplete(new Action() {
//                            @Override
//                            public void run() throws Exception {
//                                aliveThreadCount.decrementAndGet();
//                            }
//                        })
//                        .subscribe();
//            }
//
//            for (int i = 0; i < getConsumerThreadCount(); i++) {
//                Observable.create(new DownloadBlockConsumer(this, getBlockQueue()))
//                        .subscribeOn(Schedulers.io())
//                        .doOnComplete(new Action() {
//                            @Override
//                            public void run() throws Exception {
//                                int count = finishCount.incrementAndGet();
//                                Log.d(TAG, "doOnComplete count=" + count);
//                                if (count >= getConsumerThreadCount() && isRunning()) {
//                                    Log.d(TAG, "doOnComplete length=" + length + " doneLen.get()=" + done.get());
//                                    if (isFallback() || done.get() == length) {
//                                        onFinish();
//                                    } else {
//                                        pause();
//                                        for (long position = 0; position < getBlocks(); position++) {
//                                            if (!queue.contains(position) && !finished.contains(position)) {
//                                                queue.add(position);
//                                            }
//                                        }
//                                        start();
//                                    }
//                                }
//                            }
//                        })
//                        .subscribe();
//            }


            notifyStatus(MissionStatus.START);
            getHandler().post(progressRunnable);
        }
    }

    public void restart() {
        pause();
//        done = 0;
        finished.clear();
        queue.clear();
        speedHistoryList.clear();
        done.set(0);
        finishTime = 0;
        finishCount.set(0);
        aliveThreadCount.set(0);
        lastDone = 0;
        hasInit = false;
        url = originUrl;
        redirectUrl = "";
        name = "";
        missionStatus = MissionStatus.INITING;
        fallback = false;
        errCode = -1;


        currentRetryCount = DefaultConstant.RETRY_COUNT;
        threadCount = DefaultConstant.THREAD_COUNT;
        errorCount = 0;
        lastDone = -1;
        tempSpeed = "0 KB/s";
        progressInfo = null;
        blockQueue = null;

        start();
    }

    public void pause() {
        if (canPause()) {
            initCurrentRetryCount();
            missionStatus = MissionStatus.PAUSE;
            writeMissionInfo();
            notifyStatus(missionStatus);

            if (missionStatus != MissionStatus.WAITING) {
                DownloadManagerImpl.decreaseDownloadingCount();
            }

            if (getEnableNotification() && getNotificationInterceptor() != null) {
                getNotificationInterceptor().onProgress(getContext(), this, getProgress(), true);
            }
        }
    }

    public void waiting() {
        missionStatus = MissionStatus.WAITING;
        writeMissionInfo();
        notifyStatus(missionStatus);
//        pause();
    }

    public void delete() {
        if (getNotificationInterceptor() != null) {
            getNotificationInterceptor().onCancel(getContext(), this);
        }
        pause();
        deleteMissionInfo();
        new File(getDownloadPath() + File.separator + name).delete();
        DownloadManagerImpl.getInstance().getMissions().remove(this);
        DownloadManagerImpl.onMissionDelete(this);
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (WeakReference<MissionListener> ref : mListeners) {
                    final MissionListener listener = ref.get();
                    if (listener != null) {
                        listener.onDelete();
                    }
                }
            }
        });
    }

    public void clear() {
        if (getNotificationInterceptor() != null) {
            getNotificationInterceptor().onCancel(getContext(), this);
        }
        pause();
        deleteMissionInfo();
        DownloadManagerImpl.getInstance().getMissions().remove(this);
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (WeakReference<MissionListener> ref : mListeners) {
                    final MissionListener listener = ref.get();
                    if (listener != null) {
                        listener.onClear();
                    }
                }
            }
        });
    }

    public boolean renameTo(String newFileName) {
        File file2Rename = new File(getDownloadPath() + File.separator + newFileName);
        boolean success = getFile().renameTo(file2Rename);
        if (success) {
            setTaskName(newFileName);
            writeMissionInfo();
        }
        return success;
    }

    public boolean openFile(Context context) {
        File file = getFile();
        if (file.exists()) {
            FileUtils.openFile(context, getFile());
            return true;
        } else {
            return false;
        }
    }

    public boolean openFile() {
        return openFile(getContext());
    }

    //------------------------------------------------------------notify------------------------------------------------------------
    void notifyDownloaded(long deltaLen) {
        if (done.addAndGet(deltaLen) > length) {
            done.set(length);
        }
    }

    synchronized void notifyError(Error e, boolean fromThread) {
        Log.d(TAG, "err=" + e.getErrorMsg() + " fromThread=" + fromThread);
//        errorHistoryList.add(e);
        if (!(e == Error.WITHOUT_STORAGE_PERMISSIONS || e == Error.FILE_NOT_FOUND)) {
            errorCount++;
            if (fromThread) {
                aliveThreadCount.decrementAndGet();
//                finishCount++;
                finishCount.incrementAndGet();
            }
            Log.d(TAG, "aliveThreadCount=" + aliveThreadCount + " fromThread=" + fromThread);
            if (aliveThreadCount.get() <= 0 && errorCount >= threadCount) {
                currentRetryCount--;
                if (currentRetryCount >= 0) {
                    pause();
                    notifyStatus(MissionStatus.RETRY);
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, getRetryDelay());
                    return;
                }
            } else {
                return;
            }
        }

        missionStatus = MissionStatus.ERROR;

        currentRetryCount = getRetryCount();

        errCode = 1;

        Log.d("eeeeeeeeeeeeeeeeeeee", "error:" + errCode);

        writeMissionInfo();

        notifyError(e);

        DownloadManagerImpl.decreaseDownloadingCount();

        if (getEnableNotification() && getNotificationInterceptor() != null) {
            getNotificationInterceptor().onError(getContext(), this, errCode);
        }
    }

    protected void notifyError(final Error e) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (WeakReference<MissionListener> ref : mListeners) {
                    final MissionListener listener = ref.get();
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }
        });
    }

    protected void notifyStatus(final MissionStatus status) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (WeakReference<MissionListener> ref : mListeners) {
                    final MissionListener listener = ref.get();
                    if (listener != null) {
                        switch (status) {
                            case INITING:
                                listener.onInit();
                                break;
                            case START:
                                listener.onStart();
                                break;
                            case RUNNING:
                                listener.onProgress(getProgressInfo());
                                break;
                            case WAITING:
                                listener.onWaiting();
                                break;
                            case PAUSE:
                                listener.onPause();
                                break;
                            case RETRY:
                                listener.onRetry();
                                break;
                            case FINISHED:
                                ProgressInfo info = getProgressInfo();
                                info.setDone(getDone());
                                info.setSize(getLength());
                                info.setProgress(100);
                                info.setFileSizeStr(getFileSizeStr());
                                info.setDownloadedSizeStr(getDownloadedSizeStr());
                                info.setProgressStr(String.format(Locale.US, "%.2f%%", getProgress()));
                                info.setSpeedStr(tempSpeed);
                                listener.onProgress(info);
                                listener.onFinish();
                                progressInfo = null;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    protected void onFinish() {
        if (errCode > 0) {
            return;
        }
        Log.d(TAG, "onFinish");
//        done = length;
        done.set(length);
        getHandler().removeCallbacks(progressRunnable);

        missionStatus = MissionStatus.FINISHED;
        finishTime = System.currentTimeMillis();
        writeMissionInfo();

        notifyStatus(missionStatus);

        DownloadManagerImpl.decreaseDownloadingCount();

        if (getEnableNotification() && getNotificationInterceptor() != null) {
            getNotificationInterceptor().onFinished(getContext(), this);
        }
        DownloadManagerImpl.onMissionFinished(this);
    }

    public synchronized T addListener(MissionListener listener) {
        mListeners.add(new WeakReference<>(listener));
        return (T) this;
    }

    public synchronized void removeListener(MissionListener listener) {
        for (Iterator<WeakReference<MissionListener>> iterator = mListeners.iterator();
             iterator.hasNext(); ) {
            WeakReference<MissionListener> weakRef = iterator.next();
            if (listener != null && listener == weakRef.get()) {
                iterator.remove();
            }
        }
    }

    public synchronized void removeAllListener() {
        for (Iterator<WeakReference<MissionListener>> iterator = mListeners.iterator();
             iterator.hasNext(); ) {
            WeakReference<MissionListener> weakRef = iterator.next();
            iterator.remove();
        }
    }

    private void writeMissionInfo() {
        Observable.create(
                new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<Object> emitter) throws Exception {
                        String json = getGson().toJson(BaseMission.this);
                        Log.d(TAG, "writeMissionInfo json=" + json);
                        FileUtils.writeToFile(getMissionInfoFilePath(), json);
                        emitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void deleteMissionInfo() {
        File file = new File(getMissionInfoFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    private void initCurrentRetryCount() {
        if (currentRetryCount != getRetryCount()) {
            currentRetryCount = getRetryCount();
        }
    }

    //--------------------------------------------------------------getter-----------------------------------------------
    public Context getContext() {
        return DownloadManagerImpl.getInstance().getContext();
    }

    public String getUuid() {
        return uuid;
    }

    public String getTaskName() {
        if (TextUtils.isEmpty(name)) {
            return getTaskNameFromUrl();
        }
        return name;
    }

    public String getTaskNameFromUrl() {
        return getMissionNameFromUrl(this, url);
    }

    public String getUrl() {
        return url;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public int getThreadCount() {
        return threadCount;
    }

    int getAliveThreadCount() {
        return aliveThreadCount.get();
    }

    public long getBlocks() {
        return blocks;
    }

    public int getFinishCount() {
        return finishCount.get();
    }

    public long getLength() {
        return length;
    }

    public long getDone() {
        return done.get();
    }

    public MissionStatus getStatus() {
        return missionStatus;
    }

    public int getErrCode() {
        return errCode;
    }

    public boolean isFallback() {
        return fallback;
    }

    public boolean hasInit() {
        return hasInit;
    }

    public String getFilePath() {
        String path = getDownloadPath();
        if (path.endsWith(File.separator)) {
            return path + name;
        }
        return path + File.separator + name;
    }

    public File getFile() {
        return new File(getFilePath());
    }

    public String getFileSuffix() {
        return MimeTypeMap.getFileExtensionFromUrl(getFile().toURI().toString()).toLowerCase(Locale.US);
    }

    private float getProgress(long done, long length) {
        if (missionStatus == MissionStatus.FINISHED) {
            return 100f;
        } else if (length <= 0) {
            return 0f;
        }
        float progress = (float) done / (float) length;
        return progress * 100f;
    }

    public float getProgress() {
        return getProgress(getDone(), length);
    }

    public String getProgressStr() {
        return String.format(Locale.US, "%.2f%%", getProgress());
    }

    public String getFileSizeStr() {
        return FormatUtils.formatSize(length);
    }

    public String getDownloadedSizeStr() {
        return FormatUtils.formatSize(done.get());
    }

    public String getSpeed() {
        return tempSpeed;
    }

    public int getNotifyId() {
        return hashCode();
    }

    long getNextPosition() {
        if (queue.isEmpty()) {
            return -1;
        }
        return queue.poll();
    }

    void onPositionDownloadFailed(long position) {
        queue.add(position);
    }

    public String getMissionInfoFilePath() {
        return DownloadManagerImpl.TASK_PATH + File.separator + uuid + DownloadManagerImpl.MISSION_INFO_FILE_SUFFIX_NAME;
    }


    //-----------------------------------------------------setter-----------------------------------------------------------------


    public void setTaskName(String name) {
        this.name = name;
    }

    void setUrl(String url) {
        this.url = url;
    }

    void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    void setLength(long length) {
        this.length = length;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }


    //----------------------------------------------------------------other

    public boolean isBlockFinished(long block) {
        return finished.contains(block);
    }

    void onBlockFinished(long block) {
        Log.d("DownloadRunnableLog", block + " finished");
        finished.add(block);
    }

    private boolean handleResponse(IHttp.Response response, BaseMission mission) {
        Log.d("statusCode11111111", "       " + response.statusCode());
//        Log.d("response.headers()", "1111" + response.headers());
        if (TextUtils.isEmpty(mission.name)) {
            mission.name = getMissionNameFromResponse(response);
            Log.d("mission.name", "mission.name333=" + mission.name);
        }
        if (response.statusCode() == ResponseCode.RESPONSE_302
                || response.statusCode() == ResponseCode.RESPONSE_301
                || response.statusCode() == ResponseCode.RESPONSE_300) {
            String redictUrl = response.header("location");
            Log.d(TAG, "redirectUrl=" + redictUrl);
            if (!TextUtils.isEmpty(redictUrl)) {
                mission.url = redictUrl;
                mission.redirectUrl = redictUrl;
            }
        } else if (response.statusCode() == ErrorCode.ERROR_SERVER_404) {
            mission.errCode = ErrorCode.ERROR_SERVER_404;
            mission.notifyError(Error.HTTP_404, false);
            return true;
        } else if (response.statusCode() == ResponseCode.RESPONSE_206) {
            String contentLength = response.header("Content-Length");
            if (contentLength != null) {
                mission.length = Long.parseLong(contentLength);
            }
            Log.d("mission.length", "mission.length=" + mission.length);
            return !checkLength(mission);
        }
        return false;
    }

    private String getMissionNameFromResponse(IHttp.Response response) {
        String contentDisposition = response.header("Content-Disposition");
        Log.d("contentDisposition", "contentDisposition=" + contentDisposition);
        if (contentDisposition != null) {
            String[] dispositions = contentDisposition.split(";");
            for (String disposition : dispositions) {
                Log.d("disposition", "disposition=" + disposition);
                if (disposition.contains("filename=")) {
                    return disposition.replace("filename=", "").trim();
                }
            }
        }
        return "";
    }

    protected String getMissionNameFromUrl(BaseMission mission, String url) {
        Log.d("getMissionNameFromUrl", "1");
        if (!TextUtils.isEmpty(url)) {
            int index = url.lastIndexOf("/");

            if (index > 0) {
                int end = url.lastIndexOf("?");

                if (end < index) {
                    end = url.length();
                }

                String name = url.substring(index + 1, end);
                Log.d("getMissionNameFromUrl", "2");

                if (!TextUtils.isEmpty(mission.originUrl) && !TextUtils.equals(url, mission.originUrl)) {
                    String originName = getMissionNameFromUrl(mission, mission.originUrl);
                    Log.d("getMissionNameFromUrl", "3");
                    if (FileUtils.checkFileType(originName) != FileUtils.FileType.UNKNOWN) {
                        Log.d("getMissionNameFromUrl", "4");
                        return originName;
                    }
                }

                if (FileUtils.checkFileType(name) != FileUtils.FileType.UNKNOWN || name.contains(".")) {
                    Log.d("getMissionNameFromUrl", "5");
                    return name;
                } else {
                    Log.d("getMissionNameFromUrl", "6");
                    return name + ".ext";
                }
            }
        }
        Log.d("getMissionNameFromUrl", "7");
        return "未知文件.ext";
    }

    private boolean checkLength(BaseMission mission) {
        if (mission.length <= 0) {
            mission.errCode = ErrorCode.ERROR_SERVER_UNSUPPORTED;
            mission.notifyError(Error.SERVER_UNSUPPORTED, false);
            return false;
        } else if (mission.length >= FileUtils.getAvailableSize()) {
            mission.errCode = ErrorCode.ERROR_NO_ENOUGH_SPACE;
            mission.notifyError(Error.NO_ENOUGH_SPACE, false);
            return false;
        }
        return true;
    }

    public static class ProgressInfo {

        private long size;
        private long done;
        private float progress;
        private String fileSizeStr;
        private String downloadedSizeStr;
        private String progressStr;
        private String speedStr;

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getDone() {
            return done;
        }

        public void setDone(long done) {
            this.done = done;
        }

        public float getProgress() {
            return progress;
        }

        void setProgress(float progress) {
            this.progress = progress;
        }

        public String getFileSizeStr() {
            return fileSizeStr;
        }

        void setFileSizeStr(String fileSizeStr) {
            this.fileSizeStr = fileSizeStr;
        }

        public String getDownloadedSizeStr() {
            return downloadedSizeStr;
        }

        void setDownloadedSizeStr(String downloadedSizeStr) {
            this.downloadedSizeStr = downloadedSizeStr;
        }

        public String getProgressStr() {
            return progressStr;
        }

        void setProgressStr(String progressStr) {
            this.progressStr = progressStr;
        }

        public String getSpeedStr() {
            return speedStr;
        }

        void setSpeedStr(String speedStr) {
            this.speedStr = speedStr;
        }
    }

}
