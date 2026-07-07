package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.inventory.AbstractTransmutationScrollMenu;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.alchemy_slots.AbstractAlchemySlot;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractTransmutationScrollItem extends Item {
    protected AbstractTransmutationScrollItem(Identifier id, ExpireInfo expireInfo, int durability) {
        super(getCommonProperties(id)
                .component(InitDataComponents.EXPIRE_INFO, expireInfo)
                .setNoCombineRepair()
                .durability(durability));
    }

    protected AbstractTransmutationScrollItem(Identifier id, ExpireInfo expireInfo) {
        super(getCommonProperties(id)
                .component(InitDataComponents.EXPIRE_INFO, expireInfo)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .setNoCombineRepair());
    }

    protected AbstractTransmutationScrollItem(Identifier id, int durability) {
        super(getCommonProperties(id).durability(durability).setNoCombineRepair());
    }

    protected AbstractTransmutationScrollItem(Identifier id) {
        super(getCommonProperties(id).component(DataComponents.UNBREAKABLE, Unit.INSTANCE).setNoCombineRepair());
    }

    private static Item.Properties getCommonProperties(Identifier id) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1)
                .component(DataComponents.CONTAINER, ItemContainerContents.fromItems(
                        NonNullList.withSize(AbstractTransmutationScrollMenu.CONTAINER_SLOTS, ItemStack.EMPTY)));
    }

    /**
     * 获取物品关联的配方
     * @return 如有关联则返回配方，否则返回 null
     */
    public abstract CrucibleRecipe getRecipe(Level level, ItemStack itemStack);

    /**
     * 初次激活卷轴，设置配方条件并生成随机的连通六边形炼金槽位
     * @param level  用于获取 RandomSource
     * @param scrollStack 卷轴 ItemStack
     * @param inputStack 玩家放入卷轴的 ItemStack。对于炼金复制，放入卷轴的是输出；对于炼金转化，放入卷轴的是输入
     * @param recipe 关联的配方
     */
    public abstract void activate(Level level, ItemStack scrollStack, ItemStack inputStack, CrucibleRecipe recipe);

    /**
     * 随机突变炼金槽位中的源质金属，不改变槽位坐标、类型或数量。
     * @param level 用于获取 RandomSource
     * @param stack 卷轴 ItemStack
     * @param times 突变次数
     */
    public static void changeEssence(Level level, ItemStack stack, int times) {
        List<AbstractAlchemySlot> slots = stack.get(InitDataComponents.ALCHEMY_SLOTS);
        if (slots == null || slots.isEmpty()) {
            return;
        }

        RandomSource random = level.getRandom();
        EssenceMetal[] allMetals = EssenceMetal.values();
        float mutationChance = 1.0f - (float) Math.pow(0.2f, times);

        for (AbstractAlchemySlot slot : slots) {
            if (random.nextFloat() < mutationChance) {
                slot.setEssenceMetal(allMetals[random.nextInt(allMetals.length)]);
            }
        }
        for (AbstractAlchemySlot slot : slots) {
            slot.setShowEssence(false);
        }

        stack.set(InitDataComponents.ALCHEMY_SLOTS, slots);
        stack.set(InitDataComponents.MAGIC_NUMBER, random.nextInt());
    }

    /**
     * 设置下一次过期时间并计算突变次数，若需重写，可去掉 static
     * @param level Level
     * @param stack Stack of scroll
     * @return 需要突变的次数
     */
    public static int checkAndSetExpire(Level level, ItemStack stack) {
        ExpireInfo expireInfo = stack.get(InitDataComponents.EXPIRE_INFO);
        if (expireInfo == null) {
            return 0;   // 永不过期
        }

        long clockTime = level.getOverworldClockTime();
        long currentOffset = clockTime % expireInfo.period();
        long currentPeriod = clockTime / expireInfo.period();
        long currentNextExpire = stack.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE);
        // 无论何种情况，下一次过期时间都可由该式计算
        long nextExpire = (currentPeriod + (currentOffset >= expireInfo.offset() ? 1 : 0)) * expireInfo.period() + expireInfo.offset();

        if (currentNextExpire != nextExpire) {
            stack.set(InitDataComponents.NEXT_EXPIRE, nextExpire);
        }

        // 当前时间未超出过期时间，不重置。即使玩家 time set，NEXT_EXPIRE 组件也会被正确设置，无须担心
        if (clockTime < currentNextExpire) {
            return 0;
        }

        // 当前时间超出过期时间，正常计算
        return (int) ((clockTime - currentNextExpire) / expireInfo.period()) + 1;
    }

    public static int getPredictedScrollDamage(ItemStack itemStack) {
        int entropy = itemStack.getOrDefault(InitDataComponents.ENTROPY, 0);
        int slotSize = itemStack.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of()).size();
        float damagePerSlot = Math.max(1f, 1f + entropy * 0.2f);
        return (int) (slotSize * damagePerSlot);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        RecipeConditions conditions = stack.get(InitDataComponents.RECIPE_CONDITIONS);
        if (conditions != null && conditions.oneTime()) {
            tooltipAdder.accept(Component.translatable("tooltip.transmutatoria.scroll.one_time").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        // 只有当加载了配方时才 tick
        if (itemStack.has(InitDataComponents.ALCHEMY_SLOTS)) {
            int times = checkAndSetExpire(level, itemStack);
            if (times > 0) {
                changeEssence(level, itemStack, times);
            }
        }
    }
}
