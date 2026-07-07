package com.linngdu664.transmutatoria.recipe.generator;

import com.google.gson.*;
import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitRecipes;
import com.linngdu664.transmutatoria.inventory.AlchemyRecipeGeneratorMenu;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Optional;

public final class AlchemyRecipeGenerator {
    public static final String PACK_FOLDER = "transmutatoria_generated_recipes";
    public static final String PACK_ID = "file/" + PACK_FOLDER;
    private static final int GENERATED_RECIPE_PRIORITY = 1000;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private AlchemyRecipeGenerator() {
    }

    public static SaveResult save(
            MinecraftServer server,
            AlchemyRecipeGeneratorMenu.Kind kind,
            ItemStack input,
            ItemStack output,
            int minEp,
            int maxEp,
            int minLevel,
            int maxLevel,
            boolean oneTime,
            @Nullable Identifier ingredientTag
    ) throws IOException {
        validate(kind, input, output, minEp, maxEp, minLevel, maxLevel, ingredientTag);

        ItemStack normalizedInput = input.isEmpty() ? ItemStack.EMPTY : input.copyWithCount(1);
        ItemStack normalizedOutput = output.copyWithCount(1);
        Ingredient ingredient = createIngredient(server, kind == AlchemyRecipeGeneratorMenu.Kind.REPLICATION
                ? normalizedOutput : normalizedInput, ingredientTag);
        FixedLevel level = new FixedLevel(minLevel, maxLevel);
        Recipe<?> recipe;
        if (kind == AlchemyRecipeGeneratorMenu.Kind.REPLICATION) {
            Optional<ItemStackTemplate> recipeInput = normalizedInput.isEmpty()
                    ? Optional.empty()
                    : Optional.of(template(normalizedInput));
            recipe = new AlchemicalReplicationRecipe(recipeInput, ingredient, oneTime, level, minEp, maxEp);
        } else {
            recipe = new AlchemicalTransformationRecipe(ingredient, template(normalizedOutput), oneTime, level, minEp, maxEp);
        }

        Identifier recipeId = recipeId(kind, normalizedOutput, ingredientTag);
        Path packRoot = server.getWorldPath(LevelResource.DATAPACK_DIR).resolve(PACK_FOLDER).normalize();
        ensurePackMetadata(packRoot);
        Path recipeFile = packRoot.resolve("data")
                .resolve(ArsTransmutatoria.MODID)
                .resolve("recipe")
                .resolve(recipeId.getPath() + ".json")
                .normalize();
        if (!recipeFile.startsWith(packRoot)) {
            throw new IOException("Generated recipe path escaped the generated datapack");
        }

        boolean overwroteFile = Files.exists(recipeFile);
        boolean relatedRecipeExists = hasRelatedRecipe(server, kind,
                kind == AlchemyRecipeGeneratorMenu.Kind.REPLICATION ? normalizedOutput : normalizedInput);
        JsonElement recipeJson = Recipe.CODEC.encodeStart(
                server.registryAccess().createSerializationContext(JsonOps.INSTANCE), recipe
        ).getOrThrow(IllegalArgumentException::new);
        writeJsonAtomically(recipeFile, recipeJson);
        updateRecipePriorities(packRoot, recipeId);
        return new SaveResult(recipeId, recipeFile, overwroteFile, relatedRecipeExists);
    }

    public static Identifier recipeId(
            AlchemyRecipeGeneratorMenu.Kind kind,
            ItemStack output,
            @Nullable Identifier ingredientTag
    ) {
        Identifier namingId;
        String prefix;
        if (kind == AlchemyRecipeGeneratorMenu.Kind.REPLICATION) {
            namingId = ingredientTag != null
                    ? ingredientTag
                    : BuiltInRegistries.ITEM.getKey(output.getItem());
            prefix = "alchemical_replication/";
        } else {
            namingId = BuiltInRegistries.ITEM.getKey(output.getItem());
            prefix = "alchemical_transformation/";
        }
        return ArsTransmutatoria.makeMyIdentifier(prefix + namingId.getNamespace() + "/" + namingId.getPath());
    }

    private static void validate(
            AlchemyRecipeGeneratorMenu.Kind kind,
            ItemStack input,
            ItemStack output,
            int minEp,
            int maxEp,
            int minLevel,
            int maxLevel,
            @Nullable Identifier ingredientTag
    ) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Output item is required");
        }
        if (kind == AlchemyRecipeGeneratorMenu.Kind.TRANSFORMATION && input.isEmpty()) {
            throw new IllegalArgumentException("Transformation input item is required");
        }
        if (minEp < -50 || maxEp > 50 || minEp > maxEp) {
            throw new IllegalArgumentException("EP range must be within -50..50 and min must not exceed max");
        }
        if (!new FixedLevel(minLevel, maxLevel).isValid()) {
            throw new IllegalArgumentException("Alchemy level range must be within 2..24 and min must not exceed max");
        }
        if (ingredientTag != null) {
            ItemStack tagStack = kind == AlchemyRecipeGeneratorMenu.Kind.REPLICATION ? output : input;
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ingredientTag);
            if (!tagStack.is(tagKey)) {
                throw new IllegalArgumentException("Selected item is not in tag #" + ingredientTag);
            }
        }
    }

    private static Ingredient createIngredient(MinecraftServer server, ItemStack stack, @Nullable Identifier tagId) {
        if (tagId == null) {
            return Ingredient.of(stack.getItem());
        }
        TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
        return Ingredient.of(server.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(tag));
    }

    private static ItemStackTemplate template(ItemStack stack) {
        return new ItemStackTemplate(stack.getItem(), stack.getComponentsPatch());
    }

    private static boolean hasRelatedRecipe(
            MinecraftServer server,
            AlchemyRecipeGeneratorMenu.Kind kind,
            ItemStack stack
    ) {
        if (kind == AlchemyRecipeGeneratorMenu.Kind.REPLICATION) {
            return hasMatch(server, InitRecipes.ALCHEMICAL_REPLICATION_PRECISE_TYPE.get(), stack)
                    || hasMatch(server, InitRecipes.ALCHEMICAL_REPLICATION_TYPE.get(), stack);
        }
        return hasMatch(server, InitRecipes.ALCHEMICAL_TRANSFORMATION_PRECISE_TYPE.get(), stack)
                || hasMatch(server, InitRecipes.ALCHEMICAL_TRANSFORMATION_TYPE.get(), stack);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean hasMatch(MinecraftServer server, RecipeType<?> type, ItemStack stack) {
        Collection<RecipeHolder<?>> recipes = (Collection) server.getRecipeManager().recipeMap().byType((RecipeType) type);
        for (RecipeHolder<?> holder : recipes) {
            if (((CrucibleRecipe) holder.value()).matches(stack)) {
                return true;
            }
        }
        return false;
    }

    private static void ensurePackMetadata(Path packRoot) throws IOException {
        Files.createDirectories(packRoot.resolve("data"));
        Path metadataFile = packRoot.resolve("pack.mcmeta");
        if (Files.exists(metadataFile)) {
            return;
        }
        PackMetadataSection metadata = new PackMetadataSection(
                Component.literal("Recipes generated by Ars Transmutatoria"),
                SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).minorRange()
        );
        JsonObject root = new JsonObject();
        root.add(PackMetadataSection.SERVER_TYPE.name(),
                PackMetadataSection.SERVER_TYPE.codec().encodeStart(JsonOps.INSTANCE, metadata).getOrThrow());
        writeJsonAtomically(metadataFile, root);
    }

    private static void updateRecipePriorities(Path packRoot, Identifier recipeId) throws IOException {
        Path priorityFile = packRoot.resolve("data/neoforge/recipe_priorities.json");
        JsonObject root;
        if (Files.exists(priorityFile)) {
            try (var reader = Files.newBufferedReader(priorityFile, StandardCharsets.UTF_8)) {
                root = JsonParser.parseReader(reader).getAsJsonObject();
            }
        } else {
            root = new JsonObject();
            root.addProperty("replace", false);
            root.add("entries", new JsonObject());
        }
        JsonObject entries = root.has("entries") && root.get("entries").isJsonObject()
                ? root.getAsJsonObject("entries")
                : new JsonObject();
        root.add("entries", entries);
        entries.addProperty(recipeId.toString(), GENERATED_RECIPE_PRIORITY);
        writeJsonAtomically(priorityFile, root);
    }

    private static void writeJsonAtomically(Path target, JsonElement json) throws IOException {
        Files.createDirectories(target.getParent());
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(temporary, GSON.toJson(json) + System.lineSeparator(), StandardCharsets.UTF_8);
        try {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public record SaveResult(
            Identifier recipeId,
            Path file,
            boolean overwroteFile,
            boolean relatedRecipeExisted
    ) {
    }
}
