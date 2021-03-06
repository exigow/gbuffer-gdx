package main.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import main.logging.Log;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class ResourceLoader {

  public static ShaderProgram loadShader(String vertexPath, String fragmentPath) {
    ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertexPath), Gdx.files.internal(fragmentPath));
    if (!shader.isCompiled())
      throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
    Log.log("Shader loaded successfully (vertex: " + vertexPath + ", fragment: " + fragmentPath + ")");
    return shader;
  }

  public static Texture loadTexture(String texturePath) {
    Texture texture = new Texture(Gdx.files.internal(texturePath));
    Texture.TextureFilter filter = Texture.TextureFilter.Linear;
    texture.setFilter(filter, filter);
    Texture.TextureWrap wrap = Texture.TextureWrap.Repeat;
    texture.setWrap(wrap, wrap);
    Log.log("Texture loaded succesfully (path: " + texturePath + ")");
    return texture;
  }

  public static JsonObject loadJson(String path) {
    Reader reader = pathToReader(path);
    JsonParser parser = new JsonParser();
    JsonObject json = parser.parse(reader).getAsJsonObject();
    Log.log("JSON loaded succesfully (path: " + path + ")");
    return json;
  }

  private static Reader pathToReader(String path) {
    try {
      return new FileReader(Gdx.files.internal(path).file());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
