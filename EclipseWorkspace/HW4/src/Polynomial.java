import tester.Tester;

//represents a monomial
class Monomial {
  int coefficient;
  int degree;

  // constructs a monomial with the given coefficient and degrees
  Monomial(int coefficient, int degree) {
    if (degree < 0) {
      throw new IllegalArgumentException("Cannot construct a monomial with negative degree");
    }
    this.coefficient = coefficient;
    this.degree = degree;
  }

  // returns the difference between the degree of this monomial and the given
  // monomial
  int subtractDegree(Monomial other) {
    return this.degree - other.degree;
  }

  // evaluates this monomial at the given value
  int evaluate(int x) {
    return this.coefficient * (int) Math.pow(x, this.degree);
  }

  // returns whether this monomial has coefficient 0
  boolean zeroCoefficient() {
    return this.coefficient == 0;
  }

  // increases this monomial's coefficient by the given monomial's coefficient
  Monomial addCoefficientOf(Monomial other) {
    return new Monomial(other.coefficient + this.coefficient, this.degree);
  }

  // returns the result of this monomial multiplied by to the given monomial
  Monomial multiply(Monomial other) {
    return new Monomial(other.coefficient * this.coefficient, this.degree + other.degree);
  }

}

//represents a list of monomials
interface ILoMonomial {

  // evaluates this list of monomials at the given value, adding the results of
  // each monomial evaluated at the value
  int evaluate(int x);

  // returns true if this list contains a monomial with the same degree as the
  // given monomial, false otherwise
  boolean containsDegree(Monomial other);

  // checks if this list contains two monomials with the same degree
  boolean containsDuplicateDegrees();

  // removes any terms from this list with zero coefficients and returns the
  // resulting list
  ILoMonomial removeZeroTerms();

  // returns the monomial with the lowest degree in the original list
  // ACCUMULATOR: lowestSoFar is the Monomial with the lowest degree in the
  // original list before this
  Monomial getLowestDegreeTermAcc(Monomial lowestSoFar);

  // returns a new list with all monomials in this list except the first one
  // with the same degree as the given monomial.
  ILoMonomial allButFirstWithDegree(Monomial withDegree);

  // returns a new list with all the monomials in this list in ascending degree
  // order
  ILoMonomial sortAscendingDegree();

  // returns a normalized version of this list, with no zero coefficients and
  // degrees in ascending order. Always returns this.
  ILoMonomial normalize();

  // returns the sum of this list of monomials and the given monomial
  ILoMonomial addMonomial(Monomial other);

  // returns the sum of this list of monomials and the given list of monomials
  ILoMonomial addILoMonomial(ILoMonomial other);

  // returns the highest degree monomial in this list
  int highestDegree();

  // returns the product of this list of monomials and the given monomial
  ILoMonomial multiplyMonomial(Monomial other);

  // returns the product of this list of monomials and the given list of monomials
  ILoMonomial multiplyILoMonomial(ILoMonomial other);

}

// represents an empty list of monomials
class MtLoMonomial implements ILoMonomial {
  // evaluates this empty list of monomials at the given value, always returns 0
  public int evaluate(int x) {
    return 0;
  }

  // returns true if this empty list contains a monomial with the same degree as
  // the given monomial, false otherwise (always returns false)
  public boolean containsDegree(Monomial other) {
    return false;
  }

  // checks if this empty list contains two monomials with the same degree, always
  // returns false
  public boolean containsDuplicateDegrees() {
    return false;
  }

  // removes any terms from this empty list with zero coefficients and returns the
  // resulting list (always returns this)
  public ILoMonomial removeZeroTerms() {
    return this;
  }

  // returns the monomial with the lowest degree in the original list
  // ACCUMULATOR: lowestSoFar is the Monomial with the lowest degree in the
  // original list before this. Always returns lowestSoFar
  public Monomial getLowestDegreeTermAcc(Monomial lowestSoFar) {
    return lowestSoFar;
  }

  // returns a new list with all monomials in this empty list except the first one
  // with the same degree as the given monomial. Always returns this.
  public ILoMonomial allButFirstWithDegree(Monomial withDegree) {
    return this;
  }

  // returns a normalized version of this empty list, with no zero coefficients
  // and degrees in ascending order. Always returns this.
  public ILoMonomial normalize() {
    return this;
  }

  // returns a new list with all the monomials in this empty list in ascending
  // degree order. Always returns this.
  public ILoMonomial sortAscendingDegree() {
    return this;
  }

  // returns the sum of this empty list of monomials and the given monomial;
  // returns a normalized list with just the given monomial
  public ILoMonomial addMonomial(Monomial other) {
    return new ConsLoMonomial(other, this);
  }

  // returns the sum of this empty list of monomials and the given list of
  // monomials; returns the given list normalized
  public ILoMonomial addILoMonomial(ILoMonomial other) {
    return other;
  }

  // returns the highest degree monomial from this empty list (always 0)
  public int highestDegree() {
    return 0;
  }

  // returns the product of this empty list of monomials and the given monomial
  // (just returns the empty list)
  public ILoMonomial multiplyMonomial(Monomial other) {
    return this;
  }

  // returns the product of this empty list of monomials and the given list of
  // monomials
  // (just returns the empty list)
  public ILoMonomial multiplyILoMonomial(ILoMonomial other) {
    return this;
  }

}

//represents a non-empty list of monomials
class ConsLoMonomial implements ILoMonomial {
  Monomial first;
  ILoMonomial rest;

  ConsLoMonomial(Monomial first, ILoMonomial rest) {
    this.first = first;
    this.rest = rest;
  }

  // evaluates this cons list of monomials at the given value, adding the results
  // of each monomial evaluated at the value
  public int evaluate(int x) {
    return this.first.evaluate(x) + this.rest.evaluate(x);
  }

  // returns true if this cons list contains a monomial with the same degree as
  // the given monomial, false otherwise (always returns false)
  public boolean containsDegree(Monomial other) {
    return this.first.subtractDegree(other) == 0 || this.rest.containsDegree(other);
  }

  // checks if this cons list contains two monomials with the same degree
  public boolean containsDuplicateDegrees() {
    return rest.containsDegree(first) || rest.containsDuplicateDegrees();
  }

  // removes any terms from this cons list with zero coefficients and returns the
  // resulting list
  public ILoMonomial removeZeroTerms() {
    if (this.first.zeroCoefficient()) {
      return this.rest.removeZeroTerms();
    }
    return new ConsLoMonomial(this.first, this.rest.removeZeroTerms());
  }

  // returns the monomial with the lowest degree in this list
  public Monomial getLowestDegreeTerm() {
    return this.rest.getLowestDegreeTermAcc(this.first);
  }

  // returns the monomial with the lowest degree in the original list (the first
  // one, if there are many)
  // ACCUMULATOR: lowestSoFar is the Monomial with the lowest degree in the
  // original list before this.
  public Monomial getLowestDegreeTermAcc(Monomial lowestSoFar) {
    if (this.first.subtractDegree(lowestSoFar) < 0) {
      return this.rest.getLowestDegreeTermAcc(this.first);
    }
    return this.rest.getLowestDegreeTermAcc(lowestSoFar);
  }

  // returns a new list with all monomials in this cons list except the first one
  // with the same degree as the given monomial.
  public ILoMonomial allButFirstWithDegree(Monomial withDegree) {
    if (this.first.subtractDegree(withDegree) == 0) {
      return this.rest;
    }
    return new ConsLoMonomial(this.first, this.rest.allButFirstWithDegree(withDegree));
  }

  // returns a new list with all the monomials in this cons list in ascending
  // degree order (selection sort)
  public ILoMonomial sortAscendingDegree() {
    Monomial lowestDegree = this.getLowestDegreeTerm();
    return new ConsLoMonomial(lowestDegree,
        this.allButFirstWithDegree(lowestDegree).sortAscendingDegree());
  }

  // returns a normalized version of this cons list, with no zero coefficients and
  // degrees in ascending order
  public ILoMonomial normalize() {
    return this.removeZeroTerms().sortAscendingDegree();
  }

  // returns the sum of this cons list of monomials and the given monomial
  public ILoMonomial addMonomial(Monomial other) {
    if (this.first.subtractDegree(other) == 0) {
      return new ConsLoMonomial(this.first.addCoefficientOf(other), this.rest);
    }
    if (this.first.subtractDegree(other) > 0) {
      return new ConsLoMonomial(other, this);
    }
    return new ConsLoMonomial(this.first, this.rest.addMonomial(other));

  }

  // returns the sum of this list of monomials and the given list of monomials
  public ILoMonomial addILoMonomial(ILoMonomial other) {
    return this.rest.addILoMonomial(other.addMonomial(this.first));
  }

  // returns the highest degree monomial from this cons list
  public int highestDegree() {
    return Math.max(this.first.subtractDegree(new Monomial(0, 0)), this.rest.highestDegree());
  }

  // returns the product of this cons list of monomials and the given monomial
  public ILoMonomial multiplyMonomial(Monomial other) {
    return new ConsLoMonomial(this.first.multiply(other), this.rest.multiplyMonomial(other));
  }

  // returns the product of this cons list of monomials and the given list of
  // monomials
  public ILoMonomial multiplyILoMonomial(ILoMonomial other) {
    return other.multiplyMonomial(this.first).addILoMonomial(this.rest.multiplyILoMonomial(other));
  }

}

//represents a polynomial
class Polynomial {
  ILoMonomial monomials;

  // constructs a polynomial with the given monomials
  Polynomial(ILoMonomial monomials) {
    ILoMonomial normalizedMonomials = monomials.normalize();
    if (normalizedMonomials.containsDuplicateDegrees()) {
      throw new IllegalArgumentException(
          "Cannot construct a polynomial with two monomials of the same degree");
    }
    this.monomials = normalizedMonomials;
  }

  // constructs a polynomial with no monomials, ie the zero polynomial
  Polynomial() {
    this(new MtLoMonomial());
  }

  // evaluates this monomial at the given value
  int evaluate(int x) {
    return this.monomials.evaluate(x);
  }

  // returns the result of this polynomial added to the given polynomial
  Polynomial add(Polynomial other) {
    return new Polynomial(this.monomials.addILoMonomial(other.monomials));
  }

  // returns if this polynomial is the same as the given polynomial
  boolean samePolynomial(Polynomial other) {
    int highestDegree = Math.max(this.monomials.highestDegree(), other.monomials.highestDegree());
    return this.samePolynomialHelp(other, highestDegree + 1);
  }

  // returns if this polynomial is the same as the given polynomial based on if it
  // returns the same result for (highest degree +1) different inputs. testNext
  // is the next input to test
  // TERMINATES: testNext starts at some integer and reduces by 1 each call until
  // it reaches 1
  boolean samePolynomialHelp(Polynomial other, int testNext) {
    if (testNext == 0) {
      return true;
    }
    return this.evaluate(testNext) == other.evaluate(testNext)
        && this.samePolynomialHelp(other, testNext - 1);
  }

  // returns the product of this polynomial and the given polynomial
  Polynomial multiply(Polynomial other) {
    return new Polynomial(this.monomials.multiplyILoMonomial(other.monomials));
  }

}

class ExamplesPolynomial {
  Monomial deg0Zero = new Monomial(0, 0);
  Monomial deg0 = new Monomial(2, 0);
  Monomial deg1 = new Monomial(3, 1);
  Monomial deg1Negative = new Monomial(-3, 1);
  Monomial deg2 = new Monomial(2, 2);
  Monomial deg2Zero = new Monomial(0, 2);

  ILoMonomial empty = new MtLoMonomial();
  ILoMonomial deg1List = new ConsLoMonomial(deg1, empty);
  ILoMonomial allPositiveList = new ConsLoMonomial(deg0,
      new ConsLoMonomial(deg1, new ConsLoMonomial(deg2, empty)));
  ILoMonomial missingDeg1List = new ConsLoMonomial(deg0, new ConsLoMonomial(deg2, empty));
  ILoMonomial negativeList = new ConsLoMonomial(deg0,
      new ConsLoMonomial(deg1Negative, new ConsLoMonomial(deg2, empty)));
  ILoMonomial zeroCoefficientsList = new ConsLoMonomial(deg0Zero,
      new ConsLoMonomial(deg1, new ConsLoMonomial(deg2Zero, empty)));
  ILoMonomial duplicates = new ConsLoMonomial(deg0,
      new ConsLoMonomial(deg1Negative, new ConsLoMonomial(deg1, new ConsLoMonomial(deg2, empty))));

  Polynomial defaultPol = new Polynomial(empty);
  Polynomial emptyPol = new Polynomial(empty);
  Polynomial allPositivePol = new Polynomial(allPositiveList);
  Polynomial missingDeg1Pol = new Polynomial(missingDeg1List);
  Polynomial negativePol = new Polynomial(negativeList);
  Polynomial zeroCoefficientsPol = new Polynomial(zeroCoefficientsList);
  Polynomial deg1Pol = new Polynomial(deg1List);

  // tests the Monomial constructor
  boolean testMonomialConstructor(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException("Cannot construct a monomial with negative degree"),
        "Monomial", 2, -3);
  }

  // tests the subtractDegree method on the Monomial class
  boolean testSubtractDegree(Tester t) {
    return t.checkExpect(deg1.subtractDegree(deg2), -1)
        && t.checkExpect(deg2.subtractDegree(deg0), 2)
        && t.checkExpect(deg1.subtractDegree(deg1), 0);
  }

  // tests the containsDegree method on the ILoMonomial interface
  boolean testContainsDegree(Tester t) {
    return t.checkExpect(empty.containsDegree(deg1), false)
        && t.checkExpect(allPositiveList.containsDegree(deg1), true)
        && t.checkExpect(missingDeg1List.containsDegree(deg1), false);
  }

  // tests the containsDuplicateDegrees method on the ILoMonomial interface
  boolean testContainsDuplicateDegrees(Tester t) {
    return t.checkExpect(empty.containsDuplicateDegrees(), false)
        && t.checkExpect(allPositiveList.containsDuplicateDegrees(), false)
        && t.checkExpect(duplicates.containsDuplicateDegrees(), true);
  }

  // tests the evaluate method on the Monomial class
  boolean testEvaluateMonomial(Tester t) {
    return t.checkExpect(deg0.evaluate(10432), 2) && t.checkExpect(deg1.evaluate(2), 6)
        && t.checkExpect(deg1Negative.evaluate(3), -9) && t.checkExpect(deg2.evaluate(3), 18)
        && t.checkExpect(deg2.evaluate(4), 32) && t.checkExpect(deg2Zero.evaluate(3), 0);
  }

  // tests the evaluate method on the ILoMonomial class
  boolean testEvaluateILoMonomial(Tester t) {
    return t.checkExpect(empty.evaluate(123), 0)//
        && t.checkExpect(allPositiveList.evaluate(1), 7)//
        && t.checkExpect(allPositiveList.evaluate(2), 16)//
        && t.checkExpect(negativeList.evaluate(1), 1);
  }

  // tests the evaluate method on the ILoMonomial class
  boolean testEvaluatePolymonomial(Tester t) {
    return t.checkExpect(empty.evaluate(123), 0)//
        && t.checkExpect(allPositivePol.evaluate(1), 7)//
        && t.checkExpect(allPositivePol.evaluate(2), 16)//
        && t.checkExpect(negativePol.evaluate(1), 1);

  }

  // tests the zeroCoefficient method on the ILoMonomial interface
  boolean testZeroCoefficient(Tester t) {
    return t.checkExpect(deg2.zeroCoefficient(), false)
        && t.checkExpect(deg2Zero.zeroCoefficient(), true);
  }

  // tests the removeZeroTerms method on the ILoMonomial interface
  boolean testRemoveZeroTerms(Tester t) {
    return t.checkExpect(empty.removeZeroTerms(), empty)
        && t.checkExpect(allPositiveList.removeZeroTerms(), allPositiveList)
        && t.checkExpect(zeroCoefficientsList.removeZeroTerms(), deg1List);
  }

  ConsLoMonomial list012 = (ConsLoMonomial) allPositiveList;
  ConsLoMonomial list210 = new ConsLoMonomial(deg2,
      new ConsLoMonomial(deg1, new ConsLoMonomial(deg0, empty)));
  ConsLoMonomial list021 = new ConsLoMonomial(deg0,
      new ConsLoMonomial(deg2, new ConsLoMonomial(deg1, empty)));
  ConsLoMonomial list102 = new ConsLoMonomial(deg1,
      new ConsLoMonomial(deg0, new ConsLoMonomial(deg2, empty)));
  ConsLoMonomial list121Negative = new ConsLoMonomial(deg1,
      new ConsLoMonomial(deg2, new ConsLoMonomial(deg1Negative, empty)));
  ConsLoMonomial list10 = new ConsLoMonomial(deg1, new ConsLoMonomial(deg0, empty));
  ConsLoMonomial list01 = new ConsLoMonomial(deg0, new ConsLoMonomial(deg1, empty));
  ConsLoMonomial list12 = new ConsLoMonomial(deg1, new ConsLoMonomial(deg2, empty));
  ConsLoMonomial list21 = new ConsLoMonomial(deg2, new ConsLoMonomial(deg1, empty));
  ConsLoMonomial list02 = new ConsLoMonomial(deg0, new ConsLoMonomial(deg2, empty));

  // tests the getLowestDegreeTerm method on the ConsLoMonomial class
  boolean testGetLowestDegreeTerm(Tester t) {
    return t.checkExpect(list012.getLowestDegreeTerm(), deg0)
        && t.checkExpect(list210.getLowestDegreeTerm(), deg0)
        && t.checkExpect(list021.getLowestDegreeTerm(), deg0)
        && t.checkExpect(list102.getLowestDegreeTerm(), deg0)
        && t.checkExpect(list12.getLowestDegreeTerm(), deg1)
        && t.checkExpect(list21.getLowestDegreeTerm(), deg1)
        && t.checkExpect(list121Negative.getLowestDegreeTerm(), deg1);
  }

  // tests the getLowestTermAcc method on the ILoMonomial interface
  boolean testGetLowestTermAcc(Tester t) {
    return t.checkExpect(list12.getLowestDegreeTermAcc(deg0), deg0)
        && t.checkExpect(list12.getLowestDegreeTermAcc(deg2), deg1)
        && t.checkExpect(list12.getLowestDegreeTermAcc(deg1Negative), deg1Negative)
        && t.checkExpect(empty.getLowestDegreeTermAcc(deg2), deg2);
  }

  // tests the allButFirstWithDegree method on the ILoMonomial interface
  boolean testAllButFirstWithDegree(Tester t) {
    return t.checkExpect(list01.allButFirstWithDegree(deg0), deg1List)
        && t.checkExpect(list021.allButFirstWithDegree(deg2), list01)
        && t.checkExpect(duplicates.allButFirstWithDegree(deg1), list012);
  }

  // tests the sortAscendingDegree method on the ILoMonomial interface
  boolean testSortAscendingDegree(Tester t) {
    return t.checkExpect(list102.sortAscendingDegree(), list012)
        && t.checkExpect(list021.sortAscendingDegree(), list012)
        && t.checkExpect(list210.sortAscendingDegree(), list012)
        && t.checkExpect(empty.sortAscendingDegree(), empty);
  }

  // tests the normalize method on the ILoMonomial interface
  boolean testNormalizeILoMonomial(Tester t) {
    return t.checkExpect(empty.normalize(), empty)//
        && t.checkExpect(list102.normalize(), list012)
        && t.checkExpect(list210.normalize(), list012)
        && t.checkExpect(zeroCoefficientsList.normalize(), deg1List)
        && t.checkExpect(
            new ConsLoMonomial(deg2, new ConsLoMonomial(deg2Zero, new ConsLoMonomial(deg1, empty)))
                .normalize(),
            list12);
  }

  // tests the polynomial constructors
  boolean testPolynomialConstructors(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException(
            "Cannot construct a polynomial with two monomials of the same degree"),
        "Polynomial", duplicates)//
        && t.checkExpect(defaultPol, emptyPol)//
        && t.checkExpect(new Polynomial(new ConsLoMonomial(deg0Zero, new ConsLoMonomial(deg0Zero,
            new ConsLoMonomial(deg1, new ConsLoMonomial(deg2Zero, empty))))), deg1Pol);
  }

  Monomial mon10 = new Monomial(1, 0);
  Monomial mon20 = new Monomial(2, 0);
  Monomial mon11 = new Monomial(1, 1);
  Monomial mon12 = new Monomial(1, 2);
  Monomial mon21 = new Monomial(2, 1);
  Monomial mon31 = new Monomial(3, 1);
  Monomial mon13 = new Monomial(1, 3);
  Monomial mon23 = new Monomial(2, 3);
  Monomial mon22 = new Monomial(2, 2);
  Monomial mon33 = new Monomial(3, 3);
  Monomial mon32 = new Monomial(3, 2);
  Monomial mon14 = new Monomial(1, 4);

  ConsLoMonomial loMon21Mon13 = new ConsLoMonomial(mon21, new ConsLoMonomial(mon13, empty));
  ConsLoMonomial loMon31Mon13 = new ConsLoMonomial(mon31, new ConsLoMonomial(mon13, empty));
  ConsLoMonomial loMon21Mon23 = new ConsLoMonomial(mon21, new ConsLoMonomial(mon23, empty));
  ConsLoMonomial loMon21Mon12Mon13 = new ConsLoMonomial(mon21,
      new ConsLoMonomial(mon12, new ConsLoMonomial(mon13, empty)));
  ConsLoMonomial loMon21Mon13Mon14 = new ConsLoMonomial(mon21,
      new ConsLoMonomial(mon13, new ConsLoMonomial(mon14, empty)));
  ConsLoMonomial loMon10Mon21Mon13 = new ConsLoMonomial(mon10,
      new ConsLoMonomial(mon21, new ConsLoMonomial(mon13, empty)));
  ConsLoMonomial loMon10Mon12 = new ConsLoMonomial(mon10, new ConsLoMonomial(mon12, empty));
  ConsLoMonomial loMon20Mon11 = new ConsLoMonomial(mon20, new ConsLoMonomial(mon11, empty));

  ConsLoMonomial loMon12Mon23 = new ConsLoMonomial(mon12, new ConsLoMonomial(mon23, empty));
  ConsLoMonomial loMon20Mon11Mon22Mon13 = new ConsLoMonomial(mon20,
      new ConsLoMonomial(mon11, new ConsLoMonomial(mon22, new ConsLoMonomial(mon13, empty))));
  ConsLoMonomial loMon21Mon12Mon33 = new ConsLoMonomial(mon21,
      new ConsLoMonomial(mon12, new ConsLoMonomial(mon33, empty)));

  ConsLoMonomial loMon11 = new ConsLoMonomial(mon11, empty);
  ConsLoMonomial loMon21 = new ConsLoMonomial(mon21, empty);

  // tests the addCoefficientOf method on the Monomial class
  boolean testAddCoefficientOf(Tester t) {
    return t.checkExpect(mon11.addCoefficientOf(mon21), mon31)//
        && t.checkExpect(mon13.addCoefficientOf(mon13), mon23);
  }

  // tests the addMonomial method on the ILoMonomial class
  boolean testAddMonomial(Tester t) {
    return t.checkExpect(empty.addMonomial(deg1), deg1List)//
        && t.checkExpect(loMon21Mon13.addMonomial(mon11), loMon31Mon13)
        && t.checkExpect(loMon21Mon13.addMonomial(mon13), loMon21Mon23)
        && t.checkExpect(loMon21Mon13.addMonomial(mon12), loMon21Mon12Mon13)
        && t.checkExpect(loMon21Mon13.addMonomial(mon14), loMon21Mon13Mon14)
        && t.checkExpect(loMon21Mon13.addMonomial(mon10), loMon10Mon21Mon13);
  }

  // tests the addILoMonomial method on the ILoMonomial class
  boolean testAddILoMonomial(Tester t) {
    return t.checkExpect(empty.addILoMonomial(loMon21Mon13), loMon21Mon13)//
        && t.checkExpect(loMon21Mon13.addILoMonomial(empty), loMon21Mon13)
        && t.checkExpect(loMon11.addILoMonomial(loMon11), loMon21)
        && t.checkExpect(loMon21Mon13.addILoMonomial(loMon12Mon23), loMon21Mon12Mon33);
  }

  Polynomial polMon21Mon13 = new Polynomial(loMon21Mon13);
  Polynomial polMon12Mon23 = new Polynomial(loMon12Mon23);
  Polynomial polMon21Mon12Mon33 = new Polynomial(loMon21Mon12Mon33);
  Polynomial polMon21Mon12 = new Polynomial(
      new ConsLoMonomial(mon21, new ConsLoMonomial(mon12, empty)));
  Polynomial polMon21Mon12Mon23 = new Polynomial(
      new ConsLoMonomial(mon21, new ConsLoMonomial(mon12, new ConsLoMonomial(mon23, empty))));
  Polynomial polMon10Mon12 = new Polynomial(loMon10Mon12);
  Polynomial polMon21Mon23 = new Polynomial(loMon21Mon23);

  // tests the add method on the Polynomial class
  boolean testAdd(Tester t) {
    return t.checkExpect(emptyPol.add(polMon21Mon13), polMon21Mon13)
        && t.checkExpect(polMon21Mon13.add(polMon12Mon23), polMon21Mon12Mon33)
        && t.checkExpect(
            new Polynomial(new ConsLoMonomial(deg0, new ConsLoMonomial(deg1, empty)))
                .add(new Polynomial(new ConsLoMonomial(deg1Negative, empty))),
            new Polynomial(new ConsLoMonomial(deg0, empty)));
  }

  // tests the highestDegree method on the ILoMonomial interface
  boolean testHighestDegree(Tester t) {
    return t.checkExpect(empty.highestDegree(), 0)
        && t.checkExpect(loMon21Mon13.highestDegree(), 3);
  }

  // tests the samePolynomial method on the Polynomial class
  boolean testSamePolynomial(Tester t) {
    return t.checkExpect(emptyPol.samePolynomial(emptyPol), true)
        && t.checkExpect(polMon21Mon12Mon33.samePolynomial(polMon21Mon12Mon33), true)
        && t.checkExpect(polMon21Mon12Mon33.samePolynomial(polMon21Mon12Mon23), false)
        && t.checkExpect(polMon21Mon12Mon33.samePolynomial(polMon21Mon12), false)
        && t.checkExpect(polMon21Mon12.samePolynomial(polMon21Mon12Mon33), false);
  }

  Polynomial polMon10Mon11 = new Polynomial(
      new ConsLoMonomial(mon10, new ConsLoMonomial(mon11, empty)));
  Polynomial polMon20 = new Polynomial(new ConsLoMonomial(mon20, empty));

  // tests the samePolynomialHelp method on the Polynomial class
  boolean testSamePolynomialHelp(Tester t) {
    return t.checkExpect(emptyPol.samePolynomialHelp(emptyPol, 1), true)
        && t.checkExpect(polMon10Mon11.samePolynomialHelp(polMon20, 1), true,
            "will never actually get called, but just to showcase the implementation")
        && t.checkExpect(polMon10Mon11.samePolynomialHelp(polMon20, 2), false);
  }

  // tests the multiply method on the Monomial class
  boolean testMultiplyMonomial(Tester t) {
    return t.checkExpect(mon21.multiply(mon12), mon23)
        && t.checkExpect(mon12.multiply(mon21), mon23);
  }

  // tests the multiplyMonomial method on the ILoMonomial class
  boolean testMultiplyMonomialILoMonomial(Tester t) {
    return t.checkExpect(empty.multiplyMonomial(mon12), empty)
        && t.checkExpect(loMon10Mon12.multiplyMonomial(mon21), loMon21Mon23);
  }

  // tests the multiplyILoMonomial method on the ILoMonomial class
  boolean testMultiplyILoMonomialILoMonomial(Tester t) {
    return t.checkExpect(empty.multiplyILoMonomial(loMon10Mon12), empty)
        && t.checkExpect(loMon10Mon12.multiplyILoMonomial(empty), empty)
        && t.checkExpect(loMon10Mon12.multiplyILoMonomial(loMon20Mon11), loMon20Mon11Mon22Mon13);
  }

  // tests the multiply method on the Polynomial class
  boolean testMultiplyPolynomial(Tester t) {
    return t.checkExpect(new Polynomial(empty).multiply(new Polynomial(loMon10Mon12)),
        new Polynomial(empty))
        && t.checkExpect(new Polynomial(loMon10Mon12).multiply(new Polynomial(empty)),
            new Polynomial(empty))
        && t.checkExpect(new Polynomial(loMon10Mon12).multiply(new Polynomial(loMon20Mon11)),
            new Polynomial(loMon20Mon11Mon22Mon13));
  }

}