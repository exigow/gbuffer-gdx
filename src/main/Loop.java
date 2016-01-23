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
import main.rendering.postprocess.PostProcessor;
import main.rendering.postprocess.ShaderEffect;
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
  private final MaterialsStock materials = MaterialsStock.loadMaterials();
  private final PostProcessor postProcessor = PostProcessor.withSize(WIDTH, HEIGHT);
  private float elapsedTime;

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
    clearContext(0, 0, 0);
    background.bind(0);
    showShader.begin();
    showShader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(showShader);
    showShader.end();
    buffer.paintColor(materials.get("ship").color);
    gbuffer.color.end();

    gbuffer.emissive.begin();
    clearContext(0, 0, 0);
    buffer.paintEmissive(materials.get("ship").emissive);
    gbuffer.emissive.end();

    gbuffer.velocity.begin();
    clearContext(.5f, .5f, 0);
    buffer.paintVelocity(materials.get("ship").color);
    gbuffer.velocity.end();
    buffer.reset();
    Benchmark.end();

    postProcessor.process(gbuffer);
    show(postProcessor.getResult());

    Log.log(Gdx.graphics.getFramesPerSecond() + " " + Benchmark.generateRaportAndReset());
  }

  private void show(Texture show) {
    show.bind(0);
    showShader.begin();
    showShader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(showShader);
    showShader.end();
  }

  private static void clearContext(float r, float g, float b) {
    Gdx.gl20.glClearColor(r, g, b, 1);
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
