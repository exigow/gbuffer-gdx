package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import main.resources.ResourceLoader;

public class VertexBuffer {

  private final static int MAX_INSTANCES = 2;
  private final Mesh mesh = initialiseEmptyMesh();
  private final float[] vertices = new float[MAX_INSTANCES * 7 * 4];
  //private final float[] pvertices = new float[vertices.length];
  private final ShaderProgram colorShader = ResourceLoader.loadShader("data/buffer/color.vert", "data/buffer/color.frag");
  private final ShaderProgram emissiveShader = ResourceLoader.loadShader("data/buffer/color.vert", "data/buffer/emissive.frag");
  private final ShaderProgram velocityShader = ResourceLoader.loadShader("data/buffer/velocity.vert", "data/buffer/velocity.frag");
  private final ShaderProgram idShader = ResourceLoader.loadShader("data/buffer/id.vert", "data/buffer/id.frag");
  private int pivot = 0;
  private final Matrix4 projectionMatrix = new Matrix4();

  private static Mesh initialiseEmptyMesh() {
    VertexAttribute[] attributes = new VertexAttribute[] {
      new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
      new VertexAttribute(VertexAttributes.Usage.Generic, 2, "a_velocity"),
      new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"),
      new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color")
    };
    int maxVertices = MAX_INSTANCES * 4;
    Mesh mesh = new Mesh(true, maxVertices, MAX_INSTANCES * 6, attributes);
    mesh.setIndices(generateQuadIndices());
    return mesh;
  }

  private static short[] generateQuadIndices() {
    short[] indices = new short[MAX_INSTANCES * 6];
    short j = 0;
    for (int i = 0; i < indices.length; i += 6, j += 4) {
      indices[i] = j;
      indices[i + 1] = (short) (j + 1);
      indices[i + 2] = (short) (j + 2);
      indices[i + 3] = (short) (j + 2);
      indices[i + 4] = (short) (j + 3);
      indices[i + 5] = j;
    }
    return indices;
  }

  public void putVertex(float x, float y, float u, float v, float color) {
    vertices[pivot] = x;
    vertices[pivot + 1] = y;
    vertices[pivot + 2] = .5f;//.5f + (vertices[pivot] - pvertices[pivot]) * .0025f;
    vertices[pivot + 3] = .5f;//.5f + (vertices[pivot + 1] - pvertices[pivot + 1]) * .0025f;
    vertices[pivot + 4] = u;
    vertices[pivot + 5] = v;
    vertices[pivot + 6] = color;
    pivot += 7;
  }

  public void updateProjection(Matrix4 actualized) {
    projectionMatrix.set(actualized);
  }

  public void paintColor(Texture texture) {
    paint(texture, colorShader);
  }

  public void paintVelocity(Texture texture) {
    paint(texture, velocityShader);
  }

  public void paintEmissive(Texture texture) {
    paint(texture, emissiveShader);
  }

  public void paintIds(Texture texture) {
    paint(texture, idShader);
  }

  private void paint(Texture texture, ShaderProgram shader) {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    texture.bind(0);
    shader.begin();
    shader.setUniformMatrix("u_projTrans", projectionMatrix);
    shader.setUniformi("u_texture", 0);
    mesh.setVertices(vertices, 0, pivot);
    mesh.getIndicesBuffer().position(0);
    mesh.render(shader, GL20.GL_TRIANGLES, 0, MAX_INSTANCES * 6);
    shader.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  public void reset() {
    //System.arraycopy(vertices, 0, pvertices, 0, vertices.length);
    pivot = 0;
  }

}
