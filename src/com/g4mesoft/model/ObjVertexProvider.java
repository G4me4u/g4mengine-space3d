package com.g4mesoft.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.graphics3d.IVertexProvider;
import com.g4mesoft.graphics3d.Shape3D;
import com.g4mesoft.graphics3d.Vertex3D;
import com.g4mesoft.graphics3d.VertexTessellator3D;
import com.g4mesoft.math.Vec2f;
import com.g4mesoft.math.Vec3f;

public class ObjVertexProvider implements IVertexProvider {

	private final IVertexProvider vertices;

	public ObjVertexProvider(IVertexProvider vertices) {
		this.vertices = vertices;
	}
	
	@Override
	public void prepareDraw() {
		vertices.prepareDraw();
	}

	@Override
	public boolean hasNext() {
		return vertices.hasNext();
	}

	@Override
	public Vertex3D getNextVertex() {
		return vertices.getNextVertex();
	}

	@Override
	public Shape3D getShape() {
		return vertices.getShape();
	}
	
	public static ObjVertexProvider loadFromOBJ(String filename) throws IOException {
		InputStream is = ObjVertexProvider.class.getResourceAsStream(filename);
		
		if (is == null)
			throw new IOException("File '" + filename + "' not found!");
		
		List<Vec3f> positions = new ArrayList<Vec3f>();
		List<Vec2f> texCoords = new ArrayList<Vec2f>();
		
		boolean hasTexture = false;
		
		VertexTessellator3D tessellator = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() > 2) {
					char c = line.charAt(0);
					if (c == '#')
						continue;
					
					String[] args = line.split(" ");
					if (c == 'v') {
						if (line.charAt(1) == 't') {
							Vec2f tex = new Vec2f();

							try {
								tex.x = Float.parseFloat(args[1]);
								tex.y = Float.parseFloat(args[2]);
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
							
							texCoords.add(tex);
							
							hasTexture = true;
						} else {
							Vec3f vert = new Vec3f();
							
							try {
								vert.x = Float.parseFloat(args[1]);
								vert.y = Float.parseFloat(args[2]);
								vert.z = Float.parseFloat(args[3]);
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
							
							positions.add(vert);
						}
					} else if (c == 'f') {
						for (int i = 1; i <= 3; i++) {
							String vertex = args[i];
							
							String[] indices = vertex.split("/");
							try {
								if (tessellator == null)
									tessellator = new VertexTessellator3D(hasTexture ? 2 : 0);

								Vec3f pos = positions.get(Integer.parseInt(indices[0]) - 1);
								tessellator.addVertex(pos.x, pos.y, pos.z);
								if (hasTexture)
									tessellator.setExtraVec2(0, texCoords.get(Integer.parseInt(indices[1]) - 1));
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
						}
					}
				}
			}
		}
		
		return new ObjVertexProvider(tessellator.getVertexProvider(Shape3D.TRIANGLES));
	}
}
