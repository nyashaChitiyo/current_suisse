package com.creditsuisse.loggingfilter.services;

import com.creditsuisse.loggingfilter.model.LogEvent;
import com.creditsuisse.loggingfilter.repository.LogsRepository;
import com.creditsuisse.loggingfilter.service.LogThreadingImplService;
import com.creditsuisse.loggingfilter.service.LogsService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LogThreadingImplServiceTest {

    LogsRepository repository = mock(LogsRepository.class);
    LogsService logService = mock(LogsService.class);

    @Test
    void throwExceptionForClosedExecutor(){
        LogThreadingImplService service = new LogThreadingImplService(repository,logService);
        IllegalStateException stateException = assertThrows(IllegalStateException.class,()->service.closeExecutor());
        String expectedMsg = "Executor was never executed";
        String actualMsg = stateException.getMessage();
        assertTrue(actualMsg.equals(expectedMsg));
    }

    @Test
    void throwExceptionIfEventIsnull(){
        when(repository.save(null)).thenThrow(new IllegalArgumentException("Event cannot be null"));
        LogThreadingImplService service = new LogThreadingImplService(repository,logService);
        assertThrows(IllegalArgumentException.class,()->service.saveLogEvent(null));
    }

    @Test
    void SaveEventLog(){
        LogEvent event = LogEvent.getLogEvent("nyasha",1234,"type","host");
        when(repository.save(event)).thenReturn(event);
        LogThreadingImplService service = new LogThreadingImplService(repository,logService);
        assertNotNull(service.saveLogEvent(event));
    }

    @Test
    void throwExceptionSavingEventIdIsnull(){
        LogEvent event = LogEvent.getLogEvent(null,1234,"type","host");
        when(repository.save(event)).thenThrow(new IllegalStateException("event id cannot be null"));
        LogThreadingImplService service = new LogThreadingImplService(repository,logService);
        assertThrows(IllegalStateException.class,()->service.saveLogEvent(event));
    }

}

