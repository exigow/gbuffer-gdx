package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.rendering.utils.FrameBufferCreator;

public class GBuffer {

  public final FrameBuffer color;
  public final FrameBuffer emissive;
  public final FrameBuffer velocity;

  private GBuffer(int width, int height) {
    color = FrameBufferCreator.createDefault(width, height);
    emissive = FrameBufferCreator.createDefault(512, 512);
    velocity = FrameBufferCreator.createDefault(128, 128);
  }

  public static GBuffer withSize(int width, int heigth) {
    return new GBuffer(width, heigth);
  }

}
