package main;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.rendering.utils.FrameBufferCreator;
import main.utils.lazyrun.Demo;
import main.utils.lazyrun.GdxInitializer;

public class FlareTest implements Demo {

  private final FrameBuffer cutoffBuffer = FrameBufferCreator.createDefault(512, 512);

  @Override
  public void onUpdate(float delta) {

  }

  public static void main(String[] args) {
    GdxInitializer.initializeLazy(FlareTest::new);
  }

}
