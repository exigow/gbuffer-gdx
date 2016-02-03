package main.rendering;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class DustRenderer {

  private final ShapeRenderer shape = new ShapeRenderer();

  public void updateProjection(Matrix4 actualized) {
    shape.setProjectionMatrix(actualized);
  }

  public void render() {
    shape.begin(ShapeRenderer.ShapeType.Filled);
    shape.circle(64, 64, 16, 4);
    shape.end();
  }

}
