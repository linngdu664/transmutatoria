package com.linngdu664.transmutatoria.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.Config;
import com.linngdu664.transmutatoria.inventory.AlchemyRecipeGeneratorMenu;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public class AlchemyRecipeGeneratorCommand {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (!Config.ENABLE_RECIPE_GENERATOR_COMMANDS.get()) {
            return;
        }
        event.getDispatcher().register(
                Commands.literal("transmutatoria_replication")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> open(context.getSource().getPlayerOrException(), AlchemyRecipeGeneratorMenu.Kind.REPLICATION))
        );
        event.getDispatcher().register(
                Commands.literal("transmutatoria_transformation")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> open(context.getSource().getPlayerOrException(), AlchemyRecipeGeneratorMenu.Kind.TRANSFORMATION))
        );
    }

    private static int open(ServerPlayer player, AlchemyRecipeGeneratorMenu.Kind kind) {
        Component title = Component.translatable(kind == AlchemyRecipeGeneratorMenu.Kind.REPLICATION
                ? "gui.transmutatoria.recipe_generator.replication"
                : "gui.transmutatoria.recipe_generator.transformation");
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, ignored) -> new AlchemyRecipeGeneratorMenu(containerId, inventory, kind),
                title
        ));
        return 1;
    }
}
