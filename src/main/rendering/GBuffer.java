package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

  public static GBuffer withSize(int width, int heigth) {
    return new GBuffer(width, heigth);
  }

  public void clearSubBuffers() {
    clearSubBuffer(color, Color.BLACK);
    clearSubBuffer(emissive, Color.BLACK);
    clearSubBuffer(ids, Color.BLACK);
  }

  private void clearSubBuffer(FrameBuffer buffer, Color color) {
    buffer.begin();
    clearContext(color);
    buffer.end();
  }

  private static void clearContext(Color color) {
    Gdx.gl20.glClearColor(color.r, color.g, color.b, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

}
