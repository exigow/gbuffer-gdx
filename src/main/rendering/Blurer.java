package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Blurer {

  private final ShaderProgram kawaseShader = ResourceLoader.loadShader("data/screenspace.vert", "data/blur_3x3.frag");
  private final ShaderProgram composeBlursShader = ResourceLoader.loadShader("data/screenspace.vert", "data/composeBlurs.frag");
  private final List<FrameBuffer> blurDownsamples;
  public final FrameBuffer blurDownsamplesComposition;
  private final FrameBuffer blurTempVerticalDownsamplesComposition;

  public Blurer() {
    blurDownsamples = createDownsamples();
    blurDownsamplesComposition = FrameBufferCreator.createDefault(256, 256);
    blurTempVerticalDownsamplesComposition = FrameBufferCreator.createDefault(256, 256);
  }

  private List<FrameBuffer> createDownsamples() {
    return Arrays.asList(256, 128, 64).stream()
      .map(size -> FrameBufferCreator.createDefault(size, size))
      .collect(Collectors.toList());
  }

  public void blur(FrameBuffer source) {
    blur(source, blurTempVerticalDownsamplesComposition, 1, 0);
    blur(blurTempVerticalDownsamplesComposition, blurDownsamplesComposition, 0, 1);
  }

  private void blur(FrameBuffer source, FrameBuffer to, float x, float y) {
    blitUsingKawase(source, blurDownsamples.get(0), x, y);
    for (int i = 1; i < 3; i++)
      blitUsingKawase(blurDownsamples.get(i - 1), blurDownsamples.get(i), x, y);
    to.begin();
    composeBlursShader.begin();
    for (int i = 0; i < blurDownsamples.size(); i++) {
      blurDownsamples.get(i).getColorBufferTexture().bind(i);
      composeBlursShader.setUniformi("u_texture[" + i + "]", i);
    }
    StaticFullscreenQuad.renderUsing(composeBlursShader);
    composeBlursShader.end();
    to.end();
  }

  private void blitUsingKawase(FrameBuffer from, FrameBuffer to, float x, float y) {
    float texel = 1f / to.getHeight();
    to.begin();
    clearContext();
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

  private static void clearContext() {
    Gdx.gl20.glClearColor(0, 0, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

}
