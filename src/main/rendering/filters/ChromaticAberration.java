package main.rendering.filters;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.StaticFullscreenQuad;
import main.resources.ResourceLoader;

public class ChromaticAberration {

  private final ShaderProgram shader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/chromatic-aberration.frag");

  public void apply(FrameBuffer from, FrameBuffer to) {
    to.begin();
    from.getColorBufferTexture().bind(0);
    shader.begin();
    shader.setUniformi("u_texture", 0);
    float[] texel = new float[] {1f / 1280f, 1f / 960f};
    shader.setUniform2fv("texel", texel, 0, texel.length);
    StaticFullscreenQuad.renderUsing(shader);
    shader.end();
    to.end();
  }
}
