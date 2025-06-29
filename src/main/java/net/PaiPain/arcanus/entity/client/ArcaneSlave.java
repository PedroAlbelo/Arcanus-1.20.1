package net.PaiPain.arcanus.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.PaiPain.arcanus.entity.custom.ArcaneSlaveEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ArcaneSlave<T extends Entity> extends HierarchicalModel<T> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("arcanus", "arcane_slave"), "main");

	private final ModelPart arcane_slave;
	private final ModelPart body;
	private final ModelPart right_leg;
	private final ModelPart left_arm;
	private final ModelPart torso;
	private final ModelPart right_arm;
	private final ModelPart left_leg;
	private final ModelPart head;

	public ArcaneSlave(ModelPart root) {
		this.arcane_slave = root.getChild("arcane_slave");
		this.body = this.arcane_slave.getChild("body");
		this.right_leg = this.body.getChild("right_leg");
		this.left_arm = this.body.getChild("left_arm");
		this.torso = this.body.getChild("torso");
		this.right_arm = this.body.getChild("right_arm");
		this.left_leg = this.body.getChild("left_leg");
		this.head = this.body.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartDefinition arcane_slave = root.addOrReplaceChild("arcane_slave", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition body = arcane_slave.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 20).addBox(-1.0F, -2.0F, -1.0F, 2, 9, 2), PartPose.offset(-2.0F, -7.0F, 0.0F));
		body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(8, 32).addBox(-1.0F, -2.0F, -1.0F, 2, 9, 2), PartPose.offset(2.0F, -7.0F, 0.0F));

		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(24, 31).addBox(0, -1, -1, 2, 9, 2), PartPose.offset(5.0F, -17.0F, 0.0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 32).addBox(-2, -1, -1, 2, 9, 2), PartPose.offset(-5.0F, -17.0F, 0.0F));

		body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-5, -19, -4, 10, 12, 8), PartPose.offset(0.0F, 0.0F, 0.0F));

		body.addOrReplaceChild("head", CubeListBuilder.create()
						.texOffs(0, 20).addBox(-3, -3, -6, 6, 6, 6)
						.texOffs(16, 32).addBox(-1, 0, -7, 2, 2, 2),
				PartPose.offset(0.0F, -18.0F, -3.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);

		// Default positions
		this.head.y = -18.0f;
		this.torso.y = 0.0f;

		if (entity instanceof ArcaneSlaveEntity slave) {
			ArcaneSlaveEntity.Mode mode = slave.getMode();

			if (mode == ArcaneSlaveEntity.Mode.MINE) {
				float cycle = (ageInTicks / 36f) % 1f; // ciclo bem lento e contínuo
				float armRot = (float) Math.toRadians(Mth.sin(cycle * Mth.TWO_PI) * 90f); // valor mais contido que 137.5

				this.left_arm.xRot = -armRot;
				this.right_arm.xRot = -armRot;

				// Head "pulando" levemente, suavizado
				this.head.y += Mth.sin(cycle * Mth.TWO_PI * 2f) * 0.5f;

				// Torso "pulsando" suavemente
				this.torso.y += Mth.sin(cycle * Mth.TWO_PI) * 0.4f;
				return;
			}


			if (mode == ArcaneSlaveEntity.Mode.ATTACK) {
				float cycle = (ageInTicks / 36f) % 1f; // Ciclo bem lento e contínuo
				float armRot = (float) Math.toRadians(Mth.sin(cycle * Mth.TWO_PI) * 120f); // reduzido para evitar "modo Naruto"
				this.left_arm.xRot = -armRot;
				this.right_arm.xRot = -armRot;

				this.head.xRot += Mth.sin(cycle * Mth.TWO_PI) * (float) Math.toRadians(20f);
				this.torso.y += Mth.sin(cycle * Mth.TWO_PI) * 0.3f;
				return;
			}

		}

		// Walking animation
		this.left_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.right_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

		// Idle breathing
		this.torso.y += Mth.sin(ageInTicks * 0.25f) * 0.5f;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		arcane_slave.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return arcane_slave;
	}
}
