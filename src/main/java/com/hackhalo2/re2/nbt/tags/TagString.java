package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagString extends NBTTag {
    private String value;

    public TagString(DataInputStream in, boolean managed) throws NBTException, IOException {
        super(in, managed);
    }

    public TagString(final String name, final String value) {
        super(name, TagType.STRING);
        this.value = value;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        final int stringSize = in.readInt();
        byte[] stringData = new byte[stringSize];
        in.readFully(stringData);

        this.value = new String(stringData, Charset.forName("UTF-8"));
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        System.out.println("Writing String '"+this.value+"'...");
        final byte[] stringBytes = this.value.getBytes(Charset.forName("UTF-8")); //Convert the string to a byte array with UTF8 encoding

        out.writeInt(stringBytes.length); //Write the length of the array to the stream
        System.out.println("Writing String length: "+stringBytes.length);
        out.write(stringBytes); //write the byte array to the stream
        System.out.println("Writing String byte array: "+Arrays.toString(stringBytes));
    }

    @Override
    public byte getID() {
        return TagType.STRING.getID();
    }
    
}
