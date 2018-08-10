package com.smaaash.tv.apis.base;

/**
 * Created by Siddhesh on 08-03-2018.
 */

public class AnyError implements Error {
    private String message;
    private int errorCode;

    public AnyError(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
