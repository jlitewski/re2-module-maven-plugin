package com.hackhalo2.re2.nbt.tags;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.hackhalo2.re2.nbt.ITag;

public enum TagType {
    //Null Tag
    NULL(0),
    //Primitive Tags
    BYTE(1, TagByte.class, true),
    SHORT(2, TagShort.class, true),
    INT(3, TagInt.class, true),
    LONG(4, TagLong.class, true),
    DOUBLE(5, TagDouble.class, true),
    FLOAT(6, TagFloat.class, true),
    BYTE_ARRAY(7, TagByteArray.class, true),
    STRING(9, TagString.class, true),
    //Container Tags 
    COMPOUND(10, TagCompound.class, false),
    LIST(11, TagList.class, false),
    ARRAY(12, TagArray.class, false),
    //Termination Flags
    EOT(120), //End of Tag flag
    EOF(127); //End of File flag


    private final byte id;
    private final Class<? extends ITag> tagClass;
    private final boolean primitive;
    private static final Map<Byte, TagType> types;

    private TagType(final int id) {
        this.id = ((byte)id);
        this.tagClass = null;
        this.primitive = false;
    }

    private TagType(final int id, Class<? extends ITag> tagClass, final boolean primitive) {
        this.id = ((byte)id);
        this.tagClass = tagClass;
        this.primitive = primitive;
    }

    static {
        Map<Byte, TagType> builder = new HashMap<>();

        for(TagType type : TagType.values()) {
            builder.put(type.getID(), type);
        }

        types = Collections.unmodifiableMap(builder);
    }

    public byte getID() {
        return this.id;
    }

    public Class<? extends ITag> getTagClass() {
        return this.tagClass;
    }

    public boolean isSameType(ITag otherTag) {
        //Put some null checking here to simplify other checks 
        if(otherTag == null) return false;

        return (this.id == otherTag.getID());
    }

    public boolean isPrimitive() {
        return this.primitive;
    }

    public static TagType valueOf(final byte id) {
        TagType returnedType = types.get(id);
        return (returnedType == null ? TagType.NULL : returnedType);
    }
}
