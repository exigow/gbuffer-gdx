package main.rendering.utils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class StaticFullscreenQuad {

  private final static Mesh QUAD = createQuad();

  public static void renderUsing(ShaderProgram program) {
    QUAD.render(program, GL20.GL_TRIANGLE_FAN, 0, 4);
  }

  private static Mesh createQuad() {
    float[] vertices = {
      -1, // x1
      -1, // y1
      0,  // u1
      0,  // v1

      1,  // x2
      -1, // y2
      1,  // u2
      0,  // v2

      1,  // x3
      1,  // y3
      1,  // u3
      1,  // v3

      -1, // x4
      1,  // y4
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
