package com.zpj.downloader.core;

import android.support.annotation.Keep;

@Keep
public class DownloadBlock implements IBlock {

    private final long position;
    private final long blockSize;
    private final long start;
    private final long end;
    private boolean isFinished;

    public DownloadBlock(long position, long blockSize) {
        this.position = position;
        this.blockSize = blockSize;
        this.start = position * blockSize;
        this.end = start + blockSize - 1;
    }

    public synchronized void setFinished(boolean finished) {
        isFinished = finished;
    }

    @Override
    public void execute() {
        if (!isFinished()) {

        }
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public long getBlockSize() {
        return blockSize;
    }

    @Override
    public long getStart() {
        return start;
    }

    @Override
    public long getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadBlock that = (DownloadBlock) o;

        if (position != that.position) return false;
        if (blockSize != that.blockSize) return false;
        if (start != that.start) return false;
        return end == that.end;
    }

    @Override
    public int hashCode() {
        int result = (int) (position ^ (position >>> 32));
        result = 31 * result + (int) (blockSize ^ (blockSize >>> 32));
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (end ^ (end >>> 32));
        return result;
    }

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
