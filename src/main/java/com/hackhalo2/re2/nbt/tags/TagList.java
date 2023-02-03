package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hackhalo2.re2.nbt.IUnmanagedContainer;
import com.hackhalo2.re2.nbt.ITag;
import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagList extends NBTTag implements IUnmanagedContainer {
    private List<ITag> tags = new ArrayList<>();
    private TagType listType = TagType.NULL;


    public TagList(String name, TagType listType) throws NBTException {
        this(name, listType, new ArrayList<>());
    }

    public TagList(String name, TagType listType, List<ITag> tagList) throws NBTException {
        super(name, TagType.LIST);

        if(!listType.isPrimitive()) { //Lists only contain primitive tags, so if it's anything else, throw an error
            throw new NBTException("Set type has to be a NBT Primitive Type!");
        }

        this.listType = listType; //Set the List Type

        if(tagList.isEmpty()) return; //If the array passed in is empty, bail out.

        //Iterate over the array and add the tags that match what we store to the backing List we have
        for(ITag tag : tagList) {
            if(this.listType.isSameType(tag)) {
                this.tags.add(tag);
            }
        }
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.setTagType(TagType.LIST);

        final byte listTypeByte = in.readByte();
        this.listType = TagType.valueOf(listTypeByte); //Set up what list of tags we have

        if(this.listType == TagType.NULL) { //If the type wasn't something we track, bail
            throw new IOException("Invalid List Type ID! Got ID '"+listTypeByte+"'");
        } else if(!this.listType.isPrimitive()) { //If the type we got can't be put into a list, bail
            throw new IOException("Invalid List Type ID! Tag '"+this.listType.name()+"' cannot be put into a List!");
        }

        final int size = in.readInt(); //read how big the list was

        //Do some reflection magic to construct our tags for our list
        Constructor<? extends ITag> tagConstructor = null;
        for(int i = 0; i < size; i++) {
            try {
                tagConstructor = this.listType.getTagClass().getConstructor(DataInputStream.class, boolean.class);
                tagConstructor.setAccessible(true);
                this.addTag(tagConstructor.newInstance(in, this.isManaged()));
            } catch(Exception e) {
                throw new IOException("Issue with constructing a new Tag while reading the InputStream!", e);
            } finally {
                tagConstructor = null;
            }
        }

        //Sanity check. We should read an EOT byte here
        if(TagType.valueOf(in.readByte()) != TagType.EOT) {
            throw new IOException("Problem reading List! Didn't reach End Of Tag after reading in "+size+" elements when we should have!");
        }

    }

    @Override
    public void addTag(ITag tag) throws NBTException {
        if(!this.listType.isSameType(tag)) {
            throw new NBTException("Tried to add a Tag with a different Type than this list handles! List Type: '"+this.listType.name()+"' Tag Type: '"+TagType.valueOf(tag.getID()).name()+"'");
        }

        this.tags.add(tag);
    }

    @Override
    public TagType getTypeOfTag() {
        return this.listType;
    }

    @Override
    public void removeTag(ITag tag) throws NBTException {
        if(!this.listType.isSameType(tag)) {
            throw new NBTException("Tried to remove a Tag with a different Type than this list handles! List Type: '"+this.listType.name()+"' Tag Type: '"+TagType.valueOf(tag.getID()).name()+"'");
        }

        if(this.tags.contains(tag)) {
            this.tags.remove(tag);
        }

        //If this remove makes the backing list empty, set this list type as NULL
        if(this.tags.isEmpty()) {
            this.listType = TagType.NULL;
        }
    }

    @Override
    public ITag getTag(int index) {
        this.checkIndex(index);

        return this.tags.get(index);
    }

    public List<ITag> getTags() {
        return Collections.unmodifiableList(this.tags);
    }

    @Override
    public void setTag(int index, ITag tag) {
        this.checkIndex(index);

        this.tags.set(index, tag);
    }

    private void checkIndex(int index) {
        if(index < 0 || index >= this.tags.size()) {
            throw new IndexOutOfBoundsException("Index is outside the range of the array! Index: '"+index+"'");
        }
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeByte(this.listType.getID()); //Write the byte of what tags this list holds
        out.writeInt(this.tags.size()); //Write the size of the array

        for(ITag tag : this.tags) {
            ((NBTTag)tag).writeNBT(out, this.isManaged());
        }

        //We are done writting out our stuff
        out.writeByte(TagType.EOT.getID());
        
    }
    
}
