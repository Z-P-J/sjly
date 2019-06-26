package com.zpj.qxdownloader.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;

import com.google.gson.Gson;
import com.zpj.qxdownloader.config.MissionConfig;
import com.zpj.qxdownloader.constant.ErrorCode;
import com.zpj.qxdownloader.constant.ResponseCode;
import com.zpj.qxdownloader.jsoup.Jsoup;
import com.zpj.qxdownloader.jsoup.connection.Connection;
import com.zpj.qxdownloader.util.FileUtil;
import com.zpj.qxdownloader.util.ThreadPoolFactory;
import com.zpj.qxdownloader.util.Utility;
import com.zpj.qxdownloader.util.io.BufferedRandomAccessFile;
import com.zpj.qxdownloader.util.notification.NotifyUtil;

import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Z-P-J
 */
public class DownloadMission {
    private static final String TAG = DownloadMission.class.getSimpleName();

    public interface MissionListener {
        HashMap<MissionListener, Handler> HANDLER_STORE = new HashMap<>();

        void onInit();

        void onStart();

        void onPause();

        void onWaiting();

        void onRetry();

        void onProgress(long done, long total);

        void onFinish();

        void onError(int errCode);
    }

    public enum MissionState {
        INITING,
        RUNNING,
        WAITING,
        PAUSE,
        FINISHED,
        ERROR
    }

    public String uuid = "";

    public String name = "";

    public String url = "";

    public String redictUrl = "";

    public String originUrl = "";

//	public String location = "";
//	public String cookie = "";
//	public String userAgent = "";

    public long createTime = 0;

    public int notifyId = 0;

    public long blocks = 0;
//	public int bufferSize = DefaultConstant.BUFFER_SIZE;
//	public int blockSize = DefaultConstant.BLOCK_SIZE;

    public long length = 0;

    public long done = 0;

    public int finishCount = 0;
    //单位毫秒
//	public int retryDelay = DefaultConstant.RETRY_DELAY;
//	public int connectOutTime = DefaultConstant.CONNECT_OUT_TIME;
//	public int readOutTime = DefaultConstant.READ_OUT_TIME;

    public List<Long> threadPositions = new ArrayList<>();

    public transient int a;
//	public final LongSparseBooleanArray longSparseBooleanArray = new LongSparseBooleanArray();

    public final LongSparseArray<Boolean> blockState = new LongSparseArray<>();

    public MissionState missionState = MissionState.INITING;

    public boolean fallback = false;

    public int errCode = -1;

    public long timestamp = 0;

    public boolean hasInit = false;

    public MissionConfig missionConfig = MissionConfig.with();

    public transient boolean recovered = false;

    public transient int currentRetryCount = missionConfig.getRetryCount();

    public transient int threadCount = missionConfig.getThreadPoolConfig().getCorePoolSize();
//	public transient int maximumPoolSize = missionConfig.getThreadPoolConfig().getMaximumPoolSize();
//	public transient int keepAliveTime = missionConfig.getThreadPoolConfig().getKeepAliveTime();

    //	private transient ArrayList<WeakReference<MissionListener>> mListeners = new ArrayList<>();
//	private transient WeakReference<MissionListener> missionListener;
    private transient MissionListener missionListener;
    private transient boolean mWritingToFile = false;

    private transient int errorCount = 0;

    private transient ThreadPoolExecutor threadPoolExecutor;


    //runnables

    private transient Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("Initializer", "run");
                Connection.Response response = Jsoup.connect(url)
                        .method(Connection.Method.HEAD)
                        .followRedirects(false)
                        .proxy(Proxy.NO_PROXY)
                        .userAgent(getUserAgent())
                        .header("Cookie", getCookie())
                        .header("Accept", "*/*")
                        .header("Referer", url)
//						.header("Access-Control-Expose-Headers", "Content-Disposition")
//						.header("Range", "bytes=0-")
                        .headers(getHeaders())
                        .timeout(getConnectOutTime())
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .maxBodySize(0)
                        .execute();

                if (handleResponse(response, DownloadMission.this)) {
                    return;
                }


                response = Jsoup.connect(url)
                        .method(Connection.Method.HEAD)
                        .proxy(Proxy.NO_PROXY)
                        .userAgent(getUserAgent())
                        .header("Cookie", getCookie())
                        .header("Accept", "*/*")
                        .header("Access-Control-Expose-Headers", "Content-Disposition")
                        .header("Referer", url)
                        .header("Pragma", "no-cache")
                        .header("Range", "bytes=0-")
                        .header("Cache-Control", "no-cache")
                        .headers(getHeaders())
                        .timeout(getConnectOutTime())
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
//						.validateTLSCertificates(false)
                        .maxBodySize(0)
                        .execute();

                if (handleResponse(response, DownloadMission.this)) {
                    return;
                }

                if (response.statusCode() != ResponseCode.RESPONSE_206) {
                    // Fallback to single thread if no partial content support
                    fallback = true;

                    Log.d(TAG, "falling back");
                }

                Log.d("mission.name", "mission.name444=" + name);
                if (TextUtils.isEmpty(name)) {
                    Log.d("Initializer", "getMissionNameFromUrl--url=" + url);
                    name = getMissionNameFromUrl(DownloadMission.this, url);
                }

                Log.d("mission.name", "mission.name555=" + name);

                for (DownloadMission downloadMission : DownloadManagerImpl.ALL_MISSIONS) {
                    if (!downloadMission.isIniting() && TextUtils.equals(name, downloadMission.name) &&
                            (TextUtils.equals(downloadMission.originUrl.trim(), url.trim()) ||
                                    TextUtils.equals(downloadMission.redictUrl.trim(), url.trim()))) {
                        downloadMission.start();
                        return;
                    }
                }

                blocks = length / getBlockSize();

                if (threadCount > blocks) {
                    threadCount = (int) blocks;
                }

                if (threadCount <= 0) {
                    threadCount = 1;
                }

                if (blocks * getBlockSize() < length) {
                    blocks++;
                }


                File loacation = new File(getDownloadPath());
                if (!loacation.exists()) {
                    loacation.mkdirs();
                }
                File file = new File(getFilePath());
                if (!file.exists()) {
                    file.createNewFile();
                }

                Log.d(TAG, "storage=" + Utility.getAvailableSize());
                hasInit = true;

                BufferedRandomAccessFile af = new BufferedRandomAccessFile(getFilePath(), "rw");
                af.setLength(length);
                af.close();

                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private transient Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (missionListener != null) {
                missionListener.onProgress(done, length);
            }

            if (missionConfig.getEnableNotificatio()) {
                NotifyUtil.with(getContext())
                        .buildProgressNotify()
                        .setProgressAndFormat(getProgress(), false, "")
                        .setContentTitle(name)
                        .setId(getId())
                        .show();
            }
        }
    };

    private transient Runnable writeMissionInfoRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (blockState) {
                Utility.writeToFile(getMissionInfoFilePath(), new Gson().toJson(DownloadMission.this));
                mWritingToFile = false;
            }
        }
    };


    public void init() {
        if (threadPoolExecutor == null || threadPoolExecutor.getCorePoolSize() != 2 * threadCount) {
            threadPoolExecutor = ThreadPoolFactory.newFixedThreadPool(missionConfig.getThreadPoolConfig());
        }
        if (hasInit) {
            if (missionListener != null) {
                missionListener.onInit();
            }
        } else {
            writeMissionInfo();
            threadPoolExecutor.submit(initRunnable);
        }
    }

    public void start() {
        errorCount = 0;
        if (!isRunning() && !isFinished()) {
            initCurrentRetryCount();
            if (DownloadManagerImpl.getInstance().shouldMissionWaiting()) {
                waiting();
                return;
            }

            DownloadManagerImpl.increaseDownloadingCount();

//			waiting = false;
//			running = true;
            missionState = MissionState.RUNNING;

//			ExecutorService executorService;
            if (!fallback) {
//				executorService = Executors.newFixedThreadPool(threadCount);
//				for (int i = 0; i < threadCount; i++) {
//					if (threadPositions.size() <= i && !recovered) {
//						threadPositions.add((long) i);
//					}
//					executorService.submit(new DownloadRunnable(this, i));
////					new Thread(new DownloadRunnable(this, i)).start();
//				}
            } else {
                // In fallback mode, resuming is not supported.
                missionConfig.getThreadPoolConfig().setCorePoolSize(1);
                threadCount = 1;
                done = 0;
                blocks = 0;
//				executorService = Executors.newFixedThreadPool(1);
//				executorService.submit(new DownloadRunnableFallback(this));
            }

            if (threadPoolExecutor == null || threadPoolExecutor.getCorePoolSize() != 2 * threadCount) {
                threadPoolExecutor = ThreadPoolFactory.newFixedThreadPool(missionConfig.getThreadPoolConfig());
            }
            for (int i = 0; i < threadCount; i++) {
                if (threadPositions.size() <= i && !recovered) {
                    threadPositions.add((long) i);
                }
                threadPoolExecutor.submit(new DownloadRunnable(this, i));
            }

            writeMissionInfo();

            if (missionListener != null) {
                MissionListener.HANDLER_STORE.get(missionListener).post(new Runnable() {
                    @Override
                    public void run() {
                        missionListener.onStart();
                    }
                });
            }

//			for (WeakReference<MissionListener> ref: mListeners) {
//				final MissionListener listener = ref.get();
//				if (listener != null) {
//					MissionListener.HANDLER_STORE.get(listener).post(new Runnable() {
//						@Override
//						public void run() {
//							listener.onStart();
//						}
//					});
//				}
//			}
        }
    }

    public void pause() {
        initCurrentRetryCount();
        if (isRunning() || isWaiting()) {
//			running = false;
            missionState = MissionState.PAUSE;
            recovered = true;

            writeMissionInfo();

            Log.d(TAG, "已暂停");

            if (missionListener != null) {
                MissionListener.HANDLER_STORE.get(missionListener).post(new Runnable() {
                    @Override
                    public void run() {
                        missionListener.onPause();
                    }
                });
            }

            if (missionState != MissionState.WAITING) {
                DownloadManagerImpl.decreaseDownloadingCount();
            }

            if (missionConfig.getEnableNotificatio()) {
                NotifyUtil.with(getContext())
                        .buildProgressNotify()
                        .setProgressAndFormat(getProgress(), false, "")
                        .setId(getId())
                        .setContentTitle("已暂停：" + name)
                        .show();
            }
        }
    }

    public void delete() {
        deleteMissionInfo();
        new File(missionConfig.getDownloadPath() + File.separator + name).delete();
    }


//	private transient final ProgressBuilder progressBuilder = new ProgressBuilder();

    public void initNotification() {
//		progressBuilder.setId(getId());
//		progressBuilder.setSmallIcon(android.R.mipmap.sym_def_app_icon);
    }

    public boolean isBlockPreserved(long block) {
        Boolean state = blockState.get(block);
        return state != null && state;
    }

    public void preserveBlock(long block) {
        synchronized (blockState) {
            blockState.put(block, true);
        }
    }


    //progress
    public synchronized void notifyProgress(long deltaLen) {
        if (missionState != MissionState.RUNNING) {
            return;
        }

        if (recovered) {
            recovered = false;
        }

        done += deltaLen;

        if (done > length) {
            done = length;
        }

        if (done != length) {
            Log.d(TAG, "已下载");
            writeMissionInfo();
            threadPoolExecutor.submit(progressRunnable);

//			for (WeakReference<MissionListener> ref: mListeners) {
//				final MissionListener listener = ref.get();
//				if (listener != null) {
//					MissionListener.HANDLER_STORE.get(listener).post(new Runnable() {
//						@Override
//						public void run() {
//							listener.onProgressUpdate(done, length);
//						}
//					});
//				}
//			}
        }
    }

    public synchronized void notifyFinished() {
        if (errCode > 0) {
            return;
        }

        finishCount++;

        if (finishCount == threadCount) {
            onFinish();
        }
    }

    private void onFinish() {
        if (errCode > 0) {
            return;
        }
        Log.d(TAG, "onFinish");

        missionState = MissionState.FINISHED;

        writeMissionInfo();

        if (missionListener != null) {
            MissionListener.HANDLER_STORE.get(missionListener).post(new Runnable() {
                @Override
                public void run() {
                    missionListener.onFinish();
                }
            });
        }

        DownloadManagerImpl.decreaseDownloadingCount();

        if (missionConfig.getEnableNotificatio()) {
            NotifyUtil.with(getContext())
                    .buildNotify()
                    .setContentTitle(name)
                    .setContentText("下载已完成")
                    .setId(getId())
                    .show();
        }

//		for (WeakReference<MissionListener> ref : mListeners) {
//			final MissionListener listener = ref.get();
//			if (listener != null) {
//				MissionListener.HANDLER_STORE.get(listener).post(new Runnable() {
//					@Override
//					public void run() {
//						listener.onFinish();
//					}
//				});
//			}
//		}
    }

    private synchronized void onRetry() {
        if (missionListener != null) {
            MissionListener.HANDLER_STORE.get(missionListener).post(new Runnable() {
                @Override
                public void run() {
                    missionListener.onRetry();
                }
            });
        }
    }

    synchronized void notifyError(int err) {

        if (!(err == ErrorCode.ERROR_WITHOUT_STORAGE_PERMISSIONS || err == ErrorCode.ERROR_FILE_NOT_FOUND)) {
            errorCount++;
            if (errorCount == threadCount) {
                currentRetryCount--;
                if (currentRetryCount >= 0) {
                    pause();
                    onRetry();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, missionConfig.getRetryDelay());
                    return;
                }
            }
        }

        missionState = MissionState.ERROR;

        currentRetryCount = missionConfig.getRetryCount();

        errCode = err;

        Log.d("eeeeeeeeeeeeeeeeeeee", "error:" + errCode);

        writeMissionInfo();

        if (missionListener != null) {
            MissionListener.HANDLER_STORE.get(missionListener).post(new Runnable() {
                @Override
                public void run() {
                    missionListener.onError(errCode);
                }
            });
        }

        DownloadManagerImpl.decreaseDownloadingCount();

        if (missionConfig.getEnableNotificatio()) {
            NotifyUtil.with(getContext())
                    .buildNotify()
                    .setContentTitle("下载出错" + errCode + ":" + name)
                    .setId(getId())
                    .show();
        }
    }

    public void waiting() {
        pause();
        notifyWaiting();
    }

    private void notifyWaiting() {
//		waiting = true;
        missionState = MissionState.WAITING;
    }

    public synchronized void addListener(MissionListener listener) {
        Handler handler = new Handler(Looper.getMainLooper());
        MissionListener.HANDLER_STORE.put(listener, handler);
//		mListeners.add(new WeakReference<>(listener));
        missionListener = listener;
    }

    public synchronized void removeListener(MissionListener listener) {
//		for (Iterator<WeakReference<MissionListener>> iterator = mListeners.iterator();
//             iterator.hasNext(); ) {
//			WeakReference<MissionListener> weakRef = iterator.next();
//			if (listener!=null && listener == weakRef.get())
//			{
//				iterator.remove();
//			}
//		}
        missionListener = null;
    }


    public void writeMissionInfo() {
        if (!mWritingToFile) {
            mWritingToFile = true;
            if (threadPoolExecutor == null) {
                threadPoolExecutor = ThreadPoolFactory.newFixedThreadPool(missionConfig.getThreadPoolConfig());
            }
            threadPoolExecutor.submit(writeMissionInfoRunnable);
        }
    }

    public void deleteMissionInfo() {
        File file = new File(getMissionInfoFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    private void initCurrentRetryCount() {
        if (currentRetryCount != missionConfig.getRetryCount()) {
            currentRetryCount = missionConfig.getRetryCount();
        }
    }

    private Context getContext() {
        return DownloadManagerImpl.getInstance().getContext();
    }

    public String getDownloadPath() {
        return missionConfig.getDownloadPath();
    }

    public String getFilePath() {
        return getDownloadPath() + File.separator + name;
    }

    public String getUserAgent() {
        return missionConfig.getUserAgent();
    }

    public String getCookie() {
        return missionConfig.getCookie();
    }

    public int getBlockSize() {
        return missionConfig.getBlockSize();
    }

    public int getConnectOutTime() {
        return missionConfig.getConnectOutTime();
    }

    public int getReadOutTime() {
        return missionConfig.getReadOutTime();
    }

    Map<String, String> getHeaders() {
        return missionConfig.getHeaders();
    }

    public float getProgress() {
        if (missionState == MissionState.FINISHED) {
            return 100f;
        } else if (length <= 0) {
            return 0f;
        }
        float progress = (float) done / (float) length;
        return progress * 100f;
    }

    public String getSpeed() {
        return "";
    }

    private int getId() {
        if (notifyId == 0) {
            notifyId = (int) (createTime / 10000) + (int) (createTime % 10000) * 100000;
        }
        return notifyId;
    }

    public void setPosition(int id, long position) {
        threadPositions.set(id, position);
    }

    public long getPosition(int id) {
        return threadPositions.get(id);
    }

    public String getMissionInfoFilePath() {
        return DownloadManagerImpl.TASK_PATH + File.separator + uuid + DownloadManagerImpl.MISSION_INFO_FILE_SUFFIX_NAME;
    }


    //下载任务状态
    public boolean isIniting() {
        return missionState == MissionState.INITING;
    }

    public boolean isRunning() {
        return missionState == MissionState.RUNNING;
    }

    public boolean isWaiting() {
        return missionState == MissionState.WAITING;
    }

    public boolean isPause() {
        return missionState == MissionState.PAUSE;
    }

    public boolean isFinished() {
        return missionState == MissionState.FINISHED;
    }

    public boolean isError() {
        return missionState == MissionState.ERROR;
    }





    private boolean handleResponse(Connection.Response response, DownloadMission mission) {
        if (response.statusCode() == ResponseCode.RESPONSE_302
                || response.statusCode() == ResponseCode.RESPONSE_301
                || response.statusCode() == ResponseCode.RESPONSE_300) {
            String redictUrl = response.header("location");
            Log.d(TAG, "redictUrl=" + redictUrl);
            if (redictUrl != null) {
                mission.url = redictUrl;
                mission.redictUrl = redictUrl;
            }
        } else if (response.statusCode() == ErrorCode.ERROR_SERVER_404){
            mission.errCode = ErrorCode.ERROR_SERVER_404;
            mission.notifyError(ErrorCode.ERROR_SERVER_404);
            return true;
        } else if (response.statusCode() == ResponseCode.RESPONSE_206){
            Log.d("statusCode11111111", "       " + response.statusCode());
            String contentLength = response.header("Content-Length");
            Log.d("response.headers()", "1111" + response.headers());

            if (TextUtils.isEmpty(mission.name)) {
                mission.name = getMissionNameFromResponse(response);
                Log.d("mission.name", "mission.name333=" + mission.name);
            }

            mission.length = Long.parseLong(contentLength);

            Log.d("mission.length", "mission.length=" + mission.length);

            return !checkLength(mission);
        }
        return false;
    }

    private String getMissionNameFromResponse(Connection.Response response) {
        String contentDisposition = response.header("Content-Disposition");
        Log.d("contentDisposition", "contentDisposition=" + contentDisposition);
        if (contentDisposition != null) {
            String[] dispositions = contentDisposition.split(";");
            for (String disposition : dispositions) {
                Log.d("disposition", "disposition=" + disposition);
                if (disposition.contains("filename=")) {
                    return disposition.replace("filename=", "");
                }
            }
        }
        return "";
    }

    private String getMissionNameFromUrl(DownloadMission mission, String url) {
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
                    if (FileUtil.checkFileType(originName) != FileUtil.FILE_TYPE.UNKNOWN) {
                        Log.d("getMissionNameFromUrl", "4");
                        return originName;
                    }
                }

                if (FileUtil.checkFileType(name) != FileUtil.FILE_TYPE.UNKNOWN || name.contains(".")) {
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

    private boolean checkLength(DownloadMission mission) {
        if (mission.length <= 0) {
            mission.errCode = ErrorCode.ERROR_SERVER_UNSUPPORTED;
            mission.notifyError(ErrorCode.ERROR_SERVER_UNSUPPORTED);
            return false;
        } else if (mission.length >= Utility.getAvailableSize()) {
            mission.errCode = ErrorCode.ERROR_NO_ENOUGH_SPACE;
            mission.notifyError(ErrorCode.ERROR_NO_ENOUGH_SPACE);
            return false;
        }
        return true;
    }

}
