package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.hackhalo2.re2.nbt.IManagedContainer;
import com.hackhalo2.re2.nbt.ITag;
import com.hackhalo2.re2.nbt.exceptions.NBTException;

public class TagArray extends NBTTag implements IManagedContainer {
    private TagType arrayType;
    private Map<String, ITag> arrayMap = new HashMap<>();

    public TagArray(String name, TagType arrayType) throws NBTException {
        super(name, TagType.ARRAY);
        
        if(!arrayType.isPrimitive()) {
            throw new NBTException("Set type has to be a NBT Primitive Type!");
        }

        this.arrayType = arrayType;
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.setTagType(TagType.ARRAY);

        final byte arrayTypeByte = in.readByte();
        this.arrayType = TagType.valueOf(arrayTypeByte);

        if(this.arrayType == TagType.NULL) {
            throw new IOException("Invalid Array Type ID! Got ID '"+arrayTypeByte+"'");
        } else if(!this.arrayType.isPrimitive()) {
            throw new IOException("Invalid Array Type ID! Tag '"+this.arrayType.name()+"' cannot be put into an Array!");
        }

        final int size = in.readInt(); //read how big the list was

        //Do some reflection magic to construct our tags for our list
        Constructor<? extends ITag> tagConstructor = null;
        for(int i = 0; i < size; i++) {
            try {
                tagConstructor = this.arrayType.getTagClass().getConstructor(DataInputStream.class, boolean.class);
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
            throw new IOException("Problem reading Array! Didn't reach End Of Tag after reading in "+size+" elements when we should have!");
        }
    }

    @Override
    public void addTag(ITag tag) throws NBTException {
        if(tag == null) { //Check to make sure the tag isn't null
            throw new NBTException("Tag cannot be null!");
        }

        if(!this.arrayType.isSameType(tag)) { //Make sure that the tag we are trying to add is one that we can add to the Array
            throw new NBTException("Tried to add a '"+TagType.valueOf(tag.getID()).name()+"' Tag into an Array of '"+this.arrayType.name()+"' Tags");
        }

        //Checks to make sure we aren't trying to add a tag we already have into the Array
        if(this.arrayMap.containsKey(tag.getName())) {
            if(this.arrayMap.get(tag.getName()).equals(tag)) { //The tags are the same, return without error
                return;
            } else { //The tag isn't the same, error out
                //TODO: Figure out a better error message for this
                throw new NBTException("Tried to add a Tag to the Array that has the same name as one we own!");
            }
        }

        //If we get here, we should be good to gain ownership of the tag
        NBTTag nbtTag = ((NBTTag)tag);

        //If the Tag already has a parent, let that parent know that we are taking ownership
        if(nbtTag.hasParent()) {
            nbtTag.getParent().removeTag(tag);
        }

        //Set the parent to us
        nbtTag.setParent(this);

        //Now we can add the tag to our map
        this.arrayMap.put(tag.getName(), tag);

    }

    @Override
    public void removeTag(ITag tag) throws NBTException {
        if(!this.arrayType.isSameType(tag)) {
            throw new NBTException("Tried to remove a Tag with a different Type than this list handles! List Type: '"+this.arrayType.name()+"' Tag Type: '"+TagType.valueOf(tag.getID()).name()+"'");
        }

        NBTTag nbtTag = ((NBTTag)tag);

        if(!tag.hasParent() || nbtTag.getParent() != this) return;

        nbtTag.setParent(null);

        this.arrayMap.remove(tag.getName());
    }

    @Override
    public void removeTag(String name) throws NBTException {
        if(name == null) {
            throw new NBTException("Tag Name cannot be null!");
        }

        if(this.arrayMap.containsKey(name)) {
            this.removeTag(this.arrayMap.get(name));
        }   
    }

    public TagType getArrayType() {
        return this.arrayType;
    }

    @Override
    public ITag getTag(String name) {
        return this.arrayMap.get(name);
    }

    @Override
    public boolean containsTag(String name) {
        return this.arrayMap.containsKey(name);
    }

    @Override
    public TagType getTypeOfTag(String name) {
        if(!this.arrayMap.containsKey(name)) { //If we don't have the name mapped, return a null tag
            return TagType.NULL;
        }

        //If we get here, all the tags in the ArrayMap should be the same
        return this.getArrayType();
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        // TODO Auto-generated method stub
        
    }
    
}
