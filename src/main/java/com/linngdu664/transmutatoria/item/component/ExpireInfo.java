package com.linngdu664.transmutatoria.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ExpireInfo(int period, int offset) {
    public static final Codec<ExpireInfo> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("period").forGetter(ExpireInfo::period),
                    Codec.INT.fieldOf("offset").forGetter(ExpireInfo::offset)
            ).apply(instance, ExpireInfo::new)
    );
    public static final StreamCodec<ByteBuf, ExpireInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ExpireInfo::period,
            ByteBufCodecs.VAR_INT, ExpireInfo::offset,
            ExpireInfo::new
    );
    public static final ExpireInfo DEFAULT = new ExpireInfo(24000, 6000);
    public static final ExpireInfo MOON = new ExpireInfo(192000, 114000);
}
