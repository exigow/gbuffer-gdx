package main.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ShipDefinitionStock {

  private static final Map<String, ShipDefinition> MAP = new HashMap<>();

  private ShipDefinitionStock() {
  }

  public static void initialise() {
    JsonObject json = ResourceLoader.loadJson("data/ships.json");
    for (JsonElement o : json.getAsJsonArray("ships")) {
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
    definition.weapons = parseWeapons(source);
    return definition;
  }

  private static Iterable<ShipDefinition.Weapon> parseWeapons(JsonObject source) {
    Collection<ShipDefinition.Weapon> weapons = new ArrayList<>();
    for (JsonElement o : source.getAsJsonArray("weapons")) {
      JsonObject w = o.getAsJsonObject();
      ShipDefinition.Weapon weapon = new ShipDefinition.Weapon();
      weapon.x = w.get("positionX").getAsFloat();
      weapon.y = w.get("positionY").getAsFloat();
      weapons.add(weapon);
    }
    return weapons;
  }

  public static ShipDefinition get(String name) {
    ShipDefinition ref = MAP.get(name);
    if (ref == null)
      throw new RuntimeException("ShipDefinition (id: " + name + ") not found");
    return ref;
  }

}
