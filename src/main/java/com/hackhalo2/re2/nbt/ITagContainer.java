package com.hackhalo2.re2.nbt;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

public interface ITagContainer extends ITag {

    public void addTag(ITag tag) throws NBTException;
    
    public void removeTag(ITag tag) throws NBTException;

    public boolean isManaged();
}
