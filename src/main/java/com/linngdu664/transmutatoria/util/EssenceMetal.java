package com.linngdu664.transmutatoria.util;

import com.linngdu664.transmutatoria.init.InitItems;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum EssenceMetal implements StringRepresentable {
    A("eclipsium", new DeferredItem[]{InitItems.NIGREDO_TAINTED_ECLIPSIUM, InitItems.ECLIPSIUM, InitItems.ALBEDO_INFUSED_ECLIPSIUM, InitItems.CITRINITAS_INFUSED_ECLIPSIUM}),
    B("lunargent", new DeferredItem[]{InitItems.NIGREDO_TAINTED_LUNARGENT, InitItems.LUNARGENT, InitItems.ALBEDO_INFUSED_LUNARGENT, InitItems.CITRINITAS_INFUSED_LUNARGENT}),
    C("astrotite", new DeferredItem[]{InitItems.NIGREDO_TAINTED_ASTROTITE, InitItems.ASTROTITE, InitItems.ALBEDO_INFUSED_ASTROTITE, InitItems.CITRINITAS_INFUSED_ASTROTITE}),

    D("abyssion", new DeferredItem[]{InitItems.NIGREDO_TAINTED_ABYSSION, InitItems.ABYSSION, InitItems.ALBEDO_INFUSED_ABYSSION, InitItems.CITRINITAS_INFUSED_ABYSSION}),
    E("animercury", new DeferredItem[]{InitItems.NIGREDO_TAINTED_ANIMERCURY, InitItems.ANIMERCURY, InitItems.ALBEDO_INFUSED_ANIMERCURY, InitItems.CITRINITAS_INFUSED_ANIMERCURY}),
    F("necroplumb", new DeferredItem[]{InitItems.NIGREDO_TAINTED_NECROPLUMB, InitItems.NECROPLUMB, InitItems.ALBEDO_INFUSED_NECROPLUMB, InitItems.CITRINITAS_INFUSED_NECROPLUMB}),

    G("sanguibronze", new DeferredItem[]{InitItems.NIGREDO_TAINTED_SANGUIBRONZE, InitItems.SANGUIBRONZE, InitItems.ALBEDO_INFUSED_SANGUIBRONZE, InitItems.CITRINITAS_INFUSED_SANGUIBRONZE}),
    H("venotite", new DeferredItem[]{InitItems.NIGREDO_TAINTED_VENOTITE, InitItems.VENOTITE, InitItems.ALBEDO_INFUSED_VENOTITE, InitItems.CITRINITAS_INFUSED_VENOTITE}),
    I("ossantimony", new DeferredItem[]{InitItems.NIGREDO_TAINTED_OSSANTIMONY, InitItems.OSSANTIMONY, InitItems.ALBEDO_INFUSED_OSSANTIMONY, InitItems.CITRINITAS_INFUSED_OSSANTIMONY}),

    J("fulgurzinc", new DeferredItem[]{InitItems.NIGREDO_TAINTED_FULGURZINC, InitItems.FULGURZINC, InitItems.ALBEDO_INFUSED_FULGURZINC, InitItems.CITRINITAS_INFUSED_FULGURZINC}),
    K("chronoplatinum", new DeferredItem[]{InitItems.NIGREDO_TAINTED_CHRONOPLATINUM, InitItems.CHRONOPLATINUM, InitItems.ALBEDO_INFUSED_CHRONOPLATINUM, InitItems.CITRINITAS_INFUSED_CHRONOPLATINUM}),
    L("pandemonium", new DeferredItem[]{InitItems.NIGREDO_TAINTED_PANDEMONIUM, InitItems.PANDEMONIUM, InitItems.ALBEDO_INFUSED_PANDEMONIUM, InitItems.CITRINITAS_INFUSED_PANDEMONIUM});

    public static final Codec<EssenceMetal> CODEC = StringRepresentable.fromEnum(EssenceMetal::values);
    public static final Codec<List<EssenceMetal>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<FriendlyByteBuf, EssenceMetal> STREAM_CODEC = StreamCodec.of(
            // 编码器 (写入网络数据)
            FriendlyByteBuf::writeEnum,
            // 解码器 (读取网络数据)
            (buf) -> buf.readEnum(EssenceMetal.class)
    );
    public static final StreamCodec<FriendlyByteBuf, List<EssenceMetal>> LIST_STREAM_CODEC = StreamCodec.of(
            // 编码器：写入集合
            (buf, list) -> buf.writeCollection(list, EssenceMetal.STREAM_CODEC),
            // 解码器：读取列表
            (buf) -> buf.readList(EssenceMetal.STREAM_CODEC)
    );

    private final String key;
    private final DeferredItem[] stateItems;
    private Set<EssenceMetal> restrains;
    private Set<EssenceMetal> double_restrains;
    private Set<EssenceMetal> symbiosisWith;
    // 静态初始化逻辑
    static {
        A.restrains = EnumSet.of(J,K);
        B.restrains = EnumSet.of(J,K);
        B.symbiosisWith = EnumSet.of(H);
        C.restrains = EnumSet.of(J,K);
        D.restrains = EnumSet.of(A,B,C,E);
        D.symbiosisWith = EnumSet.of(J);
        E.restrains = EnumSet.of(A,B,C);
        F.restrains = EnumSet.of(B,C,I);
        F.double_restrains = EnumSet.of(A);
        G.restrains = EnumSet.of(D,E,F,B);
        G.symbiosisWith = EnumSet.of(I);
        H.restrains = EnumSet.of(D,E,F,G);
        H.symbiosisWith = EnumSet.of(B);
        I.restrains = EnumSet.of(D,E,F,H);
        I.symbiosisWith = EnumSet.of(G);
        J.restrains = EnumSet.of(G,H,I);
        J.symbiosisWith = EnumSet.of(D);
        K.restrains = EnumSet.of(G,H,I,C);
        L.restrains = EnumSet.of(A,B,C,D,E,F,G,H,I,J,K);
    }

    EssenceMetal(String key, DeferredItem[] stateItems) {
        this.key = key;
        this.stateItems = stateItems;
    }

    public Relation getRelationTo(EssenceMetal other) {
        if (this.equals(other)) return Relation.SAME;//相同
        if (this.symbiosisWith.contains(other)) return Relation.SYMBIOSIS;//相生
        if (this.restrains.contains(other)) return other.restrains.contains(this) ? Relation.MUTUAL_RESTRAINED : Relation.RESTRAIN;//克制或互克
        if (this.double_restrains.contains(other)) return Relation.DOUBLE_RESTRAIN;//双倍克制
        if (other.restrains.contains(this)) return this.restrains.contains(other) ? Relation.MUTUAL_RESTRAINED : Relation.BE_RESTRAINED;//被克制或互克
        if (other.double_restrains.contains(this)) return Relation.DOUBLE_BE_RESTRAINED;//双倍被克制
        return Relation.NEUTRAL;//无关
    }

    public ItemStack getItemStack(int state) {
        if (state < -1) {
            return InitItems.ALCHEMICAL_DROSS.toStack();
        }
        if (state > 2) {
            return Items.REDSTONE.getDefaultInstance();
        }
        return stateItems[state + 1].toStack();
    }

    @Override
    public String getSerializedName() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public enum Relation {
        SYMBIOSIS(1,1),
        RESTRAIN(1,-1),
        DOUBLE_RESTRAIN(2,-2),
        BE_RESTRAINED(-1,1),
        DOUBLE_BE_RESTRAINED(-2,2),
        MUTUAL_RESTRAINED(-1,-1),
        NEUTRAL(0,0),
        SAME(0,0);
        public final int self;
        public final int other;
        Relation(int self, int other) {
            this.self = self;
            this.other = other;
        }
    }

    public String getKeyWithPrefix(int state) {
        return switch (state) {
            case -1 -> "nigredo_tainted_" + key;
            case 1 -> "albedo_infused_" + key;
            case 2 -> "citrinitas_infused_" + key;
            default -> key;
        };
    }
}
