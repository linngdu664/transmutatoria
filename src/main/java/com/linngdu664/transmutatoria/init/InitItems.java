package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.item.EssenceMetal;
import com.linngdu664.transmutatoria.item.ItemAlchemistStorageBox;
import com.linngdu664.transmutatoria.item.ItemEssenceMetal;
import com.linngdu664.transmutatoria.item.ItemTransmutationCrucible;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
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

    // ================= [ 炼金术士储物盒 ] =================
    public static DeferredItem<Item> ALCHEMIST_STORAGE_BOX = ITEMS.register("alchemist_storage_box",
            (id) -> new ItemAlchemistStorageBox(id, 0));
    public static DeferredItem<Item> NIGREDO_ALCHEMIST_STORAGE_BOX = ITEMS.register("nigredo_alchemist_storage_box",
            (id) -> new ItemAlchemistStorageBox(id, -1));
    public static DeferredItem<Item> ALBEDO_ALCHEMIST_STORAGE_BOX = ITEMS.register("albedo_alchemist_storage_box",
            (id) -> new ItemAlchemistStorageBox(id, 1));
    public static DeferredItem<Item> CITRINITAS_ALCHEMIST_STORAGE_BOX = ITEMS.register("citrinitas_alchemist_storage_box",
            (id) -> new ItemAlchemistStorageBox(id, 2));

    // ================= [ 基础物品 ] =================
    public static DeferredItem<Item> TRANSMUTATION_CRYSTAL = ITEMS.registerSimpleItem("transmutation_crystal");
    public static DeferredItem<Item> ALCHEMICAL_DROSS = ITEMS.registerSimpleItem("alchemical_dross");
    public static DeferredItem<BlockItem> ALCHEMICAL_DROSS_BLOCK = ITEMS.registerSimpleBlockItem("alchemical_dross_block",InitBlocks.ALCHEMICAL_DROSS_BLOCK);

    // ================= [ 金属变体 (12种 x 4状态) ] =================
    // 【星火】 蚀日金 (A) / 泣月银 (B) / 灾星钛 (C)
//    public static DeferredItem<Item> ECLIPSIUM = ITEMS.register(EssenceMetal.A.getKeyWithPrefix(0),()->new ItemEssenceMetal(EssenceMetal.A, 0));
    public static DeferredItem<Item> ECLIPSIUM = essenceMetalRegister(EssenceMetal.A, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_ECLIPSIUM = essenceMetalRegister(EssenceMetal.A, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_ECLIPSIUM = essenceMetalRegister(EssenceMetal.A, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_ECLIPSIUM = essenceMetalRegister(EssenceMetal.A, 2);

    public static DeferredItem<Item> LUNARGENT = essenceMetalRegister(EssenceMetal.B, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_LUNARGENT = essenceMetalRegister(EssenceMetal.B, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_LUNARGENT = essenceMetalRegister(EssenceMetal.B, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_LUNARGENT = essenceMetalRegister(EssenceMetal.B, 2);

    public static DeferredItem<Item> ASTROTITE = essenceMetalRegister(EssenceMetal.C, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_ASTROTITE = essenceMetalRegister(EssenceMetal.C, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_ASTROTITE = essenceMetalRegister(EssenceMetal.C, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_ASTROTITE = essenceMetalRegister(EssenceMetal.C, 2);

    //  【渊水】 渊海钢 (D) / 幻魂汞 (E) / 冥沼铅 (F)
    public static DeferredItem<Item> ABYSSION = essenceMetalRegister(EssenceMetal.D, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_ABYSSION = essenceMetalRegister(EssenceMetal.D, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_ABYSSION = essenceMetalRegister(EssenceMetal.D, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_ABYSSION = essenceMetalRegister(EssenceMetal.D, 2);

    public static DeferredItem<Item> ANIMERCURY = essenceMetalRegister(EssenceMetal.E, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_ANIMERCURY = essenceMetalRegister(EssenceMetal.E, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_ANIMERCURY = essenceMetalRegister(EssenceMetal.E, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_ANIMERCURY = essenceMetalRegister(EssenceMetal.E, 2);

    public static DeferredItem<Item> NECROPLUMB = essenceMetalRegister(EssenceMetal.F, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_NECROPLUMB = essenceMetalRegister(EssenceMetal.F, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_NECROPLUMB = essenceMetalRegister(EssenceMetal.F, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_NECROPLUMB = essenceMetalRegister(EssenceMetal.F, 2);

    // 【腐土】 棘血铜 (G) / 鸩林锡 (H) / 骸骨锑 (I)
    public static DeferredItem<Item> SANGUIBRONZE = essenceMetalRegister(EssenceMetal.G, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_SANGUIBRONZE = essenceMetalRegister(EssenceMetal.G, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_SANGUIBRONZE = essenceMetalRegister(EssenceMetal.G, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_SANGUIBRONZE = essenceMetalRegister(EssenceMetal.G, 2);

    public static DeferredItem<Item> VENOTITE = essenceMetalRegister(EssenceMetal.H, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_VENOTITE = essenceMetalRegister(EssenceMetal.H, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_VENOTITE = essenceMetalRegister(EssenceMetal.H, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_VENOTITE = essenceMetalRegister(EssenceMetal.H, 2);

    public static DeferredItem<Item> OSSANTIMONY = essenceMetalRegister(EssenceMetal.I, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_OSSANTIMONY = essenceMetalRegister(EssenceMetal.I, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_OSSANTIMONY = essenceMetalRegister(EssenceMetal.I, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_OSSANTIMONY = essenceMetalRegister(EssenceMetal.I, 2);

    // 【劫风】 怒雷锌 (J) / 凝时铂 (K) / 无相源金 (L)
    public static DeferredItem<Item> FULGURZINC = essenceMetalRegister(EssenceMetal.J, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_FULGURZINC = essenceMetalRegister(EssenceMetal.J, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_FULGURZINC = essenceMetalRegister(EssenceMetal.J, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_FULGURZINC = essenceMetalRegister(EssenceMetal.J, 2);

    public static DeferredItem<Item> CHRONOPLATINUM = essenceMetalRegister(EssenceMetal.K, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_CHRONOPLATINUM = essenceMetalRegister(EssenceMetal.K, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_CHRONOPLATINUM = essenceMetalRegister(EssenceMetal.K, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_CHRONOPLATINUM = essenceMetalRegister(EssenceMetal.K, 2);

    public static DeferredItem<Item> PANDEMONIUM = essenceMetalRegister(EssenceMetal.L, 0);
    public static DeferredItem<Item> NIGREDO_TAINTED_PANDEMONIUM = essenceMetalRegister(EssenceMetal.L, -1);
    public static DeferredItem<Item> ALBEDO_INFUSED_PANDEMONIUM = essenceMetalRegister(EssenceMetal.L, 1);
    public static DeferredItem<Item> CITRINITAS_INFUSED_PANDEMONIUM = essenceMetalRegister(EssenceMetal.L, 2);

    public static DeferredItem<Item>[] ESSENCE_METAL_ITEMS = new DeferredItem[]{
        ECLIPSIUM, LUNARGENT, ASTROTITE, ABYSSION, ANIMERCURY, NECROPLUMB, SANGUIBRONZE, VENOTITE, OSSANTIMONY, FULGURZINC, CHRONOPLATINUM, PANDEMONIUM
    };

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

    public static DeferredItem<Item> VOID_SIGIL_SCROLL = ITEMS.registerSimpleItem("void_sigil_scroll");
    public static DeferredItem<Item> ACTIVATED_VOID_SIGIL_SCROLL = ITEMS.registerSimpleItem("activated_void_sigil_scroll");

    public static DeferredItem<Item> TRANSMUTATION_EQUATION_SCROLL = ITEMS.registerSimpleItem("transmutation_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_TRANSMUTATION_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_transmutation_equation_scroll");

    public static DeferredItem<Item> TERRESTRIAL_EQUATION_SCROLL = ITEMS.registerSimpleItem("terrestrial_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_TERRESTRIAL_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_terrestrial_equation_scroll");

    public static DeferredItem<Item> LUNAR_EQUATION_SCROLL = ITEMS.registerSimpleItem("lunar_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_LUNAR_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_lunar_equation_scroll");

    public static DeferredItem<Item> SOLAR_EQUATION_SCROLL = ITEMS.registerSimpleItem("solar_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_SOLAR_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_solar_equation_scroll");

    public static DeferredItem<Item> VOID_EQUATION_SCROLL = ITEMS.registerSimpleItem("void_equation_scroll");
    public static DeferredItem<Item> ACTIVATED_VOID_EQUATION_SCROLL = ITEMS.registerSimpleItem("activated_void_equation_scroll");

    public static DeferredItem<Item> essenceMetalRegister(EssenceMetal essenceMetal, int state){
        return ITEMS.register(essenceMetal.getKeyWithPrefix(state),()->new ItemEssenceMetal(essenceMetal, state));
    }


    // ================= [ 创造模式物品栏注册 ] =================
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TRANSMUTATORIA_TAB = TABS.register(MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.transmutatoria"))
            .icon(() -> new ItemStack(InitBlocks.TRANSMUTATION_CRUCIBLE.get()))
            .displayItems((parameters, output) -> {

                output.accept(new ItemStack(TRANSMUTATION_CRUCIBLE.get()));

                // 储物盒
                output.accept(new ItemStack(ALCHEMIST_STORAGE_BOX.get()));
                output.accept(new ItemStack(NIGREDO_ALCHEMIST_STORAGE_BOX.get()));
                output.accept(new ItemStack(ALBEDO_ALCHEMIST_STORAGE_BOX.get()));
                output.accept(new ItemStack(CITRINITAS_ALCHEMIST_STORAGE_BOX.get()));

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
                output.accept(new ItemStack(VOID_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_VOID_SIGIL_SCROLL.get()));

                output.accept(new ItemStack(TRANSMUTATION_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_TRANSMUTATION_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(TERRESTRIAL_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_TERRESTRIAL_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(LUNAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_LUNAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(SOLAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_SOLAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(VOID_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(ACTIVATED_VOID_EQUATION_SCROLL.get()));

            }).build()
    );
}
