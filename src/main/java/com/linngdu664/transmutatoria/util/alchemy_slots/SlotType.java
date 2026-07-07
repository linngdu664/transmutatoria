package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum SlotType implements StringRepresentable {
    NORMAL("normal"),
    DETERIORATION("deterioration"), //劣化
    ACTIVATION("activation"),       //活化
    INVERSION("inversion"),         //反转
    DIFFUSION("diffusion"),         //扩散
    INHIBITION("inhibition"),       //抑制
    PURGE("purge"),                 //清理
    RESTORATION("restoration"),     //还原
    RESONANCE("resonance"),         //共振
    ACTIVITY("activity"),           //活动
    EXCHANGE("exchange"),           //交换
    SPIN("spin"),                   //自旋
    UNSTABLE("unstable");           //不稳

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
