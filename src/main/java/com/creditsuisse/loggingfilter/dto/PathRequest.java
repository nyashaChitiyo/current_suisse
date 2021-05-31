package com.creditsuisse.loggingfilter.dto;

public class PathRequest {

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "PathRequest{" +
                "path='" + path + '\'' +
                '}';
    }
}
