package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.linngdu664.transmutatoria.recipe.IAlchemicalRecipe;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractTransmutationScrollItem extends Item {
    // 不会过期的卷轴
    protected AbstractTransmutationScrollItem(Identifier id) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).stacksTo(1));
    }

    // 会过期的卷轴
    protected AbstractTransmutationScrollItem(Identifier id, ExpireInfo expireInfo) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).stacksTo(1).component(InitDataComponents.EXPIRE_INFO, expireInfo));
    }

    /**
     * 获取物品关联的配方
     * @return 如有关联则返回配方，否则返回 null
     */
    public abstract IAlchemicalRecipe getRecipe(Level level, ItemStack itemStack);

    /**
     * 初次激活卷轴，设置配方条件并生成随机的连通六边形炼金槽位
     * @param level  用于获取 RandomSource
     * @param itemStack 卷轴 ItemStack
     * @param recipe 关联的配方
     */
    public static void activate(Level level, ItemStack itemStack, IAlchemicalRecipe recipe) {
        itemStack.set(InitDataComponents.RECIPE_CONDITIONS, new RecipeConditions(recipe.oneTime(), recipe.minPolarity(), recipe.maxPolarity()));

        RandomSource random = level.getRandom();
        int minLevel = recipe.minLevel();
        int maxLevel = recipe.maxLevel();
        int count = minLevel + random.nextInt(maxLevel - minLevel + 1);

        List<HexPos> positions = generateConnectedHexPositions(count, random);
        List<AbstractAlchemySlot> slots = new ArrayList<>(count);
        EssenceMetal[] allMetals = EssenceMetal.values();

        for (HexPos pos : positions) {
            slots.add(AbstractAlchemySlot.create(
                    pickSlotType(count, random),
                    allMetals[random.nextInt(allMetals.length)],
                    pos.x, pos.y
            ));
        }

        itemStack.set(InitDataComponents.ALCHEMY_SLOTS, slots);
    }

    // region 连通六边形槽位生成

    private static final int[][] HEX_OFFSETS = {
            {0, -2}, {1, -1}, {1, 1}, {0, 2}, {-1, 1}, {-1, -1}
    };

    private static final SlotType[] SPECIAL_TYPES = Arrays.stream(SlotType.values())
            .filter(t -> t != SlotType.NORMAL)
            .toArray(SlotType[]::new);

    private record HexPos(int x, int y) {}

    /**
     * 用 frontier 扩张算法生成 count 个连通的六边形坐标，从 (0,0) 开始
     */
    private static List<HexPos> generateConnectedHexPositions(int count, RandomSource random) {
        Set<HexPos> placed = new HashSet<>();
        List<HexPos> frontier = new ArrayList<>();

        HexPos origin = new HexPos(0, 0);
        placed.add(origin);
        addFrontier(origin, placed, frontier);

        while (placed.size() < count && !frontier.isEmpty()) {
            int idx = random.nextInt(frontier.size());
            HexPos pos = frontier.remove(idx);
            if (placed.add(pos)) {
                addFrontier(pos, placed, frontier);
            }
        }

        return new ArrayList<>(placed);
    }

    private static void addFrontier(HexPos pos, Set<HexPos> placed, List<HexPos> frontier) {
        for (int[] off : HEX_OFFSETS) {
            HexPos neighbor = new HexPos(pos.x + off[0], pos.y + off[1]);
            if (!placed.contains(neighbor) && !frontier.contains(neighbor)) {
                frontier.add(neighbor);
            }
        }
    }

    private static SlotType pickSlotType(int slotCount, RandomSource random) {
        if (slotCount <= 8) return SlotType.NORMAL;
        if (random.nextFloat() < 0.75f) return SlotType.NORMAL;
        return SPECIAL_TYPES[random.nextInt(SPECIAL_TYPES.length)];
    }

    // endregion

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

        for (int i = 0; i < times; i++) {
            for (AbstractAlchemySlot slot : slots) {
                if (random.nextFloat() < 0.8F) {
                    slot.setEssenceMetal(allMetals[random.nextInt(allMetals.length)]);
                }
            }
        }

        stack.set(InitDataComponents.ALCHEMY_SLOTS, slots);
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

        long gameTime = level.getGameTime();
        long currentOffset = gameTime % expireInfo.period();
        long currentPeriod = gameTime / expireInfo.period();
        long currentNextExpire = stack.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE);
        // 无论何种情况，下一次过期时间都可由该式计算
        long nextExpire = (currentPeriod + (currentOffset >= expireInfo.offset() ? 1 : 0)) * expireInfo.period() + expireInfo.offset();

        if (currentNextExpire != nextExpire) {
            stack.set(InitDataComponents.NEXT_EXPIRE, nextExpire);
        }

        // 当前时间未超出过期时间，不重置。即使玩家 time set，NEXT_EXPIRE 组件也会被正确设置，无须担心
        if (gameTime < currentNextExpire) {
            return 0;
        }

        // 当前时间超出过期时间，正常计算
        return (int) ((gameTime - currentNextExpire) / expireInfo.period()) + 1;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        // 只有当加载了配方时才 tick
//        if (itemStack.getOrDefault(InitDataComponents.ACTIVATED, false)) {
//            int times = checkAndSetExpire(level, itemStack);
//            if (times > 0) {
//                changeEssence(level, itemStack, times);
//            }
//        }
    }
}
