package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

import static com.badlogic.gdx.math.MathUtils.sin;

public class Loop {

  private final static int WIDTH = 800;
  private final static int HEIGHT = 600;
//  private final FrameBuffer colorBuffer = new FrameBuffer(Pixmap.Format.RGB888, WIDTH, HEIGHT, false);
//  private final FrameBuffer emmisiveBuffer = new FrameBuffer(Pixmap.Format.RGB888, WIDTH, HEIGHT, false);
  private final OrthographicCamera camera = createCamera();
  private float elapsedTime = 0;
  private final Texture texture = new Texture(Gdx.files.internal("data/test.png"));

  private static OrthographicCamera createCamera() {
    OrthographicCamera cam = new OrthographicCamera();
    cam.setToOrtho(true, WIDTH, HEIGHT);
    return cam;
  }
  private final Buffer buffer = new Buffer();

  public void onUpdate(float delta) {
    elapsedTime += delta;
    Gdx.gl20.glClearColor(0, 0, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

    buffer.putVertex(128 + fun(1), 128 + fun(2), 0, 0);
    buffer.putVertex(256 + fun(3), 128 + fun(4), 1, 0);
    buffer.putVertex(256 + fun(5), 256 + fun(6), 1, 1);
    buffer.putVertex(128 + fun(7), 256 + fun(8), 0, 1);

    buffer.flush(camera.combined, texture);
  }

  private float fun(float mul) {
    return sin(elapsedTime * mul) * 32;
  }

}
