package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitDatapacks;
import com.linngdu664.transmutatoria.recipe.CatalystShapelessRecipe;
import com.linngdu664.transmutatoria.util.SafeInstance;
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
        Level level = SafeInstance.getMC().level;
        if (level == null) return;

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

        RegistryAccess registryAccess = level.registryAccess();
        // 以下为炼金转化
        registryAccess.lookup(InitDatapacks.ALCHEMICAL_TRANSFORMATION_KEY).ifPresent(transLookup -> {
            // 用 map 来进行配方覆盖和取消
            HashMap<Item, JEIAlchemicalTransformationDisplay> itemToTransDisplay = new HashMap<>();
            // 顺序遍历所有数据包配方
            transLookup.listElements().map(Holder.Reference::value).forEach(recipe -> {
                Item outputItem = BuiltInRegistries.ITEM.getValue(recipe.outputId());
                // 不允许输出空气
                if (outputItem != Items.AIR) {
                    switch (recipe.inputType()) {
                        case ITEM -> {
                            Item item = BuiltInRegistries.ITEM.getValue(recipe.inputId());
                            if (item != Items.AIR) {
                                if (recipe.isValid()) {
                                    itemToTransDisplay.put(item, new JEIAlchemicalTransformationDisplay(recipe, item.getDefaultInstance(), outputItem.getDefaultInstance()));
                                } else {
                                    itemToTransDisplay.remove(item);
                                }
                            }
                        }
                        case TAG -> {
                            TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), recipe.inputId());
                            if (recipe.isValid()) {
                                for (Item item : tagToItems.getOrDefault(tag, new ArrayList<>())) {
                                    itemToTransDisplay.put(item, new JEIAlchemicalTransformationDisplay(recipe, item.getDefaultInstance(), outputItem.getDefaultInstance()));
                                }
                            } else {
                                for (Item item : tagToItems.getOrDefault(tag, new ArrayList<>())) {
                                    itemToTransDisplay.remove(item);
                                }
                            }
                        }
                        case NAMESPACE -> {
                            String namespace = recipe.inputId().getNamespace();
                            if (recipe.isValid()) {
                                for (Item item : namespaceToItems.getOrDefault(namespace, new ArrayList<>())) {
                                    itemToTransDisplay.put(item, new JEIAlchemicalTransformationDisplay(recipe, item.getDefaultInstance(), outputItem.getDefaultInstance()));
                                }
                            } else {
                                for (Item item : namespaceToItems.getOrDefault(namespace, new ArrayList<>())) {
                                    itemToTransDisplay.remove(item);
                                }
                            }
                        }
                    }
                }
            });
            // 注册展开后的列表
            registration.addRecipes(AlchemicalJeiTypes.ALCHEMICAL_TRANSFORMATION, itemToTransDisplay.values().stream().sorted().toList());
        });

        // 以下为炼金复制
        registryAccess.lookup(InitDatapacks.ALCHEMICAL_REPLICATION_KEY).ifPresent(repLookup -> {
            // 用 map 来进行配方覆盖和取消
            HashMap<Item, JEIAlchemicalReplicationDisplay> itemToRepDisplay = new HashMap<>();
            // 顺序遍历所有数据包配方
            repLookup.listElements().map(Holder.Reference::value).forEach(recipe -> {
                Item inputItem = BuiltInRegistries.ITEM.getValue(recipe.inputId());
                // 炼金复制允许输入空气
                switch (recipe.outputType()) {
                    case ITEM -> {
                        Item item = BuiltInRegistries.ITEM.getValue(recipe.outputId());
                        if (item != Items.AIR) {
                            if (recipe.isValid()) {
                                itemToRepDisplay.put(item, new JEIAlchemicalReplicationDisplay(recipe, inputItem.getDefaultInstance(), item.getDefaultInstance()));
                            } else {
                                itemToRepDisplay.remove(item);
                            }
                        }
                    }
                    case TAG -> {
                        TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), recipe.outputId());
                        if (recipe.isValid()) {
                            for (Item item : tagToItems.getOrDefault(tag, new ArrayList<>())) {
                                itemToRepDisplay.put(item, new JEIAlchemicalReplicationDisplay(recipe, inputItem.getDefaultInstance(), item.getDefaultInstance()));
                            }
                        } else {
                            for (Item item : tagToItems.getOrDefault(tag, new ArrayList<>())) {
                                itemToRepDisplay.remove(item);
                            }
                        }
                    }
                    case NAMESPACE -> {
                        String namespace = recipe.outputId().getNamespace();
                        if (recipe.isValid()) {
                            for (Item item : namespaceToItems.getOrDefault(namespace, new ArrayList<>())) {
                                itemToRepDisplay.put(item, new JEIAlchemicalReplicationDisplay(recipe, inputItem.getDefaultInstance(), item.getDefaultInstance()));
                            }
                        } else {
                            for (Item item : namespaceToItems.getOrDefault(namespace, new ArrayList<>())) {
                                itemToRepDisplay.remove(item);
                            }
                        }
                    }
                }
            });
            // 注册展开后的列表
            registration.addRecipes(AlchemicalJeiTypes.ALCHEMICAL_REPLICATION, itemToRepDisplay.values().stream().sorted().toList());
        });
    }
}