package com.linngdu664.transmutatoria.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum SlotType implements StringRepresentable {
    NORMAL("normal"),
    DETERIORATION("deterioration"),
    ACTIVATION("activation"),
    INVERSION("inversion"),
    DIFFUSION("diffusion"),
    INHIBITION("inhibition"),
    PURGE("purge"),
    RESTORATION("restoration"),
    RESONANCE("resonance"),
    ACTIVITY("activity"),
    EXCHANGE("exchange"),
    SPIN("spin"),
    UNSTABLE("unstable");

    private final String key;

    SlotType(String key) {
        this.key = key;
    }

    @Override
    public String getSerializedName() {
        return key;
    }

    public static final Codec<SlotType> CODEC = StringRepresentable.fromEnum(SlotType::values);
    public static final StreamCodec<FriendlyByteBuf, SlotType> STREAM_CODEC = StreamCodec.of(
            // 编码器 (写入网络数据)
            FriendlyByteBuf::writeEnum,
            // 解码器 (读取网络数据)
            (buf) -> buf.readEnum(SlotType.class)
    );
}
