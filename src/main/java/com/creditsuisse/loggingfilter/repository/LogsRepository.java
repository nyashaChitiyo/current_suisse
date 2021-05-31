package com.creditsuisse.loggingfilter.repository;
import com.creditsuisse.loggingfilter.model.LogEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends CrudRepository<LogEvent,String> {
}
