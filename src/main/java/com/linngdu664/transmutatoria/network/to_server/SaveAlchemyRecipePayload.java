package com.linngdu664.transmutatoria.network.to_server;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.Config;
import com.linngdu664.transmutatoria.inventory.AlchemyRecipeGeneratorMenu;
import com.linngdu664.transmutatoria.recipe.generator.AlchemyRecipeGenerator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import java.util.ArrayList;

public record SaveAlchemyRecipePayload(
        int containerId,
        int minEp,
        int maxEp,
        int minLevel,
        int maxLevel,
        boolean oneTime,
        String tagId
) implements CustomPacketPayload {
    public static final Type<SaveAlchemyRecipePayload> TYPE =
            new Type<>(ArsTransmutatoria.makeMyIdentifier("save_alchemy_recipe"));

    public static final StreamCodec<FriendlyByteBuf, SaveAlchemyRecipePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.containerId);
                buf.writeVarInt(payload.minEp);
                buf.writeVarInt(payload.maxEp);
                buf.writeVarInt(payload.minLevel);
                buf.writeVarInt(payload.maxLevel);
                buf.writeBoolean(payload.oneTime);
                buf.writeUtf(payload.tagId);
            },
            buf -> new SaveAlchemyRecipePayload(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readBoolean(),
                    buf.readUtf()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(net.minecraft.world.entity.player.Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)
                || !Config.ENABLE_RECIPE_GENERATOR_COMMANDS.get()
                || !serverPlayer.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
                || serverPlayer.containerMenu.containerId != containerId
                || !(serverPlayer.containerMenu instanceof AlchemyRecipeGeneratorMenu menu)) {
            return;
        }

        Identifier selectedTag = tagId.isEmpty() ? null : Identifier.tryParse(tagId);
        if (!tagId.isEmpty() && selectedTag == null) {
            serverPlayer.sendSystemMessage(Component.translatable("gui.transmutatoria.recipe_generator.error.invalid_tag"));
            return;
        }

        try {
            var server = serverPlayer.level().getServer();
            AlchemyRecipeGenerator.SaveResult result = AlchemyRecipeGenerator.save(
                    server, menu.kind(), menu.inputSample(), menu.outputSample(),
                    minEp, maxEp, minLevel, maxLevel, oneTime, selectedTag
            );
            menu.clearSamples();
            menu.setSaveResult(true);

            var repository = server.getPackRepository();
            repository.reload();
            ArrayList<String> selectedPacks = new ArrayList<>(repository.getSelectedIds());
            if (!selectedPacks.contains(AlchemyRecipeGenerator.PACK_ID)) {
                selectedPacks.add(AlchemyRecipeGenerator.PACK_ID);
            }
            if (!repository.isAvailable(AlchemyRecipeGenerator.PACK_ID)) {
                throw new IllegalStateException("Generated datapack was not discovered: " + AlchemyRecipeGenerator.PACK_ID);
            }

            server.reloadResources(selectedPacks).whenComplete((ignored, throwable) -> server.execute(() -> {
                if (throwable != null) {
                    menu.setSaveResult(false);
                    ArsTransmutatoria.LOGGER.error("Failed to reload generated alchemy recipe {}", result.recipeId(), throwable);
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "gui.transmutatoria.recipe_generator.error.reload", result.file().toString()));
                    return;
                }
                serverPlayer.sendSystemMessage(Component.translatable(
                        result.overwroteFile()
                                ? "gui.transmutatoria.recipe_generator.saved_overwrite"
                                : "gui.transmutatoria.recipe_generator.saved",
                        result.recipeId().toString()
                ));
                if (result.relatedRecipeExisted() && !result.overwroteFile()) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "gui.transmutatoria.recipe_generator.saved_related_warning"));
                }
            }));
        } catch (Exception exception) {
            menu.setSaveResult(false);
            ArsTransmutatoria.LOGGER.error("Failed to generate alchemy recipe", exception);
            serverPlayer.sendSystemMessage(Component.translatable(
                    "gui.transmutatoria.recipe_generator.error.save", exception.getMessage()));
        }
    }
}
