package com.hackhalo2.re2.nbt;

import com.hackhalo2.re2.nbt.tags.TagType;

public interface IUnmanagedContainer extends ITagContainer {

    @Override
    public default boolean isManaged() { return false; }

    public ITag getTag(final int index);

    public void setTag(final int index, ITag tag);

    public TagType getTypeOfTag();
    
}
