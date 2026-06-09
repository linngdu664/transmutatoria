// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

package com.linngdu664.transmutatoria.client.model;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class AlchemistStorageBoxModel extends Model<Float> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final Identifier TEXTURE_LOCATION = ArsTransmutatoria.makeMyIdentifier("textures/item/model/alchemist_storage_box.png");
	public static final Identifier NIGREDO_TEXTURE_LOCATION = ArsTransmutatoria.makeMyIdentifier("textures/item/model/nigredo_alchemist_storage_box.png");
	public static final Identifier ALBEDO_TEXTURE_LOCATION = ArsTransmutatoria.makeMyIdentifier("textures/item/model/albedo_alchemist_storage_box.png");
	public static final Identifier CITRINITAS_TEXTURE_LOCATION = ArsTransmutatoria.makeMyIdentifier("textures/item/model/citrinitas_alchemist_storage_box.png");
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TEXTURE_LOCATION, "main");
	private final ModelPart cover;
	private final ModelPart body;

	public AlchemistStorageBoxModel(ModelPart root) {
		super(root, RenderTypes::entityCutout);
		this.cover = root.getChild("cover");
		this.body = root.getChild("body");
	}

	public ModelPart getCover() {
		return cover;
	}

	public ModelPart getBody() {
		return body;
	}

	public static Identifier getTextureLocation(int boxState) {
		return switch (boxState) {
			case -1 -> NIGREDO_TEXTURE_LOCATION;
			case 1 -> ALBEDO_TEXTURE_LOCATION;
			case 2 -> CITRINITAS_TEXTURE_LOCATION;
			default -> TEXTURE_LOCATION;
		};
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition cover = partdefinition.addOrReplaceChild("cover", CubeListBuilder.create().texOffs(0, 11).addBox(-6.0F, -2.0F, 0.0F, 12.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(6, 23).addBox(4.1F, -2.1F, 7.1F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 24).addBox(4.1F, -2.1F, -0.1F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 21).addBox(-6.1F, -2.1F, -0.1F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(-6.1F, -2.1F, 0.9F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 27).addBox(-6.1F, -1.1F, -0.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(20, 25).addBox(5.1F, -2.1F, 0.9F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 25).addBox(5.1F, -1.1F, -0.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 23).addBox(-6.1F, -2.1F, 7.1F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 25).addBox(-6.1F, -2.1F, 6.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 28).addBox(-6.1F, -1.1F, 7.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 25).addBox(5.1F, -2.1F, 6.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 27).addBox(5.1F, -1.1F, 7.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 21).addBox(-1.0F, -1.3F, 7.6F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 21.0F, -4.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -3.0F, -4.0F, 12.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(18, 23).addBox(4.1F, -0.9F, 3.1F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 25).addBox(-6.1F, -1.9F, -4.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 29).addBox(5.1F, -1.9F, -4.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 25).addBox(4.1F, -0.9F, -4.1F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 29).addBox(5.1F, -0.9F, -3.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 23).addBox(-6.1F, -0.9F, -4.1F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 27).addBox(-6.1F, -0.9F, -3.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 27).addBox(5.1F, -0.9F, 2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 27).addBox(5.1F, -1.9F, 3.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 27).addBox(-6.1F, -1.9F, 3.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(18, 21).addBox(-6.1F, -0.9F, 3.1F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(20, 27).addBox(-6.1F, -0.9F, 2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 21).addBox(-5.0F, -3.5F, -4.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.2F))
		.texOffs(12, 21).addBox(3.0F, -3.5F, -4.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Float openness) {
		super.setupAnim(openness);
		cover.xRot = openness * Mth.HALF_PI;
	}
}
