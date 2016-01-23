package main.rendering.filters;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.StaticFullscreenQuad;
import main.resources.ResourceLoader;

public class Fxaa {

  private final ShaderProgram shader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/fxaa.frag");

  public void apply(FrameBuffer from, FrameBuffer to) {
    to.begin();
    from.getColorBufferTexture().bind(0);
    shader.begin();
    shader.setUniformi("u_texture", 0);
    shader.setUniformf("FXAA_REDUCE_MIN", 1f / 128f);
    shader.setUniformf("FXAA_REDUCE_MUL", 1f / 8f);
    shader.setUniformf("FXAA_SPAN_MAX", 8f);
    float[] texel = new float[] {1f / 1280f, 1f / 960f};
    shader.setUniform2fv("texel", texel, 0, texel.length);
    StaticFullscreenQuad.renderUsing(shader);
    shader.end();
    to.end();
  }

}
