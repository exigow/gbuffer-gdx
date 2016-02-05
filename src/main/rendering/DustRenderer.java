package main.rendering;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class DustRenderer {

  private final ShapeRenderer shape = new ShapeRenderer();
  private final Dust[] dusts;

  private DustRenderer(int capacity) {
    Supplier<Dust> createAndReset = () -> {
      Dust dust = new Dust();
      resetDust(dust);
      return dust;
    };
    dusts = Stream.generate(createAndReset).limit(capacity).toArray(Dust[]::new);
  }

  public static DustRenderer withCapacity(int capacity) {
    return new DustRenderer(capacity);
  }

  public void updateProjection(Matrix4 actualized) {
    shape.setProjectionMatrix(actualized);
}

  public void render() {
    shape.begin(ShapeRenderer.ShapeType.Filled);
    for (Dust dust : dusts)
      shape.circle(dust.x, dust.y, 4, 4);
    shape.end();
  }

  private static void resetDust(Dust dust) {
    dust.x = MathUtils.random(-128, 128);
    dust.y = MathUtils.random(-128, 128);
  }

  private static class Dust {

    public float x;
    public float y;

  }

}
