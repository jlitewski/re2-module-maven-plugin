package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class TagInt extends NBTTag {
    private int value;

    public TagInt(final String name, final int value) {
        super(name, TagType.INT);
        this.value = value;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.setTagType(TagType.INT);

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
    
}
