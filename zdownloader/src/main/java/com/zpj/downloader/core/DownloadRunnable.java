package com.zpj.downloader.core;

import android.util.Log;

import com.zpj.downloader.constant.Error;
import com.zpj.downloader.constant.ErrorCode;
import com.zpj.downloader.constant.ResponseCode;
import com.zpj.downloader.util.io.BufferedRandomAccessFile;
import com.zpj.downloader.util.permission.PermissionUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;

public class DownloadRunnable implements Runnable {
    private static final String TAG = DownloadRunnable.class.getSimpleName();

    private static final int BUFFER_SIZE = 512;

    private final DownloadMission mMission;
    private int mId;

    private final byte[] buf = new byte[BUFFER_SIZE];

    private BufferedRandomAccessFile f;

    DownloadRunnable(DownloadMission mission, int id) {
        mMission = mission;
        mId = id;
        try {
            f = new BufferedRandomAccessFile(mMission.getFilePath(), "rw");
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof FileNotFoundException) {
				notifyError(Error.FILE_NOT_FOUND);
			} else {
				if (PermissionUtil.checkStoragePermissions(DownloadManagerImpl.getInstance().getContext())) {
					notifyError(new Error(e.getMessage()));
				} else {
					notifyError(Error.WITHOUT_STORAGE_PERMISSIONS);
				}
			}
        }
    }

    @Override
    public void run() {
		if (mMission.isFallback()) {
			try {
				HttpURLConnection conn = HttpUrlConnectionFactory.getConnection(mMission);
				if (conn.getResponseCode() / 100 != 2) {
					Log.d("DownRunFallback", "error:206");
					notifyError(Error.SERVER_UNSUPPORTED);
					return;
				} else {
					f.seek(0);
					BufferedInputStream ipt = new BufferedInputStream(conn.getInputStream());

					int total = 0;
//					int lastTotal = 0;
					while (mMission.isRunning()) {
						long readStartTime = System.currentTimeMillis();
						final int len  = ipt.read(buf, 0, BUFFER_SIZE);
						long readFinishedTime = System.currentTimeMillis();
						Log.d(TAG, "readTime=" + (readFinishedTime - readStartTime));
						if (len == -1) {
							notifyProgress(0);
							break;
						}
						total += len;
						f.write(buf, 0, len);
						f.flush();
						notifyProgress(len);
//						notifyProgress(total - lastTotal);
//						lastTotal = total;
						mMission.setLength(total);


						if (Thread.currentThread().isInterrupted()) {
							return;
						}
						Log.d(TAG, "writeTime=" + (System.currentTimeMillis() - readFinishedTime));
					}

//					f.close();
					ipt.close();
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
//				notifyError(ErrorCode.ERROR_CONNECTION_TIMED_OUT);
				notifyError(new Error(e.getMessage()));
				return;
			}
		} else {
			mMission.setErrCode(-1);

			Log.d(TAG, mId + ":isRunning=" + mMission.isRunning());
			Log.d(TAG, mId + ":blocks=" + mMission.getBlocks());
			while (mMission.getErrCode() == -1 && mMission.isRunning()) {

				Log.d("timetimetimetime" + mId, "----------------------------start-------------------------");
				long time0 = System.currentTimeMillis();

				if (Thread.currentThread().isInterrupted()) {
					return;
				}

				long position = mMission.getPosition();
				Log.d(TAG, "id=" + mId + " position=" + position + " blocks=" + mMission.getBlocks());
				if (position < 0 || position > mMission.getBlocks()) {
					break;
				}

				Log.d(TAG, mId + ":preserving position " + position);

				long start = position * mMission.getBlockSize();
				long end = start + mMission.getBlockSize() - 1;

				if (start >= mMission.getLength()) {
					continue;
				}

				if (end >= mMission.getLength()) {
					end = mMission.getLength() - 1;
				}

				HttpURLConnection conn;

				int total = 0;

				long time_0 = System.currentTimeMillis();
				Log.d("timetimetimetime" + mId, "timetime=" + (time_0 - time0));
				try {
					conn = HttpUrlConnectionFactory.getConnection(mMission, start, end);

					Log.d(TAG, mId + ":" + conn.getRequestProperty("Range"));
					Log.d(TAG, mId + ":Content-Length=" + conn.getContentLength() + " Code:" + conn.getResponseCode());

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
						notifyError(Error.getHttpError(conn.getResponseCode()));

						Log.e(TAG, mId + ":Unsupported " + conn.getResponseCode());

						return;
					}

					long time_1 = System.currentTimeMillis();
					Log.d("timetimetimetime" + mId, "ttttttttt=" + (time_1 - time_0));

					f.seek(start);
					BufferedInputStream ipt = new BufferedInputStream(conn.getInputStream());
					long time1 = System.currentTimeMillis();
					Log.d("timetimetimetime" + mId, "hhhhhhhhhh=" + (time1 - time_1));
					int i = 0;
					int tempTime = 0;
					int tempTime2 = 0;
					int tempTime3 = 0;
					int tempTime4 = 0;

					while (start < end && mMission.isRunning()) {
						i++;
						long time3 = System.currentTimeMillis();
						final int len = ipt.read(buf, 0, BUFFER_SIZE);
						long timet = System.currentTimeMillis();
						tempTime4 += (timet - time3);

						if (len == -1) {
							break;
						} else {
							start += len;
							total += len;
							long time_4 = System.currentTimeMillis();
							f.write(buf, 0, len);

							Log.d("len", "len=" + len);
							long time4 = System.currentTimeMillis();
							tempTime2 += (time4 - time_4);
							notifyProgress(len);
							long time5 = System.currentTimeMillis();
							tempTime3 += (time5 - time4);
							tempTime += (time5 - time3);
						}
					}
					Log.d("timetimetimetime" + mId, "tempTime=" + tempTime);
					Log.d("timetimetimetime" + mId, "tempTime2=" + tempTime2);
					Log.d("timetimetimetime" + mId, "tempTime3=" + tempTime3);
					Log.d("timetimetimetime" + mId, "tempTime4=" + tempTime4);
					ipt.close();
					conn.disconnect();

					long time2 = System.currentTimeMillis();
					Log.d("timetimetimetime" + mId, "time3333=" + (time2 - time1));
					Log.d("timetimetimetime" + mId, "----------------------------finished-------------------------");
					Log.d(TAG, mId + ":position " + position + " finished, total length " + total);

					Log.d(TAG, mId + ":errorCode=" + mMission.getErrCode());
					Log.d(TAG, mId + ":isRunning=" + mMission.isRunning());
					Log.d(TAG, mId + ":blocks=" + mMission.getBlocks());
					mMission.preserveBlock(position);

					// TODO We should save progress for each thread
				} catch (IOException e) {

					notifyProgress(-total);
					mMission.onPositionDownloadFailed(position);

					Log.d(TAG, mId + ":position " + position + " retrying");
				}
			}
		}

        Log.d(TAG, "thread " + mId + " exited main loop");
        Log.d(TAG, "mMission.getDone()=" + mMission.getDone());
        Log.d(TAG, "mMission.getLength()=" + mMission.getLength());
		File file = mMission.getFile();
        if (mMission.getErrCode() == -1 && mMission.isRunning() && (mMission.getDone() == mMission.getLength() || (file != null && mMission.getLength() == file.length()) || mMission.isFallback())) {
            Log.d(TAG, "no error has happened, notifying");
            notifyFinished();
        }

//        if (!mMission.isRunning()) {
//            Log.d(TAG, "The mission has been paused. Passing.");
//        }
        try {
            f.flush();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyProgress(final int len) {
        Log.d("notifyProgress", "len=" + len);
		synchronized (mMission) {
			mMission.notifyProgress(len);
		}
    }

//    private void notifyError(final int err) {
//        synchronized (mMission) {
//            mMission.notifyError(err, true);
//        }
//    }

	private void notifyError(final Error e) {
		synchronized (mMission) {
			mMission.notifyError(e, true);
		}
	}

    private void notifyFinished() {
        synchronized (mMission) {
            mMission.notifyFinished();
        }
    }
}
