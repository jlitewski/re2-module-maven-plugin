package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagByte extends NBTTag {
    private byte value;

    public TagByte(DataInputStream in, boolean managed) throws NBTException, IOException {
        super(in, managed);
    }

    public TagByte(final String name, final byte value) {
        super(name, TagType.BYTE);
        this.value = value;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.value = in.readByte();
    }

    public byte getValue() {
        return this.value;
    }

    public void setValue(final byte value) {
        this.value = value;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeByte(this.value);
    }

    @Override
    public byte getID() {
        return TagType.BYTE.getID();
    }
    
}
