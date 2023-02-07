package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagDouble extends NBTTag {
    private double value;

    public TagDouble(DataInputStream in, boolean managed) throws NBTException, IOException {
        super(in, managed);
    }

    public TagDouble(final String name, final double value) {
        super(name, TagType.DOUBLE);
        this.value = value;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.value = in.readDouble();
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeDouble(this.value);
    }

    @Override
    public byte getID() {
        return TagType.DOUBLE.getID();
    }
    
}
