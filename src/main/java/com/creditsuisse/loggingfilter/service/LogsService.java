package com.creditsuisse.loggingfilter.service;

import com.creditsuisse.loggingfilter.model.LogEvent;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

public interface LogsService {
    void parseJson(String json);
    LinkedList<LogEvent> getLogEventList();
    Iterable<LogEvent> getAll();
}
