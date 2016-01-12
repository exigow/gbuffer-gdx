package main.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderLoader {

  public static ShaderProgram loadAndCompile(String vertexPath, String fragmentPath) {
    ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertexPath), Gdx.files.internal(fragmentPath));
    if (!shader.isCompiled())
      throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
    System.err.println("Shader loaded successfully (vertex: " + vertexPath + ", fragment: " + fragmentPath + ")");
    return shader;
  }

}
