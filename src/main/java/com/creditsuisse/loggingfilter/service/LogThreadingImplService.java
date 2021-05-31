package com.creditsuisse.loggingfilter.service;

import com.creditsuisse.loggingfilter.model.LogEvent;
import com.creditsuisse.loggingfilter.repository.LogsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@Slf4j
public class LogThreadingImplService implements LogThreadsService {

    private LogsRepository repository;
    private LogsService logsService;
    private ScheduledExecutorService service = null;

    public LogThreadingImplService(LogsRepository repository,LogsService logsService){
        this.repository = repository;
        this.logsService = logsService;
    }
    public void getFile(String path){
        log.debug("it's working yes!!!");
    }

    public void assignThreads(String path){

        //Validate String if it is a valid path

        Path b = validatePath(path);

            try{
                service = getExecutorService();

                /*Thread which will extract json string from the file
                  Convert it to an Object
                  and save it to a Map
                 */

                Runnable run = ()->{
                    try (Stream<String> stream = Files.lines(b)) {
                       log.debug("in thread");
                        stream.peek(x->{logsService.parseJson(x);}).count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };

                //Thread which will save and remove objects in the list

                Runnable run1 = ()->{
                    saveToDB(logsService.getLogEventList());
                };

                service.submit(run);

                //2nd thread will restart every second after it finishes execution
                // to save objects added in the list by the 1st thread

                service.scheduleAtFixedRate(run1,0,1, TimeUnit.SECONDS);
            }
            finally {

            }
    }

    private Path validatePath(String strPath) {

        // method to validate path variable

        Path path;
        try{
            path = Paths.get(strPath);
            return path;
        }
        catch (InvalidPathException ex) {
            throw new IllegalStateException("Invalid path");
        }
    }

    //this mothod takes a list of objects to be saved
    //and queries one by one and saving to the database

    private void saveToDB(LinkedList<LogEvent> logEventList){
        if(logEventList.isEmpty())
            return;
        log.debug("saved in HSQLDB "+logEventList);
        ListIterator it = logEventList.listIterator();
        while (it.hasNext()){
            LogEvent event = logEventList.remove();
            saveLogEvent(event);
             log.debug("saved in HSQLDB "+event);
        }
    }

    //saves to the actual database

    public LogEvent saveLogEvent(LogEvent event){
        if(event.getId() == null)
            throw new IllegalStateException("event id cannot be null");
        if(event == null)
            throw new IllegalArgumentException("Event cannot be null");
        return Optional.ofNullable(repository.save(event)).orElseThrow();
    }

    // check is our Executor service is running or not
    public boolean isExecutorRunning(){
        if(service == null)
            throw new IllegalStateException("Executor was never executed");
        if(service.isShutdown()){
            return false;
        }
        return true;
    }

    // closes Executor services
    public String closeExecutor(){
        if(service == null)
            throw new IllegalStateException("Executor was never executed");
        service.shutdown();
        return "Executor was succesfully closed";
    }

    //creates ScheduledExecutorService with a thread pool of 4 threads
    public ScheduledExecutorService getExecutorService(){
        return Executors.newScheduledThreadPool(4);
    }
}
