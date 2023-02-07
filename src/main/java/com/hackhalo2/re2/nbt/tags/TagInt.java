package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagInt extends NBTTag {
    private int value;

    public TagInt(DataInputStream in, boolean managed) throws NBTException, IOException {
        super(in, managed);
    }

    public TagInt(final String name, final int value) {
        super(name, TagType.INT);
        this.value = value;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.value = in.readInt();
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeInt(this.value);
    }

    @Override
    public byte getID() {
        return TagType.INT.getID();
    }
    
}
