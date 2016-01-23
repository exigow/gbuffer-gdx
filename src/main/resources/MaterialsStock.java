package main.resources;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialsStock {

  private final Map<String, Material> materials = new HashMap<>();

  private MaterialsStock() {
    JsonObject json = ResourceLoader.loadJson("data/materials.json");
    JsonArray mats = json.getAsJsonArray("materials");
    for (JsonElement o : mats) {
      JsonObject props = o.getAsJsonObject();
      String name = asNullableString(props, "name");
      String color = asNullableString(props, "color");
      String emissive = asNullableString(props, "emissive");

      Material mat = new Material();
      mat.color = loadTextureNullable(color);
      mat.emissive = loadTextureNullable(emissive);
      materials.put(name, mat);
    }
  }

  private static Texture loadTextureNullable(String path) {
    if (path == null)
      return null;
    return ResourceLoader.loadTexture(path);
  }

  private static String asNullableString(JsonObject from, String name) {
    JsonElement e = from.get(name);
    if (e.isJsonNull())
      return null;
    return e.getAsString();
  }

  public static MaterialsStock loadMaterials() {
    return new MaterialsStock();
  }

  public Material get(String name) {
    Material ref = materials.get(name);
    if (ref == null)
      throw new RuntimeException("Material (name: " + name + ") not found");
    return ref;
  }

}
