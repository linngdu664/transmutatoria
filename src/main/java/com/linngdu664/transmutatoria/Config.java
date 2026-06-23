package com.linngdu664.transmutatoria;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_RECIPE_GENERATOR_COMMANDS = BUILDER
            .comment(
                    "Whether to register the in-game alchemy recipe generator commands.",
                    "When disabled, the commands are absent from command suggestions and cannot open the generator GUI.",
                    "Requires a server restart or command reload after changing."
            )
            .define("enableRecipeGeneratorCommands", false);

//    public static final int DEFAULT_TRANSMUTATION_SCROLL_DURABILITY = 32;
//    public static final int DEFAULT_TERRESTRIAL_SCROLL_DURABILITY = 64;
//    public static final int DEFAULT_LUNAR_SCROLL_DURABILITY = 96;
//    public static final int DEFAULT_SOLAR_SCROLL_DURABILITY = 128;
//    public static final int DEFAULT_VOID_SCROLL_DURABILITY = 0;
//
//    public static final ModConfigSpec.IntValue TRANSMUTATION_SCROLL_DURABILITY;
//    public static final ModConfigSpec.IntValue TERRESTRIAL_SCROLL_DURABILITY;
//    public static final ModConfigSpec.IntValue LUNAR_SCROLL_DURABILITY;
//    public static final ModConfigSpec.IntValue SOLAR_SCROLL_DURABILITY;
//    public static final ModConfigSpec.IntValue VOID_SCROLL_DURABILITY;
//
//    static {
//        BUILDER.push("scrollDurability");
//
//        TRANSMUTATION_SCROLL_DURABILITY = BUILDER
//                .comment("Maximum durability for basic transmutation scrolls.")
//                .defineInRange("transmutationScroll", DEFAULT_TRANSMUTATION_SCROLL_DURABILITY, 1, Integer.MAX_VALUE);
//        TERRESTRIAL_SCROLL_DURABILITY = BUILDER
//                .comment("Maximum durability for terrestrial transmutation scrolls.")
//                .defineInRange("terrestrialScroll", DEFAULT_TERRESTRIAL_SCROLL_DURABILITY, 1, Integer.MAX_VALUE);
//        LUNAR_SCROLL_DURABILITY = BUILDER
//                .comment("Maximum durability for lunar transmutation scrolls.")
//                .defineInRange("lunarScroll", DEFAULT_LUNAR_SCROLL_DURABILITY, 1, Integer.MAX_VALUE);
//        SOLAR_SCROLL_DURABILITY = BUILDER
//                .comment("Maximum durability for solar transmutation scrolls.")
//                .defineInRange("solarScroll", DEFAULT_SOLAR_SCROLL_DURABILITY, 1, Integer.MAX_VALUE);
//        VOID_SCROLL_DURABILITY = BUILDER
//                .comment("Maximum durability for void transmutation scrolls. Set to 0 to make them unbreakable.")
//                .defineInRange("voidScroll", DEFAULT_VOID_SCROLL_DURABILITY, 0, Integer.MAX_VALUE);
//
//        BUILDER.pop();
//    }

    static final ModConfigSpec SPEC = BUILDER.build();
}
