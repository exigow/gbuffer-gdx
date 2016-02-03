package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import main.camera.CameraController;
import main.rendering.utils.FrameBufferCreator;
import main.rendering.utils.StaticFullscreenQuad;
import main.rendering.utils.TwoPassBufferBlurer;
import main.resources.ResourceLoader;
import main.runner.Demo;
import main.runner.GdxInitializer;

import static com.badlogic.gdx.math.MathUtils.sin;
import static java.lang.Math.min;

public class FlareTest implements Demo {

  private final static int WIDTH = 1024;
  private final static int HEIGHT = 768;
  private final FrameBuffer sourceBuffer = FrameBufferCreator.createDefault(WIDTH, HEIGHT);
  private final TwoPassBufferBlurer blurer = new TwoPassBufferBlurer();
  private final ShaderProgram flareShader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/flare.frag");
  private final ShapeRenderer renderer = new ShapeRenderer();
  private final CameraController cameraController = CameraController.setUp(WIDTH, HEIGHT);
  private final Texture lensDirt = ResourceLoader.loadTexture("data/textures/lens-dirt.png");
  private float time = 0;

  @Override
  public void onUpdate(float delta) {
    time += delta;
    cameraController.update(delta);

    sourceBuffer.begin();
    clearContext();
    renderer.setProjectionMatrix(cameraController.matrix());
    renderer.begin(ShapeRenderer.ShapeType.Filled);
    Vector2 mouse = cameraController.unprojectedMouse();
    circle(mouse.x, mouse.y, 32, Color.WHITE);
    scaledCircle(sin(time * .57f), sin(time * 1.32f), 16 + 8 * sin(time * 2.13f), Color.RED);
    scaledCircle(sin(time * 1.23f), sin(time * 3.19f), 16 + 8 * sin(time * 1.08f), Color.GREEN);
    scaledCircle(sin(time * 2.65f), sin(time * .86f), 16 + 8 * sin(time * 4.61f), Color.BLUE);
    scaledCircle(sin(time * .97f), sin(time * 2.32f), 16 + 8 * sin(time * 1.23f), Color.CYAN);
    scaledCircle(sin(time * 4.35f), sin(time * 1.34f), 16 + 8 * sin(time * .89f), Color.PURPLE);
    renderer.end();
    sourceBuffer.end();

    blurer.blur(sourceBuffer);

    blurer.getResult().getColorBufferTexture().bind(0);
    lensDirt.bind(1);
    flareShader.begin();
    flareShader.setUniformi("u_texture", 0);
    flareShader.setUniformi("u_texture_lens_dirt", 1);
    StaticFullscreenQuad.renderUsing(flareShader);
    flareShader.end();
  }

  private void scaledCircle(float x, float y, float size, Color color) {
    circle(x * 256, y * 256, size, color);
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

  public static void main(String[] args) {
    GdxInitializer.initializeLazy(FlareTest::new);
  }

}
