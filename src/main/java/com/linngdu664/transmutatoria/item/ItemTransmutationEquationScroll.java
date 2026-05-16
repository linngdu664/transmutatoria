package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.gui.MenuTransmutationSigilScroll;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.recipe.AlchemicalRecipeManager;
import com.linngdu664.transmutatoria.recipe.AlchemicalTransformationRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemTransmutationEquationScroll extends AbstractItemTransmutationScroll {
    private static final Random RANDOM = new Random();

    public ItemTransmutationEquationScroll(Identifier id) {
        super(id);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) ->
                            new MenuTransmutationSigilScroll(containerId, playerInventory, stack),
                    stack.getHoverName()));
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * 检测并激活卷轴，激活成功会将产物放入容器槽位
     * @param container 卷轴对应的容器，用于同步产物到槽位
     */
    public static boolean tryActivate(Level level, ItemStack scrollStack, ItemStack inputStack,
                                      net.minecraft.world.Container container) {
        Boolean activated = scrollStack.get(InitDataComponents.ACTIVATED.get());
        if (activated != null && activated) {
            return false;
        }
        if (inputStack.isEmpty()) {
            return false;
        }

        AlchemicalTransformationRecipe recipe = AlchemicalRecipeManager.findMatchTrans(level, inputStack);
        if (recipe == null) {
            return false;
        }

        EssenceMetal[] allMetals = EssenceMetal.values();
        List<EssenceMetal> essences = new ArrayList<>();
        for (int i = 0; i < recipe.minLevel(); i++) {
            essences.add(allMetals[RANDOM.nextInt(allMetals.length)]);
        }

        scrollStack.set(InitDataComponents.ESSENCES, essences);
        scrollStack.set(InitDataComponents.ACTIVATED, true);

        // 产物图标：方程卷产物为配方目标物品
        Item outputItem = BuiltInRegistries.ITEM.get(recipe.targetId())
                .map(net.minecraft.core.Holder.Reference::value).orElse(Items.AIR);
        container.setItem(0, outputItem.getDefaultInstance());

        return true;
    }
}
