package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.recipe.AlchemicalReplicationRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * @param recipe      原始的数据包配方规则（包含等级、极性等信息）
 * @param displayItem 这个展示页面对应的具体物品（从 Tag/Namespace 中拆分出来的某一个）
 */
public record JEIAlchemicalReplicationDisplay(AlchemicalReplicationRecipe recipe, ItemStack displayItem) implements Comparable<JEIAlchemicalReplicationDisplay> {
    @Override
    public int compareTo(@NonNull JEIAlchemicalReplicationDisplay o) {
        return Item.getId(displayItem.getItem()) - Item.getId(o.displayItem.getItem());
    }
}
