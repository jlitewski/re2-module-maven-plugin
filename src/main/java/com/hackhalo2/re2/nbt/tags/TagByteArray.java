package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagByteArray extends NBTTag {
    private byte[] value;
    private int arraySize; //TODO: Finish implementing this logic

    public TagByteArray(DataInputStream in, boolean managed) throws NBTException, IOException {
        super(in, managed);
    }

    public TagByteArray(final String name, final int capacity) {
        super(name, TagType.BYTE_ARRAY);
        this.arraySize = 0;
        this.value = new byte[capacity];
    }

    public TagByteArray(final String name, final byte[] value) {
        super(name, TagType.BYTE_ARRAY);
        this.value = value;
        this.arraySize = value.length;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.arraySize = in.readInt();
        this.value = new byte[this.arraySize];
        in.readFully(this.value);
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(final byte[] value) {
        this.arraySize = value.length;
        this.value = value;
    }

    public byte getAt(final int index) {
        if(index < 0 || index >= this.value.length) {
            throw new IndexOutOfBoundsException("Index '"+index+"' is out of bounds for Array '"+this.getName()+"'");
        }

        return this.value[index];
    }

    public void setAt(final int index, final byte newValue) {
        if(index < 0 || index >= this.value.length) {
            throw new IndexOutOfBoundsException("Index '"+index+"' is out of bounds for Array '"+this.getName()+"'");
        }

        this.value[index] = newValue;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeInt(this.arraySize);
        out.write(this.value, 0, this.arraySize);
    }

    @Override
    public byte getID() {
        return TagType.BYTE_ARRAY.getID();
    }
    
}
