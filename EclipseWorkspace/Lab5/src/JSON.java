//Represents functions of signature A -> R, for some argument type A and
//result type R
interface IFunc<A, R> {
  R apply(A input);
}

//Represents functions of signature AB -> R, for some argument types A, B and
//result type R
interface IFunc2<A, B, R> {
  R apply(A input, B input2);
}

//generic list
interface IList<T> {
  // map over a list, and produce a new list with a (possibly different)
  // element type
  <U> IList<U> map(IFunc<T, U> f);

  // foldrs over a list
  <U> U foldr(IFunc2<T, U, U> f, U base);

  <U> U findSolutionOrElse(IFunc<T, U> convert, IPred<U> pred, U backup);
}

//empty generic list
class MtList<T> implements IList<T> {
  public <U> IList<U> map(IFunc<T, U> f) {
    return new MtList<U>();
  }

  // foldrs over a list
  public <U> U foldr(IFunc2<T, U, U> f, U base) {
    return base;
  }

  public <U> U findSolutionOrElse(IFunc<T, U> convert, IPred<U> pred, U backup) {
    return backup;
  }
}

//non-empty generic list
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  public <U> IList<U> map(IFunc<T, U> f) {
    return new ConsList<U>(f.apply(this.first), this.rest.map(f));
  }

  // foldrs over a list
  public <U> U foldr(IFunc2<T, U, U> f, U base) {
    return f.apply(this.first, this.rest.foldr(f, base));
  }

  public <U> U findSolutionOrElse(IFunc<T, U> convert, IPred<U> pred, U backup) {
    U res = convert.apply(this.first);
    if (pred.apply(res)) {
      return res;
    }
    else {
      return this.rest.findSolutionOrElse(convert, pred, backup);
    }
  }
}

// a json value
interface JSON {
  <U> U accept(JSONVisitor<U> f);
}

// no value
class JSONBlank implements JSON {

  public <U> U accept(JSONVisitor<U> f) {
    return f.visit(this);
  }
}

// a number
class JSONNumber implements JSON {
  int number;

  JSONNumber(int number) {
    this.number = number;
  }

  public <U> U accept(JSONVisitor<U> f) {
    return f.visit(this);
  }
}

// a boolean
class JSONBool implements JSON {
  boolean bool;

  JSONBool(boolean bool) {
    this.bool = bool;
  }

  public <U> U accept(JSONVisitor<U> f) {
    return f.visit(this);
  }
}

// a string
class JSONString implements JSON {
  String str;

  JSONString(String str) {
    this.str = str;
  }

  public <U> U accept(JSONVisitor<U> f) {
    return f.visit(this);
  }
}

//a list of JSON values
class JSONList implements JSON {
  IList<JSON> values;

  JSONList(IList<JSON> values) {
    this.values = values;
  }

  public <U> U accept(JSONVisitor<U> f) {
    return f.visit(this);
  }
}

class AddJSONToNumber implements IFunc2<JSON, Integer, Integer> {
  public Integer apply(JSON n1, Integer n2) {
    return new JSONToNumber().apply(n1) + n2;
  }
}

interface JSONVisitor<T> extends IFunc<JSON, T> {
  T visit(JSONBlank b);

  T visit(JSONNumber n);

  T visit(JSONBool b);

  T visit(JSONString s);

  T visit(JSONList s);

  T visit(JSONObject s);

}

class JSONToNumber implements JSONVisitor<Integer> {
  public Integer visit(JSONBlank b) {
    return 0;
  }

  public Integer visit(JSONNumber n) {
    return n.number;
  }

  public Integer visit(JSONBool b) {
    if (b.bool) {
      return 1;
    }
    return 0;
  }

  public Integer visit(JSONString s) {
    return s.str.length();
  }

  public Integer visit(JSONList s) {
    return s.values.foldr(new AddJSONToNumber(), 0);
  }

  public Integer visit(JSONObject s) {
    return s.pairs.map(new getY<String, JSON>()).foldr(new AddJSONToNumber(), 0);
  }

  public Integer apply(JSON j) {
    return j.accept(this);
  }
}

interface IPred<T> extends IFunc<T, Boolean> {
};

//a list of JSON pairs
class JSONObject implements JSON {
  IList<Pair<String, JSON>> pairs;

  JSONObject(IList<Pair<String, JSON>> pairs) {
    this.pairs = pairs;
  }

  public <U> U accept(JSONVisitor<U> f) {
    return f.visit(this);
  }
}

//generic pairs
class Pair<X, Y> {
  X x;
  Y y;

  Pair(X x, Y y) {
    this.x = x;
    this.y = y;
  }
}

class getY<X, Y> implements IFunc<Pair<X, Y>, Y> {

  public Y apply(Pair<X, Y> p) {
    return p.y;
  }

}

class Examples {
  IList<JSON> foo = new ConsList<JSON>(new JSONBlank(),
      new ConsList<JSON>(new JSONNumber(4), new ConsList<JSON>(new JSONBool(true),
          new ConsList<JSON>(new JSONString("wow"), new MtList<JSON>()))));

  IList<Integer> fooLengths = foo.map(new JSONToNumber());

}