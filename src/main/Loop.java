package main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import main.camera.CameraController;
import main.rendering.GBuffer;
import main.rendering.GBufferFiller;
import main.rendering.PlanetRenderer;
import main.rendering.VertexBuffer;
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
  private final VertexBuffer buffer = new VertexBuffer();
  private final RenderTextureUtility show = new RenderTextureUtility();
  private final PostProcessor postProcessor = PostProcessor.withSize(WIDTH, HEIGHT);
  private final PlanetRenderer planetRenderer = new PlanetRenderer();
  private float elapsedTime = 0;

  {
    Materials.initialise();
  }

  @Override
  public void onUpdate(float delta) {
    cameraController.update(delta);

    buffer.updateProjection(cameraController.matrix());
    planetRenderer.updateProjection(cameraController.matrix());
    renderRotatedQuad(384, 0, 1, 256, Color.WHITE);
    GBufferFiller.fill(buffer, gbuffer, show);
    buffer.reset();

    elapsedTime += delta;
    planetRenderer.render(gbuffer, elapsedTime);

    //postProcessor.process(gbuffer);
    //show.show(postProcessor.getResult());
    show.show(gbuffer.color.getColorBufferTexture());
  }

  private void renderRotatedQuad(float x, float y, float r, float scale, Color color) {
    float cos = MathUtils.cos(r);
    float sin = sin(r);
    float packed = color.toFloatBits();
    buffer.putVertex(x - cos * scale, y - sin * scale, 0, 0, packed);
    buffer.putVertex(x + sin * scale, y - cos * scale, 1, 0, packed);
    buffer.putVertex(x + cos * scale, y + sin * scale, 1, 1, packed);
    buffer.putVertex(x - sin * scale, y + cos * scale, 0, 1, packed);
  }


  public static void main(String[] args) {
    GdxInitializer.initializeLazy(Loop::new);
  }

}
