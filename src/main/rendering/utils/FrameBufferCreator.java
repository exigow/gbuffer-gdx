package main.rendering.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.logging.Log;

public class FrameBufferCreator {

  public static FrameBuffer createDefault(int width, int height) {
    Log.log("FrameBuffer created succesfully (width: " + width + ", height: " + height + ")");
    FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
    Texture.TextureWrap wrap = Texture.TextureWrap.ClampToEdge;
    buffer.getColorBufferTexture().setWrap(wrap, wrap);
    return buffer;
  }

}
