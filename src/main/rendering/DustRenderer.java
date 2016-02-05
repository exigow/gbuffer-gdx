package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import main.resources.Materials;
import main.resources.ResourceLoader;

public class DustRenderer {

  private final ShaderProgram shader = ResourceLoader.loadShader("data/dust-point/dust-point.vert", "data/dust-point/dust-point.frag");
  private final Matrix4 projection = new Matrix4();
  private final Mesh mesh;
  private float[] vertices;

  private DustRenderer(int count) {
    vertices = new float[count * 3];
    for (int i = 0; i < vertices.length; i += 3)
      resetPoint(i);
    mesh = initialiseMesh(count);
  }

  public static DustRenderer withCapacity(int count) {
    return new DustRenderer(count);
  }

  public void updateProjection(Matrix4 actualized) {
    projection.set(actualized);
}

  public void render() {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
    Gdx.gl.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
    Gdx.gl.glEnable(0x8861); // hack, its GL11.GL_POINT_SPRITE_OES
    Materials.get("star").color.bind(0);
    shader.begin();
    shader.setUniformMatrix("projection", projection);
    shader.setUniformi("texture", 0);
    mesh.setVertices(vertices, 0, vertices.length);
    mesh.getIndicesBuffer().position(0);
    mesh.render(shader, GL20.GL_POINTS, 0, vertices.length / 3);
    shader.end();
    Gdx.gl.glDisable(0x8861);
    Gdx.gl.glDisable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  private void resetPoint(int pivot) {
    float s = 256;
    vertices[pivot] = MathUtils.random(-s, s);
    vertices[pivot + 1] = MathUtils.random(-s, s);
    vertices[pivot + 2] = MathUtils.random(-s, 0);
  }

  private static Mesh initialiseMesh(int count) {
    return new Mesh(true, count, 0, new VertexAttribute(VertexAttributes.Usage.Position, 3, "positionAttr"));
  }

}
