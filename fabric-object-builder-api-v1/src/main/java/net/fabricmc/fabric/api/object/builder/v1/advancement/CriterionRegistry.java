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

package net.fabricmc.fabric.api.object.builder.v1.advancement;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.util.Identifier;

/**
 * Allows registering advancement criteria for triggers.
 *
 * <p>A registered criterion (trigger) can be retrieved through
 * {@link Criteria#getById(Identifier)}.</p>
 *
 * @see Criteria
 * @deprecated Replaced by access widener for {@link Criteria#register(Criterion)}
 * in Fabric Transitive Access Wideners (v1).
 */
@Deprecated
public final class CriterionRegistry {
	/**
	 * Registers a criterion for a trigger for advancements.
	 *
	 * @param <T> the criterion's type
	 * @param criterion the criterion registered
	 * @return the criterion registered, for chaining
	 * @throws IllegalArgumentException if a criterion with the same {@link
	 *                                  Criterion#getId() id} exists
	 */
	public static <T extends Criterion<?>> T register(T criterion) {
		return Criteria.register(criterion);
	}
}
