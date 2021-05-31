package com.creditsuisse.loggingfilter.service;


import com.creditsuisse.loggingfilter.dto.LogDto;
import com.creditsuisse.loggingfilter.model.LogEvent;
import com.creditsuisse.loggingfilter.repository.LogsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class LogsServiceImpl implements LogsService{

    //map which will save ID of log and Log Object as value
    private Map<String, LogDto> iDTimestamp = new ConcurrentHashMap<>();

    //List which will store all processed logs so that
    // they can be saved to the database
    private LinkedList<LogEvent> logEventList = new LinkedList<>();

    private LogsRepository repository;

    public LogsServiceImpl(LogsRepository repository) {
        this.repository = repository;
    }

    /// validates if the parsed string is a valid json
    public LogDto validateJson(String json){
        if(json == null)
            throw new IllegalStateException("Json Object cannot be null");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readTree(json);
            LogDto logDto = objectMapper.readValue(json, LogDto.class);
            return logDto;
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Invalid Json Object");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("an error occured while proccessing Json Object");
    }
    public void parseJson(String json){

        LogDto currentDto = validateJson(json);
        long duration;
        //get log ID
            String key = currentDto.getId();

            /*Store id in map
            /* If id already exists calculate duration ith the current object
            /* and object in the map with same ID */
            if(iDTimestamp.containsKey(key)){
                //calculates duration
                duration = calcDuration(currentDto);

                //saves object to list of procesed logs
                saveToList(currentDto, duration);

                //remove processed log objects from map
                iDTimestamp.remove(key);
            }
            else {

                // save object if ID does not exist in map
                iDTimestamp.put(key,currentDto);
            }

    }

    // calculate the duration of the starting time and finishing time of the logs
    private long calcDuration(LogDto currentDto){
        if(currentDto == null){
            throw new IllegalStateException("currentDto cannot be null");
        }

        LogDto savedLogDto = iDTimestamp.get(currentDto.getId());
        long currentTimestamp = currentDto.getTimestamp();
        long savedTimestamp = savedLogDto.getTimestamp();

        String savedState = savedLogDto.getState();
        String currentState = currentDto.getState();
        long duration;


        if(savedState.equals("STARTED") && currentState.equals("FINISHED")){
            System.out.println("in calc duration start"+currentDto);
            if(currentTimestamp < savedTimestamp)
                throw new IllegalStateException("FINISHED time cannot be smaller than STARTING time");
            duration = Math.subtractExact(currentTimestamp,savedTimestamp);
        }
        else if(savedState.equals("FINISHED") && currentState.equals("STARTED")){
            System.out.println("in calc duration finished"+currentDto);
            if(currentTimestamp > savedTimestamp)
                throw new IllegalStateException("FINISHED time cannot be smaller than STARTING time");
            duration = Math.subtractExact(savedTimestamp,currentTimestamp);
        }
        else{
            throw new IllegalStateException("Invalid State");
        }
        System.out.println("duration of "+duration);
        return duration;
    }

    // save prrocessed log to list awaiting to be saaved to database
    private LogEvent saveToList(LogDto currentDto, long currentDuration){
        String id = currentDto.getId();
        long duration = currentDuration;
        String type = currentDto.getType();
        String host = currentDto.getHost();

        LogEvent event = LogEvent.getLogEvent(id,duration,type,host);
        getLogEventList().add(event);
        return event;
    }

    //returns list with objects to be saved to the database
    public synchronized LinkedList<LogEvent> getLogEventList(){
        return logEventList;
    }

    public Iterable<LogEvent> getAll(){
        return  repository.findAll();
    }
}
