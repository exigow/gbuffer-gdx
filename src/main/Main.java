package main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {

  public static void main(String[] args) {
    new LwjglApplication(new Wrapper(), new LwjglApplicationConfiguration() {{
      width = 1280;
      height = 800;
      vSyncEnabled = false;
      foregroundFPS = 60;
      backgroundFPS = 60;
      resizable = false;
    }});
   }

  private static class Wrapper extends ApplicationAdapter {

    private Loop loop;

    @Override
    public void create() {
      loop = new Loop();
    }

    @Override
    public void render() {
      float delta = Gdx.graphics.getDeltaTime();
      loop.onUpdate(delta);
    }

  }

}
