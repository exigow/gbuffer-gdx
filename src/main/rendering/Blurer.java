package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

import java.util.Arrays;

public class Blurer {

  private static final int SIZE = 512;
  private final ShaderProgram shader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/blur-gauss.frag");
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
    float texel = 1f / SIZE;
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
