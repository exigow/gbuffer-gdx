package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.debug.Benchmark;
import main.rendering.Blurer;
import main.rendering.Buffer;
import main.rendering.GBuffer;
import main.rendering.GBufferTexture;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.Logger;
import main.utils.ResourceLoader;

public class Loop {

  private final static int WIDTH = Gdx.graphics.getWidth();
  private final static int HEIGHT = Gdx.graphics.getHeight();
  private final GBuffer gbuffer = GBuffer.withSize(WIDTH, HEIGHT);
  private final OrthographicCamera camera = createCamera();
  private final Buffer buffer = new Buffer();
  private final ShaderProgram composeForBloomShader = ResourceLoader.loadShader("data/screenspace.vert", "data/composeForBloom.frag");
  private final ShaderProgram flareShader = ResourceLoader.loadShader("data/screenspace.vert", "data/flare.frag");
  private final ShaderProgram showShader = ResourceLoader.loadShader("data/screenspace.vert", "data/buffer.frag");
  private final ShaderProgram composeShader = ResourceLoader.loadShader("data/screenspace.vert", "data/compose.frag");
  private final GBufferTexture gBufferTexture = loadTestGBufferTexture();
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();
  private final Blurer blurer = new Blurer();
  private float elapsedTime;
  private final FrameBuffer tempBuffer = FrameBufferCreator.createDefault(512, 512);

  private static OrthographicCamera createCamera() {
    OrthographicCamera cam = new OrthographicCamera();
    cam.setToOrtho(true, WIDTH, HEIGHT);
    return cam;
  }

  private static GBufferTexture loadTestGBufferTexture() {
    GBufferTexture texture = new GBufferTexture();
    texture.color = ResourceLoader.loadTexture("data/textures/wall_color.png");
    texture.emissive = ResourceLoader.loadTexture("data/textures/wall_emissive.png");
    return texture;
  }

  public void onUpdate(float delta) {
    elapsedTime += delta;

    Benchmark.start("storing vertex buffer");
    buffer.updateProjection(camera.combined);
    renderQuad(384, 384);
    fillUsing(gbuffer.color, gBufferTexture.color);
    fillUsing(gbuffer.emissive, gBufferTexture.emissive);
    buffer.reset();
    Benchmark.end();

    gbuffer.emissive.begin();
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(1, .75f, .5f, 1);
    shapeRenderer.circle(Gdx.input.getX(), Gdx.input.getY(), 16);
    shapeRenderer.end();
    gbuffer.emissive.end();

    Benchmark.start("emissive blur");
    blurer.blur(gbuffer.emissive);
    Benchmark.end();

    Benchmark.start("mixing bloom buffer");
    tempBuffer.begin();
    gbuffer.color.getColorBufferTexture().bind(0);
    blurer.blurDownsamplesComposition.getColorBufferTexture().bind(1);
    composeForBloomShader.begin();
    composeForBloomShader.setUniformi("u_texture_color", 0);
    composeForBloomShader.setUniformi("u_texture_emissive", 1);
    StaticFullscreenQuad.renderUsing(composeForBloomShader);
    composeForBloomShader.end();
    tempBuffer.end();
    Benchmark.end();

    Benchmark.start("bloom blur");
    blurer.blur(tempBuffer);
    Benchmark.end();

    Benchmark.start("anamorphic flares");
    tempBuffer.begin();
    blurer.blurDownsamplesComposition.getColorBufferTexture().bind(0);
    flareShader.begin();
    flareShader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(flareShader);
    flareShader.end();
    tempBuffer.end();
    Benchmark.end();

    Benchmark.start("mixing & presentation");
    gbuffer.color.getColorBufferTexture().bind(0);
    tempBuffer.getColorBufferTexture().bind(1);
    composeShader.begin();
    composeShader.setUniformi("u_texture_color", 0);
    composeShader.setUniformi("u_texture_bloom", 1);
    StaticFullscreenQuad.renderUsing(composeShader);
    composeShader.end();
    Benchmark.end();

    Logger.log("" + Benchmark.generateRaportAndReset());
  }

  private void fillUsing(FrameBuffer frameBuffer, Texture texture) {
    frameBuffer.begin();
    clearContext();
    buffer.paint(texture);
    frameBuffer.end();
  }

  private static void clearContext() {
    Gdx.gl20.glClearColor(0, 0, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

  private void renderQuad(float x, float y) {
    float scale = 256;
    buffer.putVertex(x - scale, y - scale, 0, 0);
    buffer.putVertex(x + scale, y - scale, 1, 0);
    buffer.putVertex(x + scale, y + scale, 1, 1);
    buffer.putVertex(x - scale, y + scale, 0, 1);
  }

}
