package com.linngdu664.transmutatoria.util;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.init.InitItems;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public enum EssenceMetal implements StringRepresentable {
    A("eclipsium", Textures.ECLIPSIUM),
    B("lunargent", Textures.LUNARGENT),
    C("astrotite",  Textures.ASTROTITE),

    D("abyssion", Textures.ABYSSION),
    E("animercury", Textures.ANIMERCURY),
    F("necroplumb", Textures.NECROPLUMB),

    G("sanguibronze", Textures.SANGUIBRONZE),
    H("venotite", Textures.VENOTITE),
    I("ossantimony", Textures.OSSANTIMONY),

    J("fulgurzinc", Textures.FULGURZINC),
    K("chronoplatinum", Textures.CHRONOPLATINUM),
    L("pandemonium", Textures.PANDEMONIUM),;

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
    private final TextureRenderable defaultTexture;
    private Set<EssenceMetal> restrains = Set.of();
    private Set<EssenceMetal> doubleRestrains = Set.of();
    private Set<EssenceMetal> symbiosisWith = Set.of();

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
        F.doubleRestrains = EnumSet.of(A);
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

    EssenceMetal(String key, TextureRenderable defaultTexture) {
        this.key = key;
        this.defaultTexture = defaultTexture;
    }

    public Relation getRelationTo(EssenceMetal other) {
        if (this == other) return Relation.SAME;//相同
        if (this.symbiosisWith.contains(other)) return Relation.SYMBIOSIS;//相生
        if (this.restrains.contains(other)) return other.restrains.contains(this) ? Relation.MUTUAL_RESTRAINED : Relation.RESTRAIN;//克制或互克
        if (this.doubleRestrains.contains(other)) return Relation.DOUBLE_RESTRAIN;//双倍克制
        if (other.restrains.contains(this)) return this.restrains.contains(other) ? Relation.MUTUAL_RESTRAINED : Relation.BE_RESTRAINED;//被克制或互克
        if (other.doubleRestrains.contains(this)) return Relation.DOUBLE_BE_RESTRAINED;//双倍被克制
        return Relation.NEUTRAL;//无关
    }

    public Set<EssenceMetal> getRestrainsAndDoubleRestrains() {
        EnumSet<EssenceMetal> merged = EnumSet.copyOf(restrains);
        merged.addAll(doubleRestrains);
        return merged;
    }

    public ItemStack getItemStack(int state) {
        if (state < -1) {
            return InitItems.ALCHEMICAL_DROSS.toStack();
        }
        if (state > 2) {
            return Items.REDSTONE.getDefaultInstance();
        }
        return new ItemStack(BuiltInRegistries.ITEM.getValue(ArsTransmutatoria.makeMyIdentifier(getKeyWithPrefix(state))));
    }

    @Override
    public String getSerializedName() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public TextureRenderable getDefaultTexture() {
        return defaultTexture;
    }

    public String getKeyWithPrefix(int state) {
        return switch (state) {
            case -1 -> "nigredo_" + key;
            case 1 -> "albedo_" + key;
            case 2 -> "citrinitas_" + key;
            default -> key;
        };
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
}
