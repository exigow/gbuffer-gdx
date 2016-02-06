package main;

import main.camera.CameraController;
import main.game.Ship;
import main.rendering.DustRenderer;
import main.rendering.GBuffer;
import main.rendering.MaterialRenderer;
import main.rendering.PlanetRenderer;
import main.rendering.postprocess.PostProcessor;
import main.rendering.utils.RenderTextureUtility;
import main.resources.MaterialStock;
import main.resources.ShipDefinitionStock;
import main.runner.Demo;
import main.runner.GdxInitializer;

public class Loop implements Demo {

  private final static int WIDTH = 1024;
  private final static int HEIGHT = 768;
  private final GBuffer gbuffer = GBuffer.withSize(WIDTH, HEIGHT);
  private final CameraController cameraController = CameraController.setUp(WIDTH, HEIGHT);
  private final RenderTextureUtility show = new RenderTextureUtility();
  private final PostProcessor postProcessor = PostProcessor.withSize(WIDTH, HEIGHT);
  private final PlanetRenderer planetRenderer = new PlanetRenderer();
  private final DustRenderer dustRenderer = DustRenderer.withCapacity(256);
  private final MaterialRenderer materialRenderer = new MaterialRenderer();
  private float elapsedTime = 0;
  private final Ship testShip;

  {
    testShip = new Ship();
    testShip.x = 1;
    testShip.y = 1;
    testShip.angle = 1;
    testShip.definition = ShipDefinitionStock.get("test-fighter");
  }

  @Override
  public void onUpdate(float delta) {
    cameraController.update(delta);

    materialRenderer.updateProjection(cameraController.matrix());
    planetRenderer.updateProjection(cameraController.matrix());
    dustRenderer.updateProjection(cameraController.matrix());
    dustRenderer.update(cameraController.eye);

    gbuffer.clearSubBuffers();

    gbuffer.color.begin();
    show.show(MaterialStock.get("back").color);
    dustRenderer.render(true);
    gbuffer.color.end();

    elapsedTime += delta;
    planetRenderer.render(gbuffer, elapsedTime);

    materialRenderer.render(0, 0, 1, gbuffer, MaterialStock.get("ship"));

    gbuffer.color.begin();
    dustRenderer.render(false);
    gbuffer.color.end();

    postProcessor.process(gbuffer);
    show.show(postProcessor.getResult());
  }

  public static void main(String[] args) {
    GdxInitializer.initializeLazy(Loop::new);
  }

}
