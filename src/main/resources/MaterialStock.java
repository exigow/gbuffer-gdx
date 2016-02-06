package main.resources;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class MaterialStock {

  private static final Map<String, Material> MAP = new HashMap<>();

  private MaterialStock() {
  }

  public static void initialise() {
    JsonObject json = ResourceLoader.loadJson("data/materials.json");
    JsonArray mats = json.getAsJsonArray("materials");
    for (JsonElement o : mats) {
      JsonObject props = o.getAsJsonObject();
      String id = props.get("id").getAsString();
      String color = asNullableString(props, "color");
      String emissive = asNullableString(props, "emissive");
      Material mat = new Material();
      mat.color = loadTextureNullable(color);
      mat.emissive = loadTextureNullable(emissive);
      MAP.put(id, mat);
    }
  }

  private static Texture loadTextureNullable(String path) {
    if (path == null)
      return null;
    return ResourceLoader.loadTexture(path);
  }

  private static String asNullableString(JsonObject from, String name) {
    JsonElement e = from.get(name);
    if (e == null)
      return null;
    if (e.isJsonNull())
      return null;
    return e.getAsString();
  }

  public static Material get(String name) {
    Material ref = MAP.get(name);
    if (ref == null)
      throw new RuntimeException("Material (id: " + name + ") not found");
    return ref;
  }

}
