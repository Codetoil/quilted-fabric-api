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

package net.fabricmc.fabric.api.client.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.impl.client.container.ScreenProviderRegistryImpl;

/**
 * @deprecated Use {@link net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry} instead.
 */
@Deprecated
public interface ScreenProviderRegistry {
	ScreenProviderRegistry INSTANCE = new ScreenProviderRegistryImpl();

	/**
	 * Register a "Container -&gt; ContainerScreen" factory. This is used only on the client side.
	 *
	 * @param identifier             a shared identifier, this identifier should also be used to register a container using {@link ContainerProviderRegistry}
	 * @param containerScreenFactory the supplier that should be used to create the new gui
	 */
	<C extends ScreenHandler> void registerFactory(Identifier identifier, ContainerScreenFactory<C> containerScreenFactory);

	/**
	 * Register a "packet -&gt; ContainerScreen" factory. This is used only on the client side, and allows you
	 * to override the default behaviour of re-using the existing "packet -&gt; Container" logic.
	 *
	 * @param identifier a shared identifier, this identifier should also be used to register a container using {@link ContainerProviderRegistry}
	 * @param factory    the gui factory, this should return a new {@link HandledScreen}
	 */
	void registerFactory(Identifier identifier, ContainerFactory<HandledScreen> factory);
}
