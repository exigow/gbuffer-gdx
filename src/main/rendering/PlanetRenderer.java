package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import main.resources.Materials;
import main.resources.ResourceLoader;

public class PlanetRenderer {

  private final static Mesh QUAD = createQuad();
  private final Matrix4 projectionMatrix = new Matrix4();
  private final ShaderProgram shader = ResourceLoader.loadShader("data/planet/planet.vert", "data/planet/planet.frag");

  public void render(GBuffer gbuffer, float time) {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    Materials.get("planet").color.bind(0);
    gbuffer.color.begin();
    shader.begin();
    shader.setUniformMatrix("u_projTrans", projectionMatrix);
    shader.setUniformi("u_texture", 0);
    shader.setUniformf("u_rotation", time * .25f);
    QUAD.render(shader, GL20.GL_TRIANGLE_FAN, 0, 4);
    shader.end();
    gbuffer.color.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  public void updateProjection(Matrix4 actualized) {
    projectionMatrix.set(actualized);
  }

  private static Mesh createQuad() {
    float size = 256;
    float[] vertices = {
      -size, // x1
      -size, // y1
      0,  // u1
      0,  // v1

      size,  // x2
      -size, // y2
      1,  // u2
      0,  // v2

      size,  // x3
      size,  // y3
      1,  // u3
      1,  // v3

      -size, // x4
      size,  // y4
      0,  // u4
      1   // v4
    };
    Mesh mesh = new Mesh(Mesh.VertexDataType.VertexArray, true, 4, 0,
      new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
      new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
    );
    mesh.setVertices(vertices);
    return mesh;
  }

}
