package com.linngdu664.transmutatoria.item;

import java.util.EnumSet;
import java.util.Set;

public enum EssenceMetal {
    A("eclipsium"),
    B("lunargent"),
    C("astrotite"),

    D("abyssion"),
    E("animercury"),
    F("necroplumb"),

    G("sanguibronze"),
    H("venotite"),
    I("ossantimony"),

    J("fulgurzinc"),
    K("chronoplatinum"),
    L("pandemonium");
    private final String key;

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
    public Relation getRelationTo(EssenceMetal other) {
        if (this.equals(other)) return Relation.SAME;//相同
        if (this.symbiosisWith.contains(other)) return Relation.SYMBIOSIS;//相生
        if (this.restrains.contains(other)) return other.restrains.contains(this)?Relation.MUTUAL_RESTRAINED:Relation.RESTRAIN;//克制或互克
        if (this.double_restrains.contains(other)) return Relation.DOUBLE_RESTRAIN;//双倍克制
        if (other.restrains.contains(this)) return this.restrains.contains(other)?Relation.MUTUAL_RESTRAINED:Relation.BE_RESTRAINED;//被克制或互克
        if (other.double_restrains.contains(this)) return Relation.DOUBLE_BE_RESTRAINED;//双倍被克制
        return Relation.NEUTRAL;//无关
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

    EssenceMetal(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
    public String getKeyWithPrefix(int state) {
        return switch (state) {
            case -1 -> "nigredo_tainted_" + key;
            case 1 -> "albedo_infused_" + key;
            case 2 -> "citrinitas_infused" + key;
            default -> key;
        };
    }
}
