/*
 * Copyright 2016, 2017, 2018, 2019 FabricMC
 * Copyright 2022 The Quilt Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.client.rendering;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

@Mixin(BuiltinModelItemRenderer.class)
abstract class BuiltinModelItemRendererMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void fabric_onRender(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo info) {
		@Nullable
		BuiltinItemRendererRegistry.DynamicItemRenderer renderer = BuiltinItemRendererRegistry.INSTANCE.get(stack.getItem());

		if (renderer != null) {
			renderer.render(stack, mode, matrices, vertexConsumers, light, overlay);
			info.cancel();
		}
	}
}
