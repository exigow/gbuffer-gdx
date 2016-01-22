package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import main.debug.Benchmark;
import main.rendering.Blurer;
import main.rendering.Buffer;
import main.rendering.GBuffer;
import main.rendering.GBufferTexture;
import main.rendering.filters.ChromaticAberration;
import main.rendering.filters.Fxaa;
import main.rendering.filters.PingPong;
import main.rendering.filters.Sharpen;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.Logger;
import main.utils.ResourceLoader;

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
  private final Sharpen sharpen = new Sharpen();
  private final Fxaa fxaa = new Fxaa();
  private final ChromaticAberration aberration = new ChromaticAberration();
  private final GBufferTexture gBufferTexture = loadTestGBufferTexture();
  private final Blurer blurer = new Blurer();
  private float elapsedTime;
  private final FrameBuffer colorPlusEmissiveBuffer = FrameBufferCreator.createDefault(WIDTH, HEIGHT);
  private final PingPong pingPong = PingPong.withSize(WIDTH, HEIGHT);

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
    renderRotatedQuad(WIDTH / 2, HEIGHT / 2, -elapsedTime, 784);
    renderRotatedQuad(256, 256, elapsedTime * 16, 256);
    renderRotatedQuad(1024, 512, elapsedTime * .125f, 256 + sin(elapsedTime * 32) * 128);
    renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 2) * .5f), 256, elapsedTime * 4, 128);
    renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 3) * .5f), 768, elapsedTime * 8, 256);
    renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 4) * .5f), 512, -elapsedTime * 12, 192);
    renderRotatedQuad(Gdx.input.getX(), Gdx.input.getY(), elapsedTime * .125f, 256);
    fillColor(gbuffer.color, gBufferTexture.color);
    fillColor(gbuffer.emissive, gBufferTexture.emissive);
    gbuffer.velocity.begin();
    Gdx.gl20.glClearColor(.5f, .5f, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    buffer.paintVelocity(gBufferTexture.color);
    gbuffer.velocity.end();
    buffer.reset();
    Benchmark.end();

    Benchmark.start("blur emissive");
    blurer.blur(gbuffer.emissive, 1);
    Benchmark.end();

    Benchmark.start("mix color & emissive");
    colorPlusEmissiveBuffer.begin();
    gbuffer.color.getColorBufferTexture().bind(0);
    blurer.result.getColorBufferTexture().bind(1);
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

    Benchmark.start("cool stuff");
    aberration.apply(pingPong.first, pingPong.second);
    fxaa.apply(pingPong.second, pingPong.first);
    sharpen.apply(pingPong.first, pingPong.second);
    Benchmark.end();

    show(pingPong.second);

    Logger.log(Gdx.graphics.getFramesPerSecond() + " " + Benchmark.generateRaportAndReset());
  }

  private void show(FrameBuffer show) {
    show.getColorBufferTexture().bind(0);
    showShader.begin();
    showShader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(showShader);
    showShader.end();
  }

  private void fillColor(FrameBuffer frameBuffer, Texture texture) {
    frameBuffer.begin();
    clearContext();
    buffer.paintColor(texture);
    frameBuffer.end();
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
