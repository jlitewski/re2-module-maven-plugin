package com.hackhalo2.re2.nbt;

import com.hackhalo2.re2.nbt.exceptions.NBTException;
import com.hackhalo2.re2.nbt.tags.TagType;

public interface IManagedContainer extends ITagContainer {

    @Override
    public default boolean isManaged() { return true; }

    public void removeTag(final String name) throws NBTException;

    public ITag getTag(final String name);

    public boolean containsTag(final String name);

    public TagType getTypeOfTag(final String name);
    
}
