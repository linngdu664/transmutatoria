package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public class LiberTransmutationisArsTransmutatoriaItem extends Item {
    private static final Identifier AGERATUM_GUIDE_ID = ArsTransmutatoria.makeMyIdentifier("index");
    private static final String AGERATUM_MOD_ID = "ageratum";
    private static final String AGERATUM_CLASS_NAME = "dev.anvilcraft.resource.ageratum.Ageratum";

    public LiberTransmutationisArsTransmutatoriaItem(Identifier id) {
        super(new Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (openGuide(serverPlayer)) {
                level.playSound(
                        null,
                        player.getX(),
                        player.getY() + 0.5,
                        player.getZ(),
                        SoundEvents.BOOK_PAGE_TURN,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F);
            } else {
                serverPlayer.sendSystemMessage(
                        Component.translatable("message.transmutatoria.ageratum_guide.missing_backend"),
                        true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean openGuide(ServerPlayer player) {
        if (!ModList.get().isLoaded(AGERATUM_MOD_ID)) {
            return false;
        }
        try {
            // Keep Ageratum optional: call its API reflectively only when the mod is loaded.
            Class<?> ageratumClass = Class.forName(AGERATUM_CLASS_NAME);
            ageratumClass
                    .getMethod("openGuide", ServerPlayer.class, Identifier.class)
                    .invoke(null, player, AGERATUM_GUIDE_ID);
            return true;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }
}
