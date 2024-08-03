interface IFriendGroup {

}

class ExamplesFriendGroup {
  IFriendGroup labs = new FriendOf(new Person("Cameron"), "Carter");
  IFriendGroup largeGroup = new FriendOf(
      new FriendOf(new FriendOf(new Person("Sarah"), "James"), "Louis"), "Ben");
}

class Person implements IFriendGroup {

  String name;

  public Person(String name) {
    this.name = name;
  }
}

class FriendOf implements IFriendGroup {
  IFriendGroup connection;
  String name;

  FriendOf(IFriendGroup connection, String name) {
    this.connection = connection;
    this.name = name;
  }
}
