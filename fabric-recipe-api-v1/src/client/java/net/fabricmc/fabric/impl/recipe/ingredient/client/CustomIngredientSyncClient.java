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

package net.fabricmc.fabric.impl.recipe.ingredient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientSync;

/**
 * @see CustomIngredientSync
 */
public class CustomIngredientSyncClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientConfigurationNetworking.registerGlobalReceiver(CustomIngredientSync.PACKET_ID, (client, handler, buf, responseSender) -> {
			int protocolVersion = buf.readVarInt();
			handler.sendPacket(ClientConfigurationNetworking.createC2SPacket(CustomIngredientSync.PACKET_ID, CustomIngredientSync.createResponsePacket(protocolVersion)));
		});
	}
}
