package main.rendering.postprocess;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.StaticFullscreenQuad;
import main.resources.ResourceLoader;

public class ShaderEffect {

  private FrameBuffer target;
  private ShaderProgram shader;
  private int bindIterator = 0;

  private ShaderEffect(ShaderProgram shader) {
    this.shader = shader;
  }

  public static ShaderEffect createGeneric(String filterName) {
    ShaderProgram shader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", filterName);
    return new ShaderEffect(shader);
  }

  public ShaderEffect renderTo(FrameBuffer target) {
    target.begin();
    shader.begin();
    this.target = target;
    return this;
  }

  public ShaderEffect bind(String name, FrameBuffer buffer) {
    buffer.getColorBufferTexture().bind(bindIterator);
    shader.setUniformi(name, bindIterator);
    bindIterator++;
    return this;
  }

  public ShaderEffect bind(String name, Texture texture) {
    texture.bind(bindIterator);
    shader.setUniformi(name, bindIterator);
    return this;
  }

  public ShaderEffect paramterize(String name, float r, float g) {
    float[] pack = new float[] {r, g};
    shader.setUniform2fv(name, pack, 0, pack.length);
    return this;
  }

  public ShaderEffect paramterize(String name, float r) {
    shader.setUniformf(name, r);
    return this;
  }

  public void flush() {
    StaticFullscreenQuad.renderUsing(shader);
    shader.end();
    target.end();
    bindIterator = 0;
  }

}
