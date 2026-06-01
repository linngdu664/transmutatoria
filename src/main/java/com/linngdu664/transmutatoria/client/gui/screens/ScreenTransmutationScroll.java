package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.Textures;
import com.linngdu664.transmutatoria.client.gui.util.TextureOption;
import com.linngdu664.transmutatoria.inventory.AbstractTransmutationScrollMenu;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.TransmutationEquationScrollItem;
import com.linngdu664.transmutatoria.recipe.IAlchemicalRecipe;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.SafeInstance;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.awt.*;
import java.util.List;

public class ScreenTransmutationScroll extends AbstractContainerScreen<AbstractTransmutationScrollMenu> {
    private static final Identifier INVENTORY_BG = ArsTransmutatoria.makeMyIdentifier("textures/gui/scroll.png");
    private static final float ESSENCE_METAL_RADIUS = 37;

    public ScreenTransmutationScroll(AbstractTransmutationScrollMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 188, 216);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_BG, xo, yo, 34F, 40F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        Minecraft mc = SafeInstance.getMC();
        // 从玩家手上获取卷轴（客户端菜单没有 scrollStack）
        ItemStack scrollStack = getScrollFromPlayer(mc.player);
//        if (scrollStack == null || scrollStack.get(InitDataComponents.RECIPE_CONDITIONS) == null) return;
        if (scrollStack == null) return;

        int xo = 30 + (this.width - this.imageWidth) / 2;
        int yo = -4 + (this.height - this.imageHeight) / 2;
        boolean isEquationScroll = scrollStack.getItem() instanceof TransmutationEquationScrollItem;
        if (isEquationScroll) {
            Textures.SCROLL_ARR_EQ_BASE.render(graphics, TextureOption.DEFAULT, xo, yo);
        } else {
            Textures.SCROLL_ARR_SG_BASE.render(graphics, TextureOption.DEFAULT, xo, yo);
        }

        // 渲染配方物品（从 CONTAINER 组件读取）
        ItemContainerContents container = scrollStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        var items = NonNullList.withSize(2, ItemStack.EMPTY);
        container.copyInto(items);
        ItemStack leftStack = items.getFirst();
        if (!leftStack.isEmpty()) {
            IntIntImmutablePair p = renderSlotItem(graphics, leftStack, true, true);
            if (mouseX >= p.leftInt() && mouseX < p.leftInt() + 16 && mouseY >= p.rightInt() && mouseY < p.rightInt() + 16) {
                graphics.setComponentTooltipForNextFrame(font, List.of(leftStack.getHoverName()), mouseX, mouseY);
            }
        }
        ItemStack rightStack = items.getLast();
        if (!rightStack.isEmpty()) {//只要右边有东西那就是激活了
            IntIntImmutablePair p  = renderSlotItem(graphics, rightStack, false, true);
            if (mouseX >= p.leftInt() && mouseX < p.leftInt() + 16 && mouseY >= p.rightInt() && mouseY < p.rightInt() + 16) {
                graphics.setComponentTooltipForNextFrame(font, List.of(rightStack.getHoverName()), mouseX, mouseY);
            }
            if (isEquationScroll){
                Textures.SCROLL_ARR_EQ_LIGHT.render(graphics, TextureOption.DEFAULT, xo, yo);
            }else{
                Textures.SCROLL_ARR_SG_LIGHT.render(graphics, TextureOption.DEFAULT, xo, yo);
            }
        }else if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {//右边没东西那就是没激活，判断鼠标所在物品是否可以激活
            ItemStack item = this.hoveredSlot.getItem();
            IAlchemicalRecipe recipe = ((AbstractTransmutationScrollItem)scrollStack.getItem()).getRecipe(mc.level, item);
            if (recipe != null) {
                if (isEquationScroll){
                    ItemStack visualRightStack = recipe.getOtherSideItemStack();
                    renderSlotItem(graphics, visualRightStack, false, false);
                    renderSlotItem(graphics, item, true, false);
                    Textures.SCROLL_ARR_EQ_LIGHT.render(graphics, TextureOption.DEFAULT, xo, yo);
                    Textures.SCROLL_ARR_EQ_SHINE.render(graphics, TextureOption.DEFAULT, xo, yo);
                }else{
                    ItemStack visualLeftStack = recipe.getOtherSideItemStack();
                    renderSlotItem(graphics, visualLeftStack, true, false);
                    renderSlotItem(graphics, item, false, false);
                    Textures.SCROLL_ARR_SG_LIGHT.render(graphics, TextureOption.DEFAULT, xo, yo);
                    Textures.SCROLL_ARR_SG_SHINE.render(graphics, TextureOption.DEFAULT, xo, yo);
                }

            }
        }


        // 渲染配方源质
        List<AbstractAlchemySlot> alchemySlots = scrollStack.get(InitDataComponents.ALCHEMY_SLOTS);
        if (alchemySlots != null && !alchemySlots.isEmpty()) {
            int centerX = leftPos + 2 + (AbstractTransmutationScrollMenu.SLOT0_X + 16 + AbstractTransmutationScrollMenu.SLOT1_X) / 2;
            int centerY = topPos + AbstractTransmutationScrollMenu.SLOT0_Y + 8;

            for (int i = 0, size = alchemySlots.size(); i < size; i++) {
                float angle = Mth.TWO_PI * i / size - Mth.HALF_PI;
                int x = centerX + Math.round(ESSENCE_METAL_RADIUS * Mth.cos(angle)) - 8;
                int y = centerY + Math.round(ESSENCE_METAL_RADIUS * Mth.sin(angle)) - 8;

                AbstractAlchemySlot slot = alchemySlots.get(i);
                boolean unlocked = slot.isShowEssence();

                if (unlocked) {
                    slot.getEssenceMetal().getDefaultTexture().render(graphics, TextureOption.DEFAULT, x, y);
                } else {
                    Textures.UNKNOWN_ESSENCE.render(graphics, TextureOption.DEFAULT, x, y);
                }

                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                    Component tooltip = unlocked
                            ? Component.translatable("item.transmutatoria." + slot.getEssenceMetal().getKey())
                            : Component.translatable("item.transmutatoria.unknown_essence");
                    graphics.setComponentTooltipForNextFrame(font, List.of(tooltip), mouseX, mouseY);
                }
            }
            graphics.text(this.font, String.valueOf(alchemySlots.size()), centerX, centerY, -12566464,false);
        }

    }

    private IntIntImmutablePair renderSlotItem(GuiGraphicsExtractor graphics, ItemStack item, boolean isLeft, boolean needDec){
        int px = leftPos + (isLeft?AbstractTransmutationScrollMenu.SLOT0_X:AbstractTransmutationScrollMenu.SLOT1_X);
        int py = topPos + (isLeft?AbstractTransmutationScrollMenu.SLOT0_Y:AbstractTransmutationScrollMenu.SLOT1_Y);
        graphics.item(item, px, py);
        if (needDec) {
            graphics.itemDecorations(font, item, px, py);
        }
        return new IntIntImmutablePair(px, py);
    }

    private static ItemStack getScrollFromPlayer(Player player) {
        if (player == null) return null;
        for (ItemStack stack : List.of(player.getMainHandItem(), player.getOffhandItem())) {
            if (stack.getItem() instanceof AbstractTransmutationScrollItem) {
                return stack;
            }
        }
        return null;
    }
}
