import tester.Tester;

// Represents a bagel recipe, with the weights of five ingredients in ounces
class BagelRecipe {
  double flour; // The amount of flour in the recipe, in ounces
  double water; // The amount of water in the recipe, in ounces
  double yeast; // The amount of yeast in the recipe, in ounces
  double salt; /// The amount of malt, in the recipe, in ounces
  double malt; /// The amount of salt, in the recipe, in ounces

  // Constructs a new BagelRecipe using all the given amounts, while throwing an
  // exception if the weights do not match a perfect recipe
  BagelRecipe(double flour, double water, double yeast, double salt, double malt) {
    if (!this.epsEqual(flour, water)) {
      throw new IllegalArgumentException(
          "This bagel recipe sucks, the water and flour have different weights");
    }
    else if (!this.epsEqual(yeast, malt)) {
      throw new IllegalArgumentException(
          "This bagel recipe sucks, the yeast and malt have different weights");
    }
    else if (!this.epsEqual(salt + yeast, .05 * flour)) {
      throw new IllegalArgumentException("This bagel recipe sucks, the flour's weight " + flour
          + " is not 20 times the salt weight " + salt + " and yeast weight " + yeast
          + " combined");
    }
    else {
      this.flour = flour;
      this.water = water;
      this.yeast = yeast;
      this.salt = salt;
      this.malt = malt;
    }
  }

  // Constructs a new BagelRecipe using the given flour and yeast amounts, and
  // deriving the remaining amounts
  BagelRecipe(double flour, double yeast) {
    this(flour, flour, yeast, (flour / 20) - yeast, yeast);
  }

  // Constructs a new BagelRecipe using the given volumes of flour, yeast, and
  // salt, and deriving the amounts for water and malt
  BagelRecipe(double flourCups, double yeastTsp, double saltTsp) {
    this(4.25 * flourCups, 4.25 * flourCups, (5.0 / 48.0) * yeastTsp, (10.0 / 48.0) * saltTsp,
        (5.0 / 48.0) * yeastTsp);
  }

  /*
   * Template
   * 
   * Fields
   * 
   * flour -- double
   * water -- double
   * yeast -- double
   * malt -- double
   * salt -- double
   * 
   * Methods
   * 
   * this.epsEqual(double a, double b) -- boolean
   * this.sameRecipe(BagelRecipe other) -- boolean
   * this.sameWeights(double otherFlour, double otherWater, double otherYeast,
   * double otherMalt, double otherSalt) -- boolean
   */

  // Returns whether the two doubles are equal within 0.001
  boolean epsEqual(double a, double b) {
    /*
     * Template
     * 
     * Everything in the Class Template
     * 
     * Parameters
     * 
     * a -- double
     * b -- double
     */
    return Math.abs(a - b) < 0.001;
  }

  // Returns whether this bagel's recipe is the same recipe as other
  boolean sameRecipe(BagelRecipe other) {
    /*
     * Template
     * 
     * Everything in the Class Template
     * 
     * Parameters
     * 
     * other -- BagelRecipe
     * 
     * Methods on Parameters
     * other.epsEqual(double a, double b) -- boolean
     * other.sameRecipe(BagelRecipe other) -- boolean
     * other.sameWeights(double otherFlour, double otherWater, double otherYeast,
     * double otherMalt, double otherSalt) -- boolean
     */
    return other.sameWeights(this.flour, this.water, this.yeast, this.malt, this.salt);
  }

  // Returns whether this bagel recipe's weights are the same as the given values
  boolean sameWeights(double otherFlour, double otherWater, double otherYeast, double otherMalt,
      double otherSalt) {
    /*
     * Template
     * 
     * Everything in the Class Template plus
     * 
     * Parameters
     * 
     * otherFlour -- double
     * otherWater -- double
     * otherYeast -- double
     * otherMalt -- double
     * otherSalt -- double
     */
    return epsEqual(this.flour, otherFlour) && epsEqual(this.water, otherWater)
        && epsEqual(this.yeast, otherYeast) && epsEqual(this.malt, otherMalt)
        && epsEqual(this.salt, otherSalt);
  }
}

class ExamplesBagels {
  BagelRecipe simpleBagel = new BagelRecipe(17, 17, .50, .35, .50);
  BagelRecipe simpleBagelMinimal = new BagelRecipe(17, .50);
  BagelRecipe simpleBagelVolume = new BagelRecipe(4, 4.8, 1.68);
  BagelRecipe roastedBagel = new BagelRecipe(700, 22);
  BagelRecipe burntBagel = new BagelRecipe(700, 700, 22, 13, 22);
  BagelRecipe differentBagel1 = new BagelRecipe(1400, 1400, 22, 48, 22);
  BagelRecipe differentBagel2 = new BagelRecipe(700, 700, 11, 24, 11);

  // tests the BagelRecipe constructor that takes in all fields and returns error
  // for invalid fields
  boolean testCorrectnessConstructor(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException(
            "This bagel recipe" + " sucks, the water and flour have different weights"),
        "BagelRecipe", 17.0, 18.0, .50, .35, .50)
        && t.checkConstructorException(
            new IllegalArgumentException(
                "This bagel recipe " + "sucks, the yeast and malt have different weights"),
            "BagelRecipe", 17.0, 17.0, .51, .35, .50)
        && t.checkConstructorException(
            new IllegalArgumentException("This bagel recipe sucks, the flour's weight " + 17.0
                + " is not 20 times the salt weight " + 0.2 + " and yeast weight " + 0.5
                + " combined"),
            "BagelRecipe", 17.0, 17.0, .50, .2, .50);
  }

  // tests the BagelRecipe constructor that only takes in flour and yeast amounts
  boolean testMininalConstructor(Tester t) {
    return t.checkExpect(simpleBagel, simpleBagelMinimal);
  }

  // tests the BagelRecipe constructor that only takes in volumes
  boolean testVolumeConstructor(Tester t) {
    return t.checkExpect(simpleBagel, simpleBagelVolume);
  }

  // Tests the epsEqual method in the BagelRecipe class
  boolean testEpsEqual(Tester t) {
    return t.checkExpect(simpleBagel.epsEqual(8.50008, 8.5), true)
        && t.checkExpect(simpleBagel.epsEqual(8.5008, 8.5), true)
        && t.checkExpect(simpleBagel.epsEqual(8.508, 8.5), false)
        && t.checkExpect(simpleBagel.epsEqual(2.70000090000001, 2.7), true);
  }

  // Tests the sameRecipe method in the BagelRecipe class
  boolean testSameRecipe(Tester t) {
    return t.checkExpect(simpleBagel.sameRecipe(simpleBagelMinimal), true)
        && t.checkExpect(simpleBagel.sameRecipe(simpleBagelVolume), true)
        && t.checkExpect(simpleBagelMinimal.sameRecipe(simpleBagelVolume), true)
        && t.checkExpect(roastedBagel.sameRecipe(burntBagel), true)
        && t.checkExpect(roastedBagel.sameRecipe(differentBagel1), false)
        && t.checkExpect(roastedBagel.sameRecipe(differentBagel2), false);
  }

  // Tests the sameWeights method in the BagelRecipe class
  boolean testSameWeights(Tester t) {
    return t.checkExpect(simpleBagel.sameWeights(17, 17, .5, .5, .35), true)
        && t.checkExpect(simpleBagelMinimal.sameWeights(17, 17, .5, .5, .35), true)
        && t.checkExpect(simpleBagelVolume.sameWeights(17, 17, .5, .5, .35), true)
        && t.checkExpect(roastedBagel.sameWeights(700, 700, 22, 22, 13), true)
        && t.checkExpect(roastedBagel.sameWeights(700, 701, 22, 28, 22), false)
        && t.checkExpect(roastedBagel.sameWeights(701, 700, 22, 28, 22), false)
        && t.checkExpect(roastedBagel.sameWeights(700, 700, 23, 28, 22), false)
        && t.checkExpect(roastedBagel.sameWeights(700, 700, 22, 29, 22), false)
        && t.checkExpect(roastedBagel.sameWeights(700, 700, 22, 28, 23), false);
  }
}