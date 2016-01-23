package main.runner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.function.Supplier;

public class GdxInitializer extends ApplicationAdapter {

  public final Supplier<Demo> supplier;
  private Demo demo;

  private GdxInitializer(Supplier<Demo> supplier) {
    this.supplier = supplier;
  }

  public static void initializeLazy(Supplier<Demo> supplier) {
    new LwjglApplication(new GdxInitializer(supplier), createDefaultConfiguration());
  }

  @Override
  public void create() {
    demo = supplier.get();
  }

  @Override
  public void render() {
    float delta = Gdx.graphics.getDeltaTime();
    demo.onUpdate(delta);
  }

  private static LwjglApplicationConfiguration createDefaultConfiguration() {
    return new LwjglApplicationConfiguration() {{
      width = 1024;
      height = 768;
      resizable = false;
    }};
  }

}
