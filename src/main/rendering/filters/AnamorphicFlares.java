package main.rendering.filters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

public class AnamorphicFlares {

  private final ShaderProgram shader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/flare.frag");
  private final Texture lensDirt = ResourceLoader.loadTexture("data/textures/lens-dirt.png");

  public void apply(FrameBuffer from, FrameBuffer to) {
    to.begin();
    from.getColorBufferTexture().bind(0);
    lensDirt.bind(1);
    shader.begin();
    shader.setUniformi("u_texture", 0);
    shader.setUniformi("u_texture_lens_dirt", 1);
    StaticFullscreenQuad.renderUsing(shader);
    shader.end();
    to.end();
  }

}
