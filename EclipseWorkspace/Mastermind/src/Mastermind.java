import java.awt.Color;
import java.util.Random;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.AboveAlignImage;
import javalib.worldimages.AlignModeX;
import javalib.worldimages.AlignModeY;
import javalib.worldimages.BesideAlignImage;
import javalib.worldimages.BesideImage;
import javalib.worldimages.CircleImage;
import javalib.worldimages.EmptyImage;
import javalib.worldimages.FontStyle;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.PhantomImage;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

// represents a game of master Mind.
class MastermindGame extends World {
  int width;
  int numGuesses;
  ConsList<Color> colors;
  IList<IList<Color>> pastGuesses;
  IList<String> feedbacks;
  IList<Color> secret;
  IList<Color> currGuess;

  static final int CONTROLS_X = 150;
  static final int COLORS_Y = 30;
  static final int RADIUS = 15;
  static final int GUESS_HEIGHT = 40;
  ColorSelect colorSelect;
  GuessButton guessAButton = new GuessButton();
  ClearButton clearAButton = new ClearButton();

  // constructor for constructing a standard instance, checks for invalid starting
  // conditions
  MastermindGame(int width, int numGuesses, ConsList<Color> colors) {
    this(width, numGuesses, colors, new Random());
  }

  // constructor for constructing a standard instance, with a set random seed
  // checks for invalid starting conditions
  MastermindGame(int width, int numGuesses, ConsList<Color> colors, Random rand) {
    if (width < 1) {
      throw new IllegalArgumentException("guess width can't be 0 or negative");
    }
    if (numGuesses < 1) {
      throw new IllegalArgumentException("number of guesses can't be 0 or negative");
    }
    if (!colors.allUnique()) {
      throw new IllegalArgumentException("all colors must be unique");
    }
    this.width = width;
    this.numGuesses = numGuesses;
    this.colors = colors;
    this.pastGuesses = new MtList<IList<Color>>();
    this.feedbacks = new MtList<String>();
    this.secret = this.generateRandomList(width, rand, colors);
    this.currGuess = new MtList<Color>();
    this.colorSelect = new ColorSelect(colors);
  }

  // manual constructor for updating the game
  MastermindGame(int numGuesses, ConsList<Color> colors, IList<IList<Color>> pastGuesses,
      IList<String> feedbacks, IList<Color> secret, IList<Color> currGuess) {
    this.numGuesses = numGuesses;
    this.colors = colors;
    this.pastGuesses = pastGuesses;
    this.feedbacks = feedbacks;
    this.secret = secret;
    this.currGuess = currGuess;
    this.colorSelect = new ColorSelect(colors);
    this.width = this.secret.len();
  }

  // generates a random list of colors with length listLength, picked from the
  // colors in _colors_
  IList<Color> generateRandomList(int listLength, Random rand, IList<Color> colors) {
    if (listLength <= 0) {
      return new MtList<Color>();
    }
    return new ConsList<Color>(colors.listRef(rand.nextInt(colors.len())),
        generateRandomList(listLength - 1, rand, colors));
  }

  // adds the given color to the current guess, if the current guess is not full.
  // Else returns the current world
  MastermindGame addColorToGuess(Color c) {
    if (this.currGuess.len() >= this.width) {
      return this;
    }
    return new MastermindGame(this.numGuesses, this.colors, this.pastGuesses, this.feedbacks,
        this.secret, this.currGuess.addToEnd(c));
  }

  // If the current guess is the correct length, gives feedback on how close to
  // the secret it is and clears the guess. Ends the game if applicable. If the
  // guess is not the correct length, returns the current world.
  World guess() {
    if (this.currGuess.len() != this.width) {
      return this;
    }
    int numCorrect = currGuess.numCorrect(this.secret);
    int numNearby = currGuess.numNearby(this.secret);
    String feedback = numCorrect + " black pegs, " + numNearby + " white pegs";
    MastermindGame afterGuess = new MastermindGame(this.numGuesses - 1, this.colors,
        this.pastGuesses.addToEnd(this.currGuess), this.feedbacks.addToEnd(feedback), this.secret,
        new MtList<Color>());
    if (numCorrect == this.width) {
      return afterGuess.endOfWorld("Won");
    }
    else if (this.numGuesses == 1) {
      return afterGuess.endOfWorld("Lost");
    }
    return afterGuess;
  }

  // draws a world scene for this game
  public WorldScene makeScene() {
    DrawGuess drawGuess = new DrawGuess();
    WorldImage guessesTitle = new PhantomImage(
        new TextImage("Guesses", 15, FontStyle.BOLD, Color.BLACK),
        this.width * 2 * MastermindGame.RADIUS, 30);
    WorldImage guessesDrawn = new AboveAlignImage(AlignModeX.LEFT, guessesTitle,
        this.pastGuesses.drawVertical(drawGuess), drawGuess.apply(this.currGuess));
    WorldImage feedbackTitle = new PhantomImage(
        new TextImage("Feedback", 15, FontStyle.BOLD, Color.BLACK), 60, 30);
    WorldImage feedbackDrawn = new AboveAlignImage(AlignModeX.LEFT, feedbackTitle,
        this.feedbacks.drawVertical(new DrawFeedback()));
    WorldImage guessesFeedback = new BesideAlignImage(AlignModeY.TOP, guessesDrawn,
        new PhantomImage(new EmptyImage(), 20, 0), feedbackDrawn);
    WorldScene guessesFeedbackScene = new WorldScene(1000, 1000).placeImageXY(guessesFeedback,
        (int) (400 + guessesFeedback.getWidth() / 2),
        (int) (100 + guessesFeedback.getHeight() / 2));
    WorldScene baseGame = this.clearAButton
        .addToScene(this.guessAButton.addToScene(this.colorSelect.addToScene(guessesFeedbackScene)))
        .placeImageXY(new TextImage("Guesses Left:" + numGuesses, 15, Color.BLACK), 500, 50);
    return baseGame;
  }

  // the scene to display on when the game is over, given a string "Won" or "Lost"
  public WorldScene lastScene(String wonOrLost) {
    if (wonOrLost.equals("Won")) {
      return this.makeScene().placeImageXY(new TextImage("Game Won!", 35, Color.BLACK), CONTROLS_X,
          300);
    }
    else {
      return this.makeScene().placeImageXY(new TextImage("Game Lost!", 35, Color.BLACK), CONTROLS_X,
          300);
    }

  }

  // Checks if the mouse is on top of any button or the color picker, and if it is
  // updates the game accordingly.
  public World onMouseClicked(Posn p) {
    if (this.guessAButton.clickedOn(p)) {
      return this.guess();
    }
    if (this.colorSelect.clickedOn(p)) {
      Color colorSelected = colorSelect.colorSelected(p);
      return this.addColorToGuess(colorSelected);
    }
    if (this.clearAButton.clickedOn(p)) {
      return new MastermindGame(this.numGuesses, this.colors, this.pastGuesses, this.feedbacks,
          this.secret, new MtList<Color>());
    }
    return this;
  }

}

//represents a button on screen
abstract class AButton {
  int x;
  int y;
  int width;
  int length;
  final double EPS = 0.001;

  // constructs a new button with the given dimensions and location
  AButton(int x, int y, int width, int length) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.length = length;
  }

  // returns whether this button has been clicked on given the mouse position
  boolean clickedOn(Posn p) {
    return Math.abs(p.x - this.x) < this.width / 2 && Math.abs(p.y - this.y) < this.length / 2;
  }

  // adds this button to the given scene
  abstract WorldScene addToScene(WorldScene currentScene);
}

// represents a colorselect button
class ColorSelect extends AButton {
  IList<Color> colors;

  // constructs a new ColorSelect with the given colors
  ColorSelect(IList<Color> colors) {
    super(MastermindGame.CONTROLS_X, 30, colors.len() * MastermindGame.RADIUS * 2,
        MastermindGame.RADIUS);
    this.colors = colors;
  }

  // adds this ColorSelect to the given scene
  WorldScene addToScene(WorldScene currentScene) {
    WorldImage colorsDrawn = this.colors.drawHorizontal(new DrawColor());
    return currentScene.placeImageXY(colorsDrawn, this.x, this.y);
  }

  // returns the color selected by the mouseClick
  // ASSUMES: this ColorSelect button is being clicked (ie the posn is over this
  // button)
  Color colorSelected(Posn p) {
    double leftEdge = ((double) this.x) - (((double) this.width) / 2);
    double distFromLeft = ((double) p.x) - leftEdge;
    double doubleIndex = distFromLeft / (((double) MastermindGame.RADIUS) * 2);
    return colors.listRef((int) doubleIndex);
  }
}

//represents a guessbutton
class GuessButton extends AButton {
  // constructs a new guessbutton
  GuessButton() {
    super(MastermindGame.CONTROLS_X, 70, 60, 30);
  }

  // adds this GuessButton to the given scene
  WorldScene addToScene(WorldScene currentScene) {
    return currentScene.placeImageXY(new OverlayImage(
        new RectangleImage(this.width, this.length, OutlineMode.OUTLINE, Color.BLACK),
        new TextImage("Guess", 15, Color.BLACK)), this.x, this.y);
  }
}

//represents a ClearButton
class ClearButton extends AButton {
  // constructs a new ClearButton
  ClearButton() {
    super(MastermindGame.CONTROLS_X, 110, 60, 30);
  }

  // adds this ClearButton to the given scene
  WorldScene addToScene(WorldScene currentScene) {
    return currentScene.placeImageXY(new OverlayImage(
        new RectangleImage(this.width, this.length, OutlineMode.OUTLINE, Color.BLACK),
        new TextImage("Clear", 15, Color.BLACK)), this.x, this.y);
  }
}

// is a representation of a metaphysical concept that is a list
interface IList<T> {
  // returns the number of elements in this and other that are the same element
  // in the same position
  int numCorrect(IList<T> other);

  // returns the number of elements in this and other that are the same element
  // in the same position, where other is non-empty
  int numCorrectCons(ConsList<T> other);

  // returns the number of elements in this that are the same element in any
  // position, using other as the comparison
  // each elem in one list can correspond to only one elem in the other
  int numCorrectOrNearby(IList<T> other);

  // returns whether this list contains thing
  boolean contains(T thing);

  // removes the first instance of thing in this list, or returns the list if
  // thing is not present
  IList<T> removeFirst(T thing);

  // returns the number of elements that are the same element but in the wrong
  // position, using other as the comparison
  // each elem in one list can correspond to only one elem in the other
  int numNearby(IList<T> other);

  // returns the length of this list
  int len();

  // draws a list of elements above each other, using the given function to draw
  // each
  WorldImage drawVertical(IFunc<T, WorldImage> drawFunc);

  // draws a list of elements next to each other, using the given function to draw
  // each
  WorldImage drawHorizontal(IFunc<T, WorldImage> drawFunc);

  // returns whether all elements in this list show up exactly once in the list
  boolean allUnique();

  // adds thing to the end of this list
  IList<T> addToEnd(T thing);

  // returns the ith element in this list, or throws an error if out of bounds
  public T listRef(int i);
}

// represents, theoretically, a list which is lacking content
class MtList<T> implements IList<T> {
  // returns the number of elements in this and other that are the same element
  // in the same position (always 0)
  public int numCorrect(IList<T> other) {
    return 0;
  }

  // returns the number of elements in this and other that are the same element
  // in the same position, where other is non-empty (but this is, so always 0)
  public int numCorrectCons(ConsList<T> other) {
    return 0;
  }

  // returns whether this list contains thing (no)
  public boolean contains(T thing) {
    return false;
  }

  // returns the number of elements in this that are the same element in any
  // position, using other as the comparison
  // each elem in one list can correspond to only one elem in the other
  // always returns 0
  public int numCorrectOrNearby(IList<T> other) {
    return 0;
  }

  // removes the first instance of thing in this list, or returns the list if
  // thing is not present (always does the latter)
  public IList<T> removeFirst(T thing) {
    return this;
  }

  // returns the number of elements that are the same element but in the wrong
  // position, using other as the comparison
  // each elem in one list can correspond to only one elem in the other
  // always returns 0
  public int numNearby(IList<T> other) {
    return 0;
  }

  // returns the length of this list (always 0)
  public int len() {
    return 0;
  }

  // draws a list of elements above to (top to bottom) each other, using the given
  // function to draw each
  public WorldImage drawVertical(IFunc<T, WorldImage> drawFunc) {
    return new EmptyImage();
  }

  // draws a list of elements next (left -> right -> done) to each other, using
  // the given function to draw
  // each
  public WorldImage drawHorizontal(IFunc<T, WorldImage> drawFunc) {
    return new EmptyImage();
  }

  // returns whether all elements in this list show up exactly once in the list
  // (always true)
  public boolean allUnique() {
    return true;
  }

  // adds thing to the end of this list
  public IList<T> addToEnd(T thing) {
    return new ConsList<T>(thing, this);
  }

  // returns the ith element in this list, or throws an error if out of bounds
  public T listRef(int i) {
    throw new IllegalArgumentException("listRef passed index out of bounds");
  }

}

//a list that is not empty is what this class represents, of which the contents are type T
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  // constructs a list with first and rest
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns the number of elements in this and other that are the same element
  // in the same position
  public int numCorrect(IList<T> other) {
    return other.numCorrectCons(this);
  }

  // returns the number of elements in this and other that are the same element
  // in the same position, where other is non-empty
  public int numCorrectCons(ConsList<T> other) {
    if (this.first.equals(other.first)) {
      return 1 + other.rest.numCorrect(this.rest);
    }
    else {
      return other.rest.numCorrect(this.rest);
    }
  }

  // returns the number of elements in this that are the same element in any
  // position, using other as the comparison
  // each elem in one list can correspond to only one elem in the other
  public int numCorrectOrNearby(IList<T> other) {
    if (other.contains(this.first)) {
      return 1 + this.rest.numCorrectOrNearby(other.removeFirst(this.first));
    }
    else {
      return this.rest.numCorrectOrNearby(other);
    }
  }

  // returns whether this list contains thing
  public boolean contains(T thing) {
    return this.first.equals(thing) || this.rest.contains(thing);
  }

  // removes the first instance of thing in this list, or returns the list if
  // thing is not present
  public IList<T> removeFirst(T thing) {
    if (this.first.equals(thing)) {
      return this.rest;
    }
    else {
      return new ConsList<T>(this.first, this.rest.removeFirst(thing));
    }
  }

  // returns the number of elements that are the same element but in the wrong
  // position, using other as the comparison
  // each elem in one list can correspond to only one elem in the other
  public int numNearby(IList<T> other) {
    return this.numCorrectOrNearby(other) - this.numCorrect(other);
  }

  // returns whether all elements in this list show up exactly once in the list
  public boolean allUnique() {
    return !this.rest.contains(this.first) && this.rest.allUnique();
  }

  // returns the length of this list
  public int len() {
    return 1 + this.rest.len();
  }

  // adds thing to the end of this list
  public IList<T> addToEnd(T thing) {
    return new ConsList<T>(this.first, this.rest.addToEnd(thing));
  }

  // draws a list of elements above each other, using the given function to draw
  // each
  public WorldImage drawVertical(IFunc<T, WorldImage> drawFunc) {
    return new AboveAlignImage(AlignModeX.LEFT, drawFunc.apply(this.first),
        this.rest.drawVertical(drawFunc));
  }

  // draws a list of elements next to each other, using the given function to draw
  // each
  public WorldImage drawHorizontal(IFunc<T, WorldImage> drawFunc) {
    return new BesideImage(drawFunc.apply(this.first), this.rest.drawHorizontal(drawFunc));
  }

  // returns the ith element in this list, or throws an error if out of bounds
  public T listRef(int i) {
    if (i == 0) {
      return this.first;
    }
    return this.rest.listRef(i - 1);
  }
}

// represents a function object from T to R
interface IFunc<T, R> {
  // applies this function object to an input
  R apply(T input);
}

//draws a color as a circle
class DrawColor implements IFunc<Color, WorldImage> {

  // draws a color as a circle
  public WorldImage apply(Color input) {
    return new CircleImage(MastermindGame.RADIUS, OutlineMode.SOLID, input);
  }

}

//draws a guess as row of a circe
class DrawGuess implements IFunc<IList<Color>, WorldImage> {

  // draws a guess as a row of circles
  public WorldImage apply(IList<Color> input) {
    WorldImage row = input.drawHorizontal(new DrawColor());
    WorldImage boundingBox = new PhantomImage(row, (int) row.getWidth(),
        MastermindGame.GUESS_HEIGHT);
    return boundingBox;
  }

}

//draws a feedback as a text image
class DrawFeedback implements IFunc<String, WorldImage> {

  // draws a feedback as a text image
  public WorldImage apply(String input) {
    WorldImage text = new TextImage(input, 15, Color.BLACK);
    WorldImage boundingBox = new PhantomImage(text, (int) text.getWidth(),
        MastermindGame.GUESS_HEIGHT);
    return boundingBox;
  }

}

class ExamplesMastermind {

  ConsList<Color> colors1 = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.BLUE,
          new ConsList<Color>(Color.GREEN, new ConsList<Color>(Color.YELLOW,
              new ConsList<Color>(Color.PINK, new MtList<Color>())))));

  ConsList<Color> colors2 = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.BLUE, new ConsList<Color>(Color.GREEN, new ConsList<Color>(
          Color.YELLOW,
          new ConsList<Color>(Color.CYAN, new ConsList<Color>(Color.PINK, new MtList<Color>()))))));

  ConsList<Color> manyColors = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.pink,
          new ConsList<Color>(Color.gray,
              new ConsList<Color>(Color.BLACK,
                  new ConsList<Color>(Color.DARK_GRAY,
                      new ConsList<Color>(Color.GREEN,
                          new ConsList<Color>(Color.YELLOW, new ConsList<Color>(Color.ORANGE,
                              new ConsList<Color>(Color.CYAN, new MtList<Color>())))))))));

  ConsList<Color> colorsInvalid = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.BLUE,
          new ConsList<Color>(Color.GREEN,
              new ConsList<Color>(Color.YELLOW, new ConsList<Color>(Color.ORANGE,
                  new ConsList<Color>(Color.RED, new MtList<Color>()))))));

  ColorSelect colorSelect1 = new ColorSelect(colors1);
  ColorSelect colorSelect2 = new ColorSelect(colors2);
  GuessButton guessButton = new GuessButton();
  ClearButton clearButton = new ClearButton();

  int DIAMETER = MastermindGame.RADIUS * 2;

  // tests the clickedOn method in all of the classes that extend AButton
  boolean testClickedOn(Tester t) {
    return t.checkExpect(guessButton.clickedOn(new Posn(MastermindGame.CONTROLS_X + 29, 84)), true)
        && t.checkExpect(guessButton.clickedOn(new Posn(MastermindGame.CONTROLS_X - 29, 56)), true)
        && t.checkExpect(guessButton.clickedOn(new Posn(MastermindGame.CONTROLS_X - 35, 56)), false)
        && t.checkExpect(guessButton.clickedOn(new Posn(MastermindGame.CONTROLS_X + 35, 56)), false)
        && t.checkExpect(guessButton.clickedOn(new Posn(MastermindGame.CONTROLS_X + 29, 101)),
            false)
        && t.checkExpect(guessButton.clickedOn(new Posn(MastermindGame.CONTROLS_X + 29, 39)), false)
        && t.checkExpect(clearButton.clickedOn(new Posn(MastermindGame.CONTROLS_X, 115)), true)
        && t.checkExpect(clearButton.clickedOn(new Posn(MastermindGame.CONTROLS_X, 40)), false)
        && t.checkExpect(colorSelect1
            .clickedOn(new Posn((int) (MastermindGame.CONTROLS_X + 2.4 * DIAMETER), 30)), true)
        && t.checkExpect(colorSelect1
            .clickedOn(new Posn((int) (MastermindGame.CONTROLS_X - 2.4 * DIAMETER), 30)), true)
        && t.checkExpect(colorSelect1
            .clickedOn(new Posn((int) (MastermindGame.CONTROLS_X - 2.6 * DIAMETER), 30)), false)
        && t.checkExpect(colorSelect1
            .clickedOn(new Posn((int) (MastermindGame.CONTROLS_X + 2.6 * DIAMETER), 30)), false)
        && t.checkExpect(colorSelect2
            .clickedOn(new Posn((int) (MastermindGame.CONTROLS_X + 2.6 * DIAMETER), 30)), true)
        && t.checkExpect(colorSelect2
            .clickedOn(new Posn((int) (MastermindGame.CONTROLS_X - 2.6 * DIAMETER), 30)), true)
        && t.checkExpect(colorSelect2
            .clickedOn(new Posn((int) (MastermindGame.CONTROLS_X - 3.1 * DIAMETER), 30)), false)
        && t.checkExpect(colorSelect2
            .clickedOn(new Posn((int) (MastermindGame.CONTROLS_X + 3.1 * DIAMETER), 30)), false);
  }

  // tests the colorSelected method on the ColorSelect class
  boolean testColorSelected(Tester t) {
    return t
        .checkExpect(colorSelect1.colorSelected(
            new Posn((int) (MastermindGame.CONTROLS_X + 1.9 * DIAMETER), 30)), Color.PINK)
        && t.checkExpect(
            colorSelect1.colorSelected(new Posn((int) (MastermindGame.CONTROLS_X), 30)),
            Color.GREEN)
        && t.checkExpect(colorSelect2.colorSelected(
            new Posn((int) (MastermindGame.CONTROLS_X + 1.9 * DIAMETER), 30)), Color.CYAN);
  }

  ConsList<Color> rgb = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.GREEN, new ConsList<Color>(Color.BLUE, new MtList<Color>())));
  ConsList<Color> rbb = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.BLUE, new ConsList<Color>(Color.BLUE, new MtList<Color>())));
  ConsList<Color> brg = new ConsList<Color>(Color.BLUE,
      new ConsList<Color>(Color.RED, new ConsList<Color>(Color.GREEN, new MtList<Color>())));
  ConsList<Color> bgbbr = new ConsList<Color>(Color.BLUE,
      new ConsList<Color>(Color.GREEN, new ConsList<Color>(Color.BLUE,
          new ConsList<Color>(Color.BLUE, new ConsList<Color>(Color.RED, new MtList<Color>())))));
  ConsList<Color> ryg = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.YELLOW, new ConsList<Color>(Color.GREEN, new MtList<Color>())));
  ConsList<Color> ypc = new ConsList<Color>(Color.YELLOW,
      new ConsList<Color>(Color.PINK, new ConsList<Color>(Color.CYAN, new MtList<Color>())));
  ConsList<Color> rpc = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.PINK, new ConsList<Color>(Color.CYAN, new MtList<Color>())));
  ConsList<Color> prc = new ConsList<Color>(Color.PINK,
      new ConsList<Color>(Color.RED, new ConsList<Color>(Color.CYAN, new MtList<Color>())));
  ConsList<Color> rbp = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.BLUE, new ConsList<Color>(Color.PINK, new MtList<Color>())));
  ConsList<Color> rrb = new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.RED, new ConsList<Color>(Color.BLUE, new MtList<Color>())));
  ConsList<Color> rrbb = new ConsList<Color>(Color.RED, new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.BLUE, new ConsList<Color>(Color.BLUE, new MtList<Color>()))));
  ConsList<Color> yypp = new ConsList<Color>(Color.YELLOW, new ConsList<Color>(Color.YELLOW,
      new ConsList<Color>(Color.PINK, new ConsList<Color>(Color.PINK, new MtList<Color>()))));
  ConsList<Color> bbrr = new ConsList<Color>(Color.BLUE, new ConsList<Color>(Color.BLUE,
      new ConsList<Color>(Color.RED, new ConsList<Color>(Color.RED, new MtList<Color>()))));
  ConsList<Color> bbrg = new ConsList<Color>(Color.BLUE, new ConsList<Color>(Color.BLUE,
      new ConsList<Color>(Color.RED, new ConsList<Color>(Color.GREEN, new MtList<Color>()))));
  ConsList<Color> gbrr = new ConsList<Color>(Color.GREEN, new ConsList<Color>(Color.BLUE,
      new ConsList<Color>(Color.RED, new ConsList<Color>(Color.RED, new MtList<Color>()))));
  ConsList<Color> grrb = new ConsList<Color>(Color.GREEN, new ConsList<Color>(Color.RED,
      new ConsList<Color>(Color.RED, new ConsList<Color>(Color.BLUE, new MtList<Color>()))));
  ConsList<Color> yry = new ConsList<Color>(Color.YELLOW,
      new ConsList<Color>(Color.RED, new ConsList<Color>(Color.YELLOW, new MtList<Color>())));

  // tests the numCorrect method in the IList interface.
  boolean testNumCorrect(Tester t) {
    return t.checkExpect(rgb.numCorrect(rgb), 3)//
        && t.checkExpect(rgb.numCorrect(brg), 0)//
        && t.checkExpect(rgb.numCorrect(ypc), 0)//
        && t.checkExpect(rgb.numCorrect(rpc), 1)//
        && t.checkExpect(rgb.numCorrect(rpc), 1)//
        && t.checkExpect(rgb.numCorrect(prc), 0)//
        && t.checkExpect(rbp.numCorrect(rgb), 1)//
        && t.checkExpect(rrbb.numCorrect(rrbb), 4)//
        && t.checkExpect(rrbb.numCorrect(yypp), 0)//
        && t.checkExpect(bbrr.numCorrect(bbrg), 3)//
        && t.checkExpect(gbrr.numCorrect(rrbb), 0)//
        && t.checkExpect(gbrr.numCorrect(rrbb), 0)//
        && t.checkExpect(gbrr.numCorrect(grrb), 2)//
        && t.checkExpect(new MtList<Color>().numCorrect(grrb), 0)
        && t.checkExpect(grrb.numCorrect(new MtList<Color>()), 0);
  }

  // tests the numCorrectCons method in the IList interface.
  boolean testNumCorrectCons(Tester t) {
    return t.checkExpect(rgb.numCorrectCons(rgb), 3)//
        && t.checkExpect(rgb.numCorrectCons(brg), 0)//
        && t.checkExpect(rgb.numCorrectCons(ypc), 0)//
        && t.checkExpect(rgb.numCorrectCons(rpc), 1)//
        && t.checkExpect(rgb.numCorrectCons(rpc), 1)//
        && t.checkExpect(rgb.numCorrectCons(prc), 0)//
        && t.checkExpect(rbp.numCorrectCons(rgb), 1)//
        && t.checkExpect(rrbb.numCorrectCons(rrbb), 4)//
        && t.checkExpect(rrbb.numCorrectCons(yypp), 0)//
        && t.checkExpect(bbrr.numCorrectCons(bbrg), 3)//
        && t.checkExpect(gbrr.numCorrectCons(rrbb), 0)//
        && t.checkExpect(gbrr.numCorrectCons(rrbb), 0)//
        && t.checkExpect(gbrr.numCorrectCons(grrb), 2)//
        && t.checkExpect(new MtList<Color>().numCorrectCons(grrb), 0);

  }

  // tests the numNearby method in the IList interface.
  boolean testNumNearby(Tester t) {
    return t.checkExpect(rgb.numNearby(rgb), 0)//
        && t.checkExpect(rgb.numNearby(brg), 3)//
        && t.checkExpect(rgb.numNearby(ypc), 0)//
        && t.checkExpect(rgb.numNearby(rpc), 0)//
        && t.checkExpect(rgb.numNearby(rpc), 0)//
        && t.checkExpect(rgb.numNearby(prc), 1)//
        && t.checkExpect(rbp.numNearby(rgb), 1)//
        && t.checkExpect(rrbb.numNearby(rrbb), 0)//
        && t.checkExpect(rrbb.numNearby(yypp), 0)//
        && t.checkExpect(bbrr.numNearby(bbrg), 0)//
        && t.checkExpect(gbrr.numNearby(rrbb), 3)//
        && t.checkExpect(gbrr.numNearby(rrbb), 3)//
        && t.checkExpect(gbrr.numNearby(grrb), 2)//
        && t.checkExpect(new MtList<Color>().numNearby(grrb), 0)
        && t.checkExpect(grrb.numNearby(new MtList<Color>()), 0);
  }

  // tests the numNearby method in the IList interface.
  boolean testNumCorrectOrNearby(Tester t) {
    return t.checkExpect(rgb.numCorrectOrNearby(rgb), 3)//
        && t.checkExpect(rgb.numCorrectOrNearby(brg), 3)//
        && t.checkExpect(rgb.numCorrectOrNearby(ypc), 0)//
        && t.checkExpect(rgb.numCorrectOrNearby(rpc), 1)//
        && t.checkExpect(rgb.numCorrectOrNearby(rpc), 1)//
        && t.checkExpect(rgb.numCorrectOrNearby(prc), 1)//
        && t.checkExpect(rbp.numCorrectOrNearby(rgb), 2)//
        && t.checkExpect(rrbb.numCorrectOrNearby(rrbb), 4)//
        && t.checkExpect(rrbb.numCorrectOrNearby(yypp), 0)//
        && t.checkExpect(bbrr.numCorrectOrNearby(bbrg), 3)//
        && t.checkExpect(gbrr.numCorrectOrNearby(rrbb), 3)//
        && t.checkExpect(gbrr.numCorrectOrNearby(rrbb), 3)//
        && t.checkExpect(gbrr.numCorrectOrNearby(grrb), 4)//
        && t.checkExpect(new MtList<Color>().numCorrectOrNearby(grrb), 0)
        && t.checkExpect(grrb.numCorrectOrNearby(new MtList<Color>()), 0);
  }

  // tests the contains method on the IList interface
  boolean testContains(Tester t) {
    return t.checkExpect(rgb.contains(Color.GREEN), true)
        && t.checkExpect(rrbb.contains(Color.GREEN), false)
        && t.checkExpect(rgb.contains(Color.RED), true)
        && t.checkExpect(rgb.contains(Color.BLUE), true)
        && t.checkExpect(rgb.contains(Color.ORANGE), false)
        && t.checkExpect(new MtList<Color>().contains(Color.RED), false);
  }

  // tests removeFirst on IList
  boolean testRemoveFirst(Tester t) {
    return t.checkExpect(rrbb.removeFirst(Color.BLUE), rrb)
        && t.checkExpect(rrbb.removeFirst(Color.RED), rbb)
        && t.checkExpect(rrbb.removeFirst(Color.GREEN), rrbb);
  }

  // tests len on IList
  boolean testLen(Tester t) {
    return t.checkExpect(new MtList<Color>().len(), 0) && t.checkExpect(rgb.len(), 3)
        && t.checkExpect(rrbb.len(), 4);
  }

  // tests the allUnique method in the IList interface
  boolean testAllUnique(Tester t) {
    return t.checkExpect(new MtList<Color>().allUnique(), true)
        && t.checkExpect(rgb.allUnique(), true)//
        && t.checkExpect(grrb.allUnique(), false);
  }

  // tests the addToEnd method in the IList interface
  boolean testAddToEnd(Tester t) {
    return t.checkExpect(new MtList<Color>().addToEnd(Color.RED),
        new ConsList<Color>(Color.RED, new MtList<Color>()))
        && t.checkExpect(rrb.addToEnd(Color.BLUE), rrbb);
  }

  // tests listRef in IList
  boolean testListRef(Tester t) {
    return t.checkExpect(rgb.listRef(0), Color.RED) && t.checkExpect(rgb.listRef(2), Color.BLUE)
        && t.checkException(new IllegalArgumentException("listRef passed index out of bounds"), rgb,
            "listRef", 3)
        && t.checkException(new IllegalArgumentException("listRef passed index out of bounds"),
            new MtList<Color>(), "listRef", 0)
        && t.checkException(new IllegalArgumentException("listRef passed index out of bounds"), rgb,
            "listRef", -1);
  }

  // tests the standard constructor for the MastermindGame class
  boolean testConstructor(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException("guess width can't be 0 or negative"), "MastermindGame", 0, 10,
        colors1)
        && t.checkConstructorException(
            new IllegalArgumentException("guess width can't be 0 or negative"), "MastermindGame",
            -1, 10, colors2)
        && t.checkConstructorException(
            new IllegalArgumentException("number of guesses can't be 0 or negative"),
            "MastermindGame", 10, -1, colors1)
        && t.checkConstructorException(
            new IllegalArgumentException("number of guesses can't be 0 or negative"),
            "MastermindGame", 10, 0, colors1)
        && t.checkConstructorException(new IllegalArgumentException("all colors must be unique"),
            "MastermindGame", 1, 10, colorsInvalid);
  }

  MastermindGame g1 = new MastermindGame(3, rgb, new MtList<IList<Color>>(), new MtList<String>(),
      rgb, new MtList<Color>());
  MastermindGame g2 = new MastermindGame(3, rgb, new MtList<IList<Color>>(), new MtList<String>(),
      rgb, rbp);
  MastermindGame g2AfterGuess = new MastermindGame(2, rgb,
      new ConsList<IList<Color>>(rbp, new MtList<IList<Color>>()),
      new ConsList<String>("1 black pegs, 1 white pegs", new MtList<String>()), rgb,
      new MtList<Color>());

  MastermindGame g3 = new MastermindGame(2, rgb,
      new ConsList<IList<Color>>(rbp, new MtList<IList<Color>>()),
      new ConsList<String>("1 black pegs, 1 white pegs", new MtList<String>()), rgb, ypc);
  MastermindGame g3AfterGuess = new MastermindGame(1, rgb,
      new ConsList<IList<Color>>(rbp, new ConsList<IList<Color>>(ypc, new MtList<IList<Color>>())),
      new ConsList<String>("1 black pegs, 1 white pegs",
          new ConsList<String>("0 black pegs, 0 white pegs", new MtList<String>())),
      rgb, new MtList<Color>());
  MastermindGame g4 = new MastermindGame(3, rgb, new MtList<IList<Color>>(), new MtList<String>(),
      rrbb, rgb);

  MastermindGame wonGame = new MastermindGame(3, rgb, new MtList<IList<Color>>(),
      new MtList<String>(), rgb, rgb);
  MastermindGame wonGameAfterGuess = new MastermindGame(2, rgb,
      new ConsList<IList<Color>>(rgb, new MtList<IList<Color>>()),
      new ConsList<String>("3 black pegs, 0 white pegs", new MtList<String>()), rgb,
      new MtList<Color>());
  MastermindGame lostGame = new MastermindGame(1, rgb, new MtList<IList<Color>>(),
      new MtList<String>(), rgb, rbp);
  MastermindGame lostGameAfterGuess = new MastermindGame(0, rgb,
      new ConsList<IList<Color>>(rbp, new MtList<IList<Color>>()),
      new ConsList<String>("1 black pegs, 1 white pegs", new MtList<String>()), rgb,
      new MtList<Color>());

  // tests generateRandomList in MastermindGame
  boolean testGenerateRandomList(Tester t) {
    return t.checkExpect(g1.generateRandomList(5, new Random(5), rgb), bgbbr)
        && t.checkExpect(g1.generateRandomList(3, new Random(1), colors1), ryg)
        && t.checkExpect(g1.generateRandomList(-1, new Random(10), colors1), new MtList<Color>())
        && t.checkExpect(g1.generateRandomList(0, new Random(10), colors1), new MtList<Color>());
  }

  // tests lastScene in MastermindGame
  boolean testLastScene(Tester t) {
    return t.checkExpect(g1.lastScene("Won"),
        g1.makeScene().placeImageXY(new TextImage("Game Won!", 35, Color.BLACK),
            MastermindGame.CONTROLS_X, 300))
        && t.checkExpect(g1.lastScene("Lost"), g1.makeScene().placeImageXY(
            new TextImage("Game Lost!", 35, Color.BLACK), MastermindGame.CONTROLS_X, 300));
  }

  // tests addColorToGuess in MastermindGame
  boolean testAddColorToGuess(Tester t) {
    return t.checkExpect(g1.addColorToGuess(Color.RED),
        new MastermindGame(3, rgb, new MtList<IList<Color>>(), new MtList<String>(), rgb,
            new ConsList<Color>(Color.RED, new MtList<Color>())))
        && t.checkExpect(g1.addColorToGuess(Color.BLUE),
            new MastermindGame(3, rgb, new MtList<IList<Color>>(), new MtList<String>(), rgb,
                new ConsList<Color>(Color.BLUE, new MtList<Color>())))
        && t.checkExpect(g2.addColorToGuess(Color.RED), g2);
  }

  // tests guess in MatermindGame
  boolean testGuess(Tester t) {
    return t.checkExpect(g4.guess(), g4)//
        && t.checkExpect(g2.guess(), g2AfterGuess)//
        && t.checkExpect(g3.guess(), g3AfterGuess)
        && t.checkExpect(wonGame.guess(), wonGameAfterGuess)
        && t.checkExpect(lostGame.guess(), lostGameAfterGuess);
  }

  // tests makeScene in MastermindGame
  boolean testMakeScene(Tester t) {
    return t.checkExpect(g1.makeScene().equals(g2.makeScene()), false)
        && t.checkExpect(g1.makeScene().equals(new WorldScene(1000, 1000)), false)
        && t.checkExpect(g1.makeScene().height == 1000, true)
        && t.checkExpect(g1.makeScene().width == 1000, true);
  }

  // play the game
  boolean testGame(Tester t) {
    return new MastermindGame(4, 8, colors1).bigBang(1000, 1000, 0);
  }
}
