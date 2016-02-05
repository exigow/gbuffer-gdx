package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import main.resources.Materials;
import main.resources.ResourceLoader;

import static com.badlogic.gdx.math.MathUtils.random;

public class DustRenderer {

  private static final float RANDOMIZE_RADIUS = 1024;
  private final ShaderProgram shader = ResourceLoader.loadShader("data/dust-point/dust-point.vert", "data/dust-point/dust-point.frag");
  private final Matrix4 projection = new Matrix4();
  private final Mesh mesh;
  private float[] vertices;
  private final Color tempColor = new Color();

  private DustRenderer(int count) {
    vertices = new float[count * 4];
    int pivot = 0;
    for (int i = 0; i < vertices.length; i += 4, pivot++) {
      vertices[i] = 0;
      vertices[i + 1] = 0;
      float n = (float) pivot / (float) count;
      float zscale = 1024;
      vertices[i + 2] = (-1 + n * 2) * zscale;
      vertices[i + 3] = randomizedColor();
      resetDustPosition(0, 0, i, 0);
    }
    mesh = initialiseMesh(count);
  }

  private float randomizedColor() {
    tempColor.r = random(.5f, 1);
    tempColor.g = random(.5f, 1);
    tempColor.b = random(.5f, 1);
    tempColor.a = random(.25f, .75f);
    return tempColor.toFloatBits();
  }

  public static DustRenderer withCapacity(int count) {
    return new DustRenderer(count);
  }

  public void updateProjection(Matrix4 actualized) {
    projection.set(actualized);
}

  public void render(boolean isBelow) {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
    Gdx.gl.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
    int GL_POINT_SPRITE_OES = 0x8861; // hack, its disabled
    Gdx.gl.glEnable(GL_POINT_SPRITE_OES);
    Materials.get("star").color.bind(0);
    shader.begin();
    shader.setUniformMatrix("projection", projection);
    shader.setUniformi("texture", 0);
    if (isBelow) {
      mesh.setVertices(vertices, 0, vertices.length);
      mesh.render(shader, GL20.GL_POINTS, vertices.length / 4 / 2, vertices.length / 4 / 2);
    } else {
      mesh.setVertices(vertices, 0, vertices.length / 2);
      mesh.render(shader, GL20.GL_POINTS, 0, vertices.length / 4 / 2);
    }
    shader.end();
    Gdx.gl.glDisable(GL_POINT_SPRITE_OES);
    Gdx.gl.glDisable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  public void update(Vector3 relative) {
    for (int i = 0; i < vertices.length; i += 4) {
      float x = vertices[i];
      float y = vertices[i + 1];
      if (Vector2.dst(x, y, relative.x, relative.y) > RANDOMIZE_RADIUS)
        resetDustPosition(relative.x, relative.y, i, RANDOMIZE_RADIUS - 1);
    }
  }

  private void resetDustPosition(float x, float y, int i, float minRadius) {
    float rads = random(MathUtils.PI2);
    float length = random(minRadius, RANDOMIZE_RADIUS);
    length /= Math.sqrt(length);
    length *= Math.sqrt(RANDOMIZE_RADIUS);
    vertices[i] = x + MathUtils.cos(rads) * length;
    vertices[i + 1] = y + MathUtils.sin(rads) * length;
  }

  private static Mesh initialiseMesh(int count) {
    return new Mesh(true, count, 0,
      new VertexAttribute(VertexAttributes.Usage.Position, 3, "positionAttr"),
      new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "colorAttr")
    );
  }

}
