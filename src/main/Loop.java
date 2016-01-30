package main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import main.camera.CameraController;
import main.rendering.GBuffer;
import main.rendering.GBufferFiller;
import main.rendering.VertexBuffer;
import main.rendering.postprocess.PostProcessor;
import main.rendering.postprocess.ShaderEffect;
import main.rendering.utils.FrameBufferCreator;
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
  private final UserInterfaceRenderer uiRenderer = UserInterfaceRenderer.withSize(WIDTH, HEIGHT);
  private float elapsedTime;
  private final ShaderEffect edge = ShaderEffect.createGeneric("data/screenspace/edge-detection.frag");
  private final FrameBuffer test = FrameBufferCreator.createDefault(WIDTH, HEIGHT);

  {
    Materials.initialise();
  }

  @Override
  public void onUpdate(float delta) {
    elapsedTime += delta;
    cameraController.update(delta);

    //Benchmark.start("painting gbuffer");
    buffer.updateProjection(cameraController.matrix());
    //renderRotatedQuad(0, 256, elapsedTime * .125f, 256 + sin(elapsedTime * 32) * 128, Color.BLUE);
    //renderRotatedQuad(MathUtils.lerp(-256, 256, .5f + sin(elapsedTime * 3) * .5f), 256, 0, 256, Color.BLUE);
    //renderRotatedQuad(lerp(256, WIDTH - 256, .5f + sin(elapsedTime * 4) * .5f), 512, -elapsedTime * 12, 256);
    //renderRotatedQuad(Gdx.input.getX(), Gdx.input.getY(), elapsedTime, 256);
    Color red = new Color(1f, .5f, .125f, 1f);
    renderRotatedQuad(-128, 0, -elapsedTime, 256, red);
    Color blue = new Color(.125f, .5f, 1f, 1f);
    renderRotatedQuad(128, 0, elapsedTime, 256, blue);
    Color fade = new Color(blue);
    fade.a = .5f + sin(elapsedTime * 4) * .5f;
    renderRotatedQuad(256, 0, 1, 256, fade);
    Vector2 mouse = cameraController.unprojectedMouse();
    renderRotatedQuad(mouse.x, mouse.y, 0, 256, blue);
    GBufferFiller.fill(buffer, gbuffer, show);
    buffer.reset();
    //Benchmark.end();

    //postProcessor.process(gbuffer);
    //show.show(postProcessor.getResult());

    //uiRenderer.render(delta);
    edge.renderTo(test)
      .bind("u_texture", 0, gbuffer.ids)
      .bind("u_texture_pattern", 1, Materials.get("pattern").color)
      .flush();

    show.show(test.getColorBufferTexture());


    //Log.log(Gdx.graphics.getFramesPerSecond() + " " + Benchmark.generateRaportAndReset());
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
