package com.hiperium.city.tasks.api.exceptions;

public class ApplicationException extends RuntimeException {

    private final String errorCode;
    private final String errorMessageKey;
    private final transient Object[] args;

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.errorMessageKey = null;
        this.args = null;
    }

    protected ApplicationException(String errorCode, String errorMessageKey, Object... args) {
        super();
        this.errorCode = errorCode;
        this.errorMessageKey = errorMessageKey;
        this.args = args;
    }

    protected ApplicationException(Exception exception, String errorCode, String errorMessageKey, Object... args) {
        super(exception.getMessage(), exception);
        this.errorCode = errorCode;
        this.errorMessageKey = errorMessageKey;
        this.args = args;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessageKey() {
        return errorMessageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
