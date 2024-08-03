
public class ExamplesGame {
  IResource jackSparrow = new Captain("Jack Sparrow", 89);
  IResource hectorBarbossa = new Crewmember("Hector Barbossa", "first mate", 52);
  IResource flyingDutchman = new Ship("sail the oceans forever", true);
  IResource gil = new Captain("Gil", 23);
  IResource david = new Crewmember("David", "loyal partner", 90);
  IResource fundies = new Ship("teach us programming", false);
  IAction purchase1 = new Purchase(22, gil);
  IAction purchase2 = new Purchase(16, hectorBarbossa);
  IAction barter1 = new Barter(david, gil);
  IAction barter2 = new Barter(jackSparrow, david);

}

interface IResource {
}

interface IAction {

}

class Purchase implements IAction {
  int cost;
  IResource item;

  Purchase(int cost, IResource item) {
    this.cost = cost;
    this.item = item;
  }
}

class Barter implements IAction {
  IResource sold;
  IResource acquired;

  Barter(IResource sold, IResource acquired) {
    this.sold = sold;
    this.acquired = acquired;
  }
}

class Captain implements IResource {
  String name;
  int battles;

  Captain(String name, int battles) {
    this.name = name;
    this.battles = battles;
  }
}

class Crewmember implements IResource {
  String name;
  String description;
  int wealth;

  Crewmember(String name, String description, int wealth) {
    this.name = name;
    this.description = description;
    this.wealth = wealth;
  }
}

class Ship implements IResource {
  String purpose;
  boolean hostile;

  Ship(String purpose, boolean hostile) {
    this.purpose = purpose;
    this.hostile = hostile;
  }
}
