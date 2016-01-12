package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.utils.FullscreenQuad;
import main.utils.ResourceLoader;

import static com.badlogic.gdx.math.MathUtils.sin;

public class Loop {

  private final static int WIDTH = 800;
  private final static int HEIGHT = 600;
  private final FrameBuffer colorBuffer = new FrameBuffer(Pixmap.Format.RGB888, WIDTH, HEIGHT, false);
  private final OrthographicCamera camera = createCamera();
  private float elapsedTime = 0;
  private final Texture texture = ResourceLoader.loadTexture("data/test.png");
  private final Buffer buffer = new Buffer();
  private final FullscreenQuad fullscreenQuad = new FullscreenQuad();
  private final ShaderProgram fxaaShader = ResourceLoader.loadShader("data/screenspace.vert", "data/fxaa.frag");

  private static OrthographicCamera createCamera() {
    OrthographicCamera cam = new OrthographicCamera();
    cam.setToOrtho(true, WIDTH, HEIGHT);
    return cam;
  }

  public void onUpdate(float delta) {
    elapsedTime += delta;

    colorBuffer.begin();
    Gdx.gl20.glClearColor(.125f, .125f, .125f, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    renderQuad(Gdx.input.getX(), Gdx.input.getY());
    buffer.flush(camera.combined, texture);
    colorBuffer.end();

    colorBuffer.getColorBufferTexture().bind(0);
    fxaaShader.begin();
    fxaaShader.setUniformi("u_texture", 0);
    fxaaShader.setUniformf("FXAA_REDUCE_MIN", 1f / 128f);
    fxaaShader.setUniformf("FXAA_REDUCE_MUL", 1f / 8f);
    fxaaShader.setUniformf("FXAA_SPAN_MAX", 8f);
    float[] viewportInverse = new float[] {1f / WIDTH, 1f / HEIGHT};
    fxaaShader.setUniform2fv("u_viewportInverse", viewportInverse, 0, viewportInverse.length);
    fullscreenQuad.render(fxaaShader);
    fxaaShader.end();
  }

  private void renderQuad(float x, float y) {
    buffer.putVertex(x - 64 + fun(1), y - 64 + fun(2), 0, 0);
    buffer.putVertex(x + 64 + fun(3), y - 64 + fun(4), 1, 0);
    buffer.putVertex(x + 64 + fun(5), y + 64 + fun(6), 1, 1);
    buffer.putVertex(x - 64 + fun(7), y + 64 + fun(8), 0, 1);
  }

  private float fun(float mul) {
    return sin(elapsedTime + mul) * 16;
  }

}
