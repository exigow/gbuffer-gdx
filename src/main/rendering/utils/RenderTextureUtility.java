package main.rendering.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.resources.ResourceLoader;

public class RenderTextureUtility {

  private final ShaderProgram showShader = ResourceLoader.loadShader("data/screenspace/screenspace.vert", "data/screenspace/show.frag");

  public void show(Texture show) {
    show.bind(0);
    showShader.begin();
    showShader.setUniformi("u_texture", 0);
    StaticFullscreenQuad.renderUsing(showShader);
    showShader.end();
  }

}
