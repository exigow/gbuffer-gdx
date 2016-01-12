package main.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ResourceLoader {

  public static ShaderProgram loadShader(String vertexPath, String fragmentPath) {
    ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertexPath), Gdx.files.internal(fragmentPath));
    if (!shader.isCompiled())
      throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
    log("Shader loaded successfully (vertex: " + vertexPath + ", fragment: " + fragmentPath + ")");
    return shader;
  }

  public static Texture loadTexture(String texturePath) {
    Texture texture = new Texture(Gdx.files.internal(texturePath));
    log("Texture loaded succesfully (path: " + texturePath + ")");
    return texture;
  }

  private static void log(String str) {
    System.out.println(str);
  }

}
