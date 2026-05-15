package com.linngdu664.transmutatoria.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public record AlchemicalTransformationRecipe(
        SourceType sourceType,
        Identifier sourceId,
        Identifier targetId,
        boolean oneTime,
        int minLevel,
        int maxLevel,
        int minPolarity,
        int maxPolarity
) {
    // 定义 Codec 用于 JSON 解析
    public static final Codec<AlchemicalTransformationRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SourceType.CODEC.fieldOf("source_type").forGetter(AlchemicalTransformationRecipe::sourceType),
            Identifier.CODEC.fieldOf("source_id").forGetter(AlchemicalTransformationRecipe::targetId),
            Identifier.CODEC.fieldOf("target_id").forGetter(AlchemicalTransformationRecipe::targetId),
            Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalTransformationRecipe::oneTime),
            Codec.INT.optionalFieldOf("min_level", 2).forGetter(AlchemicalTransformationRecipe::minLevel),
            Codec.INT.optionalFieldOf("max_level", 2).forGetter(AlchemicalTransformationRecipe::maxLevel),
            Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalTransformationRecipe::minPolarity),
            Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalTransformationRecipe::maxPolarity)
    ).apply(inst, AlchemicalTransformationRecipe::new));

    // 判断某个物品是否匹配该规则
    public boolean matches(ItemStack stack) {
        return switch (sourceType) {
            case ITEM -> stack.getItem().builtInRegistryHolder().key().identifier().equals(targetId);
            case TAG -> stack.is(TagKey.create(Registries.ITEM, targetId));
        };
    }

    // 取消配方判断
    public boolean isValid() {
        if (minPolarity > maxPolarity) return false;
        return minLevel >= 2 && maxLevel >= 2 && minLevel <= 24 && maxLevel <= 24 && minLevel <= maxLevel;
    }

    // 源类型的枚举
    public enum SourceType implements StringRepresentable {
        TAG("tag"),
        ITEM("item");

        public final String serializedName;

        SourceType(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }

        public static final Codec<SourceType> CODEC = StringRepresentable.fromEnum(SourceType::values);
    }
}
