package main.rendering.filters;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.rendering.utils.FrameBufferCreator;

public class PingPong {

  public final FrameBuffer first;
  public final FrameBuffer second;
  private boolean swapper = false;

  private PingPong(int width, int heigth) {
    first = FrameBufferCreator.createDefault(width, heigth);
    second = FrameBufferCreator.createDefault(width, heigth);
  }

  public static PingPong withSize(int width, int heigth) {
    return new PingPong(width, heigth);
  }


  public void swap() {
    swapper = !swapper;
  }

  private FrameBuffer getResult() {
    return swapper ? first : second;
  }

}
