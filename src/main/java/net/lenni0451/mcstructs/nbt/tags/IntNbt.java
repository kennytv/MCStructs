package net.lenni0451.mcstructs.nbt.tags;

import net.lenni0451.mcstructs.nbt.INbtNumber;
import net.lenni0451.mcstructs.nbt.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.NbtRegistry;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntNbt implements INbtNumber {

    private int value;

    public IntNbt(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Override
    public byte byteValue() {
        return (byte) (this.value & 0xFF);
    }

    @Override
    public short shortValue() {
        return (byte) (this.value & 0xFFFF);
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public int getId() {
        return NbtRegistry.INT_NBT;
    }

    @Override
    public void read(DataInput in, NbtReadTracker readTracker) throws IOException {
        readTracker.read(96);
        this.value = in.readInt();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

}
