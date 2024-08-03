import tester.Tester;

// Represents a pet
interface IPet {
  // returns true if this pet is cute
  boolean isCute(int minHairLength);
}

// Represents a pet cat
class Cat implements IPet {
  String name;
  int hairLength;

  Cat(String name, int hairLength) {
    this.name = name;
    this.hairLength = hairLength;
  }

  // compare this cat's hair length to the threshold
  public boolean isCute(int minHairLength) {
    return this.hairLength >= minHairLength;
  }
}

// Represents a pet rock
class Rock implements IPet {
  String name;

  Rock(String name) {
    this.name = name;
  }

  // compare this cat's hair length to the threshold
  // a rock doesn't have hair but is always cute
  public boolean isCute(int minHairLength) {
    return true;
  }
}

// Keeps track of a person's pet
class Person {
  String name;
  IPet pet;

  Person(String name, IPet pet) {
    this.name = name;
    this.pet = pet;
  }

  boolean petIsCute(int minHairLength) {
    return this.pet.isCute(minHairLength);
  }

}

class ExamplesPerson {
  IPet rhaegar = new Cat("Rhaegar", 4);
  IPet kingPaimon = new Rock("King Paimon");
  IPet calvin = new Cat("Calvin", 1);

  Person alex = new Person("Alex", rhaegar);
  Person aaron = new Person("Aaron", kingPaimon);
  Person ben = new Person("Ben", calvin);

  public boolean testIsCute(Tester t) {
    return t.checkExpect(calvin.isCute(1), true)//
        && t.checkExpect(calvin.isCute(2), false)//
        && t.checkExpect(kingPaimon.isCute(100), true);
  }

  public boolean testPetIsCute(Tester t) {
    return t.checkExpect(alex.petIsCute(1), true)//
        && t.checkExpect(ben.petIsCute(2), false)//
        && t.checkExpect(aaron.petIsCute(100), true);
  }
}