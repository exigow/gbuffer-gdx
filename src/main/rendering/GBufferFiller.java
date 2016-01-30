package main.rendering;

import main.resources.Materials;

public class GBufferFiller {

  public static void fill(VertexBuffer buffer, GBuffer gbuffer) {
    gbuffer.color.begin();;
    buffer.paintColor(Materials.get("ship").color);
    gbuffer.color.end();

    gbuffer.emissive.begin();
    buffer.paintEmissive(Materials.get("ship").emissive);
    gbuffer.emissive.end();

    gbuffer.velocity.begin();
    buffer.paintVelocity(Materials.get("ship").color);
    gbuffer.velocity.end();

    gbuffer.ids.begin();
    buffer.paintIds(Materials.get("ship").color);
    gbuffer.ids.end();
  }

}
