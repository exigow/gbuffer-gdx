package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import main.resources.Materials;

public class GBufferFiller {

  public static void fill(BatcherBuffer buffer, GBuffer gbuffer) {
    enableAlphaBlending();

    gbuffer.color.begin();;
    buffer.paintColor(Materials.get("ship").color);
    gbuffer.color.end();

    gbuffer.emissive.begin();
    buffer.paintEmissive(Materials.get("ship").emissive);
    gbuffer.emissive.end();

    gbuffer.ids.begin();
    buffer.paintIds(Materials.get("ship").color);
    gbuffer.ids.end();

    disableAlphaBlending();
  }

  private static void enableAlphaBlending() {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
  }

  private static void disableAlphaBlending() {
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

}
