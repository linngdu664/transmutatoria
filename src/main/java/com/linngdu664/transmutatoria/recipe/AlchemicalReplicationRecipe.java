package com.linngdu664.transmutatoria.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public record AlchemicalReplicationRecipe(
        TargetType targetType,
        Identifier targetId,
        boolean oneTime,
        int minLevel,
        int maxLevel,
        int minPolarity,
        int maxPolarity
) {
    // 定义 Codec 用于 JSON 解析
    public static final Codec<AlchemicalReplicationRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            TargetType.CODEC.fieldOf("target_type").forGetter(AlchemicalReplicationRecipe::targetType),
            Identifier.CODEC.fieldOf("target_id").forGetter(AlchemicalReplicationRecipe::targetId),
            Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalReplicationRecipe::oneTime),
            Codec.INT.optionalFieldOf("min_level", 2).forGetter(AlchemicalReplicationRecipe::minLevel),
            Codec.INT.optionalFieldOf("max_level", 2).forGetter(AlchemicalReplicationRecipe::maxLevel),
            Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalReplicationRecipe::minPolarity),
            Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalReplicationRecipe::maxPolarity)
    ).apply(inst, AlchemicalReplicationRecipe::new));

    // 判断某个物品是否匹配该规则
    public boolean matches(ItemStack stack) {
        return switch (targetType) {
            case ITEM -> stack.getItem().builtInRegistryHolder().key().identifier().equals(targetId);
            case TAG -> stack.is(TagKey.create(Registries.ITEM, targetId));
            case NAMESPACE -> stack.getItem().builtInRegistryHolder().key().identifier().getNamespace().equals(targetId.getNamespace());
        };
    }

    // 取消配方判断
    public boolean isValid() {
        if (minPolarity > maxPolarity) return false;
        return minLevel >= 2 && maxLevel >= 2 && minLevel <= 24 && maxLevel <= 24 && minLevel <= maxLevel;
    }

    // 目标类型的枚举
    public enum TargetType implements StringRepresentable {
        NAMESPACE("namespace"),
        TAG("tag"),
        ITEM("item");

        public final String serializedName;

        TargetType(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }

        public static final Codec<TargetType> CODEC = StringRepresentable.fromEnum(TargetType::values);
    }
}
