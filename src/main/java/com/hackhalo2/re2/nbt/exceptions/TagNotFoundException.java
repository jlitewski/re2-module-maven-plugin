package com.hackhalo2.re2.nbt.exceptions;

public class TagNotFoundException extends NBTException {

    public TagNotFoundException(String message) {
        super(message);
    }

    public TagNotFoundException(Throwable cause) {
        super(cause);
    }

    public TagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
