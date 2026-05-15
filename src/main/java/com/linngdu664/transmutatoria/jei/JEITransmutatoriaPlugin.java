package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitDatapacks;
import com.linngdu664.transmutatoria.recipe.AlchemicalReplicationRecipe;
import com.linngdu664.transmutatoria.recipe.AlchemicalTransformationRecipe;
import com.linngdu664.transmutatoria.recipe.CatalystShapelessRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;

@JeiPlugin
public class JEITransmutatoriaPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return ArsTransmutatoria.makeMyIdentifier("jei_plugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory()
                .addExtension(CatalystShapelessRecipe.class, new CatalystShapelessExtension());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlchemicalReplicationCategory());
        registration.addRecipeCategories(new AlchemicalTransformationCategory());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        RegistryAccess registryAccess = level.registryAccess();

        // 缓存
        HashMap<TagKey<Item>, ArrayList<Item>> tagToItems = new HashMap<>();
        HashMap<String, ArrayList<Item>> namespaceToItems = new HashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR) {
                item.builtInRegistryHolder().tags().forEach(tag -> tagToItems.computeIfAbsent(tag, _ -> new ArrayList<>()).add(item));
                namespaceToItems.computeIfAbsent(BuiltInRegistries.ITEM.getKey(item).getNamespace(),  _ -> new ArrayList<>()).add(item);
            }
        }

//        System.out.println(tagToItems);

        // 以下为炼金转化
        var transLookupOpt = registryAccess.lookup(InitDatapacks.ALCHEMICAL_TRANSFORMATION_KEY);
        if (transLookupOpt.isPresent()) {
            // 用 map 来进行配方覆盖和取消
            HashMap<Item, JEIAlchemicalTransformationDisplay> itemToTransDisplay = new HashMap<>();
            // 顺序遍历所有数据包配方
            for (AlchemicalTransformationRecipe recipe : transLookupOpt.get().listElements().map(Holder.Reference::value).toList()) {
                Item targetItem = BuiltInRegistries.ITEM.get(recipe.targetId()).map(Holder.Reference::value).orElse(Items.AIR);
                if (targetItem == Items.AIR) continue;
                switch (recipe.sourceType()) {
                    case ITEM -> {
                        Item sourceItem = BuiltInRegistries.ITEM.get(recipe.sourceId()).map(Holder.Reference::value).orElse(Items.AIR);
                        if (sourceItem != Items.AIR) {
                            if (recipe.isValid()) {
                                itemToTransDisplay.put(sourceItem, new JEIAlchemicalTransformationDisplay(recipe, sourceItem.getDefaultInstance(), targetItem.getDefaultInstance()));
                            } else {
                                itemToTransDisplay.remove(sourceItem);
                            }
                        }
                    }
                    case TAG -> {
                        TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), recipe.sourceId());
                        if (recipe.isValid()) {
                            for (Item item : tagToItems.getOrDefault(tag, new ArrayList<>())) {
                                itemToTransDisplay.put(item, new JEIAlchemicalTransformationDisplay(recipe, item.getDefaultInstance(), targetItem.getDefaultInstance()));
                            }
                        } else {
                            for (Item item : tagToItems.getOrDefault(tag, new ArrayList<>())) {
                                itemToTransDisplay.remove(item);
                            }
                        }
                    }
                }
            }

            // 注册展开后的列表
            registration.addRecipes(AlchemicalJeiTypes.ALCHEMICAL_TRANSFORMATION, itemToTransDisplay.values().stream().sorted().toList());
        }

        // 以下为炼金复制
        var repLookupOpt = registryAccess.lookup(InitDatapacks.ALCHEMICAL_REPLICATION_KEY);
        if (repLookupOpt.isPresent()) {
            // 用 map 来进行配方覆盖和取消
            HashMap<Item, JEIAlchemicalReplicationDisplay> itemToRepDisplay = new HashMap<>();
            // 顺序遍历所有数据包配方
            for (AlchemicalReplicationRecipe recipe : repLookupOpt.get().listElements().map(Holder.Reference::value).toList()) {
                switch (recipe.targetType()) {
                    case ITEM -> {
                        Item item = BuiltInRegistries.ITEM.get(recipe.targetId()).map(Holder.Reference::value).orElse(Items.AIR);
                        if (item != Items.AIR) {
                            if (recipe.isValid()) {
                                itemToRepDisplay.put(item, new JEIAlchemicalReplicationDisplay(recipe, item.getDefaultInstance()));
                            } else {
                                itemToRepDisplay.remove(item);
                            }
                        }
                    }
                    case TAG -> {
                        TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), recipe.targetId());
                        if (recipe.isValid()) {
                            for (Item item : tagToItems.getOrDefault(tag, new ArrayList<>())) {
                                itemToRepDisplay.put(item, new JEIAlchemicalReplicationDisplay(recipe, item.getDefaultInstance()));
                            }
                        } else {
                            for (Item item : tagToItems.getOrDefault(tag, new ArrayList<>())) {
                                itemToRepDisplay.remove(item);
                            }
                        }
                    }
                    case NAMESPACE -> {
                        String namespace = recipe.targetId().getNamespace();
                        if (recipe.isValid()) {
                            for (Item item : namespaceToItems.getOrDefault(namespace, new ArrayList<>())) {
                                itemToRepDisplay.put(item, new JEIAlchemicalReplicationDisplay(recipe, item.getDefaultInstance()));
                            }
                        } else {
                            for (Item item : namespaceToItems.getOrDefault(namespace, new ArrayList<>())) {
                                itemToRepDisplay.remove(item);
                            }
                        }
                    }
                }
            }

            // 注册展开后的列表
            registration.addRecipes(AlchemicalJeiTypes.ALCHEMICAL_REPLICATION, itemToRepDisplay.values().stream().sorted().toList());
        }
    }
}