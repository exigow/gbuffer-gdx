package main.rendering;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.function.Supplier;

public class GBuffer {

  public final FrameBuffer color;
  public final FrameBuffer emissive;

  private GBuffer(int width, int height) {
    Supplier<FrameBuffer> supplyDefault = () -> new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
    color = supplyDefault.get();
    emissive = supplyDefault.get();
  }

  public static GBuffer withSize(int width, int heigth) {
    return new GBuffer(width, heigth);
  }

}
