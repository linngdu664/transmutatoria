package com.linngdu664.transmutatoria.util;

import com.linngdu664.transmutatoria.item.ItemEssenceMetal;
import com.linngdu664.transmutatoria.util.alchemy_slots.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractAlchemySlot {
    private final EssenceMetal essenceMetal;
    // x、y 的方向与屏幕坐标系一致，即：下->y+，右->x+
    private final int x;    // [-32768, 32767]
    private final int y;    // [-32768, 32767]

    protected AbstractAlchemySlot(EssenceMetal essenceMetal, int x, int y) {
        this.essenceMetal = essenceMetal;
        this.x = x;
        this.y = y;
    }

    protected abstract SlotType getType();

    /**
     * 本槽位发生的炼金反应
     * @param inputStack 输入金属的 ItemStack
     * @param outputs 锅的所有输出槽的 ItemStack
     * @param posToOutputSlot 槽位 xy 到 输出槽下标的映射
     * @param deferredTasks 延迟任务列表
     * @param magicNumber 方块位置、下一次重置时刻、本对象对应输出槽下标 的哈希
     * @return 对锅的影响
     */
    public AlchemyReactResult react(ItemStack inputStack, List<ItemStack> outputs, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks, int magicNumber) {
        if (inputStack.getItem() instanceof ItemEssenceMetal inputEssenceMetal) {
            EssenceMetal.Relation relation = inputEssenceMetal.getRelation(essenceMetal);
            outputs.set(posToOutputSlot.get(getPackedXY(x, y)), inputEssenceMetal.change(relation.self));
            return switch (relation) {
                case DOUBLE_RESTRAIN, DOUBLE_BE_RESTRAINED -> new AlchemyReactResult(2, relation.other, false);
                case NEUTRAL -> new AlchemyReactResult(0, relation.other, false);
                case SAME -> new AlchemyReactResult(0, relation.other, true);
                default -> new AlchemyReactResult(1, relation.other, false);
            };
        }
        return new AlchemyReactResult(0, 0, false);
    }

    public EssenceMetal getEssenceMetal() {
        return essenceMetal;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private int getAdjacentPackedXY(int direction) {
        return switch (direction) {
            case 0 -> getPackedXY(x, y - 2);
            case 1 -> getPackedXY(x + 1, y - 1);
            case 2 -> getPackedXY(x + 1, y + 1);
            case 3 -> getPackedXY(x, y + 2);
            case 4 -> getPackedXY(x - 1, y + 1);
            default -> getPackedXY(x - 1, y - 1);
        };
    }

    private static int getPackedXY(int x, int y) {
        return (x << 16) | (y & 0xFFFF);
    }

    public static AbstractAlchemySlot create(SlotType type, EssenceMetal essenceMetal, int x, int y) {
        return switch (type) {
            case NORMAL -> new NormalSlot(essenceMetal, x, y);
            case DETERIORATION -> new DeteriorationSlot(essenceMetal, x, y);
            case ACTIVATION -> new ActivationSlot(essenceMetal, x, y);
            case INVERSION -> new InversionSlot(essenceMetal, x, y);
            case DIFFUSION -> new DiffusionSlot(essenceMetal, x, y);
            case INHIBITION -> new InhibitionSlot(essenceMetal, x, y);
            case PURGE -> new PurgeSlot(essenceMetal, x, y);
            case RESTORATION -> new RestorationSlot(essenceMetal, x, y);
            case RESONANCE -> new ResonanceSlot(essenceMetal, x, y);
            case ACTIVITY -> new ActivitySlot(essenceMetal, x, y);
            case EXCHANGE -> new ExchangeSlot(essenceMetal, x, y);
            case SPIN -> new SpinSlot(essenceMetal, x, y);
            case UNSTABLE -> new UnstableSlot(essenceMetal, x, y);
        };
    }

    public static final Codec<AbstractAlchemySlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SlotType.CODEC.fieldOf("type").forGetter(AbstractAlchemySlot::getType),
            EssenceMetal.CODEC.fieldOf("essence_metal").forGetter(AbstractAlchemySlot::getEssenceMetal),
            Codec.INT.fieldOf("x").forGetter(AbstractAlchemySlot::getX),
            Codec.INT.fieldOf("y").forGetter(AbstractAlchemySlot::getY)
    ).apply(instance, AbstractAlchemySlot::create));

    public static final Codec<List<AbstractAlchemySlot>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<FriendlyByteBuf, AbstractAlchemySlot> STREAM_CODEC = StreamCodec.of(
            (buf, slot) -> {
                SlotType.STREAM_CODEC.encode(buf, slot.getType());
                EssenceMetal.STREAM_CODEC.encode(buf, slot.getEssenceMetal());
                ByteBufCodecs.VAR_INT.encode(buf, slot.getX());
                ByteBufCodecs.VAR_INT.encode(buf, slot.getY());
            },
            buf -> create(
                    SlotType.STREAM_CODEC.decode(buf),
                    EssenceMetal.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf)
            )
    );

    public static final StreamCodec<FriendlyByteBuf, List<AbstractAlchemySlot>> LIST_STREAM_CODEC = StreamCodec.of(
            (buf, list) -> buf.writeCollection(list, STREAM_CODEC),
            buf -> buf.readList(STREAM_CODEC)
    );
}
