package main.rendering.filters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.StaticFullscreenQuad;

public abstract class ShaderEffect {

  private FrameBuffer target;

  protected abstract ShaderProgram getShader();

  public ShaderEffect renderTo(FrameBuffer target) {
    target.begin();
    getShader().begin();
    this.target = target;
    return this;
  }

  public ShaderEffect bind(String name, int slot, FrameBuffer buffer) {
    buffer.getColorBufferTexture().bind(slot);
    getShader().setUniformi(name, slot);
    return this;
  }

  public ShaderEffect bind(String name, int slot, Texture texture) {
    texture.bind(slot);
    getShader().setUniformi(name, slot);
    return this;
  }

  public ShaderEffect paramterize(String name, float r, float g) {
    float[] pack = new float[] {r, g};
    getShader().setUniform2fv(name, pack, 0, pack.length);
    return this;
  }

  public ShaderEffect paramterize(String name, float r) {
    getShader().setUniformf(name, r);
    return this;
  }

  public void flush() {
    StaticFullscreenQuad.renderUsing(getShader());
    getShader().end();
    target.end();
  }

}
