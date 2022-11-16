package com.github.audio.item.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class AudioWrenchItemStackTileEntityRenderer implements IBakedModel {

    private IBakedModel existingModel;

    public AudioWrenchItemStackTileEntityRenderer(IBakedModel existingModel) {
        this.existingModel = existingModel;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return this.existingModel.getQuads(state , side , rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.existingModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.existingModel.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return this.existingModel.isSideLit();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.existingModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.existingModel.getOverrides();
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            return this;
        }
        return this.existingModel.handlePerspective(cameraTransformType, mat);
    }
}
