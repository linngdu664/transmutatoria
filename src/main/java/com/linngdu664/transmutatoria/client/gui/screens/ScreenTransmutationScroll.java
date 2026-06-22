package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.tool.Easing;
import com.linngdu664.transmutatoria.client.tool.RomanNumberRenderer;
import com.linngdu664.transmutatoria.util.V2I;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.inventory.AbstractTransmutationScrollMenu;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.TransmutationEquationScrollItem;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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

public class ScreenTransmutationScroll extends AbstractContainerScreen<AbstractTransmutationScrollMenu> {
    private static final float ESSENCE_METAL_RADIUS = 37;
    private static final float OPEN_ANIM_DURATION_TICKS = 8.0f;
    private static final float FADE_ANIM_DURATION_TICKS = 8.0f;
    private static final int SCROLL_PAGE_X = 13;
    private static final int SCROLL_PAGE_Y = 0;
    private static final int SCROLL_GRIP_LEFT_X = 0;
    private static final int SCROLL_GRIP_RIGHT_X = 172;
    private static final int SCROLL_GRIP_Y = 0;
    private static final int SCROLL_CONTAINER_X = 6;
    private static final int SCROLL_CONTAINER_Y = 126;
    private static final int SCROLL_GEM_OFFSET_X = 3;
    private static final int SCROLL_GEM_OFFSET_Y = 53;
    private static final int STABILITY_TEXT_OFFSET_X = 8;
    private static final int STABILITY_TEXT_OFFSET_Y = 96;
    private static final float LEFT_GRIP_TO_PAGE_OFFSET = SCROLL_PAGE_X - SCROLL_GRIP_LEFT_X;
    private static final float RIGHT_GRIP_TO_PAGE_OFFSET = SCROLL_PAGE_X + Textures.SCROLL_PAGE.width() - SCROLL_GRIP_RIGHT_X;

    // Scroll opening, then recipe visuals fade in.
    private float openAnimTicks = 0.0f;
    private float fadeAnimTicks = 0.0f;

    public ScreenTransmutationScroll(AbstractTransmutationScrollMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 188, 216);
    }

    @Override
    protected void init() {
        super.init();
        openAnimTicks = 0.0f;
        fadeAnimTicks = 0.0f;
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
        ItemStack scrollStack = getScrollFromPlayer(Minecraft.getInstance().player);
        renderOpeningScroll(graphics, xo, yo, advanceOpenAnim(partialTick), getGemTexture(scrollStack));
        Textures.SCROLL_CONTAINER.render(graphics, xo + SCROLL_CONTAINER_X, yo + SCROLL_CONTAINER_Y);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        // 不显示标题
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        if (!isOpenAnimFinished()) {
            this.hoveredSlot = null;
            return;
        }
        float fadeProgress = advanceFadeAnim(partialTick);
        int fadeAlpha = Mth.clamp(Mth.ceil(255.0f * fadeProgress), 0, 255);
        TextureOption fadeOption = TextureOption.withAlpha(fadeAlpha);

        Minecraft mc = Minecraft.getInstance();
        // 从玩家手上获取卷轴（客户端菜单没有 scrollStack）
        ItemStack scrollStack = getScrollFromPlayer(mc.player);
        if (scrollStack.isEmpty()) return;

        int xo = 30 + (this.width - this.imageWidth) / 2;
        int yo = -4 + (this.height - this.imageHeight) / 2;
        boolean isEquationScroll = scrollStack.getItem() instanceof TransmutationEquationScrollItem;
        if (isEquationScroll) {
            Textures.SCROLL_ARR_EQ_BASE.render(graphics, fadeOption, xo, yo);
        } else {
            Textures.SCROLL_ARR_SG_BASE.render(graphics, fadeOption, xo, yo);
        }

        // 渲染配方物品（从 CONTAINER 组件读取）
        ItemContainerContents container = scrollStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        var items = NonNullList.withSize(2, ItemStack.EMPTY);
        container.copyInto(items);
        ItemStack leftStack = items.getFirst();
        if (!leftStack.isEmpty()) {
            V2I p = renderSlotItem(graphics, leftStack, true, true, fadeProgress);
            if (mouseX >= p.x() && mouseX < p.x() + 16 && mouseY >= p.y() && mouseY < p.y() + 16) {
                graphics.setComponentTooltipForNextFrame(font, List.of(leftStack.getHoverName()), mouseX, mouseY);
            }
        }
        ItemStack rightStack = items.getLast();
        if (!rightStack.isEmpty()) {    //只要右边有东西那就是激活了
            V2I p = renderSlotItem(graphics, rightStack, false, true, fadeProgress);
            if (mouseX >= p.x() && mouseX < p.x() + 16 && mouseY >= p.y() && mouseY < p.y() + 16) {
                graphics.setComponentTooltipForNextFrame(font, List.of(rightStack.getHoverName()), mouseX, mouseY);
            }
            if (isEquationScroll) {
                Textures.SCROLL_ARR_EQ_LIGHT.render(graphics, fadeOption, xo, yo);
            } else {
                Textures.SCROLL_ARR_SG_LIGHT.render(graphics, fadeOption, xo, yo);
            }
        } else if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {//右边没东西那就是没激活，判断鼠标所在物品是否可以激活
            ItemStack item = this.hoveredSlot.getItem();
            CrucibleRecipe recipe = ((AbstractTransmutationScrollItem)scrollStack.getItem()).getRecipe(mc.level, item);
            if (recipe != null) {
                if (isEquationScroll){
                    ItemStack visualRightStack = recipe.getOtherSideItemStack();
                    renderSlotItem(graphics, visualRightStack, false, false, fadeProgress, 0.5f);
                    renderSlotItem(graphics, item, true, false, fadeProgress, 0.5f);
                    Textures.SCROLL_ARR_EQ_LIGHT.render(graphics, fadeOption, xo, yo);
                    Textures.SCROLL_ARR_EQ_SHINE.render(graphics, fadeOption, xo, yo);
                } else {
                    ItemStack visualLeftStack = recipe.getOtherSideItemStack();
                    renderSlotItem(graphics, visualLeftStack, true, false, fadeProgress, 0.5f);
                    renderSlotItem(graphics, item, false, false, fadeProgress, 0.5f);
                    Textures.SCROLL_ARR_SG_LIGHT.render(graphics, fadeOption, xo, yo);
                    Textures.SCROLL_ARR_SG_SHINE.render(graphics, fadeOption, xo, yo);
                }

            }
        }

        List<AbstractAlchemySlot> alchemySlots = scrollStack.get(InitDataComponents.ALCHEMY_SLOTS);
        if (alchemySlots != null && !alchemySlots.isEmpty()) {
            int centerX = leftPos + 2 + (AbstractTransmutationScrollMenu.SLOT0_X + 16 + AbstractTransmutationScrollMenu.SLOT1_X) / 2;
            int centerY = topPos + AbstractTransmutationScrollMenu.SLOT0_Y + 8;
            int size = alchemySlots.size();

            for (int i = 0; i < size; i++) {
                float angle = Mth.TWO_PI * i / size - Mth.HALF_PI;
                float offsetX = ESSENCE_METAL_RADIUS * Mth.cos(angle);
                float offsetY = ESSENCE_METAL_RADIUS * Mth.sin(angle);

                int curX = Math.round(centerX - 8 + offsetX);
                int curY = Math.round(centerY - 8 + offsetY);

                AbstractAlchemySlot slot = alchemySlots.get(i);
                boolean unlocked = slot.isShowEssence();

                if (unlocked) {
                    slot.getEssenceMetal().getDefaultTexture().render(graphics, fadeOption, curX, curY);
                } else {
                    Textures.UNKNOWN_ESSENCE.render(graphics, fadeOption, curX, curY);
                }

                // 鼠标检测用动画后的位置和大小
                if (mouseX >= curX && mouseX < curX + 16 && mouseY >= curY && mouseY < curY + 16) {
                    Component tooltip = unlocked
                            ? Component.translatable("item.transmutatoria." + slot.getEssenceMetal().getKey())
                            : Component.translatable("item.transmutatoria.unknown_essence");
                    graphics.setComponentTooltipForNextFrame(font, List.of(tooltip), mouseX, mouseY);
                }
            }
            int romanX = xo + Textures.SCROLL_ARR_EQ_BASE.width() / 2 - RomanNumberRenderer.width(size) / 2;
            int romanY = yo + Textures.SCROLL_ARR_EQ_BASE.height() / 2 - Textures.ROMAN_I.height() / 2;
            RomanNumberRenderer.render(graphics, fadeOption, size, romanX, romanY);
        }

        renderStabilityText(graphics, scrollStack, fadeAlpha);
    }

    private void renderStabilityText(GuiGraphicsExtractor graphics, ItemStack scrollStack, int fadeAlpha) {
        Component text = Component.translatable(getStabilityTranslationKey(scrollStack));
        int color = colorWithAlpha(0x4a2f21, fadeAlpha);
        graphics.text(font, text, leftPos + SCROLL_PAGE_X + STABILITY_TEXT_OFFSET_X, topPos + SCROLL_PAGE_Y + STABILITY_TEXT_OFFSET_Y, color, false);
    }

    private static String getStabilityTranslationKey(ItemStack scrollStack) {
        int entropy = scrollStack.getOrDefault(InitDataComponents.ENTROPY, 0);
        int maxDurability = scrollStack.getOrDefault(DataComponents.MAX_DAMAGE, 0);
        if (entropy <= 0 || maxDurability <= 0) {
            return "gui.transmutatoria.scroll_stability.stable";
        }

        double ratio = (double) entropy / maxDurability;
        if (ratio <= 0.025) {
            return "gui.transmutatoria.scroll_stability.slight_disturbance";
        }
        if (ratio <= 0.05) {
            return "gui.transmutatoria.scroll_stability.slightly_unstable";
        }
        if (ratio <= 0.10) {
            return "gui.transmutatoria.scroll_stability.unstable";
        }
        if (ratio <= 0.20) {
            return "gui.transmutatoria.scroll_stability.clearly_unstable";
        }
        if (ratio <= 0.30) {
            return "gui.transmutatoria.scroll_stability.very_unstable";
        }
        return "gui.transmutatoria.scroll_stability.near_collapse";
    }

    private V2I renderSlotItem(GuiGraphicsExtractor graphics, ItemStack item, boolean isLeft, boolean needDeco, float fadeProgress){
        return renderSlotItem(graphics, item, isLeft, needDeco, fadeProgress, 1.0f);
    }

    private V2I renderSlotItem(GuiGraphicsExtractor graphics, ItemStack item, boolean isLeft, boolean needDeco, float fadeProgress, float opacity){
        int px = leftPos + (isLeft ? AbstractTransmutationScrollMenu.SLOT0_X : AbstractTransmutationScrollMenu.SLOT1_X);
        int py = topPos + (isLeft ? AbstractTransmutationScrollMenu.SLOT0_Y : AbstractTransmutationScrollMenu.SLOT1_Y);
        graphics.item(item, px, py);
        if (needDeco) {
            graphics.itemDecorations(font, item, px, py);
        }
        coverSlotItemWithPage(graphics, px, py, fadeProgress, opacity);
        return new V2I(px, py);
    }

    private void renderOpeningScroll(GuiGraphicsExtractor graphics, int x, int y, float progress, TextureRenderable gemTexture) {
        float pageHalfWidth = Textures.SCROLL_PAGE.width() * 0.5f;
        float visibleHalfWidth = pageHalfWidth * progress;
        float pageCenterX = x + SCROLL_PAGE_X + pageHalfWidth;
        float pageY = y + SCROLL_PAGE_Y;
        float pageLeftX = pageCenterX - visibleHalfWidth;
        float pageRightX = pageCenterX + visibleHalfWidth;

        if (visibleHalfWidth > 0.0f) {
            int scrollHeight = Textures.SCROLL_PAGE.height();
            Textures.SCROLL_PAGE.render(graphics, TextureOption.DEFAULT, pageLeftX, pageY, pageHalfWidth - visibleHalfWidth, 0, visibleHalfWidth, scrollHeight);
            Textures.SCROLL_PAGE.render(graphics, TextureOption.DEFAULT, pageCenterX, pageY, pageHalfWidth, 0, visibleHalfWidth, scrollHeight);
//            GuiUtil.blit(
//                    graphics,
//                    RenderPipelines.GUI_TEXTURED,
//                    SCROLL_PAGE.identifier(),
//                    pageLeftX,
//                    pageY,
//                    pageHalfWidth - visibleHalfWidth,
//                    0,
//                    visibleHalfWidth,
//                    SCROLL_PAGE.height(),
//                    visibleHalfWidth,
//                    SCROLL_PAGE.height(),
//                    SCROLL_PAGE.width(),
//                    SCROLL_PAGE.height()
//            );
//            GuiUtil.blit(
//                    graphics,
//                    RenderPipelines.GUI_TEXTURED,
//                    SCROLL_PAGE.identifier(),
//                    pageCenterX,
//                    pageY,
//                    pageHalfWidth,
//                    0,
//                    visibleHalfWidth,
//                    SCROLL_PAGE.height(),
//                    visibleHalfWidth,
//                    SCROLL_PAGE.height(),
//                    SCROLL_PAGE.width(),
//                    SCROLL_PAGE.height()
//            );
        }

        float leftGripX = pageLeftX - LEFT_GRIP_TO_PAGE_OFFSET;
        float rightGripX = pageRightX - RIGHT_GRIP_TO_PAGE_OFFSET;
        float gripY = y + SCROLL_GRIP_Y;
        float gemY = y + SCROLL_GEM_OFFSET_Y;
        Textures.SCROLL_GRIP_LEFT.render(graphics, leftGripX, gripY);
        Textures.SCROLL_GRIP_RIGHT.render(graphics, rightGripX, gripY);
        gemTexture.render(graphics, leftGripX + SCROLL_GEM_OFFSET_X, gemY);
        gemTexture.render(graphics, rightGripX + SCROLL_GEM_OFFSET_X, gemY);

//        renderTexture(graphics, SCROLL_GRIP_LEFT, leftGripX, gripY);
//        renderTexture(graphics, SCROLL_GRIP_RIGHT, rightGripX, gripY);
//        renderTexture(graphics, gemTexture, leftGripX + SCROLL_GEM_OFFSET_X, y + SCROLL_GEM_OFFSET_Y);
//        renderTexture(graphics, gemTexture, rightGripX + SCROLL_GEM_OFFSET_X, y + SCROLL_GEM_OFFSET_Y);
    }

//    private void renderTexture(GuiGraphicsExtractor graphics, GuiTexture texture, float x, float y) {
//        GuiUtil.blit(graphics, texture.identifier(), x, y, 0, 0, texture.width(), texture.height(), texture.width(), texture.height());
//    }

    private void coverSlotItemWithPage(GuiGraphicsExtractor graphics, int x, int y, float fadeProgress, float opacity) {
        int alpha = Mth.clamp(Mth.ceil(255.0f * (1.0f - fadeProgress * opacity)), 0, 255);
        if (alpha <= 0) {
            return;
        }
        float u = x - leftPos - SCROLL_PAGE_X;
        float v = y - topPos - SCROLL_PAGE_Y;
        Textures.SCROLL_PAGE.render(graphics, TextureOption.withAlpha(alpha), x, y, u, v, 16, 16);
//        GuiUtil.blit(
//                graphics,
//                RenderPipelines.GUI_TEXTURED,
//                SCROLL_PAGE.identifier(),
//                x,
//                y,
//                u,
//                v,
//                16,
//                16,
//                16,
//                16,
//                SCROLL_PAGE.width(),
//                SCROLL_PAGE.height(),
//                colorWithAlpha(-1, alpha)
//        );
    }

    private float advanceOpenAnim(float partialTick) {
        openAnimTicks = Mth.clamp(openAnimTicks + partialTick, 0.0f, OPEN_ANIM_DURATION_TICKS);
        float tick = Mth.clamp(openAnimTicks, 0.0f, OPEN_ANIM_DURATION_TICKS);
        return Easing.QUARTIC_IN_OUT.ease(tick, 0.0f, 1.0f, OPEN_ANIM_DURATION_TICKS);
    }

    private boolean isOpenAnimFinished() {
        return openAnimTicks >= OPEN_ANIM_DURATION_TICKS;
    }

    private float advanceFadeAnim(float partialTick) {
        fadeAnimTicks = Mth.clamp(fadeAnimTicks + partialTick, 0.0f, FADE_ANIM_DURATION_TICKS);
        float tick = Mth.clamp(fadeAnimTicks, 0.0f, FADE_ANIM_DURATION_TICKS);
        return Easing.CUBIC_OUT.ease(tick, 0.0f, 1.0f, FADE_ANIM_DURATION_TICKS);
    }

    private static int colorWithAlpha(int color, int alpha) {
        return (Mth.clamp(alpha, 0, 255) << 24) | (color & 0x00ffffff);
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

    private static TextureRenderable getGemTexture(ItemStack scrollStack) {
        if (isScroll(scrollStack, InitItems.TERRESTRIAL_SIGIL_SCROLL, InitItems.TERRESTRIAL_EQUATION_SCROLL)) {
            return Textures.SCROLL_TERRESTRIAL;
        }
        if (isScroll(scrollStack, InitItems.LUNAR_SIGIL_SCROLL, InitItems.LUNAR_EQUATION_SCROLL)) {
            return Textures.SCROLL_LUNAR;
        }
        if (isScroll(scrollStack, InitItems.SOLAR_SIGIL_SCROLL, InitItems.SOLAR_EQUATION_SCROLL)) {
            return Textures.SCROLL_SOLAR;
        }
        if (isScroll(scrollStack, InitItems.VOID_SIGIL_SCROLL, InitItems.VOID_EQUATION_SCROLL)) {
            return Textures.SCROLL_VOID;
        }
        return Textures.SCROLL_TRANSMUTATION;
    }

    private static boolean isScroll(ItemStack scrollStack, DeferredItem<Item> sigilScroll, DeferredItem<Item> equationScroll) {
        return scrollStack.getItem() == sigilScroll.get() || scrollStack.getItem() == equationScroll.get();
    }
}
