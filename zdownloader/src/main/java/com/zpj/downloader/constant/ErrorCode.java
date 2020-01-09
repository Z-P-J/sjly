package com.zpj.downloader.constant;

public class ErrorCode {

    private ErrorCode() {

    }

    public static final int ERROR_SERVER_UNSUPPORTED = 206;
    public static final int ERROR_SERVER_404 = 404;
    public static final int ERROR_NO_ENOUGH_SPACE = 1000;

    public static final int ERROR_CONNECTION_TIMED_OUT = 0;

    public static final int ERROR_FILE_NOT_FOUND = 1;

    public static final int ERROR_WITHOUT_STORAGE_PERMISSIONS = 2;

}
