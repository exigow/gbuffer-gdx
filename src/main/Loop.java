package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import main.rendering.Blurer;
import main.rendering.Buffer;
import main.rendering.GBuffer;
import main.rendering.GBufferTexture;
import main.rendering.utils.StaticFullscreenQuad;
import main.utils.ResourceLoader;

import static com.badlogic.gdx.math.MathUtils.sin;

public class Loop {

  private final static int WIDTH = Gdx.graphics.getWidth();
  private final static int HEIGHT = Gdx.graphics.getHeight();
  private final GBuffer gbuffer = GBuffer.withSize(WIDTH, HEIGHT);
  private final OrthographicCamera camera = createCamera();
  private final Buffer buffer = new Buffer();
  private final ShaderProgram composeShader = ResourceLoader.loadShader("data/screenspace.vert", "data/compose.frag");
  private final ShaderProgram flareShader = ResourceLoader.loadShader("data/screenspace.vert", "data/flare.frag");
  private final GBufferTexture gBufferTexture = loadTestGBufferTexture();
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();
  private final Blurer blurer = new Blurer();
  private float elapsedTime;

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

    buffer.updateProjection(camera.combined);
    renderQuad(Gdx.input.getX(), Gdx.input.getY());
    fillUsing(gbuffer.color, gBufferTexture.color);
    fillUsing(gbuffer.emissive, gBufferTexture.emissive);
    buffer.reset();

    gbuffer.emissive.begin();
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    drawCircle(Gdx.input.getX(), Gdx.input.getY(), 1, 1, 1, 24);
    drawCircle(Gdx.input.getX() + sin(elapsedTime * 1.02f) * 256, Gdx.input.getY() + sin(elapsedTime * 1.42f) * 256, 1, .25f, .25f, 8);
    drawCircle(Gdx.input.getX() + sin(elapsedTime * 2.41f) * 256, Gdx.input.getY() + sin(elapsedTime * 3.17f) * 256, .25f, 1, .25f, 12);
    drawCircle(Gdx.input.getX() + sin(elapsedTime * 3.31f) * 256, Gdx.input.getY() + sin(elapsedTime * .97f) * 256, .25f, .25f, 1, 16);
    shapeRenderer.end();
    gbuffer.emissive.end();

    blurer.blur(gbuffer.emissive);

    gbuffer.color.getColorBufferTexture().bind(0);
    blurer.blurDownsamplesComposition.getColorBufferTexture().bind(1);
    composeShader.begin();
    composeShader.setUniformi("u_texture0", 0);
    composeShader.setUniformi("u_texture1", 1);
    StaticFullscreenQuad.renderUsing(composeShader);
    composeShader.end();
  }

  private void drawCircle(float x, float y, float r, float g, float b, float radius) {
    shapeRenderer.setColor(r, g, b, 1);
    shapeRenderer.circle(x, y, radius);
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
