package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.item.*;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import com.linngdu664.transmutatoria.util.EssenceMetal;

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
    public static DeferredItem<Item> TRANSMUTATION_CRUCIBLE = ITEMS.register("transmutation_crucible", TransmutationCrucibleItem::new);

    // ================= [ 炼金术士储物盒 ] =================
    public static DeferredItem<Item> ALCHEMIST_STORAGE_BOX = ITEMS.register("alchemist_storage_box",
            (id) -> new AlchemistStorageBoxItem(id, InitBlocks.ALCHEMIST_STORAGE_BOX.get(), 0));
    public static DeferredItem<Item> NIGREDO_ALCHEMIST_STORAGE_BOX = ITEMS.register("nigredo_alchemist_storage_box",
            (id) -> new AlchemistStorageBoxItem(id, InitBlocks.NIGREDO_ALCHEMIST_STORAGE_BOX.get(), -1));
    public static DeferredItem<Item> ALBEDO_ALCHEMIST_STORAGE_BOX = ITEMS.register("albedo_alchemist_storage_box",
            (id) -> new AlchemistStorageBoxItem(id, InitBlocks.ALBEDO_ALCHEMIST_STORAGE_BOX.get(), 1));
    public static DeferredItem<Item> CITRINITAS_ALCHEMIST_STORAGE_BOX = ITEMS.register("citrinitas_alchemist_storage_box",
            (id) -> new AlchemistStorageBoxItem(id, InitBlocks.CITRINITAS_ALCHEMIST_STORAGE_BOX.get(), 2));

    // ================= [ 基础物品 ] =================
    public static DeferredItem<Item> TRANSMUTATION_CRYSTAL = ITEMS.registerSimpleItem("transmutation_crystal");
    public static DeferredItem<Item> ALCHEMICAL_DROSS = ITEMS.registerSimpleItem("alchemical_dross");
    public static DeferredItem<BlockItem> ALCHEMICAL_DROSS_BLOCK = ITEMS.registerSimpleBlockItem("alchemical_dross_block",InitBlocks.ALCHEMICAL_DROSS_BLOCK);

    // ================= [ 金属变体 (12种 x 4状态) ] =================
    // 【星火】 蚀日金 (A) / 泣月银 (B) / 灾星钛 (C)
//    public static DeferredItem<Item> ECLIPSIUM = ITEMS.register(EssenceMetal.A.getKeyWithPrefix(0),()->new ItemEssenceMetal(EssenceMetal.A, 0));
    public static DeferredItem<Item> ECLIPSIUM = essenceMetalRegister(EssenceMetal.A, 0);
    public static DeferredItem<Item> NIGREDO_ECLIPSIUM = essenceMetalRegister(EssenceMetal.A, -1);
    public static DeferredItem<Item> ALBEDO_ECLIPSIUM = essenceMetalRegister(EssenceMetal.A, 1);
    public static DeferredItem<Item> CITRINITAS_ECLIPSIUM = essenceMetalRegister(EssenceMetal.A, 2);

    public static DeferredItem<Item> LUNARGENT = essenceMetalRegister(EssenceMetal.B, 0);
    public static DeferredItem<Item> NIGREDO_LUNARGENT = essenceMetalRegister(EssenceMetal.B, -1);
    public static DeferredItem<Item> ALBEDO_LUNARGENT = essenceMetalRegister(EssenceMetal.B, 1);
    public static DeferredItem<Item> CITRINITAS_LUNARGENT = essenceMetalRegister(EssenceMetal.B, 2);

    public static DeferredItem<Item> ASTROTITE = essenceMetalRegister(EssenceMetal.C, 0);
    public static DeferredItem<Item> NIGREDO_ASTROTITE = essenceMetalRegister(EssenceMetal.C, -1);
    public static DeferredItem<Item> ALBEDO_ASTROTITE = essenceMetalRegister(EssenceMetal.C, 1);
    public static DeferredItem<Item> CITRINITAS_ASTROTITE = essenceMetalRegister(EssenceMetal.C, 2);

    //  【渊水】 渊海钢 (D) / 幻魂汞 (E) / 冥沼铅 (F)
    public static DeferredItem<Item> ABYSSION = essenceMetalRegister(EssenceMetal.D, 0);
    public static DeferredItem<Item> NIGREDO_ABYSSION = essenceMetalRegister(EssenceMetal.D, -1);
    public static DeferredItem<Item> ALBEDO_ABYSSION = essenceMetalRegister(EssenceMetal.D, 1);
    public static DeferredItem<Item> CITRINITAS_ABYSSION = essenceMetalRegister(EssenceMetal.D, 2);

    public static DeferredItem<Item> ANIMERCURY = essenceMetalRegister(EssenceMetal.E, 0);
    public static DeferredItem<Item> NIGREDO_ANIMERCURY = essenceMetalRegister(EssenceMetal.E, -1);
    public static DeferredItem<Item> ALBEDO_ANIMERCURY = essenceMetalRegister(EssenceMetal.E, 1);
    public static DeferredItem<Item> CITRINITAS_ANIMERCURY = essenceMetalRegister(EssenceMetal.E, 2);

    public static DeferredItem<Item> NECROPLUMB = essenceMetalRegister(EssenceMetal.F, 0);
    public static DeferredItem<Item> NIGREDO_NECROPLUMB = essenceMetalRegister(EssenceMetal.F, -1);
    public static DeferredItem<Item> ALBEDO_NECROPLUMB = essenceMetalRegister(EssenceMetal.F, 1);
    public static DeferredItem<Item> CITRINITAS_NECROPLUMB = essenceMetalRegister(EssenceMetal.F, 2);

    // 【腐土】 棘血铜 (G) / 鸩林锡 (H) / 骸骨锑 (I)
    public static DeferredItem<Item> SANGUIBRONZE = essenceMetalRegister(EssenceMetal.G, 0);
    public static DeferredItem<Item> NIGREDO_SANGUIBRONZE = essenceMetalRegister(EssenceMetal.G, -1);
    public static DeferredItem<Item> ALBEDO_SANGUIBRONZE = essenceMetalRegister(EssenceMetal.G, 1);
    public static DeferredItem<Item> CITRINITAS_SANGUIBRONZE = essenceMetalRegister(EssenceMetal.G, 2);

    public static DeferredItem<Item> VENOTITE = essenceMetalRegister(EssenceMetal.H, 0);
    public static DeferredItem<Item> NIGREDO_VENOTITE = essenceMetalRegister(EssenceMetal.H, -1);
    public static DeferredItem<Item> ALBEDO_VENOTITE = essenceMetalRegister(EssenceMetal.H, 1);
    public static DeferredItem<Item> CITRINITAS_VENOTITE = essenceMetalRegister(EssenceMetal.H, 2);

    public static DeferredItem<Item> OSSANTIMONY = essenceMetalRegister(EssenceMetal.I, 0);
    public static DeferredItem<Item> NIGREDO_OSSANTIMONY = essenceMetalRegister(EssenceMetal.I, -1);
    public static DeferredItem<Item> ALBEDO_OSSANTIMONY = essenceMetalRegister(EssenceMetal.I, 1);
    public static DeferredItem<Item> CITRINITAS_OSSANTIMONY = essenceMetalRegister(EssenceMetal.I, 2);

    // 【劫风】 怒雷锌 (J) / 凝时铂 (K) / 无相源金 (L)
    public static DeferredItem<Item> FULGURZINC = essenceMetalRegister(EssenceMetal.J, 0);
    public static DeferredItem<Item> NIGREDO_FULGURZINC = essenceMetalRegister(EssenceMetal.J, -1);
    public static DeferredItem<Item> ALBEDO_FULGURZINC = essenceMetalRegister(EssenceMetal.J, 1);
    public static DeferredItem<Item> CITRINITAS_FULGURZINC = essenceMetalRegister(EssenceMetal.J, 2);

    public static DeferredItem<Item> CHRONOPLATINUM = essenceMetalRegister(EssenceMetal.K, 0);
    public static DeferredItem<Item> NIGREDO_CHRONOPLATINUM = essenceMetalRegister(EssenceMetal.K, -1);
    public static DeferredItem<Item> ALBEDO_CHRONOPLATINUM = essenceMetalRegister(EssenceMetal.K, 1);
    public static DeferredItem<Item> CITRINITAS_CHRONOPLATINUM = essenceMetalRegister(EssenceMetal.K, 2);

    public static DeferredItem<Item> PANDEMONIUM = essenceMetalRegister(EssenceMetal.L, 0);
    public static DeferredItem<Item> NIGREDO_PANDEMONIUM = essenceMetalRegister(EssenceMetal.L, -1);
    public static DeferredItem<Item> ALBEDO_PANDEMONIUM = essenceMetalRegister(EssenceMetal.L, 1);
    public static DeferredItem<Item> CITRINITAS_PANDEMONIUM = essenceMetalRegister(EssenceMetal.L, 2);

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
    public static DeferredItem<Item> PHILOSOPHERS_STONE = ITEMS.register("philosophers_stone", PhilosophersStoneItem::new);

    // ================= [ 卷轴 — 印记（复制） ] =================
    public static DeferredItem<Item> TRANSMUTATION_SIGIL_SCROLL = ITEMS.register("transmutation_sigil_scroll",
            () -> new TransmutationSigilScrollItem(ArsTransmutatoria.makeMyIdentifier("transmutation_sigil_scroll"), ExpireInfo.DEFAULT, 32));
    public static DeferredItem<Item> TERRESTRIAL_SIGIL_SCROLL = ITEMS.register("terrestrial_sigil_scroll",
            () -> new TransmutationSigilScrollItem(ArsTransmutatoria.makeMyIdentifier("terrestrial_sigil_scroll"), ExpireInfo.DEFAULT, 64));
    public static DeferredItem<Item> LUNAR_SIGIL_SCROLL = ITEMS.register("lunar_sigil_scroll",
            () -> new TransmutationSigilScrollItem(ArsTransmutatoria.makeMyIdentifier("lunar_sigil_scroll"), ExpireInfo.LUNAR, 96));
    public static DeferredItem<Item> SOLAR_SIGIL_SCROLL = ITEMS.register("solar_sigil_scroll",
            () -> new TransmutationSigilScrollItem(ArsTransmutatoria.makeMyIdentifier("solar_sigil_scroll"), 128));
    public static DeferredItem<Item> VOID_SIGIL_SCROLL = ITEMS.register("void_sigil_scroll",
            () -> new TransmutationSigilScrollItem(ArsTransmutatoria.makeMyIdentifier("void_sigil_scroll")));

    // ================= [ 卷轴 — 方程（转化） ] =================
    public static DeferredItem<Item> TRANSMUTATION_EQUATION_SCROLL = ITEMS.register("transmutation_equation_scroll",
            () -> new TransmutationEquationScrollItem(ArsTransmutatoria.makeMyIdentifier("transmutation_equation_scroll"), ExpireInfo.DEFAULT, 32));
    public static DeferredItem<Item> TERRESTRIAL_EQUATION_SCROLL = ITEMS.register("terrestrial_equation_scroll",
            () -> new TransmutationEquationScrollItem(ArsTransmutatoria.makeMyIdentifier("terrestrial_equation_scroll"), ExpireInfo.DEFAULT, 64));
    public static DeferredItem<Item> LUNAR_EQUATION_SCROLL = ITEMS.register("lunar_equation_scroll",
            () -> new TransmutationEquationScrollItem(ArsTransmutatoria.makeMyIdentifier("lunar_equation_scroll"), ExpireInfo.LUNAR, 96));
    public static DeferredItem<Item> SOLAR_EQUATION_SCROLL = ITEMS.register("solar_equation_scroll",
            () -> new TransmutationEquationScrollItem(ArsTransmutatoria.makeMyIdentifier("solar_equation_scroll"), 128));
    public static DeferredItem<Item> VOID_EQUATION_SCROLL = ITEMS.register("void_equation_scroll",
            () -> new TransmutationEquationScrollItem(ArsTransmutatoria.makeMyIdentifier("void_equation_scroll")));

    public static DeferredItem<Item> essenceMetalRegister(EssenceMetal essenceMetal, int state){
        return ITEMS.register(essenceMetal.getKeyWithPrefix(state),() -> new EssenceMetalItem(essenceMetal, state));
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

                // 普通金属
                output.accept(new ItemStack(ECLIPSIUM.get()));
                output.accept(new ItemStack(LUNARGENT.get()));
                output.accept(new ItemStack(ASTROTITE.get()));
                output.accept(new ItemStack(ABYSSION.get()));
                output.accept(new ItemStack(ANIMERCURY.get()));
                output.accept(new ItemStack(NECROPLUMB.get()));
                output.accept(new ItemStack(SANGUIBRONZE.get()));
                output.accept(new ItemStack(VENOTITE.get()));
                output.accept(new ItemStack(OSSANTIMONY.get()));
                output.accept(new ItemStack(FULGURZINC.get()));
                output.accept(new ItemStack(CHRONOPLATINUM.get()));
                output.accept(new ItemStack(PANDEMONIUM.get()));

                // 黑化金属
                output.accept(new ItemStack(NIGREDO_ECLIPSIUM.get()));
                output.accept(new ItemStack(NIGREDO_LUNARGENT.get()));
                output.accept(new ItemStack(NIGREDO_ASTROTITE.get()));
                output.accept(new ItemStack(NIGREDO_ABYSSION.get()));
                output.accept(new ItemStack(NIGREDO_ANIMERCURY.get()));
                output.accept(new ItemStack(NIGREDO_NECROPLUMB.get()));
                output.accept(new ItemStack(NIGREDO_SANGUIBRONZE.get()));
                output.accept(new ItemStack(NIGREDO_VENOTITE.get()));
                output.accept(new ItemStack(NIGREDO_OSSANTIMONY.get()));
                output.accept(new ItemStack(NIGREDO_FULGURZINC.get()));
                output.accept(new ItemStack(NIGREDO_CHRONOPLATINUM.get()));
                output.accept(new ItemStack(NIGREDO_PANDEMONIUM.get()));

                // 白化金属
                output.accept(new ItemStack(ALBEDO_ECLIPSIUM.get()));
                output.accept(new ItemStack(ALBEDO_LUNARGENT.get()));
                output.accept(new ItemStack(ALBEDO_ASTROTITE.get()));
                output.accept(new ItemStack(ALBEDO_ABYSSION.get()));
                output.accept(new ItemStack(ALBEDO_ANIMERCURY.get()));
                output.accept(new ItemStack(ALBEDO_NECROPLUMB.get()));
                output.accept(new ItemStack(ALBEDO_SANGUIBRONZE.get()));
                output.accept(new ItemStack(ALBEDO_VENOTITE.get()));
                output.accept(new ItemStack(ALBEDO_OSSANTIMONY.get()));
                output.accept(new ItemStack(ALBEDO_FULGURZINC.get()));
                output.accept(new ItemStack(ALBEDO_CHRONOPLATINUM.get()));
                output.accept(new ItemStack(ALBEDO_PANDEMONIUM.get()));

                // 黄化金属
                output.accept(new ItemStack(CITRINITAS_ECLIPSIUM.get()));
                output.accept(new ItemStack(CITRINITAS_LUNARGENT.get()));
                output.accept(new ItemStack(CITRINITAS_ASTROTITE.get()));
                output.accept(new ItemStack(CITRINITAS_ABYSSION.get()));
                output.accept(new ItemStack(CITRINITAS_ANIMERCURY.get()));
                output.accept(new ItemStack(CITRINITAS_NECROPLUMB.get()));
                output.accept(new ItemStack(CITRINITAS_SANGUIBRONZE.get()));
                output.accept(new ItemStack(CITRINITAS_VENOTITE.get()));
                output.accept(new ItemStack(CITRINITAS_OSSANTIMONY.get()));
                output.accept(new ItemStack(CITRINITAS_FULGURZINC.get()));
                output.accept(new ItemStack(CITRINITAS_CHRONOPLATINUM.get()));
                output.accept(new ItemStack(CITRINITAS_PANDEMONIUM.get()));

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

                // 卷轴 — 印记
                output.accept(new ItemStack(TRANSMUTATION_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(TERRESTRIAL_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(LUNAR_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(SOLAR_SIGIL_SCROLL.get()));
                output.accept(new ItemStack(VOID_SIGIL_SCROLL.get()));

                // 卷轴 — 方程
                output.accept(new ItemStack(TRANSMUTATION_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(TERRESTRIAL_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(LUNAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(SOLAR_EQUATION_SCROLL.get()));
                output.accept(new ItemStack(VOID_EQUATION_SCROLL.get()));

            }).build()
    );
}
