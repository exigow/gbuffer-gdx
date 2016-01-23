package main.rendering.postprocess;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.debug.Benchmark;
import main.rendering.Blurer;
import main.rendering.GBuffer;
import main.rendering.utils.FrameBufferCreator;
import main.resources.ResourceLoader;

public class PostProcessor {

  private final ShaderEffect mixColorAndEmissive = ShaderEffect.createGeneric("data/screenspace/compose.frag");
  private final ShaderEffect sharpen = ShaderEffect.createGeneric("data/screenspace/sharpen.frag");
  private final ShaderEffect fxaa = ShaderEffect.createGeneric("data/screenspace/fxaa.frag");
  private final ShaderEffect flares = ShaderEffect.createGeneric("data/screenspace/flare.frag");
  private final ShaderEffect cutoff = ShaderEffect.createGeneric("data/screenspace/luminance-cutoff.frag");
  private final ShaderEffect aberration = ShaderEffect.createGeneric("data/screenspace/chromatic-aberration.frag");
  private final ShaderEffect motionBlur = ShaderEffect.createGeneric("data/screenspace/motion-blur.frag");
  private final ShaderEffect mix = ShaderEffect.createGeneric("data/screenspace/mix-bloom.frag");
  private final Blurer blurer = new Blurer();
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
    Benchmark.start("blur emissive");
    blurer.blur(gbuffer.emissive);
    Benchmark.end();

    Benchmark.start("mix color & emissive");
    mixColorAndEmissive.renderTo(colorPlusEmissiveBuffer)
      .bind("u_texture_color", 0, gbuffer.color)
      .bind("u_texture_emissive", 1, blurer.getResult())
      .flush();
    Benchmark.end();

    Benchmark.start("motion blur");
    motionBlur.renderTo(firstTempBuffer)
      .bind("u_texture_source", 0, colorPlusEmissiveBuffer)
      .bind("u_texture_velocity", 1, gbuffer.velocity)
      .paramterize("texel", 1f / height)
      .flush();
    Benchmark.end();

    Benchmark.start("luma cutoff");
    cutoff.renderTo(cutoffBuffer)
      .bind("u_texture", 0, firstTempBuffer)
      .flush();
    Benchmark.end();

    Benchmark.start("flares");
    flares.renderTo(bloomBuffer)
      .bind("u_texture", 0, cutoffBuffer)
      .bind("u_texture_lens_dirt", 1, lensDirt)
      .flush();
    Benchmark.end();

    Benchmark.start("add flares");
    mix.renderTo(secondTempBuffer)
      .bind("u_texture_base", 0, firstTempBuffer)
      .bind("u_texture_bloom", 1, bloomBuffer)
      .flush();
    Benchmark.end();

    Benchmark.start("abberation");
    aberration.renderTo(firstTempBuffer)
      .bind("u_texture", 0, secondTempBuffer)
      .paramterize("texel", 1f / width, 1f / height)
      .flush();
    Benchmark.end();

    Benchmark.start("fxaa");
    fxaa.renderTo(secondTempBuffer)
      .bind("u_texture", 0, firstTempBuffer)
      .paramterize("FXAA_REDUCE_MIN", 1f / 128f)
      .paramterize("FXAA_REDUCE_MUL", 1f / 8f)
      .paramterize("FXAA_SPAN_MAX", 8f)
      .paramterize("texel", 1f / width, 1f / height)
      .flush();
    Benchmark.end();

    Benchmark.start("sharpen");
    sharpen.renderTo(firstTempBuffer)
      .bind("u_texture", 0, secondTempBuffer)
      .paramterize("texel", 1f / width, 1f / height)
      .flush();
    Benchmark.end();
  }

  public Texture getResult() {
    return firstTempBuffer.getColorBufferTexture();
  }
  
}
