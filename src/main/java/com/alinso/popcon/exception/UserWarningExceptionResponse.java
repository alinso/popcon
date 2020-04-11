package com.alinso.popcon.exception;
public class UserWarningExceptionResponse {

    private String userWarningMessage;

    public UserWarningExceptionResponse(String message) {
        this.userWarningMessage = message;
    }

    public String getUserWarningMessage() {
        return userWarningMessage;
    }

    public void setUserWarningMessage(String userWarningMessage) {
        this.userWarningMessage = userWarningMessage;
    }
}