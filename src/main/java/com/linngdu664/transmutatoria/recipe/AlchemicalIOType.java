package com.linngdu664.transmutatoria.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum AlchemicalIOType implements StringRepresentable {
    NAMESPACE("namespace"),
    TAG("tag"),
    ITEM("item");

    public final String serializedName;

    AlchemicalIOType(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public static final Codec<AlchemicalIOType> CODEC = StringRepresentable.fromEnum(AlchemicalIOType::values);
}
