import java.awt.Color;

import javalib.funworld.WorldScene;
import javalib.worldcanvas.WorldCanvas;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.RotateImage;
import javalib.worldimages.VisiblePinholeImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

interface IRiverDelta {
  // Draws this IRiverDelta as an image
  WorldImage draw();

  // Returns whether every point in this delta has at least as much capacity as
  // water is flowing in
  boolean isFloodSafe();

  // Returns whether this delta has at least the given capacity
  boolean hasMinimumCapacity(int capacity);

  // Rotates this delta by the given number of degrees
  IRiverDelta rotateDelta(double theta);

  // Takes the current delta and a given delta and produces a Fork using the given
  // arguments, twisting the deltas by the given angles
  IRiverDelta combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
      double leftTheta, double rightTheta, IRiverDelta otherTree);

  // Gets the farthest left sea position in this delta, relative to the base of
  // the currentdelta
  double getLeftWidth();

  // Gets the farthest right sea position in this delta, relative to the base of
  // the current delta
  double getRightWidth();

  // Gets the distance between the farthest left and right sea position in this
  // delta
  double getWidth();
}

// represents the river reaching the sea
class Sea implements IRiverDelta {
  // Draws this Sea as an image
  public WorldImage draw() {
    return new RectangleImage(50, 10, "solid", Color.RED);
  }

  public boolean isFloodSafe() {
    return true;
  }

  public boolean hasMinimumCapacity(int capacity) {
    return true;
  }

  public IRiverDelta rotateDelta(double theta) {
    return this;
  }

  public IRiverDelta combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
      double leftTheta, double rightTheta, IRiverDelta otherTree) {
    return new Fork(leftLength, rightLength, leftCapacity, rightCapacity, leftTheta, rightTheta,
        this.rotateDelta(90 - leftTheta), otherTree.rotateDelta(90 - rightTheta));
  }

  public double getLeftWidth() {
    return 0;
  }

  public double getRightWidth() {
    return 0;
  }

  public double getWidth() {
    return 0;
  }
}

// represents a straight section of flowing water
class Stream implements IRiverDelta {
  // How long this flow is
  int length;
  // The maximum amount of water this flow can carry per second, in millions of
  // gallons/second
  int capacity;
  // The angle (in degrees) of this flow, relative to the +x axis
  double theta;
  // The rest of the delta
  IRiverDelta delta;

  Stream(int length, int capacity, double theta, IRiverDelta delta) {
    this.length = length;
    this.capacity = capacity;
    this.theta = theta;
    this.delta = delta;
  }

  // Draws this Stream as an image
  public WorldImage draw() {
    return pinholeOffset(
        new OverlayImage(new RotateImage(new RectangleImage(length, capacity, "solid", Color.BLUE)
            .movePinholeTo(new Posn((1 * (length / 2)), 0)), (360 - theta)), delta.draw()),
        length, theta);
  }

  // Offsets the pinhole for this stream, moving it from the front to the back
  // of the stream
  public WorldImage pinholeOffset(WorldImage image, int length, double theta) {
    return new VisiblePinholeImage(
        image.movePinhole(pinholeXOffset(length, theta), pinholeYOffset(length, theta)));
  }

  // Gives the x offset for the pinhole, given the stream's length and angle theta
  double pinholeXOffset(int length, double theta) {
    return (-1 * length * Math.cos(Math.toRadians(theta)));
  }

  // Gives the y offset for the pinhole, given the stream's length and angle theta
  double pinholeYOffset(int length, double theta) {
    return (1 * length * Math.sin(Math.toRadians(theta)));
  }

  public boolean isFloodSafe() {
    return delta.hasMinimumCapacity(capacity);
  }

  public boolean hasMinimumCapacity(int capacity) {
    return this.capacity >= capacity;
  }

  public IRiverDelta rotateDelta(double theta) {
    return new Stream(this.length, this.capacity, (this.theta + theta),
        this.delta.rotateDelta(theta));
  }

  public IRiverDelta combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
      double leftTheta, double rightTheta, IRiverDelta otherTree) {
    return new Fork(leftLength, rightLength, leftCapacity, rightCapacity, leftTheta, rightTheta,
        this.rotateDelta(leftTheta - 90), otherTree.rotateDelta(rightTheta - 90));
  }

  public double getLeftWidth() {
    double leftWidth = 1 * this.pinholeXOffset(this.length, this.theta);
    double deltaLeftWidth = delta.getLeftWidth();
    return leftWidth + deltaLeftWidth;
  }

  public double getRightWidth() {
    double rightWidth = -1 * this.pinholeXOffset(this.length, this.theta);
    double deltaRightWidth = delta.getRightWidth();
    return rightWidth + deltaRightWidth;
  }

  public double getWidth() {
    return this.getLeftWidth() + this.getRightWidth();
  }
}

// represents the river splitting in two
class Fork implements IRiverDelta {
  // How long the left and right branches are
  int leftLength;
  int rightLength;
  // The amount of water the left and right branches can carry
  int leftCapacity;
  int rightCapacity;
  // The angle (in degrees) of the two branches, relative to the +x axis,
  double leftTheta;
  double rightTheta;
  // The remaining parts of the delta
  IRiverDelta left;
  IRiverDelta right;

  Fork(int leftLength, int rightLength, int leftCapacity, int rightCapacity, double leftTheta,
      double rightTheta, IRiverDelta left, IRiverDelta right) {
    this.leftLength = leftLength;
    this.rightLength = rightLength;
    this.leftCapacity = leftCapacity;
    this.rightCapacity = rightCapacity;
    this.leftTheta = leftTheta;
    this.rightTheta = rightTheta;
    this.left = left;
    this.right = right;
  }

  // Draws this Fork as an image
  public WorldImage draw() {
    return new OverlayImage(
        drawBranch(this.leftLength, this.leftCapacity, this.leftTheta, this.left),
        drawBranch(this.rightLength, this.rightCapacity, this.rightTheta, this.right));
  }

  // Draws one branch of a fork, given the length, capacity, and angle, and the
  // rest of its delta
  public WorldImage drawBranch(int length, int capacity, double theta, IRiverDelta rest) {
    return pinholeOffset(
        new OverlayImage(new RotateImage(new RectangleImage(length, capacity, "solid", Color.BLUE)
            .movePinholeTo(new Posn((1 * (length / 2)), 0)), (360 - theta)), rest.draw()),
        length, theta);
  }

  // Offsets the pinhole for a fork branch, moving it from the front to the back
  // of the stream
  public WorldImage pinholeOffset(WorldImage image, int length, double theta) {
    return image.movePinhole(pinholeXOffset(length, theta), pinholeYOffset(length, theta));
  }

  // Gives the x offset for the pinhole, given a branch's length and angle theta
  double pinholeXOffset(int length, double theta) {
    return (-1 * length * Math.cos(Math.toRadians(theta)));
  }

  // Gives the y offset for the pinhole, given a branch's length and angle theta
  double pinholeYOffset(int length, double theta) {
    return (length * Math.sin(Math.toRadians(theta)));
  }

  public boolean isFloodSafe() {
    return left.hasMinimumCapacity(leftCapacity) && right.hasMinimumCapacity(rightCapacity);
  }

  public boolean hasMinimumCapacity(int capacity) {
    return (leftCapacity + rightCapacity >= capacity) && this.isFloodSafe();
  }

  public IRiverDelta rotateDelta(double theta) {
    return new Fork(this.leftLength, this.rightLength, this.leftCapacity, this.rightCapacity,
        (this.leftTheta + theta), (this.rightTheta + theta), this.left.rotateDelta(theta),
        this.right.rotateDelta(theta));
  }

  public IRiverDelta combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
      double leftTheta, double rightTheta, IRiverDelta otherTree) {
    return new Fork(leftLength, rightLength, leftCapacity, rightCapacity, leftTheta, rightTheta,
        this.rotateDelta(leftTheta - 90), otherTree.rotateDelta(rightTheta - 90));
  }

  public double getLeftWidth() {
    double leftBranchWidth = 1 * this.pinholeXOffset(this.leftLength, this.leftTheta);
    double rightBranchWidth = 1 * this.pinholeXOffset(this.rightLength, this.rightTheta);
    double leftDeltaLeftWidth = left.getLeftWidth();
    double rightDeltaLeftWidth = right.getLeftWidth();
    return Math.max(leftBranchWidth + leftDeltaLeftWidth, rightBranchWidth + rightDeltaLeftWidth);
  }

  public double getRightWidth() {
    double leftBranchWidth = -1 * this.pinholeXOffset(this.leftLength, this.leftTheta);
    double rightBranchWidth = -1 * this.pinholeXOffset(this.rightLength, this.rightTheta);
    double leftDeltaRightWidth = left.getRightWidth();
    double rightDeltaRightWidth = right.getRightWidth();
    return Math.max(leftBranchWidth + leftDeltaRightWidth, rightBranchWidth + rightDeltaRightWidth);
  }

  public double getWidth() {
    return this.getLeftWidth() + this.getRightWidth();
  }

}

class ExamplesRiverDeltas {
  IRiverDelta sea = new Sea();
  IRiverDelta DELTA1 = new Fork(30, 30, 10, 10, 135, 40, sea, sea);
  IRiverDelta DELTA2 = new Fork(30, 30, 10, 10, 115, 65, sea, sea);
  IRiverDelta stream1 = new Stream(40, 10, 90, DELTA1);
  IRiverDelta stream2 = new Stream(50, 10, 90, DELTA2);
  IRiverDelta stream3 = new Stream(50, 10, 45, DELTA2);
  IRiverDelta stream4 = new Stream(50, 10, 45, sea);
  IRiverDelta fork1 = new Fork(40, 50, 10, 10, 150, 30, DELTA1, DELTA2);
  Fork fork2 = new Fork(40, 50, 10, 10, 150, 30, DELTA1, DELTA2);
  IRiverDelta floodFor = new Fork(30, 30, 10, 10, 135, 45, new Stream(40, 10, 90, sea),
      new Stream(40, 9, 90, sea));
  IRiverDelta floodStr = new Stream(40, 10, 45, new Stream(40, 1, 135, sea));
  IRiverDelta fork3 = new Fork(40, 50, 10, 10, 150, 30, stream1, DELTA2);
  IRiverDelta fork4 = DELTA1.combine(40, 50, 10, 10, 150, 30, DELTA2);
  IRiverDelta fork5 = stream1.combine(40, 50, 10, 10, 150, 30, stream3);
  IRiverDelta fork6 = new Fork(100, 30, 10, 10, 60, 10, DELTA1, DELTA2);

  double EPS = 0.001;

  boolean testThing(Tester jon) {
    WorldCanvas c = new WorldCanvas(500, 500);
    WorldScene s = new WorldScene(500, 500);
    return c.drawScene(s.placeImageXY(fork6.draw(), 250, 250)) && c.show(); // sanity chekcs
  }

  boolean testDraw(Tester jon) {
    return jon.checkExpect(sea.draw(), new RectangleImage(50, 10, "solid", Color.RED));
  }

  boolean testPinholeXOffset(Tester jon) {
    return jon.checkInexact(fork2.pinholeXOffset(1, 90), 0.0, EPS)
        && jon.checkInexact(fork2.pinholeXOffset(10, 135), 7.071, EPS);
  }

  boolean testPinholeYOffset(Tester jon) {
    return jon.checkInexact(fork2.pinholeYOffset(1, 90), 1.0, EPS)
        && jon.checkInexact(fork2.pinholeYOffset(10, 135), 7.071, EPS);
  }

  // Tests the isFloodSafe method in the IRiverDelta interface
  boolean testIsFloodSafe(Tester jon) {
    return jon.checkExpect(sea.isFloodSafe(), true)//
        && jon.checkExpect(stream1.isFloodSafe(), true)//
        && jon.checkExpect(fork1.isFloodSafe(), true)//
        && jon.checkExpect(floodStr.isFloodSafe(), false)
        && jon.checkExpect(floodFor.isFloodSafe(), false)
        && jon.checkExpect(fork3.isFloodSafe(), true);
  }

  // Tests the hasMinimumCapacity method in the IRiverDelta interface
  boolean testHasMinimumCapacity(Tester jon) {
    return jon.checkExpect(sea.hasMinimumCapacity(1000000), true)
        && jon.checkExpect(DELTA1.hasMinimumCapacity(20), true)
        && jon.checkExpect(floodFor.hasMinimumCapacity(21), false)
        && jon.checkExpect(stream1.hasMinimumCapacity(5), true)
        && jon.checkExpect(floodStr.hasMinimumCapacity(11), false);
  }

  // Tests the rotateDelta method in the IRiverDelta interface
  boolean testRotateDelta(Tester jon) {
    return jon.checkExpect(sea.rotateDelta(90), sea)//
        && jon.checkExpect(stream1.rotateDelta(45),
            new Stream(40, 10, 135, new Fork(30, 30, 10, 10, 180, 85, sea, sea)))
        && jon.checkExpect(DELTA1.rotateDelta(46), new Fork(30, 30, 10, 10, 181, 86, sea, sea));
  }

  // Tests the combine method in the IRiverDelta interface
  boolean testCombine(Tester jon) {
    return jon.checkExpect(stream4.combine(10, 10, 10, 10, 135, 45, sea),
        new Fork(10, 10, 10, 10, 135, 45, stream4.rotateDelta(45), sea))
        && jon.checkExpect(stream1.combine(30, 30, 10, 10, 135, 40, DELTA1),
            new Fork(30, 30, 10, 10, 135, 40,
                new Stream(40, 10, 135, new Fork(30, 30, 10, 10, 180, 85, sea, sea)),
                new Fork(30, 30, 10, 10, 85, -10, sea, sea)));
  }

  // Tests the getLeftWidth method in the IRiverDelta interface
  boolean testGetLeftWidth(Tester jon) {
    return jon.checkInexact(fork1.getLeftWidth(), 55.854, EPS)
        && jon.checkInexact(stream4.getLeftWidth(), -35.355, EPS)
        && jon.checkInexact(sea.getLeftWidth(), 0.0, EPS)
        && jon.checkInexact(DELTA2.getLeftWidth(), 12.678, EPS)
        && jon.checkInexact(fork6.getLeftWidth(), -16.865, EPS);
  }

  // Tests the getRightWidth method in the IRiverDelta interface
  boolean testGetRightWidth(Tester jon) {
    return jon.checkInexact(fork1.getRightWidth(), 55.979, EPS)
        && jon.checkInexact(stream4.getRightWidth(), 35.355, EPS)
        && jon.checkInexact(sea.getRightWidth(), 0.0, EPS)
        && jon.checkInexact(DELTA2.getRightWidth(), 12.678, EPS)
        && jon.checkInexact(fork6.getRightWidth(), 72.981, EPS);
  }

  // Tests the getWidth method in the IRiverDelta interface
  boolean testGetWidth(Tester jon) {
    return jon.checkInexact(fork1.getWidth(), 111.833, EPS)
        && jon.checkInexact(stream4.getWidth(), 0.0, EPS)
        && jon.checkInexact(sea.getWidth(), 0.0, EPS)
        && jon.checkInexact(DELTA2.getWidth(), 25.357, EPS)
        && jon.checkInexact(fork6.getWidth(), 56.115, EPS);
  }
}