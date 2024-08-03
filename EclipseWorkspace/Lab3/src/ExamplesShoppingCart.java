import tester.Tester;

class Item {
  String name;
  int cost;

  // Construct a new cart item with the given name and cost in cents
  Item(String name, int cost) {
    this.name = name;
    this.cost = cost;
  }

  // Is this item named the given name?
  boolean isNamed(String searchedName) {
    return this.name.equals(searchedName);
  }

  // Add this item's cost to the given cost
  int addCost(int runningCost) {
    return runningCost + this.cost;
  }
}

interface ILoItem {
  // returns the length of this list
  int length();

  // returns the number of items with the given name in this list
  int itemCount(String i);

  // returns the total price of the items in this list
  int totalPrice();

  // returns the total price of the items in this list
  // ACCUMULATOR: represents the total cost counted so far
  int totalPriceAcc(int runningPrice);

  // Remove the given quantity of items of the given name from this list of items
  // and produce a new cart of the remaining items
  ILoItem removeItem(String itemName, int count);
}

class MtLoItem implements ILoItem {
  // returns the length of this list (always 0)
  public int length() {
    return 0;
  }

  // returns the number of items with the given name in this list (always 0)
  public int itemCount(String i) {
    return 0;
  }

  // returns the total price of the items in this list (always 0)
  public int totalPrice() {
    return 0;
  }

  // returns the total price of the items in this list
  // ACCUMULATOR: represents the total cost counted so far
  public int totalPriceAcc(int runningPrice) {
    return runningPrice;
  }

  // Remove the given quantity of items of the given name from this list of items
  // and produce a new cart of the remaining items (always return empty list)
  public ILoItem removeItem(String itemName, int count) {
    return this;
  }
}

class ConsLoItem implements ILoItem {
  Item first;
  ILoItem rest;

  // Construct a new cons with the given first item and rest list
  ConsLoItem(Item first, ILoItem rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns the length of this list
  public int length() {
    return 1 + this.rest.length();
  }

  // returns the number of items with the given name
  public int itemCount(String i) {
    if (this.first.isNamed(i)) {
      return 1 + this.rest.itemCount(i);
    }
    else {
      return this.rest.itemCount(i);
    }
  }

  // returns the total price of the items in this list
  public int totalPrice() {
    return this.rest.totalPriceAcc(this.first.addCost(0));
  }

  // returns the total price of the items in this list
  // ACCUMULATOR: runningPrice represents the total cost counted so far
  public int totalPriceAcc(int runningPrice) {
    return this.rest.totalPriceAcc(this.first.addCost(runningPrice));
  }

  // Remove the given quantity of items of the given name from this list of items
  // and produce a new cart of the remaining items
  public ILoItem removeItem(String itemName, int count) {
    if (count >= 0 && this.first.isNamed(itemName)) {
      return this.rest.removeItem(itemName, count - 1);
    }
    else {
      return new ConsLoItem(this.first, this.rest.removeItem(itemName, count));
    }
  }
}

interface IShoppingCart {
  // How many items are in this shopping cart?
  int numItems();

  // How many items of the given name are in this shopping cart?
  int itemCount(String itemName);

  // What is the total cost of all the items in this shopping cart,
  // in cents?
  int totalPrice();

  // Produce a shopping cart containing all the items of this one,
  // plus one item of the given name and price in cents
  IShoppingCart add(String itemName, int price);

  // Remove the given quantity of items of the given name from this shopping
  // cart, and produce a new cart of the remaining items
  IShoppingCart removeItem(String itemName, int count);

  // Produces a new, empty shopping cart
  IShoppingCart removeEverything();
}

class ShoppingCart implements IShoppingCart {
  ILoItem items;

  // creates an empty shopping cart
  ShoppingCart() {
    this(new MtLoItem());
  }

  // creates a shopping cart with the given items
  ShoppingCart(ILoItem items) {
    this.items = items;
  }

  // How many items are in this shopping cart?
  public int numItems() {
    return items.length();
  }

  // How many items of the given name are in this shopping cart?
  public int itemCount(String itemName) {
    return this.items.itemCount(itemName);
  }

  // What is the total cost of all the items in this shopping cart,
  // in cents?
  public int totalPrice() {
    return this.items.totalPrice();
  }

  // Produce a shopping cart containing all the items of this one,
  // plus one item of the given name and price in cents
  public IShoppingCart add(String itemName, int price) {
    return new ShoppingCart(new ConsLoItem(new Item(itemName, price), this.items));
  }

  // Remove the given quantity of items of the given name from this shopping
  // cart, and produce a new cart of the remaining items
  public IShoppingCart removeItem(String itemName, int count) {
    return new ShoppingCart(this.items.removeItem(itemName, count));
  }

  // Produces a new, empty shopping cart
  public IShoppingCart removeEverything() {
    return new ShoppingCart();
  }
}

class ExamplesShoppingCart {
  IShoppingCart empty = new ShoppingCart();
  IShoppingCart withCarrot = empty.add("carrot", 50);
  IShoppingCart with2Carrot = withCarrot.add("carrot", 50);
  IShoppingCart bluth = withCarrot.add("banana", 1000);

  IShoppingCart minusBanana = bluth.removeItem("banana", 1);
  IShoppingCart bluthCleared = bluth.removeEverything();

  boolean testItemCount(Tester t) {
    return t.checkExpect(with2Carrot.itemCount("carrot"), 2)
        && t.checkExpect(withCarrot.itemCount("carrot"), 1)
        && t.checkExpect(empty.itemCount("carrot"), 0);
  }

  boolean testAdd(Tester t) {
    return t.checkExpect(with2Carrot.itemCount("carrot"), 2)
        && t.checkExpect(withCarrot.itemCount("carrot"), 1)
        && t.checkExpect(empty.itemCount("carrot"), 0);
  }

  boolean testNumItems(Tester t) {
    return t.checkExpect(bluth.numItems(), 2)//
        && t.checkExpect(withCarrot.numItems(), 1) && t.checkExpect(empty.numItems(), 0);
  }

  boolean testRemoveItem(Tester t) {
    return t.checkExpect(minusBanana.itemCount("banana"), 0)
        && t.checkExpect(with2Carrot.removeItem("carrot", 1).numItems(), 1)
        && t.checkExpect(with2Carrot.removeItem("carrot", 2).numItems(), 0);
  }

  boolean testRemoveEverything(Tester t) {
    return t.checkExpect(bluthCleared.numItems(), 0);
  }
}