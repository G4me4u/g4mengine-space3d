package com.g4mesoft.model;

import com.g4mesoft.graphics3d.IVertexProvider;
import com.g4mesoft.graphics3d.Texture3D;

public class TexturedModel {

	private final IVertexProvider vertices;
	private final Texture3D texture;
	private final Texture3D glowTexture;
	
	public TexturedModel(IVertexProvider vertices, Texture3D texture, Texture3D glowTexture) {
		this.vertices = vertices;
		this.texture = texture;
		this.glowTexture = glowTexture;
	}
	
	public IVertexProvider getVertices() {
		return vertices;
	}

	public Texture3D getTexture() {
		return texture;
	}

	public Texture3D getGlowTexture() {
		return glowTexture;
	}
}
