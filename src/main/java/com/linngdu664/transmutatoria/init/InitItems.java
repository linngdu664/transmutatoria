package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.item.ItemTransmutationCrucible;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

public class InitItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // ================= [ 方块/特殊物品示例 ] =================
    public static DeferredItem<Item> TRANSMUTATION_CRUCIBLE = ITEMS.register("transmutation_crucible", ItemTransmutationCrucible::new);

    // ================= [ 基础物品 ] =================
    public static DeferredItem<Item> TRANSMUTATION_CRYSTAL = ITEMS.registerSimpleItem("transmutation_crystal");
    public static DeferredItem<Item> ALCHEMICAL_DROSS = ITEMS.registerSimpleItem("alchemical_dross");
    public static DeferredItem<Item> ALCHEMICAL_DROSS_BLOCK = ITEMS.registerSimpleItem("alchemical_dross_block");

    // ================= [ 金属变体 (12种 x 4状态) ] =================
    // 【星火】 蚀日金 / 泣月银 / 灾星钛
    public static DeferredItem<Item> ECLIPSIUM = ITEMS.registerSimpleItem("eclipsium");
    public static DeferredItem<Item> NIGREDO_TAINTED_ECLIPSIUM = ITEMS.registerSimpleItem("nigredo_tainted_eclipsium");
    public static DeferredItem<Item> ALBEDO_INFUSED_ECLIPSIUM = ITEMS.registerSimpleItem("albedo_infused_eclipsium");
    public static DeferredItem<Item> CITRINITAS_INFUSED_ECLIPSIUM = ITEMS.registerSimpleItem("citrinitas_infused_eclipsium");

    public static DeferredItem<Item> LUNARGENT = ITEMS.registerSimpleItem("lunargent");
    public static DeferredItem<Item> NIGREDO_TAINTED_LUNARGENT = ITEMS.registerSimpleItem("nigredo_tainted_lunargent");
    public static DeferredItem<Item> ALBEDO_INFUSED_LUNARGENT = ITEMS.registerSimpleItem("albedo_infused_lunargent");
    public static DeferredItem<Item> CITRINITAS_INFUSED_LUNARGENT = ITEMS.registerSimpleItem("citrinitas_infused_lunargent");

    public static DeferredItem<Item> ASTROTITE = ITEMS.registerSimpleItem("astrotite");
    public static DeferredItem<Item> NIGREDO_TAINTED_ASTROTITE = ITEMS.registerSimpleItem("nigredo_tainted_astrotite");
    public static DeferredItem<Item> ALBEDO_INFUSED_ASTROTITE = ITEMS.registerSimpleItem("albedo_infused_astrotite");
    public static DeferredItem<Item> CITRINITAS_INFUSED_ASTROTITE = ITEMS.registerSimpleItem("citrinitas_infused_astrotite");

    // 【渊水】 渊海钢 / 幻魂汞 / 冥沼铅
    public static DeferredItem<Item> ABYSSION = ITEMS.registerSimpleItem("abyssion");
    public static DeferredItem<Item> NIGREDO_TAINTED_ABYSSION = ITEMS.registerSimpleItem("nigredo_tainted_abyssion");
    public static DeferredItem<Item> ALBEDO_INFUSED_ABYSSION = ITEMS.registerSimpleItem("albedo_infused_abyssion");
    public static DeferredItem<Item> CITRINITAS_INFUSED_ABYSSION = ITEMS.registerSimpleItem("citrinitas_infused_abyssion");

    public static DeferredItem<Item> ANIMERCURY = ITEMS.registerSimpleItem("animercury");
    public static DeferredItem<Item> NIGREDO_TAINTED_ANIMERCURY = ITEMS.registerSimpleItem("nigredo_tainted_animercury");
    public static DeferredItem<Item> ALBEDO_INFUSED_ANIMERCURY = ITEMS.registerSimpleItem("albedo_infused_animercury");
    public static DeferredItem<Item> CITRINITAS_INFUSED_ANIMERCURY = ITEMS.registerSimpleItem("citrinitas_infused_animercury");

    public static DeferredItem<Item> NECROPLUMB = ITEMS.registerSimpleItem("necroplumb");
    public static DeferredItem<Item> NIGREDO_TAINTED_NECROPLUMB = ITEMS.registerSimpleItem("nigredo_tainted_necroplumb");
    public static DeferredItem<Item> ALBEDO_INFUSED_NECROPLUMB = ITEMS.registerSimpleItem("albedo_infused_necroplumb");
    public static DeferredItem<Item> CITRINITAS_INFUSED_NECROPLUMB = ITEMS.registerSimpleItem("citrinitas_infused_necroplumb");

    // 【腐土】 棘血铜 / 鸩林锡 / 骸骨锑
    public static DeferredItem<Item> SANGUIBRONZE = ITEMS.registerSimpleItem("sanguibronze");
    public static DeferredItem<Item> NIGREDO_TAINTED_SANGUIBRONZE = ITEMS.registerSimpleItem("nigredo_tainted_sanguibronze");
    public static DeferredItem<Item> ALBEDO_INFUSED_SANGUIBRONZE = ITEMS.registerSimpleItem("albedo_infused_sanguibronze");
    public static DeferredItem<Item> CITRINITAS_INFUSED_SANGUIBRONZE = ITEMS.registerSimpleItem("citrinitas_infused_sanguibronze");

    public static DeferredItem<Item> VENOTITE = ITEMS.registerSimpleItem("venotite");
    public static DeferredItem<Item> NIGREDO_TAINTED_VENOTITE = ITEMS.registerSimpleItem("nigredo_tainted_venotite");
    public static DeferredItem<Item> ALBEDO_INFUSED_VENOTITE = ITEMS.registerSimpleItem("albedo_infused_venotite");
    public static DeferredItem<Item> CITRINITAS_INFUSED_VENOTITE = ITEMS.registerSimpleItem("citrinitas_infused_venotite");

    public static DeferredItem<Item> OSSANTIMONY = ITEMS.registerSimpleItem("ossantimony");
    public static DeferredItem<Item> NIGREDO_TAINTED_OSSANTIMONY = ITEMS.registerSimpleItem("nigredo_tainted_ossantimony");
    public static DeferredItem<Item> ALBEDO_INFUSED_OSSANTIMONY = ITEMS.registerSimpleItem("albedo_infused_ossantimony");
    public static DeferredItem<Item> CITRINITAS_INFUSED_OSSANTIMONY = ITEMS.registerSimpleItem("citrinitas_infused_ossantimony");

    // 【劫风】 怒雷锌 / 凝时铂 / 无相源金
    public static DeferredItem<Item> FULGURZINC = ITEMS.registerSimpleItem("fulgurzinc");
    public static DeferredItem<Item> NIGREDO_TAINTED_FULGURZINC = ITEMS.registerSimpleItem("nigredo_tainted_fulgurzinc");
    public static DeferredItem<Item> ALBEDO_INFUSED_FULGURZINC = ITEMS.registerSimpleItem("albedo_infused_fulgurzinc");
    public static DeferredItem<Item> CITRINITAS_INFUSED_FULGURZINC = ITEMS.registerSimpleItem("citrinitas_infused_fulgurzinc");

    public static DeferredItem<Item> CHRONOPLATINUM = ITEMS.registerSimpleItem("chronoplatinum");
    public static DeferredItem<Item> NIGREDO_TAINTED_CHRONOPLATINUM = ITEMS.registerSimpleItem("nigredo_tainted_chronoplatinum");
    public static DeferredItem<Item> ALBEDO_INFUSED_CHRONOPLATINUM = ITEMS.registerSimpleItem("albedo_infused_chronoplatinum");
    public static DeferredItem<Item> CITRINITAS_INFUSED_CHRONOPLATINUM = ITEMS.registerSimpleItem("citrinitas_infused_chronoplatinum");

    public static DeferredItem<Item> PANDEMONIUM = ITEMS.registerSimpleItem("pandemonium");
    public static DeferredItem<Item> NIGREDO_TAINTED_PANDEMONIUM = ITEMS.registerSimpleItem("nigredo_tainted_pandemonium");
    public static DeferredItem<Item> ALBEDO_INFUSED_PANDEMONIUM = ITEMS.registerSimpleItem("albedo_infused_pandemonium");
    public static DeferredItem<Item> CITRINITAS_INFUSED_PANDEMONIUM = ITEMS.registerSimpleItem("citrinitas_infused_pandemonium");

    // ================= [ 炼金基质 ] =================
    public static DeferredItem<Item> SALIC_MATRIX = ITEMS.registerSimpleItem("salic_matrix");
    public static DeferredItem<Item> MERCURIAL_MATRIX = ITEMS.registerSimpleItem("mercurial_matrix");
    public static DeferredItem<Item> SULFURIC_MATRIX = ITEMS.registerSimpleItem("sulfuric_matrix");

    // ================= [ 源质与贤者之石 ] =================
    public static DeferredItem<Item> PRIMA_MATERIA = ITEMS.registerSimpleItem("prima_materia");
    public static DeferredItem<Item> NIGREDO_ESSENCE = ITEMS.registerSimpleItem("nigredo_essence");
    public static DeferredItem<Item> ALBEDO_ESSENCE = ITEMS.registerSimpleItem("albedo_essence");
    public static DeferredItem<Item> CITRINITAS_ESSENCE = ITEMS.registerSimpleItem("citrinitas_essence");
    public static DeferredItem<Item> RUBEDO_ESSENCE = ITEMS.registerSimpleItem("rubedo_essence");
    public static DeferredItem<Item> PHILOSOPHERS_STONE = ITEMS.registerSimpleItem("philosophers_stone");

    // ================= [ 卷轴 ] =================
    public static DeferredItem<Item> TRANSMUTATION_SIGIL_SCROLL = ITEMS.registerSimpleItem("transmutation_sigil_scroll");
    public static DeferredItem<Item> ACTIVATED_TRANSMUTATION_SIGIL_SCROLL = ITEMS.registerSimpleItem("activated_transmutation_sigil_scroll");

    public static DeferredItem<Item> TERRESTRIAL_SIGIL_SCROLL = ITEMS.registerSimpleItem("terrestrial_sigil_scroll");
    public static DeferredItem<Item> ACTIVATED_TERRESTRIAL_SIGIL_SCROLL = ITEMS.registerSimpleItem("activated_terrestrial_sigil_scroll");

    public static DeferredItem<Item> LUNAR_SIGIL_SCROLL = ITEMS.registerSimpleItem("lunar_sigil_scroll");
    public static DeferredItem<Item> ACTIVATED_LUNAR_SIGIL_SCROLL = ITEMS.registerSimpleItem("activated_lunar_sigil_scroll");

    public static DeferredItem<Item> SOLAR_SIGIL_SCROLL = ITEMS.registerSimpleItem("solar_sigil_scroll");
    public static DeferredItem<Item> ACTIVATED_SOLAR_SIGIL_SCROLL = ITEMS.registerSimpleItem("activated_solar_sigil_scroll");

    public static DeferredItem<Item> TRANSMUTATION_EQUATION_SCROLL = ITEMS.registerSimpleItem("transmutation_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_TRANSMUTATION_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_transmutation_equation_scroll");

    public static DeferredItem<Item> TERRESTRIAL_EQUATION_SCROLL = ITEMS.registerSimpleItem("terrestrial_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_TERRESTRIAL_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_terrestrial_equation_scroll");

    public static DeferredItem<Item> LUNAR_EQUATION_SCROLL = ITEMS.registerSimpleItem("lunar_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_LUNAR_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_lunar_equation_scroll");

    public static DeferredItem<Item> SOLAR_EQUATION_SCROLL = ITEMS.registerSimpleItem("solar_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_SOLAR_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_solar_equation_scroll");


    // ================= [ 创造模式物品栏注册 ] =================
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TRANSMUTATORIA_TAB = TABS.register(MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.transmutatoria"))
            .icon(() -> new ItemStack(InitBlocks.TRANSMUTATION_CRUCIBLE.get()))
            .displayItems((parameters, output) -> {

                output.accept(new ItemStack(TRANSMUTATION_CRUCIBLE.get()));
                output.accept(new ItemStack(TRANSMUTATION_CRYSTAL.get()));
                output.accept(new ItemStack(ALCHEMICAL_DROSS.get()));
                output.accept(new ItemStack(ALCHEMICAL_DROSS_BLOCK.get()));

                // 金属 - 蚀日金系
                output.accept(new ItemStack(ECLIPSIUM.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_ECLIPSIUM.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_ECLIPSIUM.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_ECLIPSIUM.get()));

                // 金属 - 泣月银系
                output.accept(new ItemStack(LUNARGENT.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_LUNARGENT.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_LUNARGENT.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_LUNARGENT.get()));

                // 金属 - 灾星钛系
                output.accept(new ItemStack(ASTROTITE.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_ASTROTITE.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_ASTROTITE.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_ASTROTITE.get()));

                // 金属 - 渊海钢系
                output.accept(new ItemStack(ABYSSION.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_ABYSSION.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_ABYSSION.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_ABYSSION.get()));

                // 金属 - 幻魂汞系
                output.accept(new ItemStack(ANIMERCURY.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_ANIMERCURY.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_ANIMERCURY.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_ANIMERCURY.get()));

                // 金属 - 冥沼铅系
                output.accept(new ItemStack(NECROPLUMB.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_NECROPLUMB.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_NECROPLUMB.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_NECROPLUMB.get()));

                // 金属 - 棘血铜系
                output.accept(new ItemStack(SANGUIBRONZE.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_SANGUIBRONZE.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_SANGUIBRONZE.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_SANGUIBRONZE.get()));

                // 金属 - 鸩林锡系
                output.accept(new ItemStack(VENOTITE.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_VENOTITE.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_VENOTITE.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_VENOTITE.get()));

                // 金属 - 骸骨锑系
                output.accept(new ItemStack(OSSANTIMONY.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_OSSANTIMONY.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_OSSANTIMONY.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_OSSANTIMONY.get()));

                // 金属 - 怒雷锌系
                output.accept(new ItemStack(FULGURZINC.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_FULGURZINC.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_FULGURZINC.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_FULGURZINC.get()));

                // 金属 - 凝时铂系
                output.accept(new ItemStack(CHRONOPLATINUM.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_CHRONOPLATINUM.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_CHRONOPLATINUM.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_CHRONOPLATINUM.get()));

                // 金属 - 无相源金系
                output.accept(new ItemStack(PANDEMONIUM.get()));
                output.accept(new ItemStack(NIGREDO_TAINTED_PANDEMONIUM.get()));
                output.accept(new ItemStack(ALBEDO_INFUSED_PANDEMONIUM.get()));
                output.accept(new ItemStack(CITRINITAS_INFUSED_PANDEMONIUM.get()));

                // 炼金基质
                output.accept(new ItemStack(SALIC_MATRIX.get()));
                output.accept(new ItemStack(MERCURIAL_MATRIX.get()));
                output.accept(new ItemStack(SULFURIC_MATRIX.get()));

                // 源质
                output.accept(new ItemStack(PRIMA_MATERIA.get()));
                output.accept(new ItemStack(NIGREDO_ESSENCE.get()));
                output.accept(new ItemStack(ALBEDO_ESSENCE.get()));
                output.accept(new ItemStack(CITRINITAS_ESSENCE.get()));
                output.accept(new ItemStack(RUBEDO_ESSENCE.get()));
                output.accept(new ItemStack(PHILOSOPHERS_STONE.get()));

                // 卷轴
                output.accept(new ItemStack(TRANSMUTATION_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_TRANSMUTATION_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(TERRESTRIAL_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_TERRESTRIAL_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(LUNAR_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_LUNAR_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(SOLAR_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_SOLAR_SIGIL_SCROLL.get()));

                output.accept(new ItemStack(TRANSMUTATION_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_TRANSMUTATION_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(TERRESTRIAL_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_TERRESTRIAL_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(LUNAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_LUNAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(SOLAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_SOLAR_EQUATION_SCROLL.get()));

            }).build()
    );
}
