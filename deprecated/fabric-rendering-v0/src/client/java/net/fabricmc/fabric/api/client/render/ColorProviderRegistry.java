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

package net.fabricmc.fabric.api.client.render;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemConvertible;

/**
 * @deprecated Replaced by {@link net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry}
 */
@Deprecated
public interface ColorProviderRegistry<T, Provider> {
	ColorProviderRegistry<ItemConvertible, ItemColorProvider> ITEM = new ColorProviderRegistry<ItemConvertible, ItemColorProvider>() {
		@Override
		public void register(ItemColorProvider itemColorProvider, ItemConvertible... objects) {
			net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register(itemColorProvider, objects);
		}

		@Override
		public ItemColorProvider get(ItemConvertible object) {
			return net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.get(object);
		}
	};

	ColorProviderRegistry<Block, BlockColorProvider> BLOCK = new ColorProviderRegistry<Block, BlockColorProvider>() {
		@Override
		public void register(BlockColorProvider blockColorProvider, Block... objects) {
			net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register(blockColorProvider, objects);
		}

		@Override
		public BlockColorProvider get(Block object) {
			return net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.get(object);
		}
	};

	/**
	 * Register a color provider for one or more objects.
	 *
	 * @param provider The color provider to register.
	 * @param objects  The objects which should be colored using this provider.
	 */
	void register(Provider provider, T... objects);

	/**
	 * Get a color provider for the given object.
	 *
	 * <p>Please note that the underlying registry may not be fully populated or stable until the game has started,
	 * as other mods may overwrite the registry.
	 *
	 * @param object The object to acquire the provide for.
	 * @return The registered mapper for this provider, or {@code null} if none is registered or available.
	 */
	@Nullable
	Provider get(T object);
}
