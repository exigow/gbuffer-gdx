package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

public class Blurer {

  private final ShaderProgram kawaseShader = ResourceLoader.loadShader("data/screenspace.vert", "data/gauss_1d_pass_5_lookups.frag");
  public final FrameBuffer blurDownsamplesComposition;
  private final FrameBuffer blurTempVerticalDownsamplesComposition;

  public Blurer() {
    int size = 512;
    blurDownsamplesComposition = FrameBufferCreator.createDefault(size, size);
    blurTempVerticalDownsamplesComposition = FrameBufferCreator.createDefault(size, size);
  }

  public void blur(FrameBuffer source) {
    blitUsingKawase(source, blurTempVerticalDownsamplesComposition, 1, 0);
    blitUsingKawase(blurTempVerticalDownsamplesComposition, blurDownsamplesComposition, 0, 1);
  }

  private void blitUsingKawase(FrameBuffer from, FrameBuffer to, float x, float y) {
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
