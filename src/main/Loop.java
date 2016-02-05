package main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import main.camera.CameraController;
import main.rendering.*;
import main.rendering.postprocess.PostProcessor;
import main.rendering.utils.RenderTextureUtility;
import main.resources.Materials;
import main.runner.Demo;
import main.runner.GdxInitializer;

import static com.badlogic.gdx.math.MathUtils.sin;

public class Loop implements Demo {

  private final static int WIDTH = 1024;
  private final static int HEIGHT = 768;
  private final GBuffer gbuffer = GBuffer.withSize(WIDTH, HEIGHT);
  private final CameraController cameraController = CameraController.setUp(WIDTH, HEIGHT);
  private final BatcherBuffer batcherBuffer = new BatcherBuffer();
  private final RenderTextureUtility show = new RenderTextureUtility();
  private final PostProcessor postProcessor = PostProcessor.withSize(WIDTH, HEIGHT);
  private final PlanetRenderer planetRenderer = new PlanetRenderer();
  private final DustRenderer dustRenderer = DustRenderer.withCapacity(256);
  private float elapsedTime = 0;

  @Override
  public void onUpdate(float delta) {
    cameraController.update(delta);

    batcherBuffer.updateProjection(cameraController.matrix());
    planetRenderer.updateProjection(cameraController.matrix());
    dustRenderer.updateProjection(cameraController.matrix());
    dustRenderer.update(cameraController.eye);

    gbuffer.clearSubBuffers();

    gbuffer.color.begin();
    show.show(Materials.get("back").color);
    dustRenderer.render(true);
    gbuffer.color.end();

    elapsedTime += delta;
    planetRenderer.render(gbuffer, elapsedTime);
    renderRotatedQuad(0, 0, 1, 256, Color.WHITE);
    Vector2 mouse = cameraController.unprojectedMouse();
    renderRotatedQuad(mouse.x, mouse.y, 1, 256, Color.WHITE);
    GBufferFiller.fill(batcherBuffer, gbuffer);
    batcherBuffer.reset();

    gbuffer.color.begin();
    dustRenderer.render(false);
    gbuffer.color.end();

    postProcessor.process(gbuffer);
    show.show(postProcessor.getResult());
    //show.show(gbuffer.color.getColorBufferTexture());
  }

  private void renderRotatedQuad(float x, float y, float r, float scale, Color color) {
    float cos = MathUtils.cos(r);
    float sin = sin(r);
    float packed = color.toFloatBits();
    batcherBuffer.putVertex(x - cos * scale, y - sin * scale, 0, 0, packed);
    batcherBuffer.putVertex(x + sin * scale, y - cos * scale, 1, 0, packed);
    batcherBuffer.putVertex(x + cos * scale, y + sin * scale, 1, 1, packed);
    batcherBuffer.putVertex(x - sin * scale, y + cos * scale, 0, 1, packed);
  }


  public static void main(String[] args) {
    GdxInitializer.initializeLazy(Loop::new);
  }

}
