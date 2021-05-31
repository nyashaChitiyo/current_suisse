package com.creditsuisse.loggingfilter.service;


public interface LogThreadsService {
    void getFile(String path);
    void assignThreads(String path);
    public boolean isExecutorRunning();
    public String closeExecutor();
}