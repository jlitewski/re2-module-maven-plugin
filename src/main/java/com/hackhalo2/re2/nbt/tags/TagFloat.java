package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagFloat extends NBTTag {
    private float value;

    public TagFloat(DataInputStream in, boolean managed) throws NBTException, IOException {
        super(in, managed);
    }

    public TagFloat(final String name, final float value) {
        super(name, TagType.FLOAT);
        this.value = value;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.value = in.readFloat();
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(final float value) {
        this.value = value;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeFloat(this.value);
    }

    @Override
    public byte getID() {
        return TagType.FLOAT.getID();
    }
    
}
