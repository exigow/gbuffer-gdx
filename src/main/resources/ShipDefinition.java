package main.resources;

public class ShipDefinition {

  public String fullName;
  public String materialId;
  public Iterable<Weapon> weapons;
  public Iterable<Engine> engines;

  public static class Weapon {

    public float x;
    public float y;

  }

  public static class Engine {

    public float x;
    public float y;

  }

}
