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
  private final GBufferTexture gBufferTexture = loadTestGBufferTexture();

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

    blitUsingKawase(gbuffer.emissive, gbuffer.blurDownsamples.get(0));
    blitUsingKawase(gbuffer.blurDownsamples.get(0), gbuffer.blurDownsamples.get(1));
    blitUsingKawase(gbuffer.blurDownsamples.get(1), gbuffer.blurDownsamples.get(2));
    blitUsingKawase(gbuffer.blurDownsamples.get(2), gbuffer.blurDownsamples.get(3));

    gbuffer.blurDownsamplesComposition.begin();
    composeBlursShader.begin();
    for (int i = 0; i < gbuffer.blurDownsamples.size(); i++) {
      gbuffer.blurDownsamples.get(i).getColorBufferTexture().bind(i);
      composeBlursShader.setUniformi("u_texture[" + i + "]", i);
    }
    fullscreenQuad.render(composeBlursShader);
    composeBlursShader.end();
    gbuffer.blurDownsamplesComposition.end();

    gbuffer.color.getColorBufferTexture().bind(0);
    gbuffer.blurDownsamplesComposition.getColorBufferTexture().bind(1);
    composeShader.begin();
    composeShader.setUniformi("u_texture_color", 0);
    composeShader.setUniformi("u_texture_emissive", 1);
    fullscreenQuad.render(composeShader);
    composeShader.end();
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
    buffer.putVertex(x - scale + fun(1), y - scale + fun(2), 0, 0);
    buffer.putVertex(x + scale + fun(3), y - scale + fun(4), 1, 0);
    buffer.putVertex(x + scale + fun(5), y + scale + fun(6), 1, 1);
    buffer.putVertex(x - scale + fun(7), y + scale + fun(8), 0, 1);
  }

  private float fun(float mul) {
    return sin(elapsedTime + mul) * 16;
  }

}
