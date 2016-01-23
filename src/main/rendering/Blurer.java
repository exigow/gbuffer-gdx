package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

public class Blurer {

  private final ShaderProgram shader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/blur-gauss.frag");
  private final FrameBuffer vertical;
  private final FrameBuffer horizontal;

  public Blurer(int size) {
    vertical = FrameBufferCreator.createDefault(size, size);
    horizontal = FrameBufferCreator.createDefault(size, size);
  }

  public void blur(FrameBuffer source, float firstX, float firstY, float secondX, float secondY) {
    blur(source, vertical, firstX, firstY);
    blur(vertical, horizontal, secondX, secondY);
  }

  public FrameBuffer getResult() {
    return horizontal;
  }

  private void blur(FrameBuffer from, FrameBuffer to, float x, float y) {
    float texel = 1f / to.getWidth();
    float[] offset = new float[] {texel * x, texel * y};
    to.begin();
    from.getColorBufferTexture().bind(0);
    shader.begin();
    shader.setUniformi("u_texture", 0);
    shader.setUniform2fv("offset", offset, 0, offset.length);
    StaticFullscreenQuad.renderUsing(shader);
    shader.end();
    to.end();
  }

}
