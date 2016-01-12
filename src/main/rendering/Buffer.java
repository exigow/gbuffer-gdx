package main.rendering;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import main.utils.ResourceLoader;

public class Buffer {

  private final static int QUADS_MAX_COUNT = 4;
  private final static int QUADS_MAX_INDICES = QUADS_MAX_COUNT * 6;
  private final Mesh mesh = initialiseEmptyMesh();
  private final float[] vertices = new float[QUADS_MAX_COUNT * 2 * 2];
  private final ShaderProgram bufferShader;
  private int pivot = 0;
  private final Matrix4 projectionMatrix = new Matrix4();

  public Buffer() {
    bufferShader = ResourceLoader.loadShader("data/buffer.vert", "data/buffer.frag");
  }

  private static Mesh initialiseEmptyMesh() {
    VertexAttribute[] attributes = new VertexAttribute[] {
      new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
      new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
    };
    int maxVertices = QUADS_MAX_COUNT * 4;
    Mesh mesh = new Mesh(true, maxVertices, QUADS_MAX_INDICES, attributes);
    mesh.setIndices(generateQuadIndices());
    return mesh;
  }

  private static short[] generateQuadIndices() {
    short[] indices = new short[QUADS_MAX_INDICES];
    short j = 0;
    for (int i = 0; i < QUADS_MAX_INDICES; i += 6, j += 4) {
      indices[i] = j;
      indices[i + 1] = (short) (j + 1);
      indices[i + 2] = (short) (j + 2);
      indices[i + 3] = (short) (j + 2);
      indices[i + 4] = (short) (j + 3);
      indices[i + 5] = j;
    }
    return indices;
  }

  public void putVertex(float x, float y, float u, float v) {
    put(x, y, u, v);
  }

  private void put(float... values) {
    for (float value : values)
      vertices[pivot++] = value;
  }

  public void updateProjection(Matrix4 actualized) {
    projectionMatrix.set(actualized);
  }

  public void paint(Texture texture) {
    texture.bind();
    bufferShader.begin();
    bufferShader.setUniformMatrix("u_projTrans", projectionMatrix);
    bufferShader.setUniformi("u_texture", 0);
    mesh.setVertices(vertices, 0, pivot);
    mesh.getIndicesBuffer().position(0);
    mesh.render(bufferShader, GL20.GL_TRIANGLES, 0, 3 * QUADS_MAX_COUNT);
    bufferShader.end();
  }

  public void reset() {
    pivot = 0;
  }

}
