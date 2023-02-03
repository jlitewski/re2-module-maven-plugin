package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;

import com.hackhalo2.re2.nbt.ITag;
import com.hackhalo2.re2.nbt.ITagContainer;
import com.hackhalo2.re2.nbt.exceptions.NBTException;

public abstract class NBTTag implements ITag {

    private String name = "";
    private ITagContainer parent;
    byte tempID = -1;
    protected final byte id;
    private final boolean managed;

    protected NBTTag(final String name, final TagType type) {
        this.setName(name);
        this.id = type.getID();
        this.managed = true;
    }

    protected NBTTag(DataInputStream in, final boolean managed) throws NBTException, IOException {
        if(in == null) {
            throw new NBTException("The Inputstream cannot be null!");
        }

        this.managed = managed; //Set the flag to let us know if this tag is managed or not

        if(this.managed) { //read in the name if this tag is managed
            final short nameSize = in.readShort();
            final byte[] nameBuffer = new byte[nameSize];

            in.readFully(nameBuffer);
            this.setName(new String(nameBuffer, Charset.forName("UTF-8")));
        }

        this.readData(in); //pass the deserialization call up to the implementing classes

        if(this.tempID == -1) { //Sanity check to make sure that we didn't forget to set the ID
            throw new NBTException("Tag ID wasn't set during deserialization!");
        }

        this.id = this.tempID; //Set our id to what was passed in
    }

    void setName(String name) {
        //if the name is null, or if this tag isn't managed, or if the new name is the same as the old name, then return
        if(!this.managed || name == null || name.equals(this.name)) return;

        try {
            /* 
             * If this tag has a parent, we have to make sure that we keep the reference correct with the parent.
             * Normally I would keep the logic that messes with relationship stuff inside the container classes,
             * but this is an exception to that rule since we have to remove ourselves to keep the name in the
             * parent container right. Otherwise, it would be referenced by the old name, not this new one.
             */

            ITagContainer tempParent = null;
            if(this.hasParent()) { //If we have a parent, remove ourselves and keep a temporary reference to our parent for later
                tempParent = this.parent;
                this.parent.removeTag(this); //This will set our parent reference to null
            }

            this.name = name; //set the new name

            if(tempParent != null) { //If we had a parent before, readd ourselves
                tempParent.addTag(this);
            }
        } catch(Exception e) {
            throw new IllegalStateException("Issue with setting the name '"+name+"' to an NBT Tag!", e);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    private byte[] getNameAsBytes() {
        return this.name.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public boolean hasParent() {
        return (this.parent != null);
    }

    ITagContainer getParent() {
        return this.parent;
    }

    void setParent(final ITagContainer newParent) {
        //Make sure we don't add ourself as our own parent
        if(newParent == this) {
            throw new IllegalStateException("NBT Tag '"+this.getName()+"' cannot be a parent of itself!");
        }

        this.parent = newParent;
    }

    @Override
    public byte getID() {
        return this.id;
    }

    protected void setTagType(TagType type) {
        this.tempID = type.getID();
    }

    protected void setID(final byte id) {
        this.tempID = id;
    }

    protected abstract void readData(DataInputStream in) throws IOException;

    protected abstract void writeData(DataOutputStream out) throws IOException;

    void writeNBT(DataOutputStream out, boolean isManaged) throws IOException {
        out.writeByte(this.getID()); //Write the Tag ID

        if(isManaged) { //If this tag is managed, then write the name to the stream
            final byte[] nameBytes = this.getNameAsBytes(); //Get the name as a byte array
            out.writeShort(nameBytes.length); //Write out how long the name is
            out.write(nameBytes); //Write out the name
        }

        this.writeData(out); //Pass the output stream up to the implementing classes to deal with their data
    }

    public static final TagCompound loadCompoundFromStream(DataInputStream in) throws NBTException {
        TagCompound result = null;
        Constructor<? extends ITag> tagConstructor = null;

        try {
            tagConstructor = TagType.COMPOUND.getTagClass().getConstructor(DataInputStream.class, boolean.class);
            tagConstructor.setAccessible(true);
            result = ((TagCompound)tagConstructor.newInstance(in, true));
        } catch (Exception e) {
            throw new NBTException("Issue with constructing a new Compound from the InputStream!", e);
        } finally {
            tagConstructor = null;
        }

        return result;
    }

    public static final void saveCompoundToStream(TagCompound compound, DataOutputStream out) throws NBTException {
        //TODO: Should we have some file header info in the spec?

        try {
            out.writeByte(compound.getID()); //Write the ID of the main compound

            //Write the name of the Compound
            final byte[] nameBytes = compound.getName().getBytes(Charset.forName("UTF-8"));
            out.writeShort(nameBytes.length);
            out.write(nameBytes);

            compound.writeData(out);
        } catch(Exception e) {
            throw new NBTException("There was an error trying to write the NBT Compound to disk!", e);

        }
    }

}
