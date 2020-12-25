package com.zpj.downloader;

import android.support.annotation.Keep;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

@Keep
class DownloadBlock {

    private final long position;
    private final long blockSize;
    private final long start;
    private final long end;
//    private boolean isFinished;

//    private InputStream inputStream;
    private final HttpURLConnection conn;

    DownloadBlock(long position, long blockSize, long start, long end, HttpURLConnection conn) {
        this.position = position;
        this.blockSize = blockSize;
//        this.start = position * blockSize;
//        this.end = start + blockSize - 1;
        this.start = start;
        this.end = end;
        this.conn = conn;
    }

    public void disconnect() {
        conn.disconnect();
    }

    public InputStream getInputStream() throws IOException {
        return conn.getInputStream();
    }

//    public synchronized void setFinished(boolean finished) {
//        isFinished = finished;
//    }

//    @Override
//    public void execute() {
//        if (!isFinished()) {
//
//        }
//    }
//
//
//    public static DownloadBlock fetch(DownloadMission mission, long position, long blockSize) {
//
//    }
//
//    @Override
//    public void write() {
//
//    }
//
//    @Override
//    public boolean isFinished() {
//        return isFinished;
//    }

    public long getPosition() {
        return position;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        DownloadBlock that = (DownloadBlock) o;
//
//        if (position != that.position) return false;
//        if (blockSize != that.blockSize) return false;
//        if (start != that.start) return false;
//        return end == that.end;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = (int) (position ^ (position >>> 32));
//        result = 31 * result + (int) (blockSize ^ (blockSize >>> 32));
//        result = 31 * result + (int) (start ^ (start >>> 32));
//        result = 31 * result + (int) (end ^ (end >>> 32));
//        return result;
//    }

    @Override
    public String toString() {
        return "DownloadBlock{" +
                "position=" + position +
                ", blockSize=" + blockSize +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
