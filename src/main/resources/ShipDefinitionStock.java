package main.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class ShipDefinitionStock {

  private static final Map<String, ShipDefinition> MAP = new HashMap<>();

  private ShipDefinitionStock() {
  }

  public static void initialise() {
    JsonObject json = ResourceLoader.loadJson("data/ships.json");
    JsonArray mats = json.getAsJsonArray("ships");
    for (JsonElement o : mats) {
      JsonObject props = o.getAsJsonObject();
      String id = props.get("id").getAsString();
      ShipDefinition definition = parseDefinition(props);
      MAP.put(id, definition);
      Log.log("Ship definition loaded successfully (id: " + id + ", name: " + definition.fullName + ")");
    }
  }

  private static ShipDefinition parseDefinition(JsonObject source) {
    ShipDefinition definition = new ShipDefinition();
    definition.fullName = source.get("name").getAsString();
    definition.materialId = source.get("materialId").getAsString();
    return definition;
  }

  public static ShipDefinition get(String name) {
    ShipDefinition ref = MAP.get(name);
    if (ref == null)
      throw new RuntimeException("ShipDefinition (id: " + name + ") not found");
    return ref;
  }

}
