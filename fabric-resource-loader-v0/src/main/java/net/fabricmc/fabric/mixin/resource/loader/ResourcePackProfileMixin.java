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

package net.fabricmc.fabric.mixin.resource.loader;

import org.quiltmc.qsl.resource.loader.api.GroupResourcePack;
import org.quiltmc.qsl.resource.loader.impl.ModNioResourcePack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.loader.ResourcePackSourceTracker;

/**
 * Implements resource pack source tracking (for {@link net.fabricmc.fabric.impl.resource.loader.FabricResource}).
 * {@link ResourcePack} doesn't hold a reference to its {@link ResourcePackSource}
 * so we store the source in a global tracker when the resource packs are created.
 *
 * @see ResourcePackSourceTracker
 */
@Mixin(ResourcePackProfile.class)
abstract class ResourcePackProfileMixin {
	@Shadow
	@Final
	private ResourcePackSource source;

	@Inject(method = "createResourcePack", at = @At("RETURN"))
	private void onCreateResourcePack(CallbackInfoReturnable<ResourcePack> cir) {
		if (cir.getReturnValue() instanceof GroupResourcePack grouped) {
			grouped.streamPacks().forEach(pack -> {
				if (pack instanceof DefaultResourcePack) {
					ResourcePackSourceTracker.setSource(pack, source);
				} else if (pack instanceof ModNioResourcePack) {
					// There could be a check for if it's a ModNioResourcePack, but I doubt that any other added pack wouldn't be modded
					ResourcePackSourceTracker.setSource(pack, ModResourcePackCreator.RESOURCE_PACK_SOURCE);
				}
			});
		} else {
			ResourcePackSourceTracker.setSource(cir.getReturnValue(), source);
		}
	}
}
