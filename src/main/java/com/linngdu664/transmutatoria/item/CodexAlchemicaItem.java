package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.core.registries.Registries;
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
import vazkii.patchouli.api.PatchouliAPI;

public class CodexAlchemicaItem extends Item {
    private static final Identifier BOOK_ID = ArsTransmutatoria.makeMyIdentifier("liber_transmutationis");

    public CodexAlchemicaItem(Identifier id) {
        super(new Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PatchouliAPI.get().openBookGUI(serverPlayer, BOOK_ID);
            level.playSound(
                    null,
                    player.getX(),
                    player.getY() + 0.5,
                    player.getZ(),
                    SoundEvents.BOOK_PAGE_TURN,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}
