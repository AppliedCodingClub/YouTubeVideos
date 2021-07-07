package com.appliedcoding.snakegame.exception;

public class SnakeException extends RuntimeException {

    public SnakeException(Exception e) {
        super(e);
    }

    public SnakeException(String s) {
        super(s);
    }
}
