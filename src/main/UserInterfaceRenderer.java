package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import main.rendering.utils.FrameBufferCreator;

public class UserInterfaceRenderer {

  private final ImmediateModeRenderer20 renderer = new ImmediateModeRenderer20(false, true, 0);
  //private final ShapeRenderer shape = new ShapeRenderer();
  private final FrameBuffer buffer;
  private final OrthographicCamera camera;
  private final Button test = new Button();

  private UserInterfaceRenderer(int width, int height) {
    buffer = FrameBufferCreator.createDefault(width, height);
    camera = createScreenSpaceCamera(width, height);
  }

  public static UserInterfaceRenderer withSize(int width, int height) {
    return new UserInterfaceRenderer(width, height);
  }

  public void render(float delta) {
    test.update(1f);

    //System.out.println(test.time);

    Vector2 mouse = mouseUnprojectedPosition();

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
      test.state = Button.State.BEGIN;
    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
      test.state = Button.State.INIT_VERTICAL;
    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3))
      test.state = Button.State.INIT_HORIZONTAL;

    buffer.begin();
    clearContext();
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    renderer.begin(camera.combined, GL20.GL_LINE_STRIP);
    renderer.color(1, 1, 1, 1);
    renderer.vertex(0, 0, 0);
    renderer.color(1, 1, 1, 1);
    renderer.vertex(128, 0, 0);
    renderer.color(1, 1, 1, 1);
    renderer.vertex(0, 128, 0);
    renderer.end();

    //renderButtonAnimation(buttona);
    /*shape.begin(ShapeRenderer.ShapeType.Filled);
    shape.circle(mouse.x, mouse.y, 32);
    shape.end();*/
    Gdx.gl.glDisable(GL20.GL_BLEND);
    buffer.end();
  }

  private void renderQuad(float x1, float y1, float x2, float y2, float x3, float y3) {
    renderer.color(1, 1, 1, 1);
    renderer.vertex(0, 0, 0);
    renderer.color(1, 1, 1, 1);
    renderer.vertex(128, 0, 0);
    renderer.color(1, 1, 1, 1);
    renderer.vertex(0, 128, 0);
  }

  /*private void renderButtonAnimation(Button button) {
    Rectangle rect = button.rect;
    renderBar(button, 0);
    renderBar(button, rect.height);
    shape.begin(ShapeRenderer.ShapeType.Filled);
    shape.setColor(1, 1, 1, (float) Math.pow(button.time, 8) * .25f);
    shape.rect(rect.x + rect.width / 2 - button.time * rect.width / 2, rect.y, rect.width * button.time, rect.height);
    shape.end();
  }

  private void renderBar(Button button, float addY) {
    Rectangle rect = button.rect;
    shape.begin(ShapeRenderer.ShapeType.Filled);
    shape.setColor(1, 1, 1, .5f);
    float updateVertices = (rect.width / 2) * button.time;
    float midx = rect.x + rect.width / 2;
    float y = rect.y + addY;
    float ax = midx - updateVertices;
    float bx = midx + updateVertices;
    shape.rectLine(ax, y, bx, y, 2);
    shape.setColor(1, 1, 1, 1);
    shape.circle(ax, y, 3, 4);
    shape.circle(bx, y, 3, 4);
    shape.end();
  }*/

  private Vector2 mouseUnprojectedPosition() {
    Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
    camera.unproject(pos);
    return new Vector2(pos.x, pos.y);
  }

  private static void clearContext() {
    Gdx.gl20.glClearColor(0, 0, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

  private static OrthographicCamera createScreenSpaceCamera(int width, int height) {
    OrthographicCamera camera = new OrthographicCamera();
    camera.setToOrtho(false, width, height);
    return camera;
  }

  public Texture getResult() {
    return buffer.getColorBufferTexture();
  }

  private static class Button {

    private State state = State.BEGIN;
    public float time = state.targetValue;
    public final Rectangle rect = new Rectangle(512, 512, 128, 32);

    public void update(float delta) {
      time += (state.targetValue - time) * .125f * delta;
    }

    private enum State {

      BEGIN(1),
      INIT_VERTICAL(2),
      INIT_HORIZONTAL(3);

      public final float targetValue;

      State(float targetValue) {
        this.targetValue = targetValue;
      }

    }

    /*private static class BoundingBox {

      public float x1;
      public float x1;

    }*/

  }

}
