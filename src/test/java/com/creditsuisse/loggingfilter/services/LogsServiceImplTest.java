package com.creditsuisse.loggingfilter.services;


import com.creditsuisse.loggingfilter.dto.LogDto;
import com.creditsuisse.loggingfilter.model.LogEvent;
import com.creditsuisse.loggingfilter.repository.LogsRepository;
import com.creditsuisse.loggingfilter.service.LogsServiceImpl;
import jdk.jfr.Event;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LogsServiceImplTest {

    private LogsRepository repository = mock(LogsRepository.class);

    @Test
    void throwExceptionIfJsonIsInvalid(){

        LogsServiceImpl service = new LogsServiceImpl(repository);
        IllegalStateException stateException = assertThrows(IllegalStateException.class,()->service.parseJson("12312"));
        String expectedMsg = "Invalid Json Object";
        String actualMsg = stateException.getMessage();
        assertEquals(expectedMsg,actualMsg);
    }

    @Test
    void parseValidJsonString(){
        LogsServiceImpl service = mock(LogsServiceImpl.class);
        String jsonString = "{\"id\":\"scsmbstgrc\", \"state\":\"FINISHED\", \"timestamp\":1491377495218}";

        LogDto logDto = new LogDto("scsmbstgrc","FINISHED",null,null,1491377495218l);
        when(service.validateJson(jsonString)).thenReturn(logDto);
        assertNotNull(service.validateJson(jsonString));
    }
}
