import tester.Tester;

interface ILoString {
  // produces a list of the same items in the reverse order
  ILoString reverse();
  // Our implementation of reverse() differs from the Fundies 1 version because
  // ours uses Snoc and Append, while the Fundies 1 version only uses cons.
  // Therefore, the Fundies 1 version has to use an accumulator to keep track of
  // already reversed elements, while ours can use simple recursion.

  // produces a list of the same items in the same
  // order as this list using only ConsLoString and MtLoString
  ILoString normalize();

  // produces a normalized list (see above) of the elements in this list
  // and the given list.
  // ACCUMULATOR: the items in the given list have already
  // been normalized and should come after the items in this list.
  ILoString normalizeHelp(ILoString alreadyNormalized);

  // concatenates the given string to the start of all of the strings in this list
  ILoString concatToAll(String str);

  // left-scans across the list and concatenates all the strings, and returns a
  // list of the results
  ILoString scanConcat();
}

class MtLoString implements ILoString {
  /*
   * Template
   * 
   * Methods:
   *  reverse()--ILoString
   *  normalize()--ILoString
   *  normalizeHelp(ILoString)--ILoString
   *  concatToAll(String)--String
   *  scanConcat()--String
   *  
   */

  // produces a list of the same items in the reverse order (just an empty list)
  public ILoString reverse() {
    /*
     * Template: Everything in class template
     */
    return this;
  }

  // produces a list of the same items in the same
  // order as this list using only ConsLoString and MtLoString (just an empty
  // list)
  public ILoString normalize() {
    /*
     * Template: Everything in class template
     */
    return this;
  }

  // produces a normalized list (see above) of the elements in this list
  // and the given list (ie just return the given list).
  // ACCUMULATOR: the items in the given list have already
  // been normalized and should come after the items in this list.
  public ILoString normalizeHelp(ILoString alreadyNormalized) {
    /*
     * Template: Everything in class template plus...
     * 
     * Parameters:
     *  alreadyNormalized--ILoString
     * Methods on Parameters:
     *  alreadyNormalized.reverse()--ILoString
     *  alreadyNormalized.normalize()--ILoString
     *  alreadyNormalized.normalizeHelp(ILoString)--ILoString
     *  alreadyNormalized.concatToAll(String)--ILoString
     *  alreadyNormalized.scanConcat()--ILoString
     */
    return alreadyNormalized;
  }

  // left-scans across the list and concatenates all the strings, and returns a
  // list of the results (ie just returns an empty list)
  public ILoString scanConcat() {
    /*
     * Template: Everything in class template
     */
    return this;
  }

  // concatenates the given string to the start of all of the strings in this list
  // (ie just returns an empty list)
  public ILoString concatToAll(String str) {
    /*
     * Template: Everything in class template plus...
     * 
     * Fields:
     *  str--String
     * 
     */
    return this;
  }
}

class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template
   * 
   * Methods:
   *  reverse()--ILoString
   *  normalize()--ILoString
   *  normalizeHelp(ILoString)--ILoString
   *  concatToAll(String)--String
   *  scanConcat()--String
   * 
   * Fields:
   *  first--String
   *  rest--ILoString
   *  
   * Methods on Fields:
   *  rest.reverse()--ILoString
   *  rest.normalize()--ILoString
   *  rest.normalizeHelp(ILoString)--ILoString
   *  rest.concatToAll(String)--String
   *  rest.scanConcat()--String
   *  
   */

  // produces a list of the same items in the reverse order
  public ILoString reverse() {
    /*
     * Template: Everything in class template
     */
    return new SnocLoString(this.rest.reverse(), this.first);
  }

  // produces a list of the same items in the same
  // order as this list using only ConsLoString and MtLoString
  public ILoString normalize() {
    /*
     * Template: Everything in class template
     */
    return new ConsLoString(this.first, this.rest.normalize());
  }

  // produces a normalized list (see above) of the elements in this list
  // and the given list.
  // ACCUMULATOR: the items in the given list have already
  // been normalized and should come after the items in this list.
  public ILoString normalizeHelp(ILoString alreadyNormalized) {
    /*
     * Template: Everything in class template plus...
     * 
     * Parameters:
     *  alreadyNormalized--ILoString
     * Methods on Parameters:
     *  alreadyNormalized.reverse()--ILoString
     *  alreadyNormalized.normalize()--ILoString
     *  alreadyNormalized.normalizeHelp(ILoString)--ILoString
     *  alreadyNormalized.concatToAll(String)--ILoString
     *  alreadyNormalized.scanConcat()--ILoString
     */
    return new ConsLoString(this.first, this.rest.normalizeHelp(alreadyNormalized));
  }

  // concatenates the given string to the start of all of the strings in this list
  public ILoString concatToAll(String str) {
    /*
     * Template: Everything in class template plus...
     * 
     * Parameter:
     *  str--String
     * 
     */
    return new ConsLoString(str + this.first, this.rest.concatToAll(str));
  }

  // left-scans across the list and concatenates all the strings, and returns a
  // list of the results
  public ILoString scanConcat() {
    /*
     * Template: Everything in class template
     */
    return new ConsLoString(this.first, this.rest.scanConcat().concatToAll(this.first));
  }
}

class AppendLoString implements ILoString {
  ILoString front;
  ILoString back;

  /*
   * Template
   * 
   * Methods:
   *  reverse()--ILoString
   *  normalize()--ILoString
   *  normalizeHelp(ILoString)--ILoString
   *  concatToAll(String)--String
   *  scanConcat()--String
   * 
   * Fields:
   *  front--ILoString
   *  back--ILoString
   *  
   * Methods on Fields:
   *  front.reverse()--ILoString
   *  front.normalize()--ILoString
   *  first.normalizeHelp(ILoString)--ILoString
   *  first.concatToAll(String)--String
   *  first.scanConcat()--String
   *  second.reverse()--ILoString
   *  second.normalize()--ILoString
   *  second.normalizeHelp(ILoString)--ILoString
   *  second.concatToAll(String)--String
   *  second.scanConcat()--String
   *  
   */

  AppendLoString(ILoString front, ILoString back) {
    this.front = front;
    this.back = back;
  }

  // produces a list of the same items in the reverse order
  public ILoString reverse() {
    /*
     * Template: Everything in class template
     */
    return new AppendLoString(this.back.reverse(), this.front.reverse());
  }

  // produces a list of the same items in the same
  // order as this list using only ConsLoString and MtLoString
  public ILoString normalize() {
    /*
     * Template: Everything in class template
     */
    return this.front.normalizeHelp(this.back.normalize());
  }

  // produces a normalized list (see above) of the elements in this list
  // and the given list.
  // ACCUMULATOR: the items in the given list have already
  // been normalized and should come after the items in this list.
  public ILoString normalizeHelp(ILoString alreadyNormalized) {
    /*
     * Template: Everything in class template plus...
     * 
     * Parameters:
     *  alreadyNormalized--ILoString
     * Methods on Parameters:
     *  alreadyNormalized.reverse()--ILoString
     *  alreadyNormalized.normalize()--ILoString
     *  alreadyNormalized.normalizeHelp(ILoString)--ILoString
     *  alreadyNormalized.concatToAll(String)--ILoString
     *  alreadyNormalized.scanConcat()--ILoString
     */
    return this.front.normalizeHelp(this.back.normalizeHelp(alreadyNormalized));
  }

  // concatenates the given string to the start of all of the strings in this list
  public ILoString concatToAll(String str) {
    /*
     * Template: Everything in class template plus...
     * 
     * Fields:
     *  str--String
     * 
     */
    return new AppendLoString(this.front.concatToAll(str), this.back.concatToAll(str));
  }

  // left-scans across the list and concatenates all the strings, and returns a
  // list of the results. Does this by first normalizing and then calling
  // scanConcat on the normalized list,
  // which is guaranteed not to recur infinitely because the ConsLoString and
  // MtLoString in a normalized list do not rely on this technique for scanConcat.
  public ILoString scanConcat() {
    /*
     * Template: Everything in class template
     */
    return this.normalize().scanConcat();
  }
}

class SnocLoString implements ILoString {
  ILoString front;
  String last;

  /*
   * Template
   * 
   * Methods:
   *  reverse()--ILoString
   *  normalize()--ILoString
   *  normalizeHelp(ILoString)--ILoString
   *  concatToAll(String)--String
   *  scanConcat()--String
   * 
   * Fields:
   *  front--ILoString
   *  last--String
   *  
   * Methods on Fields:
   *  front.reverse()--ILoString
   *  front.normalize()--ILoString
   *  front.normalizeHelp(ILoString)--ILoString
   *  front.concatToAll(String)--String
   *  front.scanConcat()--String
   *  
   */

  SnocLoString(ILoString front, String last) {
    this.front = front;
    this.last = last;
  }

  // produces a list of the same items in the reverse order
  public ILoString reverse() {
    /*
     * Template: Everything in class template
     */
    return new ConsLoString(this.last, this.front.reverse());
  }

  // concatenates the given string to the start of all of the strings in this list
  public ILoString concatToAll(String str) {
    /*
     * Template: Everything in class template
     */
    return new SnocLoString(this.front.concatToAll(str), str + this.last);
  }

  // produces a list of the same items in the same
  // order as this list using only ConsLoString and MtLoString
  public ILoString normalize() {
    /*
     * Template: Everything in class template
     */
    return this.front.normalizeHelp(new ConsLoString(this.last, new MtLoString()));
  }

  // produces a normalized list (see above) of the elements in this list
  // and the given list.
  // ACCUMULATOR: the items in the given list have already
  // been normalized and should come after the items in this list.
  public ILoString normalizeHelp(ILoString alreadyNormalized) {
    /*
    * Template: Everything in class template plus...
    * 
    * Parameters:
    *  alreadyNormalized--ILoString
    * Methods on Parameters:
    *  alreadyNormalized.reverse()--ILoString
    *  alreadyNormalized.normalize()--ILoString
    *  alreadyNormalized.normalizeHelp(ILoString)--ILoString
    *  alreadyNormalized.concatToAll(String)--ILoString
    *  alreadyNormalized.scanConcat()--ILoString
    */
    return this.front.normalizeHelp(new ConsLoString(this.last, alreadyNormalized));
  }

  // left-scans across the list and concatenates all the strings, and returns a
  // list of the results. Does this by first normalizing and then calling
  // scanConcat on the normalized list,
  // which is guaranteed not to recur infinitely because the ConsLoString and
  // MtLoString in a normalized list do not rely on this technique for scanConcat.
  public ILoString scanConcat() {
    /*
     * Template: Everything in class template
     */
    return this.normalize().scanConcat();
  }

}

class ExamplesILoString {
  MtLoString empty = new MtLoString();
  ConsLoString simpleCons = new ConsLoString("one", new ConsLoString("two", empty));
  AppendLoString simpleAppend = new AppendLoString(new ConsLoString("one", empty),
      new ConsLoString("two", empty));
  SnocLoString simpleSnoc = new SnocLoString(new SnocLoString(empty, "one"), "two");
  ConsLoString complicatedCons = new ConsLoString("one",
      new AppendLoString(new ConsLoString("two", empty), new SnocLoString(empty, "three")));
  AppendLoString complicatedAppend = new AppendLoString(
      new AppendLoString(new SnocLoString(empty, "one"), new SnocLoString(empty, "two")),
      new ConsLoString("three", empty));
  SnocLoString complicatedSnoc = new SnocLoString(
      new SnocLoString(new AppendLoString(empty, new ConsLoString("one", empty)), "two"), "three");

  // tests the method reverse in interface ILoString
  boolean testReverse(Tester t) {
    return t.checkExpect(empty, empty)
        && t.checkExpect(simpleCons.reverse(),
            new SnocLoString(new SnocLoString(empty, "two"), "one"))
        && t.checkExpect(simpleAppend.reverse(),
            new AppendLoString(new SnocLoString(empty, "two"), new SnocLoString(empty, "one")))
        && t.checkExpect(simpleSnoc.reverse(),
            new ConsLoString("two", new ConsLoString("one", empty)))
        && t.checkExpect(complicatedAppend.reverse(), new AppendLoString(
            new SnocLoString(empty, "three"),
            new AppendLoString(new ConsLoString("two", empty), new ConsLoString("one", empty))));
  }

  // tests the method normalize in interface ILoString
  boolean testNormalize(Tester t) {
    return t.checkExpect(empty, empty)//
        && t.checkExpect(simpleCons.normalize(), simpleCons)
        && t.checkExpect(simpleAppend.normalize(), simpleCons)
        && t.checkExpect(simpleSnoc.normalize(), simpleCons)
        && t.checkExpect(complicatedAppend.normalize(),
            new ConsLoString("one", new ConsLoString("two", new ConsLoString("three", empty))));
  }

  ConsLoString four = new ConsLoString("one",
      new ConsLoString("two", new ConsLoString("three", new ConsLoString("four", empty))));

  // tests the method normalizeHelp in interface ILoString
  boolean testNormalizeHelp(Tester t) {
    return t.checkExpect(empty.normalizeHelp(simpleCons), simpleCons)//
        && t.checkExpect(
            simpleCons.normalizeHelp(new ConsLoString("three", new ConsLoString("four", empty))),
            four)
        && t.checkExpect(
            simpleAppend.normalizeHelp(new ConsLoString("three", new ConsLoString("four", empty))),
            four)
        && t.checkExpect(
            simpleSnoc.normalizeHelp(new ConsLoString("three", new ConsLoString("four", empty))),
            four)
        && t.checkExpect(complicatedAppend.normalizeHelp(new ConsLoString("four", empty)), four);
  }

  // tests the method concatToAll in interface ILoString
  boolean testConcatToAll(Tester t) {
    return t.checkExpect(empty.concatToAll("one"), empty)
        && t.checkExpect(simpleCons.concatToAll("start "),
            new ConsLoString("start one", new ConsLoString("start two", empty)))
        && t.checkExpect(simpleAppend.concatToAll("start "),
            new AppendLoString(new ConsLoString("start one", empty),
                new ConsLoString("start two", empty)))
        && t.checkExpect(simpleSnoc.concatToAll("start "), new SnocLoString(
            new SnocLoString(empty, "start one"), "start two"))
        && t.checkExpect(complicatedAppend.concatToAll("start "),
            new AppendLoString(new AppendLoString(new SnocLoString(empty, "start one"),
                new SnocLoString(empty, "start two")), new ConsLoString("start three", empty)));
  }

  ConsLoString oneTwo = new ConsLoString("one", new ConsLoString("onetwo", empty));
  ConsLoString oneTwoThree = new ConsLoString("one",
      new ConsLoString("onetwo", new ConsLoString("onetwothree", empty)));

  // tests the method scanConcat in interface ILoString
  boolean testScanConcat(Tester t) {
    return t.checkExpect(empty.scanConcat(), empty)
        && t.checkExpect(simpleCons.scanConcat(), oneTwo)
        && t.checkExpect(simpleAppend.scanConcat(), oneTwo)
        && t.checkExpect(simpleSnoc.scanConcat(), oneTwo)
        && t.checkExpect(complicatedAppend.scanConcat(), oneTwoThree);
  }

}