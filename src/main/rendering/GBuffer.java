package main.rendering;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.utils.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GBuffer {

  public final FrameBuffer color;
  public final FrameBuffer emissive;
  public final List<FrameBuffer> blurDownsamples;

  private GBuffer(int width, int height) {
    color = createBuffer(width, height);
    emissive = createBuffer(width, height);
    blurDownsamples = createEmissiveDownsamples();
  }

  public static GBuffer withSize(int width, int heigth) {
    return new GBuffer(width, heigth);
  }

  private List<FrameBuffer> createEmissiveDownsamples() {
    return Arrays.asList(512, 256, 128, 64).stream()
      .map(size -> createBuffer(size, size))
      .collect(Collectors.toList());
  }

  private static FrameBuffer createBuffer(int width, int height) {
    Logger.log("FrameBuffer created succesfully (width: " + width + ", height: " + height + ")");
    return new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
  }

}
