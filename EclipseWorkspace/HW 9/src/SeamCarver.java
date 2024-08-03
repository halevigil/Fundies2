import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.ComputedPixelImage;
import javalib.worldimages.FromFileImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

// Represents a grid of pixels
// INVARIANT: The grid is always rectangular
// INVARIANT: There is at least one Pixel in the grid
class GridPixels {
  CornerSentinel corner;

  // Constructs a grid of pixels mirroring the given image
  GridPixels(FromFileImage image) {
    double imgWidth = image.getWidth();
    double imgHeight = image.getHeight();

    if (imgWidth == 0 || imgHeight == 0) {
      throw new IllegalArgumentException("Cannot create a pixel grid with no pixels in it");
    }

    this.corner = new CornerSentinel();
    APixel endOfRow = this.corner;
    // Initializes the top row of vertical sentinels
    for (int i = 0; i < imgWidth; i += 1) {
      endOfRow = new VerticalSentinel(endOfRow, endOfRow.right);
    }

    // Creates horizontal sentinels and pixels, row by row
    APixel startOfPrevRow = this.corner;
    for (int i = 0; i < imgHeight; i += 1) {
      endOfRow = new HorizontalSentinel(startOfPrevRow, startOfPrevRow.down);
      // Creates a row of pixels
      for (int j = 0; j < imgWidth; j += 1) {
        endOfRow = new Pixel(image.getColorAt(j, i), endOfRow, endOfRow.right, endOfRow.up.right,
            endOfRow.down.right);
      }
      startOfPrevRow = endOfRow.right;
    }

    this.checkDiagonalInvariants();
  }

  // Constructs a grid of pixels from the given list of list of colors
  GridPixels(ArrayList<ArrayList<Color>> colors) {
    double imgWidth = colors.get(0).size();
    double imgHeight = colors.size();

    if (imgWidth == 0 || imgHeight == 0) {
      throw new IllegalArgumentException("Cannot create a pixel grid with no pixels in it");
    }

    this.corner = new CornerSentinel();
    APixel endOfRow = this.corner;
    // Initializes the top row of vertical sentinels
    for (int i = 0; i < imgWidth; i += 1) {
      endOfRow = new VerticalSentinel(endOfRow, endOfRow.right);
    }

    // Creates horizontal sentinels and pixels, row by row
    APixel startOfPrevRow = this.corner;
    for (int i = 0; i < imgHeight; i += 1) {
      endOfRow = new HorizontalSentinel(startOfPrevRow, startOfPrevRow.down);
      // Creates a row of pixels
      for (int j = 0; j < imgWidth; j += 1) {
        endOfRow = new Pixel(colors.get(i).get(j), endOfRow, endOfRow.right, endOfRow.up.right,
            endOfRow.down.right);
      }
      startOfPrevRow = endOfRow.right;
    }

    this.checkDiagonalInvariants();
  }

  // Errors if not all the diagonal invariants in this grid are satisfied
  void checkDiagonalInvariants() {
    int imgWidth = this.width();
    int imgHeight = this.height();
    APixel toTest = this.corner;
    for (int i = 0; i < imgHeight + 1; i += 1) {
      for (int j = 0; j < imgWidth + 1; j += 1) {
        if (!toTest.checkDiagonalInvariants()) {
          throw new IllegalStateException(
              "At least one APixel does not satisfy the diagonal invariants");
        }
        toTest = toTest.right;
      }
      toTest = toTest.down;
    }
  }

  // EFFECT: Removes the given vertical seam from this grid (if it is not empty)
  void removeVerticalSeam(SeamInfo toRemove) {
    if (this.height() == 0 || this.width() <= 1) {
      return;
    }
    toRemove.callOnAll(new RemoveHorizontal());
    this.corner.left = this.corner.left.left;
    this.corner.left.right = this.corner;

    this.checkDiagonalInvariants();
  }

  // EFFECT: Removes the given horizontal seam from this grid (if it is not empty)
  void removeHorizontalSeam(SeamInfo toRemove) {
    if (this.height() <= 1 || this.width() == 0) {
      return;
    }
    toRemove.callOnAll(new RemoveVertical());
    this.corner.up = this.corner.up.up;
    this.corner.up.down = this.corner;

    this.checkDiagonalInvariants();
  }

  // Returns the lowest energy seam in this grid
  // (vertical if the given boolean is true and horizontal if it is false)
  SeamInfo lowestSeam(boolean vertical) {
    ArrayList<SeamInfo> prevSeamRow;
    ArrayList<SeamInfo> currentSeamRow = new ArrayList<SeamInfo>();
    int lineLength;
    int seamLength;
    if (vertical) {
      lineLength = this.width();
      seamLength = this.height();
    }
    else {
      lineLength = this.height();
      seamLength = this.width();
    }

    if (lineLength == 0 || seamLength == 0) {
      throw new IllegalArgumentException("Can't get seam of an empty image");
    }

    APixel endOfRow = this.corner.down.right;
    // Constructs the first line of SeamInfos
    for (int j = 0; j < lineLength; j += 1) {
      // In this loop, endOfRow is always a Pixel, since endOfRow starts at the first
      // Pixel and only goes to the last Pixel
      currentSeamRow.add(new SeamInfo((Pixel) endOfRow));
      if (vertical) {
        endOfRow = endOfRow.right;
      }
      else {
        endOfRow = endOfRow.down;
      }

    }

    SeamArrayUtils seamUtils = new SeamArrayUtils();
    // Loops through each line in the image, calculating the corresponding SeamInfos
    // for that line
    for (int i = 0; i < seamLength - 1; i += 1) {
      endOfRow = endOfRow.right.down;
      prevSeamRow = currentSeamRow;
      currentSeamRow = new ArrayList<SeamInfo>();
      // Calculates the SeamInfos for a given line
      for (int j = 0; j < lineLength; j += 1) {
        ArrayList<SeamInfo> possibleParents = new ArrayList<SeamInfo>();
        if (j != 0) {
          possibleParents.add(prevSeamRow.get(j - 1));
        }
        possibleParents.add(prevSeamRow.get(j));
        if (j != lineLength - 1) {
          possibleParents.add(prevSeamRow.get(j + 1));
        }
        SeamInfo cameFrom = seamUtils.lowestWeight(possibleParents);
        currentSeamRow.add(new SeamInfo((Pixel) endOfRow, cameFrom));
        if (vertical) {
          endOfRow = endOfRow.right;
        }
        else {
          endOfRow = endOfRow.down;
        }
      }
    }

    return new SeamArrayUtils().lowestWeight(currentSeamRow);
  }

  // Returns the vertical number of pixels in this grid
  int height() {
    return this.corner.down.height();
  }

  // Returns the horizontal number of pixels in this grid
  int width() {
    return this.corner.right.width();
  }

  // Renders this grid as an image
  // If energy is true, draws the energies of each pixel in a grayscale
  // Otherwise draws the pixels colors and highlights the seam to remove (if
  // needed)
  ComputedPixelImage draw(boolean energy) {
    int imgWidth = this.width();
    int imgHeight = this.height();
    ComputedPixelImage returnImage = new ComputedPixelImage(imgWidth, imgHeight);
    APixel toDraw = this.corner.down.right;
    for (int i = 0; i < imgHeight; i += 1) {
      for (int j = 0; j < imgWidth; j += 1) {
        returnImage.setPixel(j, i, ((Pixel) (toDraw)).drawColor(energy));
        toDraw = toDraw.right;
      }
      toDraw = toDraw.down.right;
    }

    return returnImage;
  }

}

// Represents an abstract pixel
abstract class APixel {
  Color color;
  APixel left;
  APixel right;
  APixel up;
  APixel down;

  // Gets the brightness of this pixel (calculates the average of RGB components,
  // normalized to [0,1])
  double getBrightness() {
    return ((double) this.color.getBlue() + (double) this.color.getRed()
        + (double) this.color.getGreen()) / (255 * 3);
  }

  // Gets the energy of this pixel (according to the formula specific in the
  // assignment)
  double getEnergy() {
    double horizontalEnergy = (this.up.left.getBrightness() + 2 * this.left.getBrightness()
        + this.down.left.getBrightness())
        - (this.up.right.getBrightness() + 2 * this.right.getBrightness()
            + this.down.right.getBrightness());
    double verticalEnergy = (this.up.left.getBrightness() + 2 * this.up.getBrightness()
        + this.up.right.getBrightness())
        - (this.down.left.getBrightness() + 2 * this.down.getBrightness()
            + this.down.right.getBrightness());
    return Math.sqrt(Math.pow(horizontalEnergy, 2) + Math.pow(verticalEnergy, 2));
  }

  // Calculates the height of this grid of pixels
  // Counts the number of nodes before reaching a vertical or corner sentinel
  // (traversing downwards)
  abstract int height();

  // Calculates the width of this grid of pixels
  // Counts the number of nodes before reaching a horizontal or corner sentinel
  // (traversing rightwards)
  abstract int width();

  // EFFECT: Swaps this APixel with the given Pixel if this is not a sentinel
  // Returns true if the swap was successful, and false otherwise
  boolean swap(Pixel toSwap) {
    return false;
  }

  // Are all the diagonal invariants for this APixel satisfied?
  boolean checkDiagonalInvariants() {
    return (this.left.up == this.up.left) && (this.up.right == this.right.up)
        && (this.right.down == this.down.right) && (this.down.left == this.left.down);
  }

}

// Represents a corner sentinel in a grid of pixels
class CornerSentinel extends APixel {
  // Constructs a new corner sentinel
  CornerSentinel() {
    this.color = Color.black;
    this.left = this;
    this.right = this;
    this.up = this;
    this.down = this;
  }

  // Calculates the height of this grid of pixels
  // Counts the number of nodes before reaching a vertical or corner sentinel
  // (traversing downwards)
  int height() {
    return 0;
  }

  // Calculates the width of this grid of pixels
  // Counts the number of nodes before reaching a horizontal or corner sentinel
  // (traversing rightwards)
  int width() {
    return 0;
  }

}

// Represents a horizontal sentinel (i.e. one that connects 
// the left and right columns) in a grid of pixels
// EFFECT: Sets the <down> field in the <up> pixel to this
// EFFECT: Sets the <up> field in the <down> pixel to this
class HorizontalSentinel extends APixel {
  // Constructs a new horizontal sentinel
  HorizontalSentinel(APixel up, APixel down) {
    this.color = Color.black;
    this.left = this;
    this.right = this;
    this.up = up;
    this.down = down;
    this.up.down = this;
    this.down.up = this;
  }

  // Calculates the height of this grid of pixels
  // Counts the number of nodes before reaching a vertical or corner sentinel
  // (traversing downwards)
  int height() {
    return 1 + this.down.height();
  }

  // Calculates the width of this grid of pixels
  // Counts the number of nodes before reaching a horizontal or corner sentinel
  // (traversing rightwards)
  int width() {
    return 0;
  }

}

// Represents a vertical sentinel (i.e. one that connects the top and bottom rows) 
// in a grid of pixels
// EFFECT: Sets the <left> field in the <right> pixel to this
// EFFECT: Sets the <right> field in the <left> pixel to this
class VerticalSentinel extends APixel {
  // Constructs a new horizontal sentinel
  VerticalSentinel(APixel left, APixel right) {
    this.color = Color.black;
    this.left = left;
    this.right = right;
    this.up = this;
    this.down = this;
    this.right.left = this;
    this.left.right = this;
  }

  // Calculates the height of this grid of pixels
  // Counts the number of nodes before reaching a vertical or corner sentinel
  // (traversing downwards)
  int height() {
    return 0;
  }

  // Calculates the width of this grid of pixels
  // Counts the number of nodes before reaching a horizontal or corner sentinel
  // (traversing rightwards)
  int width() {
    return 1 + this.right.width();
  }
}

// Represents a pixel in an image
class Pixel extends APixel {
  boolean highlighted;

  // Constructs a new Pixel
  Pixel(Color color, APixel left, APixel right, APixel up, APixel down) {

    this.color = color;
    this.left = left;
    this.right = right;
    this.up = up;
    this.down = down;

    this.right.left = this;
    this.left.right = this;
    this.up.down = this;
    this.down.up = this;
    this.highlighted = false;
  }

  // Calculates the height of this grid of pixels
  // Counts the number of nodes before reaching a vertical or corner sentinel
  // (traversing downwards)
  int height() {
    return 1 + this.down.height();
  }

  // Calculates the width of this grid of pixels
  // Counts the number of nodes before reaching a horizontal or corner sentinel
  // (traversing rightwards)
  int width() {
    return 1 + this.right.width();
  }

  // Removes this Pixel and shifts the row left
  // Note: Puts a hole at the end of the row
  void removeHorizontal() {
    this.swapToRight();
    this.left.right = this.right;
    this.right.left = this.left;
  }

  // Removes this Pixel and shifts the column upwards
  // Note: Puts a hole at the bottom of the column
  void removeVertical() {
    this.swapToBottom();
    this.down.up = this.up;
    this.up.down = this.down;
  }

  // Moves this pixel all the way to the right end of this row
  void swapToRight() {
    if (this.right.swap(this)) {
      this.swapToRight();
    }
  }

  // Moves this pixel all the way to the bottom of this column
  void swapToBottom() {
    if (this.down.swap(this)) {
      this.swapToBottom();
    }
  }

  // EFFECT: Swaps this Pixel with the given Pixel if this is not a sentinel
  // Returns true if the swap was successful, and false otherwise
  boolean swap(Pixel that) {
    APixel thisRight = this.right;
    APixel thisLeft = this.left;
    APixel thisUp = this.up;
    APixel thisDown = this.down;
    APixel thatRight = that.right;
    APixel thatLeft = that.left;
    APixel thatUp = that.up;
    APixel thatDown = that.down;

    thisRight.left = that;
    thisLeft.right = that;
    thisUp.down = that;
    thisDown.up = that;
    thatRight.left = this;
    thatLeft.right = this;
    thatUp.down = this;
    thatDown.up = this;

    thisRight = this.right;
    thisLeft = this.left;
    thisUp = this.up;
    thisDown = this.down;
    thatRight = that.right;
    thatLeft = that.left;
    thatUp = that.up;
    thatDown = that.down;

    this.right = thatRight;
    this.left = thatLeft;
    this.up = thatUp;
    this.down = thatDown;
    that.right = thisRight;
    that.left = thisLeft;
    that.up = thisUp;
    that.down = thisDown;

    return true;
  }

  // Returns the color associated with this pixel for drawing
  // If drawing energy, scales this pixel's energy by the largest possible energy
  // of a pixel
  Color drawColor(boolean energy) {
    if (highlighted) {
      return Color.red;
    }
    else if (energy) {
      int brightness = (int) ((this.getEnergy() / Math.sqrt(20)) * 255);
      return new Color(brightness, brightness, brightness);
    }
    else {
      return this.color;
    }
  }

}

// Represents a seam in an image
class SeamInfo {
  Pixel pixel;
  double totalWeight;
  SeamInfo cameFrom;

  // Constructs a SeamInfo which is not the first in the Seam
  SeamInfo(Pixel pixel, SeamInfo cameFrom) {
    this.pixel = pixel;
    this.cameFrom = cameFrom;
    this.totalWeight = this.cameFrom.totalWeight + this.pixel.getEnergy();
  }

  // Constructs a SeamInfo which is the first in the Seam
  SeamInfo(Pixel pixel) {
    this.pixel = pixel;
    this.totalWeight = this.pixel.getEnergy();
  }

  // Returns the difference of this SeamInfo's weight and that SeamInfo's weight
  double compareWeight(SeamInfo that) {
    return this.totalWeight - that.totalWeight;
  }

  // Calls the given function on all Pixels in this seamInfo
  void callOnAll(Consumer<Pixel> func) {
    func.accept(this.pixel);
    if (this.cameFrom != null) {
      this.cameFrom.callOnAll(func);
    }
  }

}

// Methods on ArrayLists of SeamInfos 
class SeamArrayUtils {
  // Returns the lowest weight SeamInfo in the ArrayList
  // In the case of ties, returns the first SeamInfo which is part of the tie
  SeamInfo lowestWeight(ArrayList<SeamInfo> list) {
    if (list.size() == 0) {
      throw new IllegalArgumentException("Cannot get the lowest weight seam of an empty list");
    }

    SeamInfo lowest = list.get(0);
    for (SeamInfo seam : list) {
      if (seam.compareWeight(lowest) < 0) {
        lowest = seam;
      }
    }
    return lowest;
  }
}

// Removes the given pixel and shifts the row to the left
class RemoveHorizontal implements Consumer<Pixel> {
  // EFFECT: Removes the given pixel and shifts the row to the left
  public void accept(Pixel pixel) {
    pixel.removeHorizontal();
  }
}

// Removes the given pixel and shifts the column to the top
class RemoveVertical implements Consumer<Pixel> {
  // EFFECT: Removes the given pixel and shifts the column to the top
  public void accept(Pixel pixel) {
    pixel.removeVertical();
  }
}

// Changes the given pixel's highlighted field to true
class Highlight implements Consumer<Pixel> {
  // EFFECT: Changes the given pixel's <highlighted> field to true
  public void accept(Pixel pixel) {
    pixel.highlighted = true;
  }
}

// Represents a world to display seam removal
class SeamRemoval extends World {
  GridPixels grid;
  boolean paused;
  String toRemove;
  SeamInfo highlightedSeam;
  boolean drawEnergy;

  // Constructs a new SeamRemoval from the given image
  SeamRemoval(FromFileImage image) {
    this.grid = new GridPixels(image);
    this.paused = false;
    this.toRemove = "none";
    this.drawEnergy = false;
  }

  // Renders this world's current grid of pixels
  public WorldScene makeScene() {
    int imgWidth = this.grid.width();
    int imgHeight = this.grid.height();
    WorldImage gridImage = this.grid.draw(drawEnergy);
    WorldScene scene = new WorldScene(imgWidth, imgHeight);
    scene.placeImageXY(gridImage, imgWidth / 2, imgHeight / 2);
    return scene;
  }

  // Removes the lowest vertical seam in the grid (while it is is non-empty)
  public void onTick() {
    if (this.toRemove.equals("vertical")) {
      this.grid.removeVerticalSeam(this.highlightedSeam);
      this.toRemove = "none";
    }
    else if (this.toRemove.equals("horizontal")) {
      this.grid.removeHorizontalSeam(this.highlightedSeam);
      this.toRemove = "none";
    }
    else if (!paused) {
      if (new Random().nextInt(2) == 0) {
        this.highlightVertical();
      }
      else {
        this.highlightHorizontal();
      }
    }
  }

  // Highlights the lowest vertical seam in the grid
  // EFFECT: Changes <toRemove> to "vertical"
  // EFFECT: Changes <highlightedSeam> to the lowest vertical seam
  // and changes all the pixels in the seam to red
  void highlightVertical() {
    this.highlightedSeam = this.grid.lowestSeam(true);
    this.highlightedSeam.callOnAll(new Highlight());
    this.toRemove = "vertical";
  }

  // Highlights the lowest horizontal seam in the grid
  // EFFECT: Changes <toRemove> to "horizontal"
  // EFFECT: Changes <highlightedSeam> to the lowest horizontal seam
  // and changes all the pixels in the seam to red
  void highlightHorizontal() {
    this.highlightedSeam = this.grid.lowestSeam(false);
    this.highlightedSeam.callOnAll(new Highlight());
    this.toRemove = "horizontal";
  }

  public void onKeyEvent(String key) {
    if (key.equals(" ")) {
      this.paused = !this.paused;
    }
    else if (key.equals("e")) {
      this.drawEnergy = !this.drawEnergy;
    }
    else if (key.equals("v") && this.paused) {
      this.highlightVertical();
    }
    else if (key.equals("h") && this.paused) {
      this.highlightHorizontal();
    }
  }

}

class TestUtils {
  // converts SeamInfo class to pixel list
  public ArrayList<Pixel> seamInfoToList(SeamInfo s) {
    ArrayList<Pixel> ret = new ArrayList<Pixel>();
    SeamInfo nextSeamInfo = s;
    while (nextSeamInfo != null) {
      ret.add(nextSeamInfo.pixel);
      nextSeamInfo = nextSeamInfo.cameFrom;
    }
    return ret;
  }

  // returns a SeamInfo with the given weight
  public SeamInfo seamInfoWithTotalWeight(int weight) {
    SeamInfo retSeam = new SeamInfo(new Pixel(new Color(weight, weight, weight),
        new CornerSentinel(), new CornerSentinel(), new CornerSentinel(), new CornerSentinel()));
    retSeam.totalWeight = weight;
    return retSeam;
  }
}

class ExamplesSeamRemoval {
  SeamRemoval world1 = new SeamRemoval(new FromFileImage("Desert.jpg"));
  ArrayList<Color> rowyybg;
  ArrayList<Color> rowyyb;
  ArrayList<Color> rowbbb;
  ArrayList<Color> rowyrbry = new ArrayList<Color>(
      List.of(Color.yellow, Color.red, Color.black, Color.red, Color.yellow));
  ArrayList<Color> bgyb = new ArrayList<Color>(
      List.of(Color.black, Color.gray, Color.yellow, Color.black));
  ArrayList<Color> bgby = new ArrayList<Color>(
      List.of(Color.black, Color.gray, Color.black, Color.yellow));
  ArrayList<Color> bygb = new ArrayList<Color>(
      List.of(Color.black, Color.yellow, Color.gray, Color.black));
  ArrayList<Color> byyb = new ArrayList<Color>(
      List.of(Color.black, Color.yellow, Color.yellow, Color.black));
  ArrayList<Color> bygy = new ArrayList<Color>(
      List.of(Color.black, Color.yellow, Color.gray, Color.yellow));
  ArrayList<Color> bbgb = new ArrayList<Color>(
      List.of(Color.black, Color.black, Color.gray, Color.black));
  ArrayList<Color> ygbb = new ArrayList<Color>(
      List.of(Color.yellow, Color.gray, Color.black, Color.black));
  ArrayList<Color> bggb = new ArrayList<Color>(
      List.of(Color.black, Color.gray, Color.gray, Color.black));
  ArrayList<Color> bgbb = new ArrayList<Color>(
      List.of(Color.black, Color.gray, Color.black, Color.black));
  ArrayList<Color> ygyb = new ArrayList<Color>(
      List.of(Color.yellow, Color.gray, Color.yellow, Color.black));

  GridPixels image1;
  GridPixels image1Remove;
  GridPixels image2;
  GridPixels image3;
  GridPixels image4;
  GridPixels imageHorizontal;
  GridPixels imageHorizontal2;

  GridPixels image1Highlighted;
  GridPixels image1RotatedHighlighted;

  GridPixels image2Highlighted;
  GridPixels imageHorizontalHighlighted;

  TestUtils testUtils = new TestUtils();
  SeamInfo weight0 = testUtils.seamInfoWithTotalWeight(0);
  SeamInfo weight15 = testUtils.seamInfoWithTotalWeight(15);
  SeamInfo weight15v2 = new SeamInfo(new Pixel(new Color(0), new CornerSentinel(),
      new CornerSentinel(), new CornerSentinel(), new CornerSentinel()), weight15);
  SeamInfo weight30 = testUtils.seamInfoWithTotalWeight(30);
  GridPixels swapTest1;
  GridPixels swapTest2;
  GridPixels swapTest3;
  GridPixels swapTest4;
  GridPixels swapTest5;
  GridPixels swapTest6;
  GridPixels swapTest7;
  GridPixels swapTest8;
  GridPixels removeTest1;
  GridPixels removeTest2;
  GridPixels energyTest1;
  GridPixels energyTest2;
  GridPixels energyTest3;
  GridPixels energyTest4;

  APixel corner = new CornerSentinel();
  APixel verSentinel = new VerticalSentinel(corner, corner);
  APixel horSentinel = new HorizontalSentinel(corner, corner);
  Pixel pixel1 = new Pixel(Color.red, horSentinel, horSentinel, verSentinel, verSentinel);
  Pixel pixel2 = new Pixel(Color.blue, new CornerSentinel(), new CornerSentinel(),
      new CornerSentinel(), new CornerSentinel());

  // Initializes examples
  void initData() {
    world1 = new SeamRemoval(new FromFileImage("Desert.jpg"));
    rowyybg = new ArrayList<Color>(List.of(Color.yellow, Color.yellow, Color.black, Color.gray));
    rowyyb = new ArrayList<Color>(List.of(Color.yellow, Color.yellow, Color.black));
    rowbbb = new ArrayList<Color>(List.of(Color.black, Color.black, Color.black));

    image1 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(rowyybg, rowyybg, rowyybg)));
    image1Remove = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(rowyyb, rowyyb, rowyyb)));
    image2 = new GridPixels(new FromFileImage("image2.png"));
    image3 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(rowbbb, rowbbb, rowbbb)));
    image4 = new GridPixels(new FromFileImage("image4.png"));
    imageHorizontal = new GridPixels(new FromFileImage("imageHorizontal.png"));
    imageHorizontal2 = new GridPixels(new FromFileImage("imageHorizontal2.png"));

    image1Highlighted = new GridPixels(
        new ArrayList<ArrayList<Color>>(List.of(rowyybg, rowyybg, rowyybg)));
    ((Pixel) image1Highlighted.corner.down.left).highlighted = true;
    ((Pixel) image1Highlighted.corner.down.down.left).highlighted = true;
    ((Pixel) image1Highlighted.corner.down.down.down.left).highlighted = true;

    image2Highlighted = new GridPixels(new FromFileImage("image2.png"));
    ((Pixel) image2Highlighted.corner.down.right.right.right).highlighted = true;
    ((Pixel) image2Highlighted.corner.down.down.right.right.right).highlighted = true;
    ((Pixel) image2Highlighted.corner.down.down.down.right.right).highlighted = true;
    ((Pixel) image2Highlighted.corner.down.down.down.down.right.right).highlighted = true;

    imageHorizontalHighlighted = new GridPixels(new FromFileImage("imageHorizontal.png"));
    ((Pixel) imageHorizontalHighlighted.corner.down.right.right.right).highlighted = true;
    ((Pixel) imageHorizontalHighlighted.corner.down.down.right.right.right).highlighted = true;
    ((Pixel) imageHorizontalHighlighted.corner.down.down.down.right.right).highlighted = true;
    ((Pixel) imageHorizontalHighlighted.corner.down.down.down.down.right.right).highlighted = true;

    image1RotatedHighlighted = new GridPixels(
        new ArrayList<ArrayList<Color>>(List.of(rowyybg, rowyybg, rowyybg)));
    ((Pixel) image1RotatedHighlighted.corner.left.up).highlighted = true;
    ((Pixel) image1RotatedHighlighted.corner.left.left.up).highlighted = true;
    ((Pixel) image1RotatedHighlighted.corner.left.left.left.up).highlighted = true;

    swapTest1 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bygb, bgyb, bygb)));
    swapTest2 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bgyb, bgyb, bygb)));
    swapTest3 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bbgb, bgyb, bygy)));
    swapTest4 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bggb, byyb, bygb)));
    swapTest5 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bgby, bgyb, bygb)));
    swapTest6 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, ygbb, bgyb, bygb)));
    swapTest7 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bggb, byyb, bygb)));
    swapTest8 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(ygyb, bgbb, bgyb, bygb)));

    removeTest1 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bgby, bgyb, bygb)));
    removeTest1.corner.down.down.left = removeTest1.corner.down.down.left.left;
    removeTest1.corner.down.down.left.right = removeTest1.corner.down.down;

    removeTest2 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bygb, bgyb, bygb)));
    removeTest2.corner.down.down.left = removeTest2.corner.down.down.left.left;
    removeTest2.corner.down.down.left.right = removeTest2.corner.down.down;

    energyTest1 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(
        new ArrayList<Color>(List.of(new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0))),
        new ArrayList<Color>(List.of(new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0))),
        new ArrayList<Color>(
            List.of(new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0))))));
    energyTest2 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(
        new ArrayList<Color>(
            List.of(new Color(0, 0, 0), new Color(255 / 8, 255 / 8, 255 / 8), new Color(0, 0, 0))),
        new ArrayList<Color>(List.of(new Color(255 / 7, 255 / 7, 255 / 7), new Color(0, 0, 0),
            new Color(255 / 7, 255 / 7, 255 / 7))),
        new ArrayList<Color>(List.of(new Color(255 / 3, 255 / 3, 255 / 3),
            new Color(255 / 4, 255 / 4, 255 / 4), new Color(255 / 3, 255 / 3, 255 / 3))))));
    energyTest3 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(
        new ArrayList<Color>(List.of(new Color(255 / 3, 255 / 3, 255 / 3),
            new Color(255 / 8, 255 / 8, 255 / 8), new Color(0, 0, 0))),
        new ArrayList<Color>(List.of(new Color(255 / 7, 255 / 7, 255 / 7), new Color(17, 34, 186),
            new Color(255 / 3, 255 / 3, 255 / 3))),
        new ArrayList<Color>(List.of(new Color(255 / 3, 255 / 3, 255 / 3),
            new Color(255 / 8, 255 / 8, 255 / 8), new Color(0, 0, 0))))));
    energyTest4 = new GridPixels(new ArrayList<ArrayList<Color>>(List.of(
        new ArrayList<Color>(List.of(new Color(255 / 3, 255 / 3, 255 / 3),
            new Color(255 / 8, 255 / 8, 255 / 8), new Color(255 / 5, 255 / 5, 255 / 5))),
        new ArrayList<Color>(List.of(new Color(255 / 7, 255 / 7, 255 / 7), new Color(150, 255, 9),
            new Color(255 / 6, 255 / 6, 255 / 6))),
        new ArrayList<Color>(List.of(new Color(255, 255, 255), new Color(255 / 2, 255 / 2, 255 / 2),
            new Color(255 / 4, 255 / 4, 255 / 4))))));

  }

  // tests the getBrightness method on APixel
  void testGetBrightness(Tester t) {
    initData();
    t.checkExpect(new CornerSentinel().getBrightness(), 0.);
    t.checkExpect(
        new HorizontalSentinel(new CornerSentinel(), new CornerSentinel()).getBrightness(), 0.);
    t.checkExpect(new VerticalSentinel(new CornerSentinel(), new CornerSentinel()).getBrightness(),
        0.);

    t.checkExpect(new Pixel(new Color(0, 0, 0), new CornerSentinel(), new CornerSentinel(),
        new CornerSentinel(), new CornerSentinel()).getBrightness(), 0.);
    t.checkExpect((new Pixel(new Color(150, 90, 203), new CornerSentinel(), new CornerSentinel(),
        new CornerSentinel(), new CornerSentinel()).getBrightness() - 0.579) < 0.01, true);
    t.checkExpect((new Pixel(new Color(90, 150, 203), new CornerSentinel(), new CornerSentinel(),
        new CornerSentinel(), new CornerSentinel()).getBrightness() - 0.579) < 0.01, true);
    t.checkExpect((new Pixel(new Color(150, 203, 90), new CornerSentinel(), new CornerSentinel(),
        new CornerSentinel(), new CornerSentinel()).getBrightness() - 0.579) < 0.01, true);
    t.checkExpect(new Pixel(new Color(255, 255, 255), new CornerSentinel(), new CornerSentinel(),
        new CornerSentinel(), new CornerSentinel()).getBrightness(), 1.);
  }

  // Tests the getEnergy method in APixel
  void testGetEnergy(Tester t) {
    initData();
    t.checkExpect(energyTest1.corner.getEnergy(), 0.0);
    t.checkExpect(energyTest1.corner.right.getEnergy(), 0.0);
    t.checkExpect(energyTest1.corner.down.getEnergy(), 0.0);
    t.checkExpect(energyTest1.corner.right.right.down.down.getEnergy(), 0.0);
    t.checkExpect(energyTest1.corner.right.right.down.getEnergy(), 0.0);
    t.checkExpect(energyTest1.corner.down.down.right.getEnergy(), 0.0);
    t.checkExpect(energyTest1.corner.right.right.up.getEnergy(), 0.0);
    t.checkExpect(energyTest1.corner.down.down.left.getEnergy(), 0.0);
    t.checkInexact(energyTest2.corner.right.right.down.down.getEnergy(), 0.917, 0.01);
    t.checkInexact(energyTest3.corner.right.right.down.down.getEnergy(), 0.282, 0.01);
    t.checkInexact(energyTest4.corner.right.right.down.down.getEnergy(), 1.688, 0.01);
  }

//  // Tests seam removal
//  void testSeamRemoval(Tester t) {
//    world1.bigBang(1000, 1000, 0.1);
//  }

  // Tests the callOnAllmethod in SeamInfo
  void testCallOnAll(Tester t) {
    initData();
    image2.lowestSeam(true).callOnAll(new RemoveHorizontal());
    image2.corner.left = image2.corner.left.left;
    image2.corner.left.right = image2.corner;
    t.checkExpect(image2, new GridPixels(new FromFileImage("image2Remove.png")));
    initData();
    image1.lowestSeam(true).callOnAll(new RemoveHorizontal());
    image1.corner.left = image1.corner.left.left;
    image1.corner.left.right = image1.corner;
    t.checkExpect(image1, image1Remove);
  }

  // ADD TESTS FOR HORIZONTAL
  // ADD TESTS FOR SMALL IMAGE
  // Tests the lowestSeam method in GridPixels
  void testLowestSeam(Tester t) {
    initData();
    t.checkExpect(testUtils.seamInfoToList(image1.lowestSeam(true)), new ArrayList<APixel>(
        List.of(image1.corner.left.up, image1.corner.left.down.down, image1.corner.left.down)));
    t.checkExpect(testUtils.seamInfoToList(image2.lowestSeam(true)),
        new ArrayList<APixel>(List.of(image2.corner.right.right.up, image2.corner.right.right.up.up,
            image2.corner.right.right.right.down.down, image2.corner.right.right.right.down)));
    t.checkExpect(testUtils.seamInfoToList(image3.lowestSeam(true)), new ArrayList<APixel>(
        List.of(image3.corner.right.up, image3.corner.right.down.down, image3.corner.right.down)));
    t.checkExpect(testUtils.seamInfoToList(image4.lowestSeam(true)),
        new ArrayList<APixel>(List.of(image4.corner.left.left.up, image4.corner.left.left.up.up,
            image4.corner.left.left.left.down.down, image4.corner.left.left.left.down)));
  }

  // Tests the checkDiagonalInvariants method in GridPixels
  void testDiagInvariantsGridPixels(Tester t) {
    initData();
    t.checkNoException(image1, "checkDiagonalInvariants");
    image1.corner.down.right = image1.corner.right;
    t.checkException(
        new IllegalStateException("At least one APixel does not satisfy the diagonal invariants"),
        image1, "checkDiagonalInvariants");
    initData();
    t.checkNoException(image1, "checkDiagonalInvariants");
    image1.corner.up = image1.corner.down;
    t.checkException(
        new IllegalStateException("At least one APixel does not satisfy the diagonal invariants"),
        image1, "checkDiagonalInvariants");
  }

  // Tests the checkDiagonalInvariants method on APixel
  void testDiagInvariantsAPixel(Tester t) {
    initData();
    t.checkExpect(image1.corner.checkDiagonalInvariants(), true);
    image1.corner.down.right = image1.corner.right;
    t.checkExpect(image1.corner.checkDiagonalInvariants(), false);
    initData();
    t.checkExpect(image1.corner.down.checkDiagonalInvariants(), true);
    image1.corner.down.up.right = image1.corner.right.right;
    t.checkExpect(image1.corner.down.checkDiagonalInvariants(), false);
    initData();
    t.checkExpect(image1.corner.right.checkDiagonalInvariants(), true);
    image1.corner.right.down.left = image1.corner.left;
    t.checkExpect(image1.corner.right.checkDiagonalInvariants(), false);
    initData();
    t.checkExpect(image1.corner.right.down.checkDiagonalInvariants(), true);
    image1.corner.right.left = image1.corner.right;
    t.checkExpect(image1.corner.right.down.checkDiagonalInvariants(), false);
  }

  // ADD A TEST FOR NON LOWEST SEAM?
  // Tests the removeVerticalSeam method in GridPixels
  void testRemoveVerticalSeam(Tester t) {
    initData();
    image2.removeVerticalSeam(image2.lowestSeam(true));
    t.checkExpect(image2, new GridPixels(new FromFileImage("image2Remove.png")));
    initData();
    image1.removeVerticalSeam(image1.lowestSeam(true));
    t.checkExpect(image1, image1Remove);
  }

  // Tests the removeHorizontalSeam method in GridPixels
  void testRemoveHorizontalSeam(Tester t) {
    initData();
    imageHorizontal.removeHorizontalSeam(imageHorizontal.lowestSeam(false));
    t.checkExpect(imageHorizontal, new GridPixels(new FromFileImage("imageHorizontalRemove.png")));
    initData();
    imageHorizontal2.removeHorizontalSeam(imageHorizontal2.lowestSeam(false));
    t.checkExpect(imageHorizontal2,
        new GridPixels(new FromFileImage("imageHorizontal2Remove.png")));
    initData();
  }

  // FIX THESE TESTS SO THEY WORK (TEST PAUSED)
  // Tests the onTick method in SeamRemoval
  void testOnTick(Tester t) {
    initData();
//    SeamRemoval world2 = new SeamRemoval(new FromFileImage("image2.png"));
//    world2.onTick();
//    t.checkExpect(world2, new SeamRemoval(new FromFileImage("image2Remove.png")));
  }

  // tests the width method on the GridPixels class
  void testWidthGridPixels(Tester t) {
    initData();
    t.checkExpect(image1.width(), 4);
    t.checkExpect(image2.width(), 5);
  }

  // tests the width method on the APixel class
  void testWidthAPixel(Tester t) {
    initData();
    t.checkExpect(image1.corner.width(), 0);
    t.checkExpect(image1.corner.right.width(), 4);
    t.checkExpect(image1.corner.down.right.width(), 4);
    t.checkExpect(image1.corner.right.right.width(), 3);
    t.checkExpect(image1.corner.down.down.width(), 0);
    t.checkExpect(image2.corner.right.width(), 5);
  }

  // tests the height method on the GridPixels class
  void testHeightGridPixels(Tester t) {
    initData();
    t.checkExpect(image1.height(), 3);
    t.checkExpect(image2.height(), 4);
  }

  // tests the width method on the APixel class
  void testHeightAPixel(Tester t) {
    initData();
    t.checkExpect(image1.corner.height(), 0);
    t.checkExpect(image1.corner.right.right.height(), 0);
    t.checkExpect(image1.corner.down.height(), 3);
    t.checkExpect(image1.corner.down.right.height(), 3);
    t.checkExpect(image1.corner.down.down.height(), 2);
    t.checkExpect(image2.corner.down.height(), 4);
  }

  // tests the compareWeight method on SeamInfo
  void testCompareWeight(Tester t) {
    initData();
    t.checkExpect(weight0.compareWeight(weight15), -15.);
    t.checkExpect(weight0.compareWeight(weight15v2), -15.);
    t.checkExpect(weight30.compareWeight(weight0), 30.);
    t.checkExpect(weight30.compareWeight(weight30), 0.);
  }

  // tests the swap method on APixel
  void testSwap(Tester t) {
    initData();
    t.checkExpect(swapTest1.corner.down.down.right.right
        .swap((Pixel) swapTest1.corner.down.down.right.right.right), true);
    t.checkExpect(swapTest1, swapTest2);

    initData();
    t.checkExpect(swapTest1.corner.down.down.right.right.right
        .swap((Pixel) swapTest1.corner.down.down.right.right), true);
    t.checkExpect(swapTest1, swapTest2);

    initData();
    t.checkExpect(swapTest1.corner.down.down.right.right
        .swap((Pixel) swapTest1.corner.down.down.down.right.right), true);
    t.checkExpect(swapTest1, swapTest4);

    initData();
    t.checkExpect(swapTest1.corner.down.down.down.right.right
        .swap((Pixel) swapTest1.corner.down.down.right.right), true);
    t.checkExpect(swapTest1, swapTest4);

    initData();
    t.checkExpect(swapTest1.corner.down.down.right.right.swap((Pixel) swapTest1.corner.up.left),
        true);
    t.checkExpect(swapTest1, swapTest3);

    initData();
    t.checkExpect(swapTest1.corner.up.left.swap((Pixel) swapTest1.corner.down.down.right.right),
        true);
    t.checkExpect(swapTest1, swapTest3);

    initData();
    t.checkExpect(swapTest1.corner.swap((Pixel) swapTest1.corner.down.right), false);
    t.checkExpect(swapTest1,
        new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bygb, bgyb, bygb))));

    initData();
    t.checkExpect(swapTest1.corner.right.right.swap((Pixel) swapTest1.corner.down.right), false);
    t.checkExpect(swapTest1,
        new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bygb, bgyb, bygb))));

    initData();
    t.checkExpect(swapTest1.corner.down.down.swap((Pixel) swapTest1.corner.down.right), false);
    t.checkExpect(swapTest1,
        new GridPixels(new ArrayList<ArrayList<Color>>(List.of(bgyb, bygb, bgyb, bygb))));
  }

  // tests the swapToRight method on Pixel
  void testSwapToRight(Tester t) {
    initData();
    ((Pixel) swapTest1.corner.down.down.right.right).swapToRight();
    t.checkExpect(swapTest1, swapTest5);

    initData();
    ((Pixel) swapTest1.corner.down.down.right).swapToRight();
    t.checkExpect(swapTest1, swapTest6);

    initData();
    ((Pixel) swapTest1.corner.down.down.left).swapToRight();
    t.checkExpect(swapTest1, swapTest1);
  }

  // Tests the swapToBottom method on Pixel
  void testSwapToBottom(Tester t) {
    initData();
    ((Pixel) swapTest1.corner.down.down.right.right).swapToBottom();
    t.checkExpect(swapTest1, swapTest7);

    initData();
    ((Pixel) swapTest6.corner.down.right).swapToBottom();
    t.checkExpect(swapTest6, swapTest8);

    initData();
    ((Pixel) swapTest1.corner.up.left).swapToBottom();
    t.checkExpect(swapTest1, swapTest1);
  }

  // tests the removeHorizontal method on Pixel
  void testRemoveHorizontal(Tester t) {
    initData();
    ((Pixel) swapTest1.corner.down.down.right.right).removeHorizontal();
    t.checkExpect(swapTest1, removeTest1);

    initData();
    ((Pixel) swapTest1.corner.down.down.left).removeHorizontal();
    t.checkExpect(swapTest1, removeTest2);
  }

  // tests the removeVertical method on Pixel

  // Tests the lowestWeight method in SeamArrayUtils
  void testLowestWeight(Tester t) {
    initData();
    t.checkExpect(new SeamArrayUtils()
        .lowestWeight(new ArrayList<SeamInfo>(List.of(weight0, weight15, weight30))), weight0);
    t.checkExpect(new SeamArrayUtils()
        .lowestWeight(new ArrayList<SeamInfo>(List.of(weight15, weight0, weight30))), weight0);
    t.checkExpect(new SeamArrayUtils()
        .lowestWeight(new ArrayList<SeamInfo>(List.of(weight15, weight30, weight0))), weight0);
    t.checkExpect(new SeamArrayUtils().lowestWeight(
        new ArrayList<SeamInfo>(List.of(weight30, weight15v2, weight15))), weight15v2);
    t.checkException(
        new IllegalArgumentException("Cannot get the lowest weight seam of an empty list"),
        new SeamArrayUtils(), "lowestWeight", new ArrayList<SeamInfo>());
  }

  // Tests the accept method in Highlight
  void testAcceptHighlight(Tester t) {
    initData();
    t.checkExpect(((Pixel) image1.corner.down.right).highlighted, false);
    new Highlight().accept((Pixel) image1.corner.down.right);
    t.checkExpect(((Pixel) image1.corner.down.right).highlighted, true);
    new Highlight().accept((Pixel) image1.corner.down.right);
    t.checkExpect(((Pixel) image1.corner.down.right).highlighted, true);
  }

  // Tests the accept method in RemoveVertical
  void testAcceptRemoveVertical(Tester t) {
    initData();

  }

  // Tests the highlightVertical method in SeamRemoval

  // Tests the highlightHorizontal method in SeamRemoval

  // Tests the onKey method in SeamRemoval

//  // Tests the constructor exceptions in GridPixels
//  void testConstructorExceptions(Tester t) {
//    t.checkConstructorException(
//        new IllegalArgumentException("Cannot create a pixel grid with no pixels in it"),
//        "GridPixels", new ArrayList<ArrayList<Color>>());
//    t.checkConstructorException(
//        new IllegalArgumentException("Cannot create a non-rectangular image"), "GridPixels",
//        new ArrayList<ArrayList<Color>>(List.of(rowbbb, bbgb)));
//  }

  // Tests the drawColor method in Pixel
  void testDrawColor(Tester t) {
    initData();
    t.checkExpect(new Pixel(Color.red, new CornerSentinel(), new CornerSentinel(),
        new CornerSentinel(), new CornerSentinel()).drawColor(false), Color.red);
    t.checkExpect(((Pixel) image3.corner.right.down).drawColor(false), Color.black);
    t.checkExpect(((Pixel) image3.corner.right.down).drawColor(true), Color.black);
    t.checkExpect(((Pixel) energyTest2.corner.right.right.down.down).drawColor(true),
        new Color((int) (0.917 / Math.sqrt(20) * 255), (int) (0.917 / Math.sqrt(20) * 255),
            (int) (0.917 / Math.sqrt(20) * 255)));
  }

  // tests the seamInfoToList method in the testUtils class
  void testSeamInfoToList(Tester t) {
    initData();
    t.checkExpect(testUtils.seamInfoToList(new SeamInfo(pixel1)),
        new ArrayList<Pixel>(List.of(pixel1)));
    t.checkExpect(testUtils.seamInfoToList(new SeamInfo(pixel1, new SeamInfo(pixel2))),
        new ArrayList<Pixel>(List.of(pixel1, pixel2)));
  }

  void testSeamInfoWithTotalWeight(Tester t) {
    initData();
    t.checkExpect(testUtils.seamInfoWithTotalWeight(4).totalWeight, 4);
    t.checkExpect(testUtils.seamInfoWithTotalWeight(0).totalWeight, 0);
  }
}
