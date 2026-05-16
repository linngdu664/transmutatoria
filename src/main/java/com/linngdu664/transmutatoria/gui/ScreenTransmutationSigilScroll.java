package com.linngdu664.transmutatoria.gui;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.AbstractItemTransmutationScroll;
import com.linngdu664.transmutatoria.item.EssenceMetal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

public class ScreenTransmutationSigilScroll extends AbstractContainerScreen<MenuTransmutationSigilScroll> {
    private static final Identifier INVENTORY_BG =
            Identifier.withDefaultNamespace("textures/gui/container/crafter.png");

    public ScreenTransmutationSigilScroll(MenuTransmutationSigilScroll menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
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
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_BG, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);

        // 从玩家手上获取卷轴（客户端菜单没有 scrollStack）
        ItemStack scrollStack = getScrollFromPlayer();
        if (scrollStack == null) return;

        Boolean activated = scrollStack.get(InitDataComponents.ACTIVATED.get());
        if (activated == null || !activated) return;

        // 渲染产物图标（从 CONTAINER 组件读取）
        ItemContainerContents container = scrollStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        var items = net.minecraft.core.NonNullList.withSize(1, ItemStack.EMPTY);
        container.copyInto(items);
        ItemStack product = items.getFirst();
        if (!product.isEmpty()) {
            int px = leftPos + MenuTransmutationSigilScroll.SLOT_X + 1;
            int py = topPos + MenuTransmutationSigilScroll.SLOT_Y + 1;
            graphics.item(product, px, py);
            graphics.itemDecorations(font, product, px, py);
            if (mouseX >= px && mouseX < px + 16 && mouseY >= py && mouseY < py + 16) {
                graphics.setComponentTooltipForNextFrame(font,
                        List.of(product.getHoverName()), mouseX, mouseY);
            }
        }

        // 渲染配方源质
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
        }
    }

    private static ItemStack getScrollFromPlayer() {
        var player = Minecraft.getInstance().player;
        if (player == null) return null;
        for (ItemStack stack : List.of(player.getMainHandItem(), player.getOffhandItem())) {
            if (stack.getItem() instanceof AbstractItemTransmutationScroll) {
                return stack;
            }
        }
        return null;
    }
}
