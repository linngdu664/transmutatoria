package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.client.event.ClientRecipeManager;
import com.linngdu664.transmutatoria.inventory.AlchemyRecipeGeneratorMenu;
import com.linngdu664.transmutatoria.network.to_server.SaveAlchemyRecipePayload;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

public class ScreenAlchemyRecipeGenerator extends AbstractContainerScreen<AlchemyRecipeGeneratorMenu> {
    private static final int PANEL_COLOR = 0xf0201714;
    private static final int INNER_COLOR = 0xff382923;
    private static final int BORDER_COLOR = 0xffa97b45;
    private static final int LABEL_COLOR = 0xffead6b8;
    private static final int WARNING_COLOR = 0xffff6b61;
    private static final int SUCCESS_COLOR = 0xff75dd75;

    private IntSlider minEpSlider;
    private IntSlider maxEpSlider;
    private IntSlider minLevelSlider;
    private IntSlider maxLevelSlider;
    private Button tagButton;
    private Button oneTimeButton;
    private Button saveButton;
    private boolean oneTime;
    private boolean saving;
    private int observedSaveRevision;
    private Component saveStatus = Component.empty();
    private int saveStatusColor = LABEL_COLOR;
    private List<Identifier> availableTags = List.of();
    private int selectedTagIndex;
    private String tagItemId = "";

    public ScreenAlchemyRecipeGenerator(AlchemyRecipeGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 272, 238);
    }

    @Override
    protected void init() {
        int oldMinEp = minEpSlider == null ? -50 : minEpSlider.intValue();
        int oldMaxEp = maxEpSlider == null ? 50 : maxEpSlider.intValue();
        int oldMinLevel = minLevelSlider == null ? 2 : minLevelSlider.intValue();
        int oldMaxLevel = maxLevelSlider == null ? 2 : maxLevelSlider.intValue();
        super.init();

        titleLabelX = (imageWidth - font.width(title)) / 2;
        titleLabelY = 7;
        inventoryLabelX = 55;
        inventoryLabelY = 146;

        minEpSlider = addRenderableWidget(new IntSlider(
                leftPos + 48, topPos + 77, 82, 20,
                Component.translatable("gui.transmutatoria.recipe_generator.min_ep"), -50, 50, oldMinEp));
        maxEpSlider = addRenderableWidget(new IntSlider(
                leftPos + 142, topPos + 77, 82, 20,
                Component.translatable("gui.transmutatoria.recipe_generator.max_ep"), -50, 50, oldMaxEp));
        minLevelSlider = addRenderableWidget(new IntSlider(
                leftPos + 48, topPos + 100, 82, 20,
                Component.translatable("gui.transmutatoria.recipe_generator.min_level"), 2, 24, oldMinLevel));
        maxLevelSlider = addRenderableWidget(new IntSlider(
                leftPos + 142, topPos + 100, 82, 20,
                Component.translatable("gui.transmutatoria.recipe_generator.max_level"), 2, 24, oldMaxLevel));

        tagButton = addRenderableWidget(Button.builder(Component.empty(), button -> cycleTag())
                .bounds(leftPos + 48, topPos + 53, 176, 20)
                .build());
        oneTimeButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
                    oneTime = !oneTime;
                    updateControls();
                })
                .bounds(leftPos + 48, topPos + 122, 86, 20)
                .build());
        saveButton = addRenderableWidget(Button.builder(
                        Component.translatable("gui.transmutatoria.recipe_generator.save"),
                        button -> saveRecipe())
                .bounds(leftPos + 140, topPos + 122, 84, 20)
                .build());

        refreshTags(true);
        observedSaveRevision = menu.saveRevision();
        updateControls();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (observedSaveRevision != menu.saveRevision()) {
            observedSaveRevision = menu.saveRevision();
            saving = false;
            if (menu.lastSaveSucceeded()) {
                saveStatus = Component.translatable("gui.transmutatoria.recipe_generator.saved_continue");
                saveStatusColor = SUCCESS_COLOR;
            } else {
                saveStatus = Component.translatable("gui.transmutatoria.recipe_generator.save_failed_short");
                saveStatusColor = WARNING_COLOR;
            }
        }
        refreshTags(false);
        updateControls();
    }

    private void refreshTags(boolean force) {
        ItemStack stack = menu.tagSample();
        String currentItemId = stack.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (!force && currentItemId.equals(tagItemId)) {
            return;
        }
        if (!currentItemId.isEmpty()) {
            saveStatus = Component.empty();
        }
        tagItemId = currentItemId;
        selectedTagIndex = 0;
        availableTags = stack.isEmpty()
                ? List.of()
                : stack.typeHolder().tags().map(TagKey::location).sorted().toList();
    }

    private void cycleTag() {
        if (!availableTags.isEmpty()) {
            selectedTagIndex = (selectedTagIndex + 1) % (availableTags.size() + 1);
            updateControls();
        }
    }

    private void updateControls() {
        if (tagButton == null) {
            return;
        }
        ItemStack tagStack = menu.tagSample();
        if (tagStack.isEmpty()) {
            tagButton.setMessage(Component.translatable("gui.transmutatoria.recipe_generator.tag.waiting"));
            tagButton.setTooltip(null);
            tagButton.active = false;
        } else if (selectedTagIndex == 0) {
            String itemId = BuiltInRegistries.ITEM.getKey(tagStack.getItem()).toString();
            tagButton.setMessage(Component.translatable("gui.transmutatoria.recipe_generator.tag.item", itemId));
            tagButton.setTooltip(Tooltip.create(Component.literal(itemId)));
            tagButton.active = !availableTags.isEmpty();
        } else {
            String tag = "#" + availableTags.get(selectedTagIndex - 1);
            tagButton.setMessage(Component.translatable("gui.transmutatoria.recipe_generator.tag.tag", tag));
            tagButton.setTooltip(Tooltip.create(Component.literal(tag)));
            tagButton.active = true;
        }

        oneTimeButton.setMessage(Component.translatable(
                oneTime
                        ? "gui.transmutatoria.recipe_generator.one_time.yes"
                        : "gui.transmutatoria.recipe_generator.one_time.no"));
        saveButton.setMessage(Component.translatable(saving
                ? "gui.transmutatoria.recipe_generator.saving"
                : "gui.transmutatoria.recipe_generator.save"));
        saveButton.active = !saving && isConfigurationValid();
    }

    private boolean isConfigurationValid() {
        int minEp = minEpSlider.intValue();
        int maxEp = maxEpSlider.intValue();
        int minLevel = minLevelSlider.intValue();
        int maxLevel = maxLevelSlider.intValue();
        if (minEp < -50 || maxEp > 50 || minEp > maxEp
                || minLevel < 2 || maxLevel > 24 || minLevel > maxLevel) {
            return false;
        }
        return !menu.outputSample().isEmpty()
                && (menu.kind() == AlchemyRecipeGeneratorMenu.Kind.REPLICATION || !menu.inputSample().isEmpty());
    }

    private void saveRecipe() {
        if (!isConfigurationValid()) {
            return;
        }
        String tag = selectedTagIndex == 0 ? "" : availableTags.get(selectedTagIndex - 1).toString();
        ClientPacketDistributor.sendToServer(new SaveAlchemyRecipePayload(
                menu.containerId,
                minEpSlider.intValue(),
                maxEpSlider.intValue(),
                minLevelSlider.intValue(),
                maxLevelSlider.intValue(),
                oneTime,
                tag
        ));
        saving = true;
        saveStatus = Component.empty();
        updateControls();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, PANEL_COLOR);
        graphics.fill(leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, INNER_COLOR);
        drawSlotFrame(graphics, leftPos + AlchemyRecipeGeneratorMenu.INPUT_SLOT_X, topPos + AlchemyRecipeGeneratorMenu.SAMPLE_SLOT_Y);
        drawSlotFrame(graphics, leftPos + AlchemyRecipeGeneratorMenu.OUTPUT_SLOT_X, topPos + AlchemyRecipeGeneratorMenu.SAMPLE_SLOT_Y);
    }

    private static void drawSlotFrame(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, BORDER_COLOR);
        graphics.fill(x, y, x + 16, y + 16, 0xff17110f);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, LABEL_COLOR, false);
        graphics.text(font, Component.translatable("gui.transmutatoria.recipe_generator.input"), 43, 19, LABEL_COLOR, false);
        graphics.text(font, Component.literal("→"), 132, 34, LABEL_COLOR, false);
        graphics.text(font, Component.translatable("gui.transmutatoria.recipe_generator.output"), 196, 19, LABEL_COLOR, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);

        if (!saveStatus.getString().isEmpty()) {
            graphics.text(font, saveStatus, (imageWidth - font.width(saveStatus)) / 2, 42, saveStatusColor, true);
        } else if (hasRelatedRecipe()) {
            Component warning = Component.translatable("gui.transmutatoria.recipe_generator.existing_warning");
            graphics.text(font, warning, (imageWidth - font.width(warning)) / 2, 42, WARNING_COLOR, true);
        }
    }

    private boolean hasRelatedRecipe() {
        ItemStack stack = menu.kind() == AlchemyRecipeGeneratorMenu.Kind.REPLICATION
                ? menu.outputSample() : menu.inputSample();
        if (stack.isEmpty()) {
            return false;
        }
        if (menu.kind() == AlchemyRecipeGeneratorMenu.Kind.REPLICATION) {
            return anyMatch(ClientRecipeManager.replicationPrecises, stack)
                    || anyMatch(ClientRecipeManager.replications, stack);
        }
        return anyMatch(ClientRecipeManager.transformationPrecises, stack)
                || anyMatch(ClientRecipeManager.transformations, stack);
    }

    private static boolean anyMatch(List<? extends RecipeHolder<? extends CrucibleRecipe>> recipes, ItemStack stack) {
        if (recipes == null) {
            return false;
        }
        for (RecipeHolder<? extends CrucibleRecipe> holder : recipes) {
            if (holder.value().matches(stack)) {
                return true;
            }
        }
        return false;
    }

    private static class IntSlider extends AbstractSliderButton {
        private final Component label;
        private final int minimum;
        private final int maximum;

        IntSlider(int x, int y, int width, int height, Component label, int minimum, int maximum, int initialValue) {
            super(x, y, width, height, Component.empty(), normalize(initialValue, minimum, maximum));
            this.label = label;
            this.minimum = minimum;
            this.maximum = maximum;
            updateMessage();
        }

        int intValue() {
            return minimum + (int) Math.round(value * (maximum - minimum));
        }

        @Override
        protected void updateMessage() {
            setMessage(label.copy().append(": ").append(Integer.toString(intValue())));
        }

        @Override
        protected void applyValue() {
        }

        private static double normalize(int value, int minimum, int maximum) {
            int clamped = Math.max(minimum, Math.min(maximum, value));
            return (double) (clamped - minimum) / (maximum - minimum);
        }
    }
}
