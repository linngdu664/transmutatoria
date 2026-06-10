package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.client.gui.GuiUtil;
import com.linngdu664.transmutatoria.client.gui.texture.GuiTexture;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.linngdu664.transmutatoria.client.gui.texture.Textures.*;

public class ScreenTransmutationScroll extends AbstractContainerScreen<AbstractTransmutationScrollMenu> {
    private static final float ESSENCE_METAL_RADIUS = 37;
    private static final float OPEN_ANIM_DURATION_TICKS = 8.0f;
    private static final float RING_ANIM_DURATION_TICKS = 8.0f;
    private static final int SCROLL_PAGE_X = 13;
    private static final int SCROLL_PAGE_Y = 0;
    private static final int SCROLL_GRIP_LEFT_X = 0;
    private static final int SCROLL_GRIP_RIGHT_X = 172;
    private static final int SCROLL_GRIP_Y = 0;
    private static final int SCROLL_CONTAINER_X = 6;
    private static final int SCROLL_CONTAINER_Y = 126;
    private static final int SCROLL_GEM_OFFSET_X = 3;
    private static final int SCROLL_GEM_OFFSET_Y = 53;
    private static final float LEFT_GRIP_TO_PAGE_OFFSET = SCROLL_PAGE_X - SCROLL_GRIP_LEFT_X;
    private static final float RIGHT_GRIP_TO_PAGE_OFFSET = SCROLL_PAGE_X + SCROLL_PAGE.width() - SCROLL_GRIP_RIGHT_X;

    // 源质圆环扩散动画
    private float openAnimTicks = 0.0f;
    private float ringAnimTicks = 0.0f;

    public ScreenTransmutationScroll(AbstractTransmutationScrollMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 188, 216);
    }

    @Override
    protected void init() {
        super.init();
        openAnimTicks = 0.0f;
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
        renderOpeningScroll(graphics, xo, yo, advanceOpenAnim(partialTick), getGemTexture(scrollStack));
        SCROLL_CONTAINER.render(graphics, xo + SCROLL_CONTAINER_X, yo + SCROLL_CONTAINER_Y);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        if (!isOpenAnimFinished()) {
            this.hoveredSlot = null;
            return;
        }
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

    private void renderOpeningScroll(GuiGraphicsExtractor graphics, int x, int y, float progress, GuiTexture gemTexture) {
        float pageHalfWidth = SCROLL_PAGE.width() * 0.5f;
        float visibleHalfWidth = pageHalfWidth * progress;
        float pageCenterX = x + SCROLL_PAGE_X + pageHalfWidth;
        float pageY = y + SCROLL_PAGE_Y;
        float pageLeftX = pageCenterX - visibleHalfWidth;
        float pageRightX = pageCenterX + visibleHalfWidth;

        if (visibleHalfWidth > 0.0f) {
            GuiUtil.blit(
                    graphics,
                    RenderPipelines.GUI_TEXTURED,
                    SCROLL_PAGE.identifier(),
                    pageLeftX,
                    pageY,
                    pageHalfWidth - visibleHalfWidth,
                    0,
                    visibleHalfWidth,
                    SCROLL_PAGE.height(),
                    visibleHalfWidth,
                    SCROLL_PAGE.height(),
                    SCROLL_PAGE.width(),
                    SCROLL_PAGE.height()
            );
            GuiUtil.blit(
                    graphics,
                    RenderPipelines.GUI_TEXTURED,
                    SCROLL_PAGE.identifier(),
                    pageCenterX,
                    pageY,
                    pageHalfWidth,
                    0,
                    visibleHalfWidth,
                    SCROLL_PAGE.height(),
                    visibleHalfWidth,
                    SCROLL_PAGE.height(),
                    SCROLL_PAGE.width(),
                    SCROLL_PAGE.height()
            );
        }

        float leftGripX = pageLeftX - LEFT_GRIP_TO_PAGE_OFFSET;
        float rightGripX = pageRightX - RIGHT_GRIP_TO_PAGE_OFFSET;
        float gripY = y + SCROLL_GRIP_Y;
        renderTexture(graphics, SCROLL_GRIP_LEFT, leftGripX, gripY);
        renderTexture(graphics, SCROLL_GRIP_RIGHT, rightGripX, gripY);
        renderTexture(graphics, gemTexture, leftGripX + SCROLL_GEM_OFFSET_X, y + SCROLL_GEM_OFFSET_Y);
        renderTexture(graphics, gemTexture, rightGripX + SCROLL_GEM_OFFSET_X, y + SCROLL_GEM_OFFSET_Y);
    }

    private void renderTexture(GuiGraphicsExtractor graphics, GuiTexture texture, float x, float y) {
        GuiUtil.blit(graphics, texture.identifier(), x, y, 0, 0, texture.width(), texture.height(), texture.width(), texture.height());
    }

    private float advanceOpenAnim(float partialTick) {
        openAnimTicks = Mth.clamp(openAnimTicks + partialTick, 0.0f, OPEN_ANIM_DURATION_TICKS);
        float tick = Mth.clamp(openAnimTicks, 0.0f, OPEN_ANIM_DURATION_TICKS);
        return Easing.CUBIC_OUT.ease(tick, 0.0f, 1.0f, OPEN_ANIM_DURATION_TICKS);
    }

    private boolean isOpenAnimFinished() {
        return openAnimTicks >= OPEN_ANIM_DURATION_TICKS;
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

    private static GuiTexture getGemTexture(ItemStack scrollStack) {
        if (isScroll(scrollStack, InitItems.TERRESTRIAL_SIGIL_SCROLL, InitItems.TERRESTRIAL_EQUATION_SCROLL)) {
            return SCROLL_TERRESTRIAL;
        }
        if (isScroll(scrollStack, InitItems.LUNAR_SIGIL_SCROLL, InitItems.LUNAR_EQUATION_SCROLL)) {
            return SCROLL_LUNAR;
        }
        if (isScroll(scrollStack, InitItems.SOLAR_SIGIL_SCROLL, InitItems.SOLAR_EQUATION_SCROLL)) {
            return SCROLL_SOLAR;
        }
        if (isScroll(scrollStack, InitItems.VOID_SIGIL_SCROLL, InitItems.VOID_EQUATION_SCROLL)) {
            return SCROLL_VOID;
        }
        return SCROLL_TRANSMUTATION;
    }

    private static boolean isScroll(ItemStack scrollStack, DeferredItem<Item> sigilScroll, DeferredItem<Item> equationScroll) {
        return scrollStack.getItem() == sigilScroll.get() || scrollStack.getItem() == equationScroll.get();
    }
}
