import tester.Tester;

// a campus tour
class CampusTour {
  int startTime; // minutes from midnight
  ITourLocation startingLocation;

  CampusTour(int startTime, ITourLocation startingLocation) {
    this.startTime = startTime;
    this.startingLocation = startingLocation;
  }

  // is this tour the same tour as the given one?
  boolean sameTour(CampusTour other) {
    return this.startTime == other.startTime
        && this.startingLocation.sameTLocation(other.startingLocation);
  }
}

// a spot on the tour
interface ITourLocation {
  boolean sameTLocation(ITourLocation other);

  boolean sameTourEnd(TourEnd other);

  boolean sameMandatory(Mandatory other);

  boolean sameBranchingTour(BranchingTour other);
}

abstract class ATourLocation implements ITourLocation {
  String speech; // the speech to give at this spot on the tour

  ATourLocation(String speech) {
    this.speech = speech;
  }

  // is this the same as the given branching tour? (no, if this isn't overridden)
  public boolean sameTourEnd(TourEnd tourEnd) {
    return false;
  }

  // is this the same as the given branching tour? (no, if this isn't overridden)
  public boolean sameMandatory(Mandatory mandatory) {
    return false;
  }

  // is this the same as the given branching tour? (no, if this isn't overridden)
  public boolean sameBranchingTour(BranchingTour branchingTour) {
    return false;
  }
}

// the end of the tour
class TourEnd extends ATourLocation {
  ICampusLocation location;

  TourEnd(String speech, ICampusLocation location) {
    super(speech);
    this.location = location;
  }

  // is this the same as the given location?
  public boolean sameTLocation(ITourLocation other) {
    return other.sameTourEnd(this);
  }

  // is this the same as the given tour end?
  @Override
  public boolean sameTourEnd(TourEnd other) {
    return this.speech.equals(other.speech) && this.location.sameCLocation(other.location);
  }
}

//a mandatory spot on the tour with the next place to go
class Mandatory extends ATourLocation {
  ICampusLocation location;
  ITourLocation next;

  Mandatory(String speech, ICampusLocation location, ITourLocation next) {
    super(speech);
    this.location = location;
    this.next = next;
  }

  // is this the same as the given location?
  public boolean sameTLocation(ITourLocation other) {
    return other.sameMandatory(this);
  }

  // is this the same as the given mandatory stop?
  @Override
  public boolean sameMandatory(Mandatory other) {
    return this.speech.equals(other.speech) && this.location.sameCLocation(other.location)
        && this.next.sameTLocation(other.next);
  }
}

// up to the tour guide where to go next
class BranchingTour extends ATourLocation {
  ITourLocation option1;
  ITourLocation option2;

  BranchingTour(String speech, ITourLocation option1, ITourLocation option2) {
    super(speech);
    this.option1 = option1;
    this.option2 = option2;
  }

  // is this the same as the given location?
  public boolean sameTLocation(ITourLocation other) {
    return other.sameBranchingTour(this);
  }

  // is this the same as the given branching tour?
  @Override
  public boolean sameBranchingTour(BranchingTour other) {
    return this.speech.equals(other.speech) && (this.option1.sameTLocation(other.option1)
        && this.option2.sameTLocation(other.option2)
        || this.option2.sameTLocation(other.option1) && this.option1.sameTLocation(other.option2));
  }
}

//represents a campus location
interface ICampusLocation {

  boolean sameBuilding(Building other);

  boolean sameCLocation(ICampusLocation other);

  boolean sameQuad(Quad other);
}

class Building implements ICampusLocation {
  String name;
  Address address;

  Building(String name, Address address) {
    this.name = name;
    this.address = address;
  }

  // is this building the same building as the given one?
  public boolean sameBuilding(Building other) {
    return this.name.equals(other.name) && this.address.sameAddress(other.address);
  }

  // is this the same as the given location?
  public boolean sameCLocation(ICampusLocation other) {
    return other.sameBuilding(this);
  }

  // is this the same as the given quad? (no)
  public boolean sameQuad(Quad other) {
    return false;
  }
}

//represents an address
class Address {
  String street;
  int number;

  Address(String street, int number) {
    this.number = number;
    this.street = street;
  }

  // is this address the same building as the given one?
  boolean sameAddress(Address other) {
    return this.number == other.number && this.street.equals(other.street);
  }
}

//represents a quad
class Quad implements ICampusLocation {
  String name;
  ILoCampusLocation surroundings; // in clockwise order, starting north

  Quad(String name, ILoCampusLocation surroundings) {
    this.name = name;
    this.surroundings = surroundings;
  }

  // is this the same as the given building? (no)
  public boolean sameBuilding(Building building) {
    return false;
  }

  // is this the same as the given locaton?
  public boolean sameCLocation(ICampusLocation other) {
    return other.sameQuad(this);
  }

  // is this the same as the given quad?
  public boolean sameQuad(Quad other) {
    return this.name.equals(other.name) && this.surroundings.sameList(other.surroundings);
  }
}

//represents a list of campus locations
interface ILoCampusLocation {
  boolean sameList(ILoCampusLocation other);

  boolean isMtLoCL();

  boolean sameConsLoCL(ConsLoCampusLocation other);
}

//represents an empty list of campus locations
class MtLoCampusLocation implements ILoCampusLocation {

  // is this the same as the given list?
  public boolean sameList(ILoCampusLocation other) {
    return other.isMtLoCL();
  }

  // is this an empty list? (yes)
  public boolean isMtLoCL() {
    return true;
  }

  // is this the same as the given nonempty list? (no)
  public boolean sameConsLoCL(ConsLoCampusLocation other) {
    return false;
  }

}

//represents a non-empty list of campus locations
class ConsLoCampusLocation implements ILoCampusLocation {
  ICampusLocation first;
  ILoCampusLocation rest;

  ConsLoCampusLocation(ICampusLocation first, ILoCampusLocation rest) {
    this.first = first;
    this.rest = rest;
  }

  // is this the same as the given list?
  public boolean sameList(ILoCampusLocation other) {
    return other.sameConsLoCL(this);
  }

  // is this the same as the given nonempty list?
  public boolean sameConsLoCL(ConsLoCampusLocation other) {
    return this.first.sameCLocation(other.first) && this.rest.sameList(other.rest);
  }

  // is this an empty list? (no)
  public boolean isMtLoCL() {
    return false;
  }
}

class ExamplesCampus {
  Address a1 = new Address("Road", 33);
  Address a2 = new Address("Road", 34);
  Address a3 = new Address("road", 33);
  Address a4 = new Address("Road", 33);

  Building b1 = new Building("Gamerville", a1);
  Building b2 = new Building("gamerville", a2);
  Building b3 = new Building("Gamerville", a3);
  Building b4 = new Building("nowhereland", a4);
  Building b5 = new Building("Gamerville", a4);

  ILoCampusLocation empty = new MtLoCampusLocation();

  Building leftBil = new Building("Left", a1);
  Building rightBil = new Building("Right", a2);
  Quad food = new Quad("court",
      new ConsLoCampusLocation(leftBil, new ConsLoCampusLocation(rightBil, empty)));
  Quad food2 = new Quad("court",
      new ConsLoCampusLocation(rightBil, new ConsLoCampusLocation(leftBil, empty)));
  Quad water = new Quad("gamer",
      new ConsLoCampusLocation(b1, new ConsLoCampusLocation(b2, new ConsLoCampusLocation(b3,
          new ConsLoCampusLocation(b4, new ConsLoCampusLocation(b5, empty))))));
  TourEnd end = new TourEnd("money", b5);
  Mandatory middle = new Mandatory("speak", b4, end);
  TourEnd trast = new TourEnd("money", b5);
  TourEnd trast2 = new TourEnd("money", b4);
  Mandatory imperoir = new Mandatory("speak", b4, end);
  BranchingTour center = new BranchingTour("win", middle, end);
  BranchingTour exterior = new BranchingTour("win", end, middle);
  BranchingTour dragon = new BranchingTour("win", end, end);
  Mandatory visitor = new Mandatory("get a job", b1, center);
  CampusTour lame = new CampusTour(1000, visitor);
  CampusTour lamer = new CampusTour(1200, visitor);
  CampusTour lamest = new CampusTour(1000, imperoir);
  CampusTour lamestk = new CampusTour(1000, visitor);

  ConsLoCampusLocation df = new ConsLoCampusLocation(b1, empty);
  ConsLoCampusLocation df2 = new ConsLoCampusLocation(b5, empty);

  // Tests the sameTour method in the CampusTour class
  boolean testSameTour(Tester t) {
    return t.checkExpect(lame.sameTour(lame), true)//
        && t.checkExpect(lamer.sameTour(lamest), false)
        && t.checkExpect(lame.sameTour(lamest), false)
        && t.checkExpect(lame.sameTour(lamestk), true);
  }

  // Tests the sameTLocation method in the ITourLocation interface
  boolean testSameTLocation(Tester t) {
    return t.checkExpect(dragon.sameTLocation(center), false)
        && t.checkExpect(visitor.sameTLocation(center), false)
        && t.checkExpect(trast.sameTLocation(trast), true)
        && t.checkExpect(end.sameTLocation(imperoir), false)
        && t.checkExpect(middle.sameTLocation(imperoir), true)
        && t.checkExpect(center.sameTLocation(exterior), true);
  }

  // Tests the sameMandatory method in the ITourLocation interface
  boolean testSameMandatory(Tester t) {
    return t.checkExpect(visitor.sameMandatory(middle), false)
        && t.checkExpect(visitor.sameMandatory(visitor), true)
        && t.checkExpect(center.sameMandatory(middle), false)
        && t.checkExpect(imperoir.sameMandatory(middle), true);
  }

  // Tests the sameBranchingTour method in the ITourLocation interface
  boolean testSameBranching(Tester t) {
    return t.checkExpect(exterior.sameBranchingTour(center), true)
        && t.checkExpect(exterior.sameBranchingTour(exterior), true)
        && t.checkExpect(dragon.sameBranchingTour(exterior), false)
        && t.checkExpect(imperoir.sameBranchingTour(exterior), false);
  }

  // Tests the sameTourEnd method in the ITourLocation interface
  boolean testSameTEnd(Tester t) {
    return t.checkExpect(end.sameTourEnd(end), true)//
        && t.checkExpect(trast.sameTourEnd(end), true)
        && t.checkExpect(trast2.sameTourEnd(end), false)
        && t.checkExpect(imperoir.sameTourEnd(end), false);
  }

  // Tests the sameCLocation method in the ICampusLocation interface
  boolean testSameCLocation(Tester t) {
    return t.checkExpect(b1.sameCLocation(b5), true)//
        && t.checkExpect(water.sameCLocation(water), true)//
        && t.checkExpect(food.sameCLocation(food2), false)
        && t.checkExpect(food.sameCLocation(b1), false)
        && t.checkExpect(b1.sameCLocation(food), false);
  }

  // Tests the sameQuad method in the ICampusLocation interface
  boolean testSameQuad(Tester t) {
    return t.checkExpect(leftBil.sameQuad(food), false)
        && t.checkExpect(water.sameQuad(water), true)//
        && t.checkExpect(food.sameQuad(food2), false);
  }

  // Tests the sameBuilding method in the ICampusLocation interface
  boolean testSameBuilding(Tester t) {
    return t.checkExpect(b1.sameBuilding(b5), true)//
        && t.checkExpect(b1.sameBuilding(b2), false)//
        && t.checkExpect(b5.sameBuilding(b1), true)//
        && t.checkExpect(b2.sameBuilding(b3), false)//
        && t.checkExpect(b3.sameBuilding(b4), false)//
        && t.checkExpect(b1.sameBuilding(b4), false)//
        && t.checkExpect(b5.sameBuilding(b2), false)//
        && t.checkExpect(b1.sameBuilding(b1), true);
  }

  // Tests the isMtLoCL method in the ILoCampusLocation interface
  boolean testIsMtLoCL(Tester t) {
    return t.checkExpect(empty.isMtLoCL(), true)
        && t.checkExpect(new ConsLoCampusLocation(b1, empty).isMtLoCL(), false);
  }

  // Tests the sameConsLoCL method in the ILoCampusLocation interface
  boolean testSameConsLoCL(Tester t) {
    return t.checkExpect(df2.sameConsLoCL(df), true)
        && t.checkExpect(new ConsLoCampusLocation(b1, empty).sameConsLoCL(df), true)
        && t.checkExpect(empty.sameConsLoCL(df), false);
  }

  // Tests the sameList method in the ILoCampusLocation interface
  boolean testSameList(Tester t) {
    return t.checkExpect(df.sameList(df2), true)//
        && t.checkExpect(df2.sameList(df), true)//
        && t.checkExpect(empty.sameList(df), false);
  }

  // Tests the sameAddress method in the Address class
  boolean testSameAddress(Tester t) {
    return t.checkExpect(a1.sameAddress(a4), true)//
        && t.checkExpect(a1.sameAddress(a2), false)//
        && t.checkExpect(a4.sameAddress(a1), true)//
        && t.checkExpect(a2.sameAddress(a3), false)//
        && t.checkExpect(a3.sameAddress(a4), false)//
        && t.checkExpect(a3.sameAddress(a2), false)//
        && t.checkExpect(a1.sameAddress(a1), true);
  }
}
