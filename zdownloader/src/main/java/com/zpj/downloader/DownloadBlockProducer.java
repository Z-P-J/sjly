//package com.zpj.downloader;
//
//import android.util.Log;
//
//import com.zpj.downloader.constant.Error;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.annotations.NonNull;
//
//class DownloadBlockProducer implements ObservableOnSubscribe<DownloadBlock> {
//
//    private static final String TAG = "DownloadProducer";
//
//    private final BaseMission<?> mMission;
//    private final ConcurrentLinkedQueue<DownloadBlock> queue;
//    private final int blockSize;
//    private final int maxSize;
//
//    DownloadBlockProducer(BaseMission<?> mission, ConcurrentLinkedQueue<DownloadBlock> queue) {
//        this.mMission = mission;
//        this.queue = queue;
//        this.blockSize = mission.getBlockSize();
////        this.maxSize = 3 * mission.getThreadCount();
//        this.maxSize = mission.getConsumerThreadCount();
////        this.maxSize = 9;
//    }
//
//    @Override
//    public void subscribe(@NonNull ObservableEmitter<DownloadBlock> emitter) throws Exception {
//        String mId = Thread.currentThread().getName();
//        while (true) {
//            long startTime = System.currentTimeMillis();
//            synchronized (mMission) {
//                if (!mMission.isRunning()) {
//                    break;
//                }
//            }
//
//            long position = mMission.getNextPosition();
////            Log.d(TAG, "id=" + mId + " position=" + position + " blocks=" + mMission.getBlocks());
//            if (position < 0 || position >= mMission.getBlocks()) {
//                break;
//            }
//
//
//
//            long start = position * blockSize;
//            long end = start + blockSize - 1;
//
//            if (start >= mMission.getLength()) {
//                continue;
//            }
//
//            if (end >= mMission.getLength()) {
//                end = mMission.getLength() - 1;
//            }
//
//            HttpURLConnection conn = null;
//
//            try {
//                conn = HttpUrlConnectionFactory.getConnection(mMission, start, end);
//
////                Log.d(TAG, mId + ":" + conn.getRequestProperty("Range"));
////                Log.d(TAG, mId + ":Content-Length=" + conn.getContentLength() + " Code:" + conn.getResponseCode());
//
//                if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
//                        || conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
//                        || conn.getResponseCode() == HttpURLConnection.HTTP_MULT_CHOICE) {
//                    String redictUrl = conn.getHeaderField("location");
//                    Log.d(TAG, "redictUrl=" + redictUrl);
//                    mMission.setUrl(redictUrl);
//                    mMission.setRedirectUrl(redictUrl);
//                    conn.disconnect();
//                    conn = HttpUrlConnectionFactory.getConnection(mMission, start, end);
//                }
//
//                // A server may be ignoring the range requet
//                if (conn.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
//                    Log.d("DownRun", "error:206");
//                    mMission.onPositionDownloadFailed(position);
//                    mMission.notifyError(Error.getHttpError(conn.getResponseCode()), true);
//
//                    Log.e(TAG, mId + ":Unsupported " + conn.getResponseCode());
//
//                    return;
//                }
//
////                InputStream stream = conn.getInputStream();
//                DownloadBlock block = new DownloadBlock(position, blockSize, start, end, conn);
////                synchronized (queue) {
////                    if (queue.remainingCapacity() > 0) {
////                        queue.add(block);
//////                        emitter.onNext(block);
////                    } else {
////                        try {
////                            queue.wait();
////                            if (queue.remainingCapacity() > 0) {
////                                queue.add(block);
////                            }
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                }
//                queue.add(block);
//                int size = queue.size();
//                Log.e(TAG, "addBlock size=" + size + " maxSize=" + maxSize);
//                if (size > maxSize) {
//                    synchronized (queue) {
//                        try {
//                            Log.e(TAG, "addBlock queue.wait()");
//                            queue.wait();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
////                synchronized (queue) {
////                    if (queue.remainingCapacity() > 0) {
////                        queue.add(block);
//////                        emitter.onNext(block);
////                    } else {
////                        try {
////                            queue.wait();
////                            if (queue.remainingCapacity() > 0) {
////                                queue.add(block);
////                            }
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                }
//            } catch (IOException e) {
//                mMission.onPositionDownloadFailed(position);
//                Log.d(TAG, mId + ":position " + position + " retrying");
//            }
//            Log.d(TAG, "DownloadBlockProducer Finished Time=" + (System.currentTimeMillis() - startTime));
////            finally {
////                if (conn != null) {
////                    conn.disconnect();
////                }
////            }
////            try {
////                Thread.sleep(200);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//        emitter.onComplete();
//    }
//}
//
