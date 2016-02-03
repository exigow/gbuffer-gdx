package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.rendering.utils.FrameBufferCreator;

public class GBuffer {

  public final FrameBuffer color;
  public final FrameBuffer emissive;
  public final FrameBuffer ids;

  private GBuffer(int width, int height) {
    color = FrameBufferCreator.createDefault(width, height);
    emissive = FrameBufferCreator.createDefault(512, 512);
    ids = FrameBufferCreator.createDefault(512, 512);
  }

  public static GBuffer withSize(int width, int height) {
    return new GBuffer(width, height);
  }

  public void clearSubBuffers() {
    clearSubBuffer(color);
    clearSubBuffer(emissive);
    clearSubBuffer(ids);
  }

  private void clearSubBuffer(FrameBuffer buffer) {
    buffer.begin();
    Gdx.gl20.glClearColor(0, 0, 0, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    buffer.end();
  }

}
