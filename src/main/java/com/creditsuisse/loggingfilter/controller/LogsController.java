package com.creditsuisse.loggingfilter.controller;

import com.creditsuisse.loggingfilter.dto.PathRequest;
import com.creditsuisse.loggingfilter.model.LogEvent;
import com.creditsuisse.loggingfilter.repository.LogsRepository;
import com.creditsuisse.loggingfilter.service.LogThreadsService;
import com.creditsuisse.loggingfilter.service.LogsService;
import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("logs")
@Slf4j
public class LogsController {

    private LogThreadsService threadsService;
    private LogsService logsService;

    public LogsController(LogThreadsService threadsService, LogsService logsService) {
        this.threadsService = threadsService;
        this.logsService = logsService;
    }

    @PostMapping("/path")
    public String getPath(@RequestBody @NotNull PathRequest path){
        log.info(path+"");
        String strPath = path.getPath();
        threadsService.assignThreads(strPath);
        return "";
    }

    @GetMapping("/all")
    public Iterable<LogEvent> getAll(){
        return logsService.getAll();
    }

}
