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

package net.fabricmc.fabric.api.registry;

import java.util.function.Consumer;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.ServerCommandSource;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * @deprecated Please migrate to v2. Please use {@link CommandRegistrationCallback} instead.
 */
@Deprecated
public class CommandRegistry {
	public static final CommandRegistry INSTANCE = new CommandRegistry();

	/**
	 * Register a command provider.
	 *
	 * @param dedicated If true, the command is only registered on the dedicated server.
	 * @param consumer  The command provider, consuming {@link CommandDispatcher}.
	 */
	public void register(boolean dedicated, Consumer<CommandDispatcher<ServerCommandSource>> consumer) {
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
			if (dedicated && !environment.dedicated) {
				return;
			}

			consumer.accept(dispatcher);
		}));
	}
}
