package com.zpj.downloader;

import android.util.Log;

import com.zpj.downloader.constant.Error;
import com.zpj.downloader.util.io.BufferedRandomAccessFile;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

class DownloadBlockProducer2 implements ObservableOnSubscribe<DownloadBlock> {

    private static final String TAG = "DownloadProducer";

    private static final int BUFFER_SIZE = 1024;

    private final BaseMission<?> mMission;
    private final int blockSize;

    DownloadBlockProducer2(BaseMission<?> mission) {
        this.mMission = mission;
        this.blockSize = mission.getBlockSize();
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<DownloadBlock> emitter) throws Exception {
        String mId = Thread.currentThread().getName();
        String threadName = Thread.currentThread().getName();
        byte[] buf = new byte[BUFFER_SIZE];
        BufferedRandomAccessFile f;
        try {
            f = new BufferedRandomAccessFile(mMission.getFilePath(), "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            synchronized (mMission) {
                mMission.notifyError(Error.FILE_NOT_FOUND, true);
                return;
            }
        }
        while (true) {
            long startTime = System.currentTimeMillis();
            synchronized (mMission) {
                if (!mMission.isRunning()) {
                    break;
                }
            }

            long position = mMission.getNextPosition();
//            Log.d(TAG, "id=" + mId + " position=" + position + " blocks=" + mMission.getBlocks());
            if (position < 0 || position >= mMission.getBlocks()) {
                break;
            }



            long start = position * blockSize;
            long end = start + blockSize - 1;

            if (start >= mMission.getLength()) {
                continue;
            }

            if (end >= mMission.getLength()) {
                end = mMission.getLength() - 1;
            }

            HttpURLConnection conn = null;

            try {
                conn = HttpUrlConnectionFactory.getConnection(mMission, start, end);

//                Log.d(TAG, mId + ":" + conn.getRequestProperty("Range"));
//                Log.d(TAG, mId + ":Content-Length=" + conn.getContentLength() + " Code:" + conn.getResponseCode());

                if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                        || conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
                        || conn.getResponseCode() == HttpURLConnection.HTTP_MULT_CHOICE) {
                    String redictUrl = conn.getHeaderField("location");
                    Log.d(TAG, "redictUrl=" + redictUrl);
                    mMission.setUrl(redictUrl);
                    mMission.setRedirectUrl(redictUrl);
                    conn.disconnect();
                    conn = HttpUrlConnectionFactory.getConnection(mMission, start, end);
                }

                // A server may be ignoring the range requet
                if (conn.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                    Log.d("DownRun", "error:206");
                    mMission.onPositionDownloadFailed(position);
                    mMission.notifyError(Error.getHttpError(conn.getResponseCode()), true);

                    Log.e(TAG, mId + ":Unsupported " + conn.getResponseCode());

                    return;
                }

                int total = 0;
                try {
                    f.seek(start);
                    InputStream stream = conn.getInputStream();
                    Log.d(TAG, threadName + " stream=" + stream);
                    BufferedInputStream ipt = new BufferedInputStream(stream);
                    while (start < end) { //  && mMission.isRunning()
                        final int len = ipt.read(buf, 0, BUFFER_SIZE);
                        if (len == -1) {
                            break;
                        } else {
                            start += len;
                            f.write(buf, 0, len);
                            total += len;
//                            Log.d(TAG, threadName + " notifyProgress len=" + len);
                            mMission.notifyDownloaded(len);
                        }
                    }
//                    Log.d(TAG, threadName + " start=" + start + " end=" + end + " total=" + total);
                    ipt.close();
                    f.flush();
                    mMission.onBlockFinished(position);
//                    Log.d(TAG, threadName + " onBlockFinished position=" + position);
                } catch (Exception e) {
                    mMission.notifyDownloaded(-total);
                    mMission.onPositionDownloadFailed(position);
                }
                conn.disconnect();
            } catch (IOException e) {
                mMission.onPositionDownloadFailed(position);
                Log.d(TAG, mId + ":position " + position + " retrying");
            }
            Log.d(TAG, "DownloadBlockProducer Finished Time=" + (System.currentTimeMillis() - startTime));
        }
        try {
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "thread=" + Thread.currentThread().getName() + " onComplete");
        emitter.onComplete();
    }
}

