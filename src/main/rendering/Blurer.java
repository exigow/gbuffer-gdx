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

  public Blurer() {
    blurDownsamples = createDownsamples();
    blurDownsamplesComposition = FrameBufferCreator.createDefault(512, 512);
  }

  private List<FrameBuffer> createDownsamples() {
    return Arrays.asList(512, 256, 128).stream()
      .map(size -> FrameBufferCreator.createDefault(size, size))
      .collect(Collectors.toList());
  }

  public void blur(FrameBuffer source) {
    blitUsingKawase(source, blurDownsamples.get(0));
    for (int i = 1; i < 3; i++)
      blitUsingKawase(blurDownsamples.get(i - 1), blurDownsamples.get(i));

    blurDownsamplesComposition.begin();
    composeBlursShader.begin();
    for (int i = 0; i < blurDownsamples.size(); i++) {
      blurDownsamples.get(i).getColorBufferTexture().bind(i);
      composeBlursShader.setUniformi("u_texture[" + i + "]", i);
    }
    StaticFullscreenQuad.renderUsing(composeBlursShader);
    composeBlursShader.end();
    blurDownsamplesComposition.end();
  }

  private void blitUsingKawase(FrameBuffer from, FrameBuffer to) {
    float texel = 1f / to.getWidth();
    to.begin();
    clearContext();
    from.getColorBufferTexture().bind(0);
    kawaseShader.begin();
    kawaseShader.setUniformi("u_texture", 0);
    kawaseShader.setUniformf("texel", texel);
    StaticFullscreenQuad.renderUsing(kawaseShader);
    kawaseShader.end();
    to.end();
  }

  private static void clearContext() {
    Gdx.gl20.glClearColor(0, 0, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

}
