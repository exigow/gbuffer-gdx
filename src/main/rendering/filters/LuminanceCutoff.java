package main.rendering.filters;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.resources.ResourceLoader;

public class LuminanceCutoff extends ShaderEffect {

  private final ShaderProgram shader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/luminance-cutoff.frag");

  @Override
  protected ShaderProgram getShader() {
    return shader;
  }

}
