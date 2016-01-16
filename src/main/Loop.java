package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.rendering.Buffer;
import main.rendering.GBuffer;
import main.rendering.GBufferTexture;
import main.utils.FullscreenQuad;
import main.utils.ResourceLoader;

import static com.badlogic.gdx.math.MathUtils.sin;

public class Loop {

  private final static int WIDTH = Gdx.graphics.getWidth();
  private final static int HEIGHT = Gdx.graphics.getHeight();
  private final GBuffer gbuffer = GBuffer.withSize(WIDTH, HEIGHT);
  private final OrthographicCamera camera = createCamera();
  private float elapsedTime = 0;
  private final Buffer buffer = new Buffer();
  private final FullscreenQuad fullscreenQuad = new FullscreenQuad();
  private final ShaderProgram kawaseShader = ResourceLoader.loadShader("data/screenspace.vert", "data/kawase.frag");
  private final ShaderProgram composeBlursShader = ResourceLoader.loadShader("data/screenspace.vert", "data/composeBlurs.frag");
  private final ShaderProgram composeShader = ResourceLoader.loadShader("data/screenspace.vert", "data/compose.frag");
  private final ShaderProgram flareShader = ResourceLoader.loadShader("data/screenspace.vert", "data/flare.frag");
  private final GBufferTexture gBufferTexture = loadTestGBufferTexture();
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();

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

    gbuffer.emissive.begin();
    clearContext();
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(1, 1, 1, 1);
    shapeRenderer.circle(Gdx.input.getX(), Gdx.input.getY(), 32);
    shapeRenderer.setColor(0, 0, 0, 1);
    shapeRenderer.rect(256, 256, 256, 256);
    shapeRenderer.end();
    gbuffer.emissive.end();

    /*buffer.updateProjection(camera.combined);
    renderQuad(Gdx.input.getX(), Gdx.input.getY());
    renderQuad(-256, 384);
    fillUsing(gbuffer.color, gBufferTexture.color);
    fillUsing(gbuffer.emissive, gBufferTexture.emissive);
    buffer.reset();*/

    blitUsingKawase(gbuffer.emissive, gbuffer.blurDownsamples.get(0));
    for (int i = 1; i < 3; i++)
      blitUsingKawase(gbuffer.blurDownsamples.get(i - 1), gbuffer.blurDownsamples.get(i));

    gbuffer.blurDownsamplesComposition.begin();
    composeBlursShader.begin();
    for (int i = 0; i < gbuffer.blurDownsamples.size(); i++) {
      gbuffer.blurDownsamples.get(i).getColorBufferTexture().bind(i);
      composeBlursShader.setUniformi("u_texture[" + i + "]", i);
    }
    fullscreenQuad.render(composeBlursShader);
    composeBlursShader.end();
    gbuffer.blurDownsamplesComposition.end();

    gbuffer.blurDownsamplesComposition.getColorBufferTexture().bind(0);
    flareShader.begin();
    flareShader.setUniformi("u_texture", 0);
    fullscreenQuad.render(flareShader);
    flareShader.end();
  }

  private void blitUsingKawase(FrameBuffer from, FrameBuffer to) {
    float texel = 1f / to.getWidth();
    to.begin();
    clearContext();
    from.getColorBufferTexture().bind(0);
    kawaseShader.begin();
    kawaseShader.setUniformi("u_texture", 0);
    kawaseShader.setUniformf("scale", texel);
    fullscreenQuad.render(kawaseShader);
    kawaseShader.end();
    to.end();
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
