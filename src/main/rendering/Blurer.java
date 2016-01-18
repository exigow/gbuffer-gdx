package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

public class Blurer {

  private static final int SIZE = 512;
  private final ShaderProgram addShader = ResourceLoader.loadShader("data/screenspace.vert", "data/add.frag");
  public final FrameBuffer result = FrameBufferCreator.createDefault(SIZE, SIZE);
  private final TwoPassBlurBuffer sub512 = new TwoPassBlurBuffer(SIZE);
  private final TwoPassBlurBuffer sub256 = new TwoPassBlurBuffer(SIZE / 2);
  private final TwoPassBlurBuffer sub128 = new TwoPassBlurBuffer(SIZE / 4);
  private final TwoPassBlurBuffer sub64 = new TwoPassBlurBuffer(SIZE / 8);

  public Blurer() {
  }

  public void blur(FrameBuffer source, float factor) {
    sub512.twoPassBlur(source, factor);
    sub256.twoPassBlur(sub512.horizontal, factor);
    sub128.twoPassBlur(sub256.horizontal, factor);
    sub64.twoPassBlur(sub128.horizontal, factor);
    mix(sub512.horizontal, sub256.horizontal, sub128.horizontal, sub64.horizontal, result);
  }

  private void mix(FrameBuffer a, FrameBuffer b, FrameBuffer c, FrameBuffer d, FrameBuffer to) {
    a.getColorBufferTexture().bind(0);
    b.getColorBufferTexture().bind(1);
    c.getColorBufferTexture().bind(2);
    d.getColorBufferTexture().bind(3);
    to.begin();
    addShader.begin();
    addShader.setUniformi("u_texture_a", 0);
    addShader.setUniformi("u_texture_b", 1);
    addShader.setUniformi("u_texture_c", 2);
    addShader.setUniformi("u_texture_d", 3);
    StaticFullscreenQuad.renderUsing(addShader);
    addShader.end();
    to.end();
  }

  private static class TwoPassBlurBuffer {

    private final ShaderProgram kawaseShader = ResourceLoader.loadShader("data/screenspace.vert", "data/gauss_1d_pass_5_lookups.frag");
    public final FrameBuffer vertical;
    public final FrameBuffer horizontal;

    public TwoPassBlurBuffer(int size) {
      vertical = FrameBufferCreator.createDefault(size, size);
      horizontal = FrameBufferCreator.createDefault(size, size);
    }

    public void twoPassBlur(FrameBuffer source, float factor) {
      blur(source, vertical, 1 * factor, 0);
      blur(vertical, horizontal, 0, 1 * factor);
    }

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

  }

}
