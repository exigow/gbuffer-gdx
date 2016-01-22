package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.rendering.Blurer;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;
import main.utils.lazyrun.Demo;
import main.utils.lazyrun.GdxInitializer;

import static com.badlogic.gdx.math.MathUtils.sin;
import static java.lang.Math.min;

public class FlareTest implements Demo {

  private final static int WIDTH = 1280;
  private final static int HEIGHT = 768;
  private final FrameBuffer sourceBuffer = FrameBufferCreator.createDefault(WIDTH, HEIGHT);
  //private final FrameBuffer bufferA = FrameBufferCreator.createDefault(512, 512);
  //private final FrameBuffer bufferB = FrameBufferCreator.createDefault(512, 512);
  private final Blurer blurer = new Blurer();
  //private final ShaderProgram blurShader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/gauss_1d_pass_5_lookups.frag");
  private final ShaderProgram flareShader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/flare.frag");
  private final ShapeRenderer renderer = new ShapeRenderer();
  private final OrthographicCamera camera = createCamera();
  private final Texture lensDirt = ResourceLoader.loadTexture("data/textures/lens-dirt.png");
  private final static int CENTER_X = WIDTH / 2;
  private final static int CENTER_Y = HEIGHT / 2;
  private float time = 0;

  @Override
  public void onUpdate(float delta) {
    time += delta;

    sourceBuffer.begin();
    clearContext();
    renderer.setProjectionMatrix(camera.combined);
    renderer.begin(ShapeRenderer.ShapeType.Filled);
    circle(Gdx.input.getX(), Gdx.input.getY(), 32, Color.WHITE);
    centeredCircle(sin(time * .57f), sin(time * 1.32f), 16 + 8 * sin(time * 2.13f), Color.RED);
    centeredCircle(sin(time * 1.23f), sin(time * 3.19f), 16 + 8 * sin(time * 1.08f), Color.GREEN);
    centeredCircle(sin(time * 2.65f), sin(time * .86f), 16 + 8 * sin(time * 4.61f), Color.BLUE);
    centeredCircle(sin(time * .97f), sin(time * 2.32f), 16 + 8 * sin(time * 1.23f), Color.CYAN);
    centeredCircle(sin(time * 4.35f), sin(time * 1.34f), 16 + 8 * sin(time * .89f), Color.PURPLE);
    renderer.end();
    sourceBuffer.end();

    blurer.blur(sourceBuffer, 1, 1);

    blurer.result.getColorBufferTexture().bind(0);
    lensDirt.bind(1);
    flareShader.begin();
    flareShader.setUniformi("u_texture", 0);
    flareShader.setUniformi("u_texture_lens_dirt", 1);
    StaticFullscreenQuad.renderUsing(flareShader);
    flareShader.end();
  }

  private void centeredCircle(float x, float y, float size, Color color) {
    circle(CENTER_X + x * 256, CENTER_Y + y * 256, size, color);
  }

  private void circle(float x, float y, float size, Color color) {
    float up = .25f;
    renderer.setColor(min(up + color.r, 1), min(up + color.g, 1), min(up + color.b, 1), 1);
    renderer.circle(x, y, size);
  }

  private static void clearContext() {
    Gdx.gl20.glClearColor(0, 0, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

  private static OrthographicCamera createCamera() {
    OrthographicCamera cam = new OrthographicCamera();
    cam.setToOrtho(true, WIDTH, HEIGHT);
    return cam;
  }

  public static void main(String[] args) {
    GdxInitializer.initializeLazy(FlareTest::new);
  }

}
