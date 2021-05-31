The system contains 4 endpoints

Post : http://localhost:8080/logs/path  json request body {path: ""}  send the path of the file as part of the request

Get  : http://localhost:8080/logs/all    returns all the saved proccessed logs

Get  : http://localhost:8080/executor/close   closes all threading services

Get  : http://localhost:8080/executor/status  returns Status whether executor is running or not

Requires a hsqldb database named creditsuisse
