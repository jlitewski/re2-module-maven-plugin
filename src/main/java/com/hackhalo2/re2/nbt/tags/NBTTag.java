package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.hackhalo2.re2.nbt.ITag;
import com.hackhalo2.re2.nbt.ITagContainer;
import com.hackhalo2.re2.nbt.exceptions.NBTException;

public abstract class NBTTag implements ITag {

    private String name = "";
    private ITagContainer parent;
    private final boolean managed;

    protected NBTTag(final String name, final TagType type) {
        this.managed = true; //THIS HAS TO BE FIRST OR BAD THINGS HAPPEN!
        this.setName(name);
    }

    protected NBTTag(DataInputStream in, final boolean managed) throws NBTException, IOException {
        if(in == null) {
            throw new NBTException("The Inputstream cannot be null!");
        }

        this.managed = managed; //Set the flag to let us know if this tag is managed or not

        if(this.managed) { //read in the name if this tag is managed
            final short nameSize = in.readShort();
            System.out.println("Name Size: "+nameSize);
            final byte[] nameBuffer = new byte[nameSize];

            in.readFully(nameBuffer);
            System.out.println("Name Byte Buffer: "+Arrays.toString(nameBuffer));
            this.setName(new String(nameBuffer, Charset.forName("UTF-8")));
        }

        System.out.println("Begin reading Tag Data...");
        this.readData(in); //pass the deserialization call up to the implementing classes
        System.out.println("Done reading "+(this.managed ? "'"+this.getName()+"'!" : "tag!"));
    }

    void setName(String name) {
        //if the name is null, or if this tag isn't managed, or if the new name is the same as the old name, then return
        //TODO: Send this to a debug stream
        if(!this.managed) {
            System.out.println("Name wasn't set! Managed: "+this.managed);
            return;
        }

        if(name == null) {
            System.out.println("Name wasn't set! isNull: "+(name == null ? "true" : "false"));
            return;
        }

        if(name.equals(this.name)) {
            System.out.println("Name wasn't set! Equals: "+name.equals(this.name));
            return;
        }

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
            System.out.println("Set Name: "+this.name);

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

    protected abstract void readData(DataInputStream in) throws IOException;

    protected abstract void writeData(DataOutputStream out) throws IOException;

    void writeNBT(DataOutputStream out, boolean isManaged) throws IOException {
        out.writeByte(this.getID()); //Write the Tag ID
        System.out.println("Writing Tag ID "+this.getID());

        if(isManaged) { //If this tag is managed, then write the name to the stream
            System.out.print("Writing name '"+this.getName()+"'... ");
            final byte[] nameBytes = this.getNameAsBytes(); //Get the name as a byte array
            out.writeShort(nameBytes.length); //Write out how long the name is
            System.out.println("Name Length: "+nameBytes.length);
            out.write(nameBytes); //Write out the name
            System.out.println("Name Bytes: "+Arrays.toString(nameBytes));
        }

        this.writeData(out); //Pass the output stream up to the implementing classes to deal with their data
    }

    public static final TagCompound loadCompoundFromStream(DataInputStream in) throws NBTException {
        TagCompound result = null;
        Constructor<?> tagConstructor = null;

        try {
            if(TagType.valueOf(in.readByte()) != TagType.COMPOUND) {
                throw new NBTException("Root NBT isn't a Compound!");
            }
            
            tagConstructor = TagType.COMPOUND.getTagClass().getDeclaredConstructor(DataInputStream.class, boolean.class);
            tagConstructor.setAccessible(true);
            result = ((TagCompound)tagConstructor.newInstance(in, true));
            tagConstructor.setAccessible(false);
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
            System.out.println("Writing Tag ID "+compound.getID());
            out.writeByte(compound.getID()); //Write the ID of the main compound

            //Write the name of the Compound
            final byte[] nameBytes = compound.getName().getBytes(Charset.forName("UTF-8"));
            out.writeShort(nameBytes.length);
            System.out.println("Writing Name Length: "+nameBytes.length);
            out.write(nameBytes);
            System.out.println("Writing Name Byte Array: "+Arrays.toString(nameBytes));

            compound.writeData(out);
        } catch(Exception e) {
            throw new NBTException("There was an error trying to write the NBT Compound to disk!", e);

        }
    }

}
