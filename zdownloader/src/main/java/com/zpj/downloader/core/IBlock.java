package com.zpj.downloader.core;

public interface IBlock {


    void execute();

    boolean isFinished();

    long getPosition();

    long getBlockSize();

    long getStart();

    long getEnd();

}
