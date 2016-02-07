package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import main.camera.CameraController;
import main.game.Ship;
import main.rendering.DustRenderer;
import main.rendering.GBuffer;
import main.rendering.MaterialRenderer;
import main.rendering.PlanetRenderer;
import main.rendering.postprocess.PostProcessor;
import main.rendering.utils.RenderTextureUtility;
import main.resources.MaterialStock;
import main.resources.ShipDefinition;
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
    testShip.angle = 0;
    testShip.definition = ShipDefinitionStock.get("test-fighter");
  }

  @Override
  public void onUpdate(float delta) {
    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
      testShip.angle += .025;
    if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
      testShip.angle -= .025;
    Vector2 mouse = cameraController.unprojectedMouse();
    testShip.x = mouse.x;
    testShip.y = mouse.y;

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

    renderShip();

    gbuffer.color.begin();
    dustRenderer.render(false);
    gbuffer.color.end();

    postProcessor.process(gbuffer);
    show.show(postProcessor.getResult());
  }

  private void renderShip() {
    ShipDefinition def = testShip.definition;
    materialRenderer.render(testShip.x, testShip.y, testShip.angle, gbuffer, MaterialStock.get(def.materialId));
    for (ShipDefinition.Weapon weapon : def.weapons) {
      float x = testShip.x + MathUtils.cos(testShip.angle) * weapon.x + MathUtils.cos(testShip.angle + MathUtils.PI / 2) * weapon.y;
      float y = testShip.y + MathUtils.sin(testShip.angle) * weapon.x + MathUtils.sin(testShip.angle + MathUtils.PI / 2) * weapon.y;
      materialRenderer.render(x, y, elapsedTime, gbuffer, MaterialStock.get("turret"));
    }
  }

  public static void main(String[] args) {
    GdxInitializer.initializeLazy(Loop::new);
  }

}
