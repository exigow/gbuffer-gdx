package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import main.debug.Benchmark;
import main.logging.Log;
import main.rendering.Blurer;
import main.rendering.Buffer;
import main.rendering.GBuffer;
import main.rendering.filters.*;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.resources.MaterialsStock;
import main.resources.ResourceLoader;
import main.runner.Demo;
import main.runner.GdxInitializer;

import static com.badlogic.gdx.math.MathUtils.lerp;
import static com.badlogic.gdx.math.MathUtils.sin;

public class Loop implements Demo {

  private final static int WIDTH = 1024;
  private final static int HEIGHT = 768;
  private final GBuffer gbuffer = GBuffer.withSize(WIDTH, HEIGHT);
  private final OrthographicCamera camera = createCamera();
  private final Buffer buffer = new Buffer();
  private final ShaderProgram showShader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/show.frag");
  private final Texture background = ResourceLoader.loadTexture("data/textures/back.png");
  private final ShaderEffect mixColorAndEmissive = ShaderEffect.createGeneric("data/screenspace/compose.frag");
  private final ShaderEffect sharpen = ShaderEffect.createGeneric("data/screenspace/sharpen.frag");
  private final ShaderEffect fxaa = ShaderEffect.createGeneric("data/screenspace/fxaa.frag");
  private final ShaderEffect flares = ShaderEffect.createGeneric("data/screenspace/flare.frag");
  private final ShaderEffect cutoff = ShaderEffect.createGeneric("data/screenspace/luminance-cutoff.frag");
  private final ShaderEffect aberration = ShaderEffect.createGeneric("data/screenspace/chromatic-aberration.frag");
  private final ShaderEffect motionBlur = ShaderEffect.createGeneric("data/screenspace/motion-blur.frag");
  private final ShaderEffect mix = ShaderEffect.createGeneric("data/screenspace/mix-bloom.frag");
  private final MaterialsStock materials = MaterialsStock.loadMaterials();
  private final Blurer blurer = new Blurer();
  private float elapsedTime;
  private final FrameBuffer colorPlusEmissiveBuffer = FrameBufferCreator.createDefault(WIDTH, HEIGHT);
  private final PingPong pingPong = PingPong.withSize(WIDTH, HEIGHT);
  private final FrameBuffer cutoffBuffer = FrameBufferCreator.createDefault(512, 512);
  private final FrameBuffer bloomBuffer = FrameBufferCreator.createDefault(512, 512);
  private final Texture lensDirt = ResourceLoader.loadTexture("data/textures/lens-dirt.png");

  private static OrthographicCamera createCamera() {
    OrthographicCamera cam = new OrthographicCamera();
    cam.setToOrtho(true, WIDTH, HEIGHT);
    return cam;
  }

  @Override
  public void onUpdate(float delta) {
    elapsedTime += delta;

    Benchmark.start("painting gbuffer");
    buffer.updateProjection(camera.combined);
    renderRotatedQuad(768, 512, elapsedTime * .125f, 256 + sin(elapsedTime * 32) * 128);
    renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 3) * .5f), 256, 0, 256);
    renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 4) * .5f), 512, -elapsedTime * 12, 256);
    renderRotatedQuad(Gdx.input.getX(), Gdx.input.getY(), elapsedTime, 256);
    gbuffer.color.begin();
    clearContext();
    background.bind(0);
    showShader.begin();
    showShader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(showShader);
    showShader.end();
    buffer.paintColor(materials.get("ship").color);
    gbuffer.color.end();

    gbuffer.emissive.begin();
    clearContext();
    buffer.paintEmissive(materials.get("ship").emissive);
    gbuffer.emissive.end();

    gbuffer.velocity.begin();
    Gdx.gl20.glClearColor(.5f, .5f, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    buffer.paintVelocity(materials.get("ship").color);
    gbuffer.velocity.end();
    buffer.reset();
    Benchmark.end();

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
    motionBlur.renderTo(pingPong.first)
      .bind("u_texture_source", 0, colorPlusEmissiveBuffer)
      .bind("u_texture_velocity", 1, gbuffer.velocity)
      .paramterize("texel", 1f / HEIGHT)
      .flush();
    Benchmark.end();

    Benchmark.start("luma cutoff");
    cutoff.renderTo(cutoffBuffer)
      .bind("u_texture", 0, pingPong.first)
      .flush();
    Benchmark.end();

    Benchmark.start("flares");
    flares.renderTo(bloomBuffer)
      .bind("u_texture", 0, cutoffBuffer)
      .bind("u_texture_lens_dirt", 1, lensDirt)
      .flush();
    Benchmark.end();

    Benchmark.start("add flares");
    mix.renderTo(pingPong.second)
      .bind("u_texture_base", 0, pingPong.first)
      .bind("u_texture_bloom", 1, bloomBuffer)
      .flush();
    Benchmark.end();

    Benchmark.start("abberation");
    aberration.renderTo(pingPong.first)
      .bind("u_texture", 0, pingPong.second)
      .paramterize("texel", 1f / WIDTH, 1f / HEIGHT)
      .flush();
    Benchmark.end();

    Benchmark.start("fxaa");
    fxaa.renderTo(pingPong.second)
      .bind("u_texture", 0, pingPong.first)
      .paramterize("FXAA_REDUCE_MIN", 1f / 128f)
      .paramterize("FXAA_REDUCE_MUL", 1f / 8f)
      .paramterize("FXAA_SPAN_MAX", 8f)
      .paramterize("texel", 1f / WIDTH, 1f / HEIGHT)
      .flush();
    Benchmark.end();

    Benchmark.start("sharpen");
    sharpen.renderTo(pingPong.first)
      .bind("u_texture", 0, pingPong.second)
      .paramterize("texel", 1f / WIDTH, 1f / HEIGHT)
      .flush();
    Benchmark.end();

    show(pingPong.first);

    Log.log(Gdx.graphics.getFramesPerSecond() + " " + Benchmark.generateRaportAndReset());
  }

  private void show(FrameBuffer show) {
    show.getColorBufferTexture().bind(0);
    showShader.begin();
    showShader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(showShader);
    showShader.end();
  }

  private static void clearContext() {
    Gdx.gl20.glClearColor(0, 0, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

  private void renderRotatedQuad(float x, float y, float r, float scale) {
    float cos = MathUtils.cos(r);
    float sin = sin(r);
    buffer.putVertex(x - cos * scale, y - sin * scale, 0, 0);
    buffer.putVertex(x + sin * scale, y - cos * scale, 1, 0);
    buffer.putVertex(x + cos * scale, y + sin * scale, 1, 1);
    buffer.putVertex(x - sin * scale, y + cos * scale, 0, 1);
  }

  public static void main(String[] args) {
    GdxInitializer.initializeLazy(Loop::new);
  }

}
