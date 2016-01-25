package main;

import com.badlogic.gdx.math.MathUtils;
import main.camera.CameraController;
import main.rendering.GBuffer;
import main.rendering.VertexBuffer;
import main.rendering.postprocess.PostProcessor;
import main.rendering.utils.RenderTextureUtility;
import main.resources.MaterialsStock;
import main.runner.Demo;
import main.runner.GdxInitializer;

import static com.badlogic.gdx.math.MathUtils.sin;

public class Loop implements Demo {

  private final static int WIDTH = 1024;
  private final static int HEIGHT = 768;
  private final GBuffer gbuffer = GBuffer.withSize(WIDTH, HEIGHT);
  private final CameraController cameraController = CameraController.setUp(WIDTH, HEIGHT);
  private final VertexBuffer buffer = new VertexBuffer();
  private final RenderTextureUtility show = new RenderTextureUtility();
  private final MaterialsStock materials = MaterialsStock.loadMaterials();
  private final PostProcessor postProcessor = PostProcessor.withSize(WIDTH, HEIGHT);
  private final UserInterfaceRenderer uiRenderer = UserInterfaceRenderer.withSize(WIDTH, HEIGHT);
  private float elapsedTime;

  @Override
  public void onUpdate(float delta) {
    elapsedTime += delta;
    cameraController.update(delta);

    //Benchmark.start("painting gbuffer");
    //buffer.updateProjection(cameraController.matrix());
    //renderRotatedQuad(768, 512, elapsedTime * .125f, 256 + sin(elapsedTime * 32) * 128);
    //renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 3) * .5f), 256, 0, 256);
    //renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 4) * .5f), 512, -elapsedTime * 12, 256);
    //renderRotatedQuad(Gdx.input.getX(), Gdx.input.getY(), elapsedTime, 256);
    /*renderRotatedQuad(0, 0, 1, 256);
    Vector2 mouse = cameraController.unprojectedMouse();
    renderRotatedQuad(mouse.x, mouse.y, elapsedTime, 256);
    GBufferFiller.fill(buffer, gbuffer, materials, show);
    buffer.reset();*/
    //Benchmark.end();

    //postProcessor.process(gbuffer);
    //show.show(postProcessor.getResult());

    uiRenderer.render(delta);

    show.show(uiRenderer.getResult());


    //Log.log(Gdx.graphics.getFramesPerSecond() + " " + Benchmark.generateRaportAndReset());
  }

  private void renderRotatedQuad(float x, float y, float r, float scale) {
    float cos = MathUtils.cos(r);
    float sin = sin(r);
    buffer.putVertex(x - cos * scale, y - sin * scale, 0, 0);
    buffer.putVertex(x + sin * scale, y - cos * scale, 1, 0);
    buffer.putVertex(x + cos * scale, y + sin * scale, 1, 1);
    buffer.putVertex(x - sin * scale, y + cos * scale, 0, 1);
  }

  public static void main(String[] args) {
    GdxInitializer.initializeLazy(Loop::new);
  }

}
