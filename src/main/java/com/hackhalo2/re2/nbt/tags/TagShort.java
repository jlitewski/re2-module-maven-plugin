package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagShort extends NBTTag {
    private short value;

    public TagShort(DataInputStream in, boolean managed) throws NBTException, IOException {
        super(in, managed);
    }

    public TagShort(final String name, final short value) {
        super(name, TagType.SHORT);
        this.value = value;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
       this.value = in.readShort();
    }

    public short getValue() {
        return this.value;
    }

    public void setValue(final short value) {
        this.value = value;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeShort(this.value);
    }

    @Override
    public byte getID() {
        return TagType.SHORT.getID();
    }
    
}
