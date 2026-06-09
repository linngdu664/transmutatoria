package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.tool.Easing;
import com.linngdu664.transmutatoria.util.V2I;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.inventory.AbstractTransmutationScrollMenu;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.TransmutationEquationScrollItem;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.SafeInstance;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScreenTransmutationScroll extends AbstractContainerScreen<AbstractTransmutationScrollMenu> {
    private static final Identifier INVENTORY_BG = ArsTransmutatoria.makeMyIdentifier("textures/gui/scroll.png");
    private static final Identifier INVENTORY_BG_1 = ArsTransmutatoria.makeMyIdentifier("textures/gui/scroll_1.png");
    private static final Identifier INVENTORY_BG_2 = ArsTransmutatoria.makeMyIdentifier("textures/gui/scroll_2.png");
    private static final Identifier INVENTORY_BG_3 = ArsTransmutatoria.makeMyIdentifier("textures/gui/scroll_3.png");
    private static final Identifier INVENTORY_BG_4 = ArsTransmutatoria.makeMyIdentifier("textures/gui/scroll_4.png");
    private static final float ESSENCE_METAL_RADIUS = 37;
    private static final float RING_ANIM_DURATION_TICKS = 8.0f;

    // 源质圆环扩散动画
    private float ringAnimTicks = 0.0f;

    public ScreenTransmutationScroll(AbstractTransmutationScrollMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 188, 216);
    }

    @Override
    protected void init() {
        super.init();
        ringAnimTicks = 0.0f;
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
        ItemStack scrollStack = getScrollFromPlayer(SafeInstance.getMC().player);
        graphics.blit(RenderPipelines.GUI_TEXTURED, getBackground(scrollStack), xo, yo, 34F, 40F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        Minecraft mc = SafeInstance.getMC();
        // 从玩家手上获取卷轴（客户端菜单没有 scrollStack）
        ItemStack scrollStack = getScrollFromPlayer(mc.player);
        if (scrollStack.isEmpty()) return;

        int xo = 30 + (this.width - this.imageWidth) / 2;
        int yo = -4 + (this.height - this.imageHeight) / 2;
        boolean isEquationScroll = scrollStack.getItem() instanceof TransmutationEquationScrollItem;
        if (isEquationScroll) {
            Textures.SCROLL_ARR_EQ_BASE.render(graphics, xo, yo);
        } else {
            Textures.SCROLL_ARR_SG_BASE.render(graphics, xo, yo);
        }

        // 渲染配方物品（从 CONTAINER 组件读取）
        ItemContainerContents container = scrollStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        var items = NonNullList.withSize(2, ItemStack.EMPTY);
        container.copyInto(items);
        ItemStack leftStack = items.getFirst();
        if (!leftStack.isEmpty()) {
            V2I p = renderSlotItem(graphics, leftStack, true, true);
            if (mouseX >= p.x() && mouseX < p.x() + 16 && mouseY >= p.y() && mouseY < p.y() + 16) {
                graphics.setComponentTooltipForNextFrame(font, List.of(leftStack.getHoverName()), mouseX, mouseY);
            }
        }
        ItemStack rightStack = items.getLast();
        if (!rightStack.isEmpty()) {    //只要右边有东西那就是激活了
            V2I p = renderSlotItem(graphics, rightStack, false, true);
            if (mouseX >= p.x() && mouseX < p.x() + 16 && mouseY >= p.y() && mouseY < p.y() + 16) {
                graphics.setComponentTooltipForNextFrame(font, List.of(rightStack.getHoverName()), mouseX, mouseY);
            }
            if (isEquationScroll) {
                Textures.SCROLL_ARR_EQ_LIGHT.render(graphics, xo, yo);
            } else {
                Textures.SCROLL_ARR_SG_LIGHT.render(graphics, xo, yo);
            }
        } else if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {//右边没东西那就是没激活，判断鼠标所在物品是否可以激活
            ItemStack item = this.hoveredSlot.getItem();
            CrucibleRecipe recipe = ((AbstractTransmutationScrollItem)scrollStack.getItem()).getRecipe(mc.level, item);
            if (recipe != null) {
                if (isEquationScroll){
                    ItemStack visualRightStack = recipe.getOtherSideItemStack();
                    renderSlotItem(graphics, visualRightStack, false, false);
                    renderSlotItem(graphics, item, true, false);
                    Textures.SCROLL_ARR_EQ_LIGHT.render(graphics, xo, yo);
                    Textures.SCROLL_ARR_EQ_SHINE.render(graphics, xo, yo);
                } else {
                    ItemStack visualLeftStack = recipe.getOtherSideItemStack();
                    renderSlotItem(graphics, visualLeftStack, true, false);
                    renderSlotItem(graphics, item, false, false);
                    Textures.SCROLL_ARR_SG_LIGHT.render(graphics, xo, yo);
                    Textures.SCROLL_ARR_SG_SHINE.render(graphics, xo, yo);
                }

            }
        }

        float ringAnimProgress = advanceRingAnim(partialTick); // 这里传进来的partialTick是这一帧的tick增量，不是当前帧在这1tick内的比例。

        // 渲染配方源质——从圆心扩散到圆环
        List<AbstractAlchemySlot> alchemySlots = scrollStack.get(InitDataComponents.ALCHEMY_SLOTS);
        if (alchemySlots != null && !alchemySlots.isEmpty()) {
            int centerX = leftPos + 2 + (AbstractTransmutationScrollMenu.SLOT0_X + 16 + AbstractTransmutationScrollMenu.SLOT1_X) / 2;
            int centerY = topPos + AbstractTransmutationScrollMenu.SLOT0_Y + 8;
            int size = alchemySlots.size();

            for (int i = 0; i < size; i++) {
                float angle = Mth.TWO_PI * i / size - Mth.HALF_PI;
                float offsetX = ESSENCE_METAL_RADIUS * Mth.cos(angle);
                float offsetY = ESSENCE_METAL_RADIUS * Mth.sin(angle);

                float curX = centerX - 8 + offsetX * ringAnimProgress;
                float curY = centerY - 8 + offsetY * ringAnimProgress;

                AbstractAlchemySlot slot = alchemySlots.get(i);
                boolean unlocked = slot.isShowEssence();

                graphics.pose().pushMatrix();
                graphics.pose().translate(curX + 8, curY + 8);
                graphics.pose().scale(ringAnimProgress, ringAnimProgress);
                graphics.pose().translate(-8, -8);

                if (unlocked) {
                    slot.getEssenceMetal().getDefaultTexture().render(graphics, 0, 0);
                } else {
                    Textures.UNKNOWN_ESSENCE.render(graphics, 0, 0);
                }

                graphics.pose().popMatrix();

                // 鼠标检测用动画后的位置和大小
                int slotSize = Math.round(16 * ringAnimProgress);
                int hitX = Math.round(curX);
                int hitY = Math.round(curY);
                if (mouseX >= hitX && mouseX < hitX + slotSize && mouseY >= hitY && mouseY < hitY + slotSize) {
                    Component tooltip = unlocked
                            ? Component.translatable("item.transmutatoria." + slot.getEssenceMetal().getKey())
                            : Component.translatable("item.transmutatoria.unknown_essence");
                    graphics.setComponentTooltipForNextFrame(font, List.of(tooltip), mouseX, mouseY);
                }
            }
            graphics.text(this.font, String.valueOf(size), centerX, centerY, -12566464,false);
        }
    }

    private V2I renderSlotItem(GuiGraphicsExtractor graphics, ItemStack item, boolean isLeft, boolean needDeco){
        int px = leftPos + (isLeft ? AbstractTransmutationScrollMenu.SLOT0_X : AbstractTransmutationScrollMenu.SLOT1_X);
        int py = topPos + (isLeft ? AbstractTransmutationScrollMenu.SLOT0_Y : AbstractTransmutationScrollMenu.SLOT1_Y);
        graphics.item(item, px, py);
        if (needDeco) {
            graphics.itemDecorations(font, item, px, py);
        }
        return new V2I(px, py);
    }

    private float advanceRingAnim(float partialTick) {
        ringAnimTicks = Mth.clamp(ringAnimTicks + partialTick, 0.0f, RING_ANIM_DURATION_TICKS);
        float tick = Mth.clamp(ringAnimTicks, 0.0f, RING_ANIM_DURATION_TICKS);
        return Easing.CUBIC_OUT.ease(tick, 0.0f, 1.0f, RING_ANIM_DURATION_TICKS);
    }

    private static @NotNull ItemStack getScrollFromPlayer(Player player) {
        if (player == null) return ItemStack.EMPTY;
        for (ItemStack stack : List.of(player.getMainHandItem(), player.getOffhandItem())) {
            if (stack.getItem() instanceof AbstractTransmutationScrollItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static Identifier getBackground(ItemStack scrollStack) {
        if (isScroll(scrollStack, InitItems.TERRESTRIAL_SIGIL_SCROLL, InitItems.TERRESTRIAL_EQUATION_SCROLL)) {
            return INVENTORY_BG_1;
        }
        if (isScroll(scrollStack, InitItems.LUNAR_SIGIL_SCROLL, InitItems.LUNAR_EQUATION_SCROLL)) {
            return INVENTORY_BG_2;
        }
        if (isScroll(scrollStack, InitItems.SOLAR_SIGIL_SCROLL, InitItems.SOLAR_EQUATION_SCROLL)) {
            return INVENTORY_BG_3;
        }
        if (isScroll(scrollStack, InitItems.VOID_SIGIL_SCROLL, InitItems.VOID_EQUATION_SCROLL)) {
            return INVENTORY_BG_4;
        }
        return INVENTORY_BG;
    }

    private static boolean isScroll(ItemStack scrollStack, DeferredItem<Item> sigilScroll, DeferredItem<Item> equationScroll) {
        return scrollStack.getItem() == sigilScroll.get() || scrollStack.getItem() == equationScroll.get();
    }
}
