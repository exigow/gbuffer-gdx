package main.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;

public class CameraController {

  public final Vector3 eye = new Vector3(0, 0, -512);
  private final Vector3 target = new Vector3(eye);
  private final PerspectiveCamera camera;
  
  public static CameraController setUp(int width, int height) {
    return new CameraController(width, height);
  }
  
  private CameraController(int width, int height) {
    camera = new PerspectiveCamera(90, width, height);
    camera.position.set(0, 0, -512);
    camera.lookAt(0, 0, 0);
    camera.near = 8;
    camera.far = 1024;
    camera.up.set(0, -1, 0);
    camera.update();
  }

  public void update(float delta) {
    Vector3 movement = calcMovementVectorFromInput().scl(delta);
    target.add(movement);
    target.z = clamp(target.z, -1024, -16);
    moveEyeToTarget(delta);
    updateOrthographicCameraState();
  }

  private static Vector3 calcMovementVectorFromInput() {
    Vector3 result = new Vector3();
    float planeScale = 512;
    float zoomScale = 512;
    if (isKey(Input.Keys.A))
      result.x -= planeScale;
    if (isKey(Input.Keys.D))
      result.x += planeScale;
    if (isKey(Input.Keys.W))
      result.y -= planeScale;
    if (isKey(Input.Keys.S))
      result.y += planeScale;
    if (isKey(Input.Keys.Q))
      result.z -= zoomScale;
    if (isKey(Input.Keys.E))
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
    float scalar = .25f;
    eye.x += (target.x - eye.x) * scalar;
    eye.y += (target.y - eye.y) * scalar;
    eye.z += (target.z - eye.z) * scalar;
  }

  private void updateOrthographicCameraState() {
    camera.position.z = eye.z;
    camera.position.x = eye.x;
    camera.position.y = eye.y;
    camera.update();
  }

  private final Vector3 tempVector = new Vector3();
  public Vector2 unprojectedMouse() {
    Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
    Plane plane = new Plane(new Vector3(0, 0, 1), 0);
    Intersector.intersectRayPlane(pickRay, plane, tempVector);
    return new Vector2(tempVector.x, tempVector.y);
  }

  public Matrix4 matrix() {
    return camera.combined;
  }

}
