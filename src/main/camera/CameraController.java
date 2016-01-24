package main.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraController {

  private final Vector3 eye = new Vector3(0, 0, 1);
  private final Vector3 target = new Vector3(eye);
  private final OrthographicCamera camera = new OrthographicCamera();
  
  public static CameraController setUp(int width, int height) {
    return new CameraController(width, height);
  }
  
  private CameraController(int width, int height) {
    camera.setToOrtho(true, width, height);
  }

  public void update(float delta) {
    Vector3 movement = calcMovementVectorFromInput().scl(delta);
    target.add(movement);
    target.z = clamp(target.z, .125f, 4);
    moveEyeToTarget(delta);
    updateOrthographicCameraState();
  }

  private static Vector3 calcMovementVectorFromInput() {
    Vector3 result = new Vector3();
    float planeScale = 1024;
    float zoomScale = 2;
    if (isKey(Input.Keys.A))
      result.x -= planeScale;
    if (isKey(Input.Keys.D))
      result.x += planeScale;
    if (isKey(Input.Keys.W))
      result.y -= planeScale;
    if (isKey(Input.Keys.S))
      result.y += planeScale;
    if (isKey(Input.Keys.E))
      result.z -= zoomScale;
    if (isKey(Input.Keys.Q))
      result.z += zoomScale;
    return result;
  }

  private static float clamp(float v, float min, float max) {
    if (v > max)
      return max;
    if (v < min)
      return min;
    return v;
  }

  private static boolean isKey(int key) {
    return Gdx.input.isKeyPressed(key);
  }

  private void moveEyeToTarget(float delta) {
    float scalar = .5f;
    eye.x += (target.x - eye.x) * scalar;
    eye.y += (target.y - eye.y) * scalar;
    eye.z += (target.z - eye.z) * scalar;
  }

  private void updateOrthographicCameraState() {
    camera.position.set(eye.x, eye.y, 0);
    camera.zoom = eye.z;
    camera.update();
  }

  public Vector2 unproject(float x, float y) {
    Vector3 boxed = new Vector3(x, y, 0);
    Vector3 unprojected = camera.unproject(boxed);
    return new Vector2(unprojected.x, unprojected.y);
  }

  public Vector2 unprojectedMouse() {
    return unproject(Gdx.input.getX(), Gdx.input.getY());
  }

  public Matrix4 matrix() {
    return camera.combined;
  }

}
