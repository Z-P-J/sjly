package io.haydar.filescanner;

import java.util.ArrayList;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore
 * @DATE 2017-04-14
 */

public class FileInfo {
    private int count;
    private String filePath;
    private long fileSize;
    private long lastModifyTime;
    private int type;//1文件夹 2 mp3

    public FileInfo(){

    }

    public int getCount() {
        ArrayList list = new ArrayList();
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "count=" + count +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", lastModifyTime=" + lastModifyTime +
                ", type=" + type +
                '}';
    }
}
