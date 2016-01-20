package main.rendering;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import main.utils.ResourceLoader;

import java.util.Arrays;

public class Buffer {

  private final static int QUADS_MAX_COUNT = 8;
  private final static int QUADS_MAX_INDICES = QUADS_MAX_COUNT * 6;
  private final Mesh mesh = initialiseEmptyMesh();
  private final float[] vertices = new float[QUADS_MAX_COUNT * 2 * 2];
  private final float[] pvertices = new float[QUADS_MAX_COUNT * 2 * 2];
  private final ShaderProgram bufferShader = ResourceLoader.loadShader("data/buffer/paint.vert", "data/buffer/paint.frag");
  private final ShaderProgram velocityShader = ResourceLoader.loadShader("data/buffer/velocity.vert", "data/buffer/velocity.frag");
  private int pivot = 0;
  private final Matrix4 projectionMatrix = new Matrix4();

  private static Mesh initialiseEmptyMesh() {
    VertexAttribute[] attributes = new VertexAttribute[] {
      new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
      new VertexAttribute(VertexAttributes.Usage.Generic, 2, "a_velocity"),
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
    vertices[pivot] = x;
    vertices[pivot + 1] = y;
    vertices[pivot + 2] = .5f + (vertices[pivot] - pvertices[pivot]) * .0025f;
    vertices[pivot + 3] = .5f + (vertices[pivot + 1] - pvertices[pivot + 1]) * .0025f;
    vertices[pivot + 4] = u;
    vertices[pivot + 5] = v;
    pivot += 6;
  }

  public void updateProjection(Matrix4 actualized) {
    projectionMatrix.set(actualized);
  }

  public void paint(Texture texture) {
    texture.bind(0);
    bufferShader.begin();
    bufferShader.setUniformMatrix("u_projTrans", projectionMatrix);
    bufferShader.setUniformi("u_texture", 0);
    mesh.setVertices(vertices, 0, pivot);
    mesh.getIndicesBuffer().position(0);
    mesh.render(bufferShader, GL20.GL_TRIANGLES, 0, 3 * QUADS_MAX_COUNT);
    bufferShader.end();
  }

  public void paintVelocity() {
    velocityShader.begin();
    velocityShader.setUniformMatrix("u_projTrans", projectionMatrix);
    mesh.setVertices(vertices, 0, pivot);
    mesh.getIndicesBuffer().position(0);
    mesh.render(velocityShader, GL20.GL_TRIANGLES, 0, 3 * QUADS_MAX_COUNT);
    velocityShader.end();
  }

  public void reset() {
    System.arraycopy(vertices, 0, pvertices, 0, vertices.length);
    pivot = 0;
  }

}
