/*
 * Copyright 2016, 2017, 2018, 2019 FabricMC
 * Copyright 2023 The Quilt Project
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

package net.fabricmc.fabric.mixin.rendering.data;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntity;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements RenderAttachmentBlockEntity, RenderDataBlockEntity {
	@Override
	@Nullable
	public Object getRenderAttachmentData() {
		return null;
	}

	/**
	 * Instead of returning null by default in v2, proxy to v1 method instead.
	 */
	@Override
	@Nullable
	public Object getRenderData() {
		return getRenderAttachmentData();
	}
}
