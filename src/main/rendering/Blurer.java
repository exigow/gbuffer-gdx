package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

public class Blurer {

  private static final int ORIGINAL_SIZE = 256;
  private static final int HALF_SIZE = 128;
  private final ShaderProgram kawaseShader = ResourceLoader.loadShader("data/screenspace.vert", "data/gauss_1d_pass_5_lookups.frag");
  private final ShaderProgram addShader = ResourceLoader.loadShader("data/screenspace.vert", "data/add.frag");
  public final FrameBuffer result = FrameBufferCreator.createDefault(ORIGINAL_SIZE, ORIGINAL_SIZE);
  //public final FrameBuffer originalVertical = FrameBufferCreator.createDefault(ORIGINAL_SIZE, ORIGINAL_SIZE);
  //public final FrameBuffer originalHorizontal = FrameBufferCreator.createDefault(ORIGINAL_SIZE, ORIGINAL_SIZE);
  private final TwoPassBlurBuffer half = new TwoPassBlurBuffer(HALF_SIZE);
  private final TwoPassBlurBuffer original = new TwoPassBlurBuffer(ORIGINAL_SIZE);

  public Blurer() {
  }

  public void blur(FrameBuffer source) {
    blur(source, original.vertical, 1, 0);
    blur(original.vertical, original.horizontal, 0, 1);

    blur(original.horizontal, half.vertical, 1, 0);
    blur(half.vertical, half.horizontal, 0, 1);

    mix(original.horizontal, half.horizontal, result);
  }

  /*private void twoPassBlur(FrameBuffer source) {
    blur(source, half.vertical, 1, 0);
    blur(half.vertical, half.horizontal, 0, 1);
  }*/

  private void blur(FrameBuffer from, FrameBuffer to, float x, float y) {
    float texel = 1f / to.getHeight();
    to.begin();
    from.getColorBufferTexture().bind(0);
    kawaseShader.begin();
    kawaseShader.setUniformi("u_texture", 0);
    kawaseShader.setUniformf("texel", texel);
    kawaseShader.setUniformf("vecX", x);
    kawaseShader.setUniformf("vecY", y);
    StaticFullscreenQuad.renderUsing(kawaseShader);
    kawaseShader.end();
    to.end();
  }

  private void mix(FrameBuffer a, FrameBuffer b, FrameBuffer to) {
    a.getColorBufferTexture().bind(0);
    b.getColorBufferTexture().bind(1);
    to.begin();
    addShader.begin();
    addShader.setUniformi("u_texture_a", 0);
    addShader.setUniformi("u_texture_b", 1);
    StaticFullscreenQuad.renderUsing(addShader);
    addShader.end();
    to.end();
  }

  private static class TwoPassBlurBuffer {

    public final FrameBuffer vertical;
    public final FrameBuffer horizontal;

    public TwoPassBlurBuffer(int size) {
      vertical = FrameBufferCreator.createDefault(size, size);
      horizontal = FrameBufferCreator.createDefault(size, size);
    }

  }

}
