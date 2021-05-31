package com.creditsuisse.loggingfilter.controller;


import com.creditsuisse.loggingfilter.service.LogThreadsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/executor")
public class ExecutorController {

    private LogThreadsService threadsService;

    public ExecutorController(LogThreadsService threadsService) {
        this.threadsService = threadsService;
    }

    @GetMapping("/status")
    public boolean isExecutorRunning(){
        return threadsService.isExecutorRunning();
    }

    @GetMapping("/close")
    public String shutdownExecutor(){
        return threadsService.closeExecutor();
    }
}
