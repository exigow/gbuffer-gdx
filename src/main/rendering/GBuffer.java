package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.rendering.utils.FrameBufferCreator;

public class GBuffer {

  public final FrameBuffer color;
  public final FrameBuffer emissive;
  public final FrameBuffer velocity;

  private GBuffer(int width, int height) {
    color = FrameBufferCreator.createDefault(width, height);
    emissive = FrameBufferCreator.createDefault(width, height);
    velocity = FrameBufferCreator.createDefault(width, height);
  }

  public static GBuffer withSize(int width, int heigth) {
    return new GBuffer(width, heigth);
  }

}
