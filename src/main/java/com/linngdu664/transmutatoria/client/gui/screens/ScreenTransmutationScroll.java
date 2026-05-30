package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.Textures;
import com.linngdu664.transmutatoria.client.gui.util.TextureOption;
import com.linngdu664.transmutatoria.inventory.AbstractTransmutationScrollMenu;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.TransmutationEquationScrollItem;
import com.linngdu664.transmutatoria.recipe.IAlchemicalRecipe;
import com.linngdu664.transmutatoria.util.SafeInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.graalvm.collections.Pair;

import java.util.List;

public class ScreenTransmutationScroll extends AbstractContainerScreen<AbstractTransmutationScrollMenu> {
    private static final Identifier INVENTORY_BG = ArsTransmutatoria.makeMyIdentifier("textures/gui/scroll.png");

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
            Pair<Integer, Integer> p = renderSlotItem(graphics, leftStack, true, true);
            if (mouseX >= p.getLeft() && mouseX < p.getLeft() + 16 && mouseY >= p.getRight() && mouseY < p.getRight() + 16) {
                graphics.setComponentTooltipForNextFrame(font, List.of(leftStack.getHoverName()), mouseX, mouseY);
            }
        }
        ItemStack rightStack = items.getLast();
        if (!rightStack.isEmpty()) {//只要右边有东西那就是激活了
            Pair<Integer, Integer> p = renderSlotItem(graphics, rightStack, false, true);
            if (mouseX >= p.getLeft() && mouseX < p.getLeft() + 16 && mouseY >= p.getRight() && mouseY < p.getRight() + 16) {
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
        // todo 重构
        /*
        List<EssenceMetal> essences = scrollStack.getOrDefault(InitDataComponents.ESSENCES, List.of());
        if (essences.isEmpty()) return;

        int startX = leftPos + imageWidth / 2 - essences.size() * 10;
        int slotY = topPos + 38;

        for (int i = 0; i < essences.size(); i++) {
            EssenceMetal em = essences.get(i);
            Identifier id = ArsTransmutatoria.makeMyIdentifier(em.getKeyWithPrefix(0));
            Item item = BuiltInRegistries.ITEM.get(id).map(Holder.Reference::value).orElse(Items.AIR);
            if (item != Items.AIR) {
                ItemStack icon = new ItemStack(item);
                int itemX = startX + i * 20;
                graphics.item(icon, itemX, slotY);
                graphics.itemDecorations(font, icon, itemX, slotY);

                // 鼠标悬停时显示源质名称
                if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                    graphics.setComponentTooltipForNextFrame(font,
                            List.of(icon.getHoverName()), mouseX, mouseY);
                }
            }
        }*/
    }

    private Pair<Integer, Integer> renderSlotItem(GuiGraphicsExtractor graphics, ItemStack item, boolean isLeft, boolean needDec){
        int px = leftPos + (isLeft?AbstractTransmutationScrollMenu.SLOT0_X:AbstractTransmutationScrollMenu.SLOT1_X);
        int py = topPos + (isLeft?AbstractTransmutationScrollMenu.SLOT0_Y:AbstractTransmutationScrollMenu.SLOT1_Y);
        graphics.item(item, px, py);
        if (needDec) {
            graphics.itemDecorations(font, item, px, py);
        }
        return Pair.create(px,py);
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
