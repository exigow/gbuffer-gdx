package main.resources;

public class ShipDefinition {

  public String fullName;
  public String materialId;
  public Iterable<Weapon> weapons;

  public static class Weapon {

    public float x;
    public float y;

  }

}
