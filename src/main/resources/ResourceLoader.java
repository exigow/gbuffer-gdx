package main.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import main.utils.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ResourceLoader {

  public static ShaderProgram loadShader(String vertexPath, String fragmentPath) {
    ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertexPath), Gdx.files.internal(fragmentPath));
    if (!shader.isCompiled())
      throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
    Logger.log("Shader loaded successfully (vertex: " + vertexPath + ", fragment: " + fragmentPath + ")");
    return shader;
  }

  public static Texture loadTexture(String texturePath) {
    Texture texture = new Texture(Gdx.files.internal(texturePath));
    Texture.TextureFilter filter = Texture.TextureFilter.Linear;
    texture.setFilter(filter, filter);
    Logger.log("Texture loaded succesfully (path: " + texturePath + ")");
    return texture;
  }

  public static JsonObject loadJson(String path) {
    Reader reader = pathToReader(path);
    JsonParser parser = new JsonParser();
    JsonObject json = parser.parse(reader).getAsJsonObject();
    Logger.log("JSON loaded succesfully (path: " + path + ")");
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
