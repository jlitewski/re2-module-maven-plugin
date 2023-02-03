package com.hackhalo2.re2.nbt.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.hackhalo2.re2.nbt.IManagedContainer;
import com.hackhalo2.re2.nbt.ITag;
import com.hackhalo2.re2.nbt.exceptions.NBTException;

public final class TagCompound extends NBTTag implements IManagedContainer {
    private Map<String, ITag> compoundMap = new HashMap<>();

    public TagCompound(final String name) {
        super(name, TagType.COMPOUND);
    }

    @Override
    protected void readData(DataInputStream in) throws IOException {
        this.setTagType(TagType.COMPOUND);

        //Declare these here so we aren't building new objects every loop
        byte tagByte;
        TagType tagType;
        Constructor<? extends ITag> tagConstructor;

        do { //Read through the input stream for this compound until the EOT flag is hit
            tagByte = in.readByte();
            tagType = TagType.valueOf(tagByte);

            if(tagType == TagType.NULL) throw new IOException("Invalid Tag ID '"+tagByte+"' was encountered when reading the InputStream!");

            if(tagType == TagType.EOT) break; //break out of the loop once the End of Tag flag is hit

            try { //Use reflection to construct the Tag
                tagConstructor = tagType.getTagClass().getConstructor(DataInputStream.class, boolean.class);
                this.addTag(tagConstructor.newInstance(in, this.isManaged()));
            } catch(Exception e) { //Fail out if something happens with reading a tag
                throw new IOException("Issue with constructing a new Tag while reading the InputStream!", e);
            } finally {
                tagConstructor = null; //null this back out to prevent issues with wrong data
            }

        } while(true);
    }

    @Override
    public void addTag(ITag tag) throws NBTException {
        if(tag == null) { //We can do anything with a null tag, fail out
            throw new NBTException("Tag cannot be null!");
        }

        if(this.compoundMap.containsKey(tag.getName())) {
            //If the key already exists in this compound, we got to fail out because we can't have two of the same keys
            throw new NBTException("Compound '"+this.getName()+"' already has a Tag named '"+tag.getName()+"' in it!");
        }

        NBTTag nbtTag = ((NBTTag)tag); //Convience cast to NBTTag

        if(nbtTag.hasParent()) { 
            //We got to take ownership of this tag if it already has a parent container that isn't us
            nbtTag.getParent().removeTag(tag);
        }

        nbtTag.setParent(this); //Set the Tag's parent to this Container

        this.compoundMap.put(tag.getName(), tag); //Add the tag to our internal map
        
    }

    @Override
    public void removeTag(ITag tag) throws NBTException {
        if(tag == null) { //We can do anything with a null tag, fail out
            throw new NBTException("Tag cannot be null!");
        }

        NBTTag nbtTag = ((NBTTag)tag);

        //If the tag we got doesn't have a parent, or if that parent isn't us, return
        if(!tag.hasParent() || nbtTag.getParent() != this) return;

        //Unparent the tag
        nbtTag.setParent(null);

        //Remove it from the Map
        this.compoundMap.remove(tag.getName());
    }

    @Override
    public void removeTag(String name) throws NBTException {
        if(name == null) {
            throw new NBTException("Tag Name cannot be null!");
        }

        if(this.compoundMap.containsKey(name)) {
            this.removeTag(this.getTag(name));
        }
    }

    @Override
    public boolean containsTag(String name) {
        return this.compoundMap.containsKey(name);
    }

    @Override
    public ITag getTag(String name) {
        return this.compoundMap.get(name);
    }

    @Override 
    public TagType getTypeOfTag(String name) {
        if(!this.compoundMap.containsKey(name)) {
            return TagType.NULL;
        }

        return TagType.valueOf(this.compoundMap.get(name).getID());
    }

    public Map<String, ITag> getTags() {
        return Collections.unmodifiableMap(this.compoundMap);
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        for(ITag tag : this.compoundMap.values()) { //Iterate over the map
            ((NBTTag)tag).writeNBT(out, this.isManaged()); //Write out the NBT data
        }

        out.writeByte(TagType.EOT.getID()); //write the End of Tag Flag when we are done serializing this Compound
        
    }

    ITag getTag(String name, TagType tagType) throws NBTException {
        ITag tag = this.getTag(name);

        if(tag == null) {
            throw new NBTException("No entry of '"+name+"' in this Compound Tag!");
        }

        Class<? extends ITag> tagClass = tagType.getTagClass();

        if (!tagClass.isInstance(tag)) {
            throw new NBTException("The entry '"+name+"' should be '"+tagClass.getSimpleName()+"', but is '"+tag.getClass().getSimpleName()+"'");
        }

        return tagClass.cast(tag);
    }

    public byte getByte(String name) throws NBTException {
		return ((TagByte)this.getTag(name, TagType.BYTE)).getValue();
	}
	
	public short getShort(String name) throws NBTException {
		return ((TagShort)this.getTag(name, TagType.SHORT)).getValue();
	}
	
	public int getInt(String name) throws NBTException {
		return ((TagInt)this.getTag(name, TagType.INT)).getValue();
	}
	
	public long getLong(String name) throws NBTException {
		return ((TagLong)this.getTag(name, TagType.LONG)).getValue();
	}
	
	public double getDouble(String name) throws NBTException {
		return ((TagDouble)this.getTag(name, TagType.DOUBLE)).getValue();
	}

    public float getFloat(String name) throws NBTException {
		return ((TagFloat)this.getTag(name, TagType.FLOAT)).getValue();
	}

    public String getString(String name) throws NBTException {
		return ((TagString)this.getTag(name, TagType.STRING)).getValue();
	}
	
	public byte[] getByteArray(String name) throws NBTException {
		return ((TagByteArray)this.getTag(name, TagType.BYTE_ARRAY)).getValue();
    }
	
	public TagList getList(String name) throws NBTException {
		return (TagList)this.getTag(name, TagType.LIST);
	}
	
	public TagCompound getCompound(String name) throws NBTException {
		return (TagCompound)this.getTag(name, TagType.COMPOUND);
	}
    
}