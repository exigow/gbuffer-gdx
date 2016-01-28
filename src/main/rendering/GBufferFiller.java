package main.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import main.rendering.utils.RenderTextureUtility;
import main.resources.MaterialsStock;

public class GBufferFiller {

  public static void fill(VertexBuffer buffer, GBuffer gbuffer, MaterialsStock materials, RenderTextureUtility show) {
    gbuffer.color.begin();
    clearContext(0, 0, 0);
    show.show(materials.get("back").color);
    buffer.paintColor(materials.get("ship").color);
    gbuffer.color.end();

    gbuffer.emissive.begin();
    clearContext(0, 0, 0);
    buffer.paintEmissive(materials.get("ship").emissive);
    gbuffer.emissive.end();

    gbuffer.velocity.begin();
    clearContext(.5f, .5f, 0);
    buffer.paintVelocity(materials.get("ship").color);
    gbuffer.velocity.end();

    gbuffer.ids.begin();
    clearContext(0, 0, 0);
    buffer.paintIds(materials.get("ship").color);
    gbuffer.ids.end();
  }

  private static void clearContext(float r, float g, float b) {
    Gdx.gl20.glClearColor(r, g, b, 1);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

}
