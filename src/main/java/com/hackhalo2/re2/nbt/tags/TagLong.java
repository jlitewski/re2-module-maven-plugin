package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagLong extends NBTTag {
    private long value;

    public TagLong(DataInputStream in, boolean managed) throws NBTException, IOException {
        super(in, managed);
    }

    public TagLong(final String name, final long value) {
        super(name, TagType.LONG);
        this.value = value;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.value = in.readLong();
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeLong(this.value);
    }

    @Override
    public byte getID() {
        return TagType.LONG.getID();
    }
    
}
