package com.hackhalo2.re2.nbt.exceptions;

public class NBTException extends Exception {
    private static final long serialVersionUID = 8964272946530976235L;

	public NBTException(String message) {
        super(message);
    }

    public NBTException(Throwable cause) {
        super(cause);
    }

    public NBTException(String message, Throwable cause) {
        super(message, cause);
    }
}
