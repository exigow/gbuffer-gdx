package main.rendering;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import main.resources.Material;
import main.resources.ResourceLoader;

public class MaterialRenderer {

  private final Matrix4 projectionMatrix = new Matrix4();
  private float vertices[] = new float[16];
  private final Mesh mesh = createMesh();
  private final ShaderProgram colorShader = ResourceLoader.loadShader("data/buffer/color.vert", "data/buffer/color.frag");
  private final ShaderProgram emissiveShader = ResourceLoader.loadShader("data/buffer/color.vert", "data/buffer/emissive.frag");

  public void render(float x, float y, float rotation, GBuffer gbuffer, Material material) {
    float width = material.color.getWidth();
    float height = material.color.getHeight();

    updateVertices(x, y, rotation, width);
    mesh.setVertices(vertices);

    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    gbuffer.color.begin();
    paintColor(material.color);
    gbuffer.color.end();

    if (material.emissive != null) {
      gbuffer.emissive.begin();
      paintEmissive(material.emissive);
      gbuffer.emissive.end();
    }

    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  public void updateVertices(float x, float y, float rotation, float size) {
    float cos = MathUtils.cos(rotation + MathUtils.PI / 4f);
    float sin = MathUtils.sin(rotation + MathUtils.PI / 4f);
    vertices[0] = x - cos * size;
    vertices[1] = y - sin * size;
    vertices[2] = 0;
    vertices[3] = 0;
    vertices[4] = x + sin * size;
    vertices[5] = y - cos * size;
    vertices[6] = 1;
    vertices[7] = 0;
    vertices[8] = x + cos * size;
    vertices[9] = y + sin * size;
    vertices[10] = 1;
    vertices[11] = 1;
    vertices[12] = x - sin * size;
    vertices[13] = y + cos * size;
    vertices[14] = 0;
    vertices[15] = 1;
  }

  public void updateProjection(Matrix4 actualized) {
    projectionMatrix.set(actualized);
  }

  private static Mesh createMesh() {
    return new Mesh(Mesh.VertexDataType.VertexArray, true, 4, 0,
      new VertexAttribute(VertexAttributes.Usage.Position, 2, "positionAttr"),
      new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "texCoordAttr")
    );
  }

  public void paintColor(Texture texture) {
    paint(texture, colorShader);
  }

  public void paintEmissive(Texture texture) {
    paint(texture, emissiveShader);
  }

  private void paint(Texture texture, ShaderProgram shader) {
    texture.bind(0);
    shader.begin();
    shader.setUniformMatrix("projection", projectionMatrix);
    shader.setUniformi("texture", 0);
    mesh.render(shader, GL20.GL_TRIANGLE_FAN, 0, 4);
    shader.end();
  }

}
