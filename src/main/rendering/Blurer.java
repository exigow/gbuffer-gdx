package main.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import main.rendering.utils.FrameBufferCreator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Blurer {

  public final List<FrameBuffer> blurDownsamples;
  public final FrameBuffer blurDownsamplesComposition;

  public Blurer() {
    blurDownsamples = createDownsamples();
    blurDownsamplesComposition = FrameBufferCreator.createDefault(512, 512);
  }

  private List<FrameBuffer> createDownsamples() {
    return Arrays.asList(512, 256, 128).stream()
      .map(size -> FrameBufferCreator.createDefault(size, size))
      .collect(Collectors.toList());
  }

}
