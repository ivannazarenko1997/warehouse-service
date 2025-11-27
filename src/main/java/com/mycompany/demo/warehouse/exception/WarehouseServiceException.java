package com.mycompany.demo.warehouse.exception;


public class WarehouseServiceException extends RuntimeException {
    public WarehouseServiceException() {
        super();
    }

    public WarehouseServiceException(String message) {
        super(message);
    }

    public WarehouseServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WarehouseServiceException(Throwable cause) {
        super(cause);
    }

    protected WarehouseServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
