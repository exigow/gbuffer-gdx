package main.rendering.filters;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

public class ChromaticAberration {

  private final ShaderProgram shader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/chromatic_aberration.frag");

  public void apply(FrameBuffer from, FrameBuffer to) {
    to.begin();
    from.getColorBufferTexture().bind(0);
    shader.begin();
    shader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(shader);
    shader.end();
    to.end();
  }
}
