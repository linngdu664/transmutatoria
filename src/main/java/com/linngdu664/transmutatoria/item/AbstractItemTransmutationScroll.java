package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
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
import java.util.List;

public class AbstractItemTransmutationScroll extends Item {
    // 不会过期的卷轴
    protected AbstractItemTransmutationScroll(Identifier id) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).stacksTo(1));
    }

    // 会过期的卷轴
    protected AbstractItemTransmutationScroll(Identifier id, ExpireInfo expireInfo) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).stacksTo(1).component(InitDataComponents.EXPIRE_INFO, expireInfo));
    }

    /**
     * 随机突变源质序列，若需重写，可去掉 static
     * @param level Level
     * @param stack Stack of scroll
     * @param times change times
     */
    public static void changeEssence(Level level, ItemStack stack, int times) {
        List<EssenceMetal> essences = stack.get(InitDataComponents.ESSENCES);
        if (essences == null) {
            return;
        }
        // todo 先定死概率，后续可能可以在配置文件里改？
        for (int i = 0; i <= times; i++) {
            ArrayList<EssenceMetal> newEssences = new ArrayList<>();
            RandomSource randomSource = level.getRandom();
            for (EssenceMetal essence : essences) {
                if (randomSource.nextFloat() < 0.8F) {
                    newEssences.add(EssenceMetal.values()[randomSource.nextInt(EssenceMetal.values().length)]);
                } else {
                    newEssences.add(essence);
                }
            }
            essences = newEssences;
        }
        stack.set(InitDataComponents.ESSENCES, essences);
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
        long nextExpire = stack.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE);

        // 无论何种情况，下一次过期时间都可由该式计算
        stack.set(InitDataComponents.NEXT_EXPIRE, (currentPeriod + (currentOffset >= expireInfo.offset() ? 1 : 0)) * expireInfo.period() + expireInfo.offset());

        // 当前时间未超出过期时间，只看对周期取余后偏移量有没有超，没超则不重置，超了则重置一次（防止玩家 time set）
        if (gameTime < nextExpire) {
            return (currentOffset >= expireInfo.offset() ? 1 : 0);
        }

        // 当前时间超出过期时间，直接正常计算
        return (int) ((gameTime - nextExpire) / expireInfo.period()) + 1;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        // todo 只有当加载了配方时才 tick？
        int times = checkAndSetExpire(level, itemStack);
        if (times > 0) {
            changeEssence(level, itemStack, times);
        }
    }
}
