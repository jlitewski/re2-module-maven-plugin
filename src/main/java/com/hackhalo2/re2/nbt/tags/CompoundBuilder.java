package com.hackhalo2.re2.nbt.tags;

import com.hackhalo2.re2.nbt.exceptions.NBTException;

//TODO: Add more error checking logic in this code
//TODO write JavaDoc

public class CompoundBuilder {
	private TagCompound compound = null;

	public CompoundBuilder() { }

	/* Initializers */

	//Start a new named Compound
	public CompoundBuilder start(String name) {
		if(this.compound != null) throw new IllegalStateException("The Compound has been initialized already!");

		this.compound = new TagCompound(name);

		return this;
	}

	//Modify an existing Compound
	public CompoundBuilder modify(TagCompound tag) {
		if(this.compound != null) throw new IllegalStateException("The Compound has been initialized already!");

		this.compound = tag;

		return this;
	}

	/* Mutators */

	//Add a Boolean to the Compound, referenced as a byte
	public CompoundBuilder addBoolean(String name, boolean value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyByteInternal(name, ((byte)(value ? 1 : 0)));
		} else {
			TagByte tag = new TagByte(name, (byte)(value ? 1 : 0));
			this.compound.addTag(tag);
		}

		return this;
	}

	//Modify an existing Boolean (which is a Byte) in the compound by name
	public CompoundBuilder modifyBoolean(String name, boolean value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyByteInternal(name, ((byte)(value ? 1 : 0)));
		}

		return this;
	}

	//Add a Byte tag to the Compound
	public CompoundBuilder addByte(String name, byte value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyByteInternal(name, value);
		} else {
			TagByte tag = new TagByte(name, value);
			this.compound.addTag(tag);
		}

		return this;
	}

	//Modify an existing Byte in the compound by name
	public CompoundBuilder modifyByte(String name, byte value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyByteInternal(name, value);
		}

		return this;
	}

	private void modifyByteInternal(String name, byte value) throws NBTException {
		TagByte tag = ((TagByte)this.compound.getTag(name, TagType.BYTE));
		tag.setValue(value);
	}

	//Add a Short Tag to the Compound
	public CompoundBuilder addShort(String name, short value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyShortInternal(name, value);
		} else {
			TagShort tag = new TagShort(name, value);
			this.compound.addTag(tag);
		}

		return this;
	}

	//Modify an existing Short in the compound by name
	public CompoundBuilder modifyShort(String name, short value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyShortInternal(name, value);
		}

		return this;
	}

	private void modifyShortInternal(String name, short value) throws NBTException {
		TagShort tag = ((TagShort)this.compound.getTag(name, TagType.SHORT));
		tag.setValue(value);
	}

	//Add an Integer Tag to the Compound
	public CompoundBuilder addInteger(String name, int value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyIntegerInternal(name, value);
		} else {
			TagInt tag = new TagInt(name, value);
			this.compound.addTag(tag);
		}

		return this;
	}

	//Modify an existing Integer in the compound by name
	public CompoundBuilder modifyInteger(String name, int value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyIntegerInternal(name, value);
		}

		return this;
	}

	private void modifyIntegerInternal(String name, int value) throws NBTException {
		TagInt tag = ((TagInt)this.compound.getTag(name, TagType.INT));
		tag.setValue(value);
	}

	//Add a Long Tag to the Compound
	public CompoundBuilder addLong(String name, long value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyLongInternal(name, value);
		} else {
			TagLong tag = new TagLong(name, value);
			this.compound.addTag(tag);
		}

		return this;
	}

	//Modify an existing Long in the compound by name
	public CompoundBuilder modifyLong(String name, long value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyLongInternal(name, value);
		}

		return this;
	}

	private void modifyLongInternal(String name, long value) throws NBTException {
		TagLong tag = ((TagLong)this.compound.getTag(name, TagType.LONG));
		tag.setValue(value);
	}

	//Add a Double Tag to the Compound
	public CompoundBuilder addDouble(String name, double value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyDoubleInternal(name, value);
		} else {
			TagDouble tag = new TagDouble(name, value);
			this.compound.addTag(tag);
		}

		return this;
	}

	//Modify an existing Double in the compound by name
	public CompoundBuilder modifyDouble(String name, double value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyDoubleInternal(name, value);
		}

		return this;
	}

	private void modifyDoubleInternal(String name, double value) throws NBTException {
		TagDouble tag = ((TagDouble)this.compound.getTag(name, TagType.DOUBLE));
		tag.setValue(value);
	}

	//Add a String Tag to the Compound
	public CompoundBuilder addString(String name, String value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyStringInternal(name, value);
		} else {
			TagString tag = new TagString(name, value);
			this.compound.addTag(tag);
		}
		
		return this;
	}

	//Modify an existing String in the compound by name
	public CompoundBuilder modifyString(String name, String value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyStringInternal(name, value);
		}

		return this;
	}

	private void modifyStringInternal(String name, String value) throws NBTException {
		TagString tag = ((TagString)this.compound.getTag(name, TagType.STRING));
		tag.setValue(value);
	}

	//Add a Byte Array Tag to the Compound
	public CompoundBuilder addByteArray(String name, byte[] value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyByteArrayInternal(name, value);
		} else {
			TagByteArray tag = new TagByteArray(name, value);
			this.compound.addTag(tag);
		}

		return this;
	}

	//Modify an existing Byte Array in the compound by name
	public CompoundBuilder modifyByteArray(String name, byte[] value) throws NBTException {
		this.checkIfInitialized();

		if(this.compound.containsTag(name)) {
			this.modifyByteArrayInternal(name, value);
		}

		return this;
	}

	private void modifyByteArrayInternal(String name, byte[] value) throws NBTException {
		TagByteArray tag = ((TagByteArray)this.compound.getTag(name, TagType.BYTE_ARRAY));
		tag.setValue(value);
	}

	//Add a List Tag to the Compound
	public CompoundBuilder addList(TagList tag) throws NBTException {
		this.checkIfInitialized();

		this.compound.addTag(tag);

		return this;
	}

	public CompoundBuilder addArray(TagArray tag) throws NBTException {
		this.checkIfInitialized();

		this.compound.addTag(tag);

		return this;
	}

	//Add a Compound Tag to this Compound
	public CompoundBuilder addCompound(TagCompound tag) throws NBTException {
		this.checkIfInitialized();

		this.compound.addTag(tag);

		return this;
	}

	//Add a Compound Tag from a Builder to this Compound
	public CompoundBuilder addCompound(CompoundBuilder builder) throws NBTException {
		this.checkIfInitialized();

		this.compound.addTag(builder.build());

		return this;
	}

	public TagCompound build() {
		TagCompound tag = this.compound;
		this.compound = null;
		return tag;
	}

	/* Utility Functions */

	private void checkIfInitialized() {
		if(this.compound != null) return;

		throw new IllegalStateException("The Compound hasn't been initialized!");
	}
    
}
