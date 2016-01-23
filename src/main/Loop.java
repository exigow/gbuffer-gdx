package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import main.debug.Benchmark;
import main.rendering.Blurer;
import main.rendering.Buffer;
import main.rendering.GBuffer;
import main.rendering.GBufferTexture;
import main.rendering.filters.*;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.Logger;
import main.utils.ResourceLoader;
import main.utils.lazyrun.Demo;

import static com.badlogic.gdx.math.MathUtils.lerp;
import static com.badlogic.gdx.math.MathUtils.sin;

public class Loop {

  private final static int WIDTH = Gdx.graphics.getWidth();
  private final static int HEIGHT = Gdx.graphics.getHeight();
  private final GBuffer gbuffer = GBuffer.withSize(WIDTH, HEIGHT);
  private final OrthographicCamera camera = createCamera();
  private final Buffer buffer = new Buffer();
  private final ShaderProgram mixColorWithBlurredEmissive = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/compose.frag");
  private final ShaderProgram showShader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/show.frag");
  private final ShaderProgram motionBlurShader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/motion_blur.frag");
  private final ShaderProgram mixShader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/mix-bloom.frag");
  private final Texture background = ResourceLoader.loadTexture("data/textures/back.png");
  private final Sharpen sharpen = new Sharpen();
  private final Fxaa fxaa = new Fxaa();
  private final AnamorphicFlares flares = new AnamorphicFlares();
  private final ChromaticAberration aberration = new ChromaticAberration();
  private final LuminanceCutoff cutoff = new LuminanceCutoff();
  private final GBufferTexture gBufferTexture = loadTestGBufferTexture();
  private final Blurer blurer = new Blurer();
  private float elapsedTime;
  private final FrameBuffer colorPlusEmissiveBuffer = FrameBufferCreator.createDefault(WIDTH, HEIGHT);
  private final PingPong pingPong = PingPong.withSize(WIDTH, HEIGHT);
  private final FrameBuffer cutoffBuffer = FrameBufferCreator.createDefault(512, 512);
  private final FrameBuffer bloomBuffer = FrameBufferCreator.createDefault(512, 512);

  private static OrthographicCamera createCamera() {
    OrthographicCamera cam = new OrthographicCamera();
    cam.setToOrtho(true, WIDTH, HEIGHT);
    return cam;
  }

  private static GBufferTexture loadTestGBufferTexture() {
    GBufferTexture texture = new GBufferTexture();
    texture.color = ResourceLoader.loadTexture("data/textures/ship-color.png");
    texture.emissive = ResourceLoader.loadTexture("data/textures/ship-emissive.png");
    return texture;
  }

  public void onUpdate(float delta) {
    elapsedTime += delta;

    Benchmark.start("storing vertex buffer");
    buffer.updateProjection(camera.combined);
    //renderRotatedQuad(WIDTH / 2, HEIGHT / 2, -elapsedTime, 1024);
    //renderRotatedQuad(256, 256, elapsedTime * 16, 256);
    //renderRotatedQuad(1024, 512, elapsedTime * .125f, 256 + sin(elapsedTime * 32) * 128);
    //renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 2) * .5f), 256, elapsedTime * 4, 128);
    //renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 3) * .5f), 768, elapsedTime * 8, 256);
    //renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 4) * .5f), 512, -elapsedTime * 12, 192);
    renderRotatedQuad(Gdx.input.getX(), Gdx.input.getY(), elapsedTime, 256);
    gbuffer.color.begin();
    clearContext();
    background.bind(0);
    showShader.begin();
    showShader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(showShader);
    showShader.end();
    buffer.paintColor(gBufferTexture.color);
    gbuffer.color.end();

    gbuffer.emissive.begin();
    clearContext();
    buffer.paintEmissive(gBufferTexture.emissive);
    gbuffer.emissive.end();

    gbuffer.velocity.begin();
    Gdx.gl20.glClearColor(.5f, .5f, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    buffer.paintVelocity(gBufferTexture.color);
    gbuffer.velocity.end();
    buffer.reset();
    Benchmark.end();

    Benchmark.start("blur emissive");
    blurer.blur(gbuffer.emissive);
    Benchmark.end();

    Benchmark.start("mix color & emissive");
    colorPlusEmissiveBuffer.begin();
    gbuffer.color.getColorBufferTexture().bind(0);
    blurer.getResult().getColorBufferTexture().bind(1);
    mixColorWithBlurredEmissive.begin();
    mixColorWithBlurredEmissive.setUniformi("u_texture_color", 0);
    mixColorWithBlurredEmissive.setUniformi("u_texture_emissive", 1);
    StaticFullscreenQuad.renderUsing(mixColorWithBlurredEmissive);
    mixColorWithBlurredEmissive.end();
    colorPlusEmissiveBuffer.end();
    Benchmark.end();

    Benchmark.start("motion blur");
    colorPlusEmissiveBuffer.getColorBufferTexture().bind(0);
    gbuffer.velocity.getColorBufferTexture().bind(1);
    pingPong.first.begin();
    motionBlurShader.begin();
    motionBlurShader.setUniformi("u_texture_source", 0);
    motionBlurShader.setUniformi("u_texture_velocity", 1);
    motionBlurShader.setUniformf("texel", 1f / HEIGHT);
    StaticFullscreenQuad.renderUsing(motionBlurShader);
    motionBlurShader.end();
    pingPong.first.end();
    Benchmark.end();

    Benchmark.start("luma cutoff");
    cutoff.apply(pingPong.first, cutoffBuffer);
    Benchmark.end();

    Benchmark.start("flares");
    flares.apply(cutoffBuffer, bloomBuffer);
    Benchmark.end();

    Benchmark.start("add flares");
    pingPong.second.begin();
    pingPong.first.getColorBufferTexture().bind(0);
    bloomBuffer.getColorBufferTexture().bind(1);
    mixShader.begin();
    mixShader.setUniformi("u_texture_base", 0);
    mixShader.setUniformi("u_texture_bloom", 1);
    StaticFullscreenQuad.renderUsing(mixShader);
    mixShader.end();
    pingPong.second.end();
    Benchmark.end();

    Benchmark.start("abber + fxaa + sharp");
    aberration.apply(pingPong.second, pingPong.first);
    fxaa.apply(pingPong.first, pingPong.second);
    sharpen.apply(pingPong.second, pingPong.first);
    Benchmark.end();

    show(pingPong.first);

    Logger.log(Gdx.graphics.getFramesPerSecond() + " " + Benchmark.generateRaportAndReset());
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

}
