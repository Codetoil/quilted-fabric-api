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

package net.fabricmc.fabric.impl.client.indigo.renderer.mesh;

import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_BITS;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_COLOR_INDEX;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_FACE_NORMAL;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_STRIDE;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.HEADER_TAG;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.QUAD_STRIDE;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_COLOR;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_LIGHTMAP;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_NORMAL;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_STRIDE;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_U;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_V;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_X;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_Y;
import static net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat.VERTEX_Z;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.NormalHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.material.RenderMaterialImpl;

/**
 * Base class for all quads / quad makers. Handles the ugly bits
 * of maintaining and encoding the quad state.
 */
public class QuadViewImpl implements QuadView {
	@Nullable
	protected Direction nominalFace;
	/** True when face normal, light face, or geometry flags may not match geometry. */
	protected boolean isGeometryInvalid = true;
	protected final Vector3f faceNormal = new Vector3f();

	/** Size and where it comes from will vary in subtypes. But in all cases quad is fully encoded to array. */
	protected int[] data;

	/** Beginning of the quad. Also the header index. */
	protected int baseIndex = 0;

	/**
	 * Decodes necessary state from the backing data array.
	 * The encoded data must contain valid computed geometry.
	 */
	public void load() {
		isGeometryInvalid = false;
		nominalFace = lightFace();
		NormalHelper.unpackNormal(packedFaceNormal(), faceNormal);
	}

	protected void computeGeometry() {
		if (isGeometryInvalid) {
			isGeometryInvalid = false;

			NormalHelper.computeFaceNormal(faceNormal, this);
			data[baseIndex + HEADER_FACE_NORMAL] = NormalHelper.packNormal(faceNormal);

			// depends on face normal
			data[baseIndex + HEADER_BITS] = EncodingFormat.lightFace(data[baseIndex + HEADER_BITS], GeometryHelper.lightFace(this));

			// depends on light face
			data[baseIndex + HEADER_BITS] = EncodingFormat.geometryFlags(data[baseIndex + HEADER_BITS], GeometryHelper.computeShapeFlags(this));
		}
	}

	/** gets flags used for lighting - lazily computed via {@link GeometryHelper#computeShapeFlags(QuadView)}. */
	public int geometryFlags() {
		computeGeometry();
		return EncodingFormat.geometryFlags(data[baseIndex + HEADER_BITS]);
	}

	public boolean hasShade() {
		return !material().disableDiffuse();
	}

	@Override
	public float x(int vertexIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X]);
	}

	@Override
	public float y(int vertexIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_Y]);
	}

	@Override
	public float z(int vertexIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_Z]);
	}

	@Override
	public float posByIndex(int vertexIndex, int coordinateIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X + coordinateIndex]);
	}

	@Override
	public Vector3f copyPos(int vertexIndex, @Nullable Vector3f target) {
		if (target == null) {
			target = new Vector3f();
		}

		final int index = baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X;
		target.set(Float.intBitsToFloat(data[index]), Float.intBitsToFloat(data[index + 1]), Float.intBitsToFloat(data[index + 2]));
		return target;
	}

	@Override
	public int color(int vertexIndex) {
		return data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_COLOR];
	}

	@Override
	public float u(int vertexIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_U]);
	}

	@Override
	public float v(int vertexIndex) {
		return Float.intBitsToFloat(data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_V]);
	}

	@Override
	public Vector2f copyUv(int vertexIndex, @Nullable Vector2f target) {
		if (target == null) {
			target = new Vector2f();
		}

		final int index = baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_U;
		target.set(Float.intBitsToFloat(data[index]), Float.intBitsToFloat(data[index + 1]));
		return target;
	}

	@Override
	public int lightmap(int vertexIndex) {
		return data[baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_LIGHTMAP];
	}

	public int normalFlags() {
		return EncodingFormat.normalFlags(data[baseIndex + HEADER_BITS]);
	}

	@Override
	public boolean hasNormal(int vertexIndex) {
		return (normalFlags() & (1 << vertexIndex)) != 0;
	}

	/** True if any vertex normal has been set. */
	public boolean hasVertexNormals() {
		return normalFlags() != 0;
	}

	/** True if all vertex normals have been set. */
	public boolean hasAllVertexNormals() {
		return (normalFlags() & 0b1111) == 0b1111;
	}

	protected final int normalIndex(int vertexIndex) {
		return baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL;
	}

	@Override
	public float normalX(int vertexIndex) {
		return hasNormal(vertexIndex) ? NormalHelper.unpackNormalX(data[normalIndex(vertexIndex)]) : Float.NaN;
	}

	@Override
	public float normalY(int vertexIndex) {
		return hasNormal(vertexIndex) ? NormalHelper.unpackNormalY(data[normalIndex(vertexIndex)]) : Float.NaN;
	}

	@Override
	public float normalZ(int vertexIndex) {
		return hasNormal(vertexIndex) ? NormalHelper.unpackNormalZ(data[normalIndex(vertexIndex)]) : Float.NaN;
	}

	@Override
	@Nullable
	public Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target) {
		if (hasNormal(vertexIndex)) {
			if (target == null) {
				target = new Vector3f();
			}

			final int normal = data[normalIndex(vertexIndex)];
			NormalHelper.unpackNormal(normal, target);
			return target;
		} else {
			return null;
		}
	}

	@Override
	@Nullable
	public final Direction cullFace() {
		return EncodingFormat.cullFace(data[baseIndex + HEADER_BITS]);
	}

	@Override
	@NotNull
	public final Direction lightFace() {
		computeGeometry();
		return EncodingFormat.lightFace(data[baseIndex + HEADER_BITS]);
	}

	@Override
	@Nullable
	public final Direction nominalFace() {
		return nominalFace;
	}

	public final int packedFaceNormal() {
		computeGeometry();
		return data[baseIndex + HEADER_FACE_NORMAL];
	}

	@Override
	public final Vector3f faceNormal() {
		computeGeometry();
		return faceNormal;
	}

	@Override
	public final RenderMaterialImpl material() {
		return EncodingFormat.material(data[baseIndex + HEADER_BITS]);
	}

	@Override
	public final int colorIndex() {
		return data[baseIndex + HEADER_COLOR_INDEX];
	}

	@Override
	public final int tag() {
		return data[baseIndex + HEADER_TAG];
	}

	@Override
	public final void toVanilla(int[] target, int targetIndex) {
		System.arraycopy(data, baseIndex + HEADER_STRIDE, target, targetIndex, QUAD_STRIDE);

		// The color is the fourth integer in each vertex.
		// EncodingFormat.VERTEX_COLOR is not used because it also
		// contains the header size; vanilla quads do not have a header.
		int colorIndex = targetIndex + 3;

		for (int i = 0; i < 4; i++) {
			target[colorIndex] = ColorHelper.toVanillaColor(target[colorIndex]);
			colorIndex += VANILLA_VERTEX_STRIDE;
		}
	}
}
