package com.upload.imagepicker.utils;

public class Resource<T> {
    private Enum status;
    private T data;
    private String message;

    public Resource(Enum status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static  <T> Resource success(T data)  {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static  <T> Resource error(String message, T data)  {
        return new Resource<>(Status.ERROR, data, message);
    }

    public static  <T> Resource authError(T data) {
        return new Resource<>(Status.AUTH_ERROR, data, null);
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Enum getStatus() {
        return status;
    }
}
