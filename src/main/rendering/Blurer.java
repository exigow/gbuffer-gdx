package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.filters.ShaderEffect;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.resources.ResourceLoader;

public class Blurer {

  private static final int SIZE = 512;
  private final ShaderEffect shader = ShaderEffect.createGeneric("data/screenspace/blur-gauss.frag");
  private final FrameBuffer vertical = FrameBufferCreator.createDefault(SIZE, SIZE);
  private final FrameBuffer horizontal = FrameBufferCreator.createDefault(SIZE, SIZE);

  public void blur(FrameBuffer source) {
    blur(source, vertical, 1, 0);
    blur(vertical, horizontal, 0, 1);
  }

  public FrameBuffer getResult() {
    return horizontal;
  }

  private void blur(FrameBuffer from, FrameBuffer to, float x, float y) {
    shader.renderTo(to)
      .bind("u_texture", 0, from)
      .paramterize("offset", x / SIZE, y / SIZE)
      .flush();
  }

}
