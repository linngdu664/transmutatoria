package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/** Advancement IDs and helpers for completed alchemical interactions. */
public final class InitAdvancements {
    public static final Identifier CRUCIBLE_PLACED = id("alchemy/crucible");
    public static final Identifier SCROLL_ACTIVATED = id("alchemy/scroll_activated");
    public static final Identifier ALCHEMICAL_REPLICATION = id("alchemy/alchemical_replication");
    public static final Identifier ALCHEMICAL_TRANSFORMATION = id("alchemy/alchemical_transformation");
    public static final Identifier CHAOS_DECOMPOSITION = id("alchemy/chaos_decomposition");
    public static final Identifier DEATHLESS_PHILOSOPHER = id("alchemy/deathless_philosopher");

    private static final String MANUAL_CRITERION = "completed";

    private InitAdvancements() {
    }

    public static void award(ServerPlayer player, Identifier advancementId) {
        AdvancementHolder advancement = player.level().getServer().getAdvancements().get(advancementId);
        if (advancement != null) {
            player.getAdvancements().award(advancement, MANUAL_CRITERION);
        }
    }

    private static Identifier id(String path) {
        return ArsTransmutatoria.makeMyIdentifier(path);
    }
}
