package com.linngdu664.transmutatoria.util;

import com.linngdu664.transmutatoria.item.EssenceMetalItem;
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
    protected EssenceMetal essenceMetal;
    // x、y 的方向与屏幕坐标系一致，即：下->y+，右->x+
    protected int x;    // [-32768, 32767]
    protected int y;    // [-32768, 32767]

    protected AbstractAlchemySlot(EssenceMetal essenceMetal, int x, int y) {
        this.essenceMetal = essenceMetal;
        this.x = x;
        this.y = y;
    }

    protected abstract SlotType getType();

    /**
     * 本槽位的炼金反应，总方法，不可被重写
     * @param scroll 卷轴 ItemStack
     * @param input 对应输入槽的 ItemStack
     * @param outputs 锅的所有输出槽的 ItemStack
     * @param inhibitionStates 输出槽位的抑制状态
     * @param posToOutputSlot 槽位 xy 到 输出槽下标的映射
     * @param deferredTasks 延迟任务列表
     * @param magicNumber 方块位置、下一次重置时刻、本对象对应输出槽下标 的哈希
     * @return 最终反应结果
     */
    public final AlchemyReactResult react(ItemStack scroll, ItemStack input, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks, int magicNumber) {
        if (!(input.getItem() instanceof EssenceMetalItem inputEssenceMetal)) {
            return new AlchemyReactResult(0, 0, 0, false, false);
        }

        AlchemyReactResult result = internalReact(scroll, inputEssenceMetal, outputs, inhibitionStates, posToOutputSlot, deferredTasks, magicNumber);
        int slot = posToOutputSlot.get(getPackedXY(x, y));

        // 如果本槽位被标记为抑制，强制把状态变化设成 0
        if (inhibitionStates[slot]) {
            result.setEssenceStateIncrease(0);
            result.setPolarityIncrease(0);
            result.setEntropyIncrease(0);
        }

        // 如果不清空物品，则设置物品（清空物品与触发损毁计算已经解耦）
        if (!result.isClearItemStack()) {
            outputs.set(slot, inputEssenceMetal.change(result.getEssenceStateIncrease()));
        }

        return result;
    }

    /**
     * 本槽位的内部炼金反应，可被重写
     * @param scroll 卷轴
     * @param inputEssence 输入的源质金属 Item
     * @param outputs 锅的所有输出槽的 ItemStack
     * @param inhibitionStates 输出槽位的抑制状态
     * @param posToOutputSlot 槽位 xy 到 输出槽下标的映射
     * @param deferredTasks 延迟任务列表
     * @param magicNumber 方块位置、下一次重置时刻、本对象对应输出槽下标 的哈希
     * @return 中间反应结果，可被调整
     */
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks, int magicNumber) {
        EssenceMetal.Relation relation = inputEssence.getRelation(essenceMetal);
        return switch (relation) {
            case DOUBLE_RESTRAIN, DOUBLE_BE_RESTRAINED -> new AlchemyReactResult(relation.self, relation.other, 2, false, false);
            case NEUTRAL -> new AlchemyReactResult(relation.self, relation.other, 0, false, false);
            case SAME -> new AlchemyReactResult(relation.self, relation.other, 0, true, true);
            default -> new AlchemyReactResult(relation.self, relation.other, 1, false, false);
        };
    }

    public EssenceMetal getEssenceMetal() {
        return essenceMetal;
    }

    public void setEssenceMetal(EssenceMetal essenceMetal) {
        this.essenceMetal = essenceMetal;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPackedXY() {
        return getPackedXY(x, y);
    }

    public void swapPropertyExceptForType(AbstractAlchemySlot other) {
        int x1 = other.x;
        int y1 = other.y;
        EssenceMetal essenceMetal1 = other.essenceMetal;

        other.x = this.x;
        other.y = this.y;
        other.essenceMetal = this.essenceMetal;

        this.x = x1;
        this.y = y1;
        this.essenceMetal = essenceMetal1;
    }

    public int getAdjacentPackedXY(int direction) {
        return switch (direction) {
            case 0 -> getPackedXY(x, y - 2);
            case 1 -> getPackedXY(x + 1, y - 1);
            case 2 -> getPackedXY(x + 1, y + 1);
            case 3 -> getPackedXY(x, y + 2);
            case 4 -> getPackedXY(x - 1, y + 1);
            default -> getPackedXY(x - 1, y - 1);
        };
    }

    protected static int getPackedXY(int x, int y) {
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
