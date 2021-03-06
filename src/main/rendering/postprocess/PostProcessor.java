package main.rendering.postprocess;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.rendering.GBuffer;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.TwoPassBufferBlurer;
import main.resources.ResourceLoader;

public class PostProcessor {

  private final ShaderEffect mixColorAndEmissive = ShaderEffect.createGeneric("data/screenspace/compose.frag");
  private final ShaderEffect sharpen = ShaderEffect.createGeneric("data/screenspace/sharpen.frag");
  private final ShaderEffect fxaa = ShaderEffect.createGeneric("data/screenspace/fxaa.frag");
  private final ShaderEffect flares = ShaderEffect.createGeneric("data/screenspace/flare.frag");
  private final ShaderEffect cutoff = ShaderEffect.createGeneric("data/screenspace/luminance-cutoff.frag");
  private final ShaderEffect aberration = ShaderEffect.createGeneric("data/screenspace/chromatic-aberration.frag");
  private final ShaderEffect mix = ShaderEffect.createGeneric("data/screenspace/mix-bloom.frag");
  private final TwoPassBufferBlurer blurer = new TwoPassBufferBlurer();
  private final FrameBuffer colorPlusEmissiveBuffer;
  private final FrameBuffer firstTempBuffer;
  private final FrameBuffer secondTempBuffer;
  private final FrameBuffer cutoffBuffer = FrameBufferCreator.createDefault(512, 512);
  private final FrameBuffer bloomBuffer = FrameBufferCreator.createDefault(512, 512);
  private final Texture lensDirt = ResourceLoader.loadTexture("data/textures/lens-dirt.png");
  private final int width;
  private final int height;

  private PostProcessor(int width, int height) {
    this.width = width;
    this.height = height;
    colorPlusEmissiveBuffer = FrameBufferCreator.createDefault(width, height);
    firstTempBuffer = FrameBufferCreator.createDefault(width, height);
    secondTempBuffer = FrameBufferCreator.createDefault(width, height);
  }

  public static PostProcessor withSize(int width, int height) {
    return new PostProcessor(width, height);
  }

  public void process(GBuffer gbuffer) {
    blurer.blur(gbuffer.emissive);

    mixColorAndEmissive.renderTo(colorPlusEmissiveBuffer)
      .bind("u_texture_color", gbuffer.color)
      .bind("u_texture_emissive", blurer.getResult())
      .flush();

    cutoff.renderTo(cutoffBuffer)
      .bind("u_texture", colorPlusEmissiveBuffer)
      .flush();

    blurer.blur(cutoffBuffer);

    flares.renderTo(bloomBuffer)
      .bind("u_texture", blurer.getResult())
      .bind("u_texture_lens_dirt", lensDirt)
      .flush();

    mix.renderTo(secondTempBuffer)
      .bind("u_texture_base", colorPlusEmissiveBuffer)
      .bind("u_texture_bloom", bloomBuffer)
      .flush();

    aberration.renderTo(firstTempBuffer)
      .bind("u_texture", secondTempBuffer)
      .flush();

    /*fxaa.renderTo(secondTempBuffer)
      .bind("u_texture", firstTempBuffer)
      .paramterize("FXAA_REDUCE_MIN", 1f / 128f)
      .paramterize("FXAA_REDUCE_MUL", 1f / 8f)
      .paramterize("FXAA_SPAN_MAX", 8f)
      .paramterize("texel", 1f / width, 1f / height)
      .flush();*/

    sharpen.renderTo(secondTempBuffer)
      .bind("u_texture", firstTempBuffer)
      .paramterize("texel", 1f / width, 1f / height)
      .flush();
  }

  public Texture getResult() {
    return secondTempBuffer.getColorBufferTexture();
  }
  
}
