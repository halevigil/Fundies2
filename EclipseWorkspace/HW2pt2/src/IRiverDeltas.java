import java.awt.Color;

import javalib.funworld.WorldScene;
import javalib.worldcanvas.WorldCanvas;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.RotateImage;
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

  // Gets the farthest leftward distance in this delta
  double getLeftWidth();

  // Gets the farthest rightward distance in this delta
  double getRightWidth();

  // Gets the distance between the farthest left and right position in this
  // delta
  double getWidth();
}

// represents the river reaching the sea
class Sea implements IRiverDelta {
  /*
   * Template
   * 
   * Methods
   * this.draw() -- WorldImage
   * this.isFloodSafe() -- boolean
   * this.hasMinimumCapacity(int capacity) -- int
   * this.rotateDelta(double theta) -- IRiverDelta
   * this.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * this.getLeftWidth() -- double
   * this.getRightWidth() -- double
   * this.getWidth() -- double
   */

  // Draws this Sea as an image
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public WorldImage draw() {
    return new RectangleImage(50, 10, "solid", Color.RED);
  }

  // Returns whether every point in this sea has at least as much capacity as
  // water is flowing in (always true)
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public boolean isFloodSafe() {
    return true;
  }

  // Returns whether this sea has at least the given capacity (always true)
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * 
   * capacity -- int
   */
  public boolean hasMinimumCapacity(int capacity) {
    return true;
  }

  // Rotates this sea by the given number of degrees (seas can't rotate, so it
  // stays the same)
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * 
   * theta -- double
   */
  public IRiverDelta rotateDelta(double theta) {
    return this;
  }

  // Takes the current sea and a given delta and produces a Fork using the given
  // arguments, twisting the deltas by the given angles
  /*
   * Template
   * 
   * Parameters
   * 
   * leftLength -- int
   * rightLength -- int
   * leftCapacity -- int
   * rightCapacity -- int
   * leftTheta -- double
   * rightTheta -- double
   * otherTree -- IRiverDelta
   * 
   * Methods on Parameters
   * 
   * otherTree.draw() -- WorldImage
   * otherTree.isFloodSafe() -- boolean
   * otherTree.hasMinimumCapacity(int capacity) -- boolean
   * otherTree.rotateDelta(double theta) -- IRiverDelta
   * otherTree.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * otherTree.getLeftWidth() -- double
   * otherTree.getRightWidth() -- double
   * otherTree.getWidth() -- double
   */
  public IRiverDelta combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
      double leftTheta, double rightTheta, IRiverDelta otherTree) {
    return new Fork(leftLength, rightLength, leftCapacity, rightCapacity, leftTheta, rightTheta,
        this.rotateDelta(leftTheta - 90), otherTree.rotateDelta(rightTheta - 90));
  }

  // Gets the farthest leftward distance in this sea (always 0)
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public double getLeftWidth() {
    return 0;
  }

  // Gets the farthest rightward distance in this sea (always 0)
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public double getRightWidth() {
    return 0;
  }

  // Gets the distance between the farthest left and right position in this
  // sea (always 0)
  /*
   * Template
   * 
   * Everything in the Class Template
   */
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

  /*
   * Template
   * 
   * Fields
   * 
   * length -- int
   * capacity -- int
   * theta -- double
   * delta -- IRiverDelta
   * 
   * Methods
   * this.draw() -- WorldImage
   * this.pinholeXOffset(int length, double theta) -- double
   * this.pinholeYOffset(int length, double theta) -- double
   * this.isFloodSafe() -- boolean
   * this.hasMinimumCapacity(int capacity) -- int
   * this.rotateDelta(double theta) -- IRiverDelta
   * this.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * this.getLeftWidth() -- double
   * this.getRightWidth() -- double
   * this.getWidth() -- double
   * 
   * Methods on Fields
   * this.delta.draw() -- WorldImage
   * this.delta.isFloodSafe() -- boolean
   * this.delta.hasMinimumCapacity(int capacity) -- boolean
   * this.delta.rotateDelta(double theta) -- IRiverDelta
   * this.delta.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * this.delta.getLeftWidth() -- double
   * this.delta.getRightWidth() -- double
   * this.delta.getWidth() -- double
   */

  // Draws this Stream as an image
  public WorldImage draw() {
    return new OverlayImage(
        new RotateImage(new RectangleImage(this.length, this.capacity, "solid", Color.BLUE)
            .movePinholeTo(new Posn((1 * (this.length / 2)), 0)), (360 - theta)),
        this.delta.draw()).movePinhole(this.pinholeXOffset(this.length, this.theta),
            this.pinholeYOffset(this.length, this.theta));
  }

  // Gives the x offset for the pinhole, given the stream's length and angle theta
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * 
   * length -- int
   * theta -- double
   */
  double pinholeXOffset(int length, double theta) {
    return (-1 * length * Math.cos(Math.toRadians(theta)));
  }

  // Gives the y offset for the pinhole, given the stream's length and angle theta
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * 
   * length -- int
   * theta -- double
   */
  double pinholeYOffset(int length, double theta) {
    return (1 * length * Math.sin(Math.toRadians(theta)));
  }

  // Returns whether every point in and after this stream has at least as much
  // capacity as water is flowing in
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public boolean isFloodSafe() {
    return delta.hasMinimumCapacity(capacity) && delta.isFloodSafe();
  }

  // Returns whether this stream has at least the given capacity
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * 
   * capacity -- int
   */
  public boolean hasMinimumCapacity(int capacity) {
    return this.capacity >= capacity;
  }

  // Rotates this stream by the given number of degrees
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * 
   * theta -- double
   */
  public IRiverDelta rotateDelta(double theta) {
    return new Stream(this.length, this.capacity, (this.theta + theta),
        this.delta.rotateDelta(theta));
  }

  // Takes the current stream and a given delta and produces a Fork using the
  // given arguments, twisting the deltas by the given angles
  /*
   * Template
   * 
   * Parameters
   * 
   * leftLength -- int
   * rightLength -- int
   * leftCapacity -- int
   * rightCapacity -- int
   * leftTheta -- double
   * rightTheta -- double
   * otherTree -- IRiverDelta
   * 
   * Methods on Parameters
   * 
   * otherTree.draw() -- WorldImage
   * otherTree.isFloodSafe() -- boolean
   * otherTree.hasMinimumCapacity(int capacity) -- boolean
   * otherTree.rotateDelta(double theta) -- IRiverDelta
   * otherTree.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * otherTree.getLeftWidth() -- double
   * otherTree.getRightWidth() -- double
   * otherTree.getWidth() -- double
   */
  public IRiverDelta combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
      double leftTheta, double rightTheta, IRiverDelta otherTree) {
    return new Fork(leftLength, rightLength, leftCapacity, rightCapacity, leftTheta, rightTheta,
        this.rotateDelta(leftTheta - 90), otherTree.rotateDelta(rightTheta - 90));
  }

  // Gets the farthest leftward distance in this stream
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public double getLeftWidth() {
    double leftWidth = 1 * this.pinholeXOffset(this.length, this.theta);
    double deltaLeftWidth = delta.getLeftWidth();
    return Math.max(leftWidth + deltaLeftWidth, leftWidth);
  }

  // Gets the farthest rightward distance in this stream
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public double getRightWidth() {
    double rightWidth = -1 * this.pinholeXOffset(this.length, this.theta);
    double deltaRightWidth = delta.getRightWidth();
    return Math.max(rightWidth + deltaRightWidth, rightWidth);
  }

  // Gets the distance between the farthest left and right position in this
  // stream
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public double getWidth() {
    double leftWidth = this.getLeftWidth();
    double rightWidth = this.getRightWidth();
    return Math.max(leftWidth, Math.max(rightWidth, leftWidth + rightWidth));
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

  /*
   * Template
   * 
   * Fields
   * 
   * this.leftLength -- int
   * this.rightLength -- int
   * this.leftCapacity -- int
   * this.rightCapacity -- int
   * this.leftTheta -- double
   * this.rightTheta -- double
   * this.left -- IRiverDelta
   * this.right -- IRiverDelta
   * 
   * Methods
   * this.draw() -- WorldImage
   * this.drawBranch(int length, int capacity, double theta, IRiverDelta rest) -- WorldImage
   * this.pinholeXOffset(int length, double theta) -- double
   * this.pinholeYOffset(int length, double theta) -- double
   * this.isFloodSafe() -- boolean
   * this.hasMinimumCapacity(int capacity) -- int
   * this.rotateDelta(double theta) -- IRiverDelta
   * this.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * this.getLeftWidth() -- double
   * this.getRightWidth() -- double
   * this.getWidth() -- double
   * 
   * Methods on Fields
   * 
   * this.left.draw() -- WorldImage
   * this.right.draw() -- WorldImage
   * this.left.isFloodSafe() -- boolean
   * this.right.isFloodSafe() -- boolean
   * this.left.hasMinimumCapacity(int capacity) -- boolean
   * this.right.hasMinimumCapacity(int capacity) -- boolean
   * this.left.rotateDelta(double theta) -- IRiverDelta
   * this.right.rotateDelta(double theta) -- IRiverDelta
   * this.left.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * this.right.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * this.left.getLeftWidth() -- double
   * this.right.getLeftWidth() -- double
   * this.left.getRightWidth() -- double
   * this.right.getRightWidth() -- double
   * this.left.getWidth() -- double
   * this.right.getWidth() -- double
   */

  // Draws this Fork as an image
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public WorldImage draw() {
    return new OverlayImage(
        drawBranch(this.leftLength, this.leftCapacity, this.leftTheta, this.left),
        drawBranch(this.rightLength, this.rightCapacity, this.rightTheta, this.right));
  }

  // Draws one branch of a fork, given the length, capacity, and angle, and the
  // rest of its delta
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * length -- int
   * capacity -- int
   * theta -- double
   * rest -- IRiverDelta
   * 
   * Methods on Parameters
   * this.rest.draw() -- WorldImage
   * this.rest.isFloodSafe() -- boolean
   * this.rest.hasMinimumCapacity(int capacity) -- boolean
   * this.rest.rotateDelta(double theta) -- IRiverDelta
   * this.rest.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * this.rest.getLeftWidth() -- double
   * this.rest.getRightWidth() -- double
   * this.rest.getWidth() -- double
   */
  public WorldImage drawBranch(int length, int capacity, double theta, IRiverDelta rest) {
    return new OverlayImage(
        new RotateImage(new RectangleImage(length, capacity, "solid", Color.BLUE)
            .movePinholeTo(new Posn((1 * (length / 2)), 0)), (360 - theta)),
        rest.draw()).movePinhole(this.pinholeXOffset(length, theta),
            this.pinholeYOffset(length, theta));
  }

  // Gives the x offset for the pinhole, given a branch's length and angle theta
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * length -- int
   * theta -- double
   */
  double pinholeXOffset(int length, double theta) {
    return (-1 * length * Math.cos(Math.toRadians(theta)));
  }

  // Gives the y offset for the pinhole, given a branch's length and angle theta
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * 
   * length -- int
   * theta -- double
   */
  double pinholeYOffset(int length, double theta) {
    return (length * Math.sin(Math.toRadians(theta)));
  }

  // Returns whether every point in this fork has at least as much capacity as
  // water is flowing in
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public boolean isFloodSafe() {
    return this.left.hasMinimumCapacity(this.leftCapacity)
        && this.right.hasMinimumCapacity(this.rightCapacity) && this.left.isFloodSafe()
        && this.right.isFloodSafe();
  }

  // Returns whether this delta has at least the given capacity
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * Parameters
   * 
   * capacity -- int
   */
  public boolean hasMinimumCapacity(int capacity) {
    return (this.leftCapacity + this.rightCapacity >= capacity);
  }

  // Rotates this delta by the given number of degrees
  /*
   * Template
   * 
   * Everything in the Class Template
   * 
   * theta -- double
   */
  public IRiverDelta rotateDelta(double theta) {
    return new Fork(this.leftLength, this.rightLength, this.leftCapacity, this.rightCapacity,
        (this.leftTheta + theta), (this.rightTheta + theta), this.left.rotateDelta(theta),
        this.right.rotateDelta(theta));
  }

  // Takes the current delta and a given delta and produces a Fork using the given
  // arguments, twisting the deltas by the given angles
  /*
   * Template
   * 
   * Parameters
   * 
   * leftLength -- int
   * rightLength -- int
   * leftCapacity -- int
   * rightCapacity -- int
   * leftTheta -- double
   * rightTheta -- double
   * otherTree -- IRiverDelta
   * 
   * Methods on Parameters
   * 
   * otherTree.draw() -- WorldImage
   * otherTree.isFloodSafe() -- boolean
   * otherTree.hasMinimumCapacity(int capacity) -- boolean
   * otherTree.rotateDelta(double theta) -- IRiverDelta
   * otherTree.combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
   *              double leftTheta, double rightTheta, IRiverDelta otherTree) -- IRiverDelta
   * otherTree.getLeftWidth() -- double
   * otherTree.getRightWidth() -- double
   * otherTree.getWidth() -- double
   */
  public IRiverDelta combine(int leftLength, int rightLength, int leftCapacity, int rightCapacity,
      double leftTheta, double rightTheta, IRiverDelta otherTree) {
    return new Fork(leftLength, rightLength, leftCapacity, rightCapacity, leftTheta, rightTheta,
        this.rotateDelta(leftTheta - 90), otherTree.rotateDelta(rightTheta - 90));
  }

  // Gets the farthest leftward distance in this fork
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public double getLeftWidth() {
    double leftBranchWidth = 1 * this.pinholeXOffset(this.leftLength, this.leftTheta);
    double rightBranchWidth = 1 * this.pinholeXOffset(this.rightLength, this.rightTheta);
    double leftDeltaLeftWidth = left.getLeftWidth();
    double rightDeltaLeftWidth = right.getLeftWidth();
    return Math.max(leftBranchWidth, Math.max(rightBranchWidth,
        Math.max(leftBranchWidth + leftDeltaLeftWidth, rightBranchWidth + rightDeltaLeftWidth)));
  }

  // Gets the farthest rightward distance in this fork
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public double getRightWidth() {
    double leftBranchWidth = -1 * this.pinholeXOffset(this.leftLength, this.leftTheta);
    double rightBranchWidth = -1 * this.pinholeXOffset(this.rightLength, this.rightTheta);
    double leftDeltaRightWidth = left.getRightWidth();
    double rightDeltaRightWidth = right.getRightWidth();
    return Math.max(leftBranchWidth, Math.max(rightBranchWidth,
        Math.max(leftBranchWidth + leftDeltaRightWidth, rightBranchWidth + rightDeltaRightWidth)));
  }

  // Gets the distance between the farthest left and right position in this
  // fork
  /*
   * Template
   * 
   * Everything in the Class Template
   */
  public double getWidth() {
    double leftWidth = this.getLeftWidth();
    double rightWidth = this.getRightWidth();
    return Math.max(rightWidth, Math.max(leftWidth, leftWidth + rightWidth));
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
  IRiverDelta delta1Left = new Stream(30, 10, 135, sea);
  IRiverDelta delta1Right = new Stream(30, 10, 40, sea);

  double EPS = 0.001;

  // Displays an image on the canvas
  boolean testImage(Tester jon) {
    WorldCanvas c = new WorldCanvas(500, 500);
    WorldScene s = new WorldScene(500, 500);
    return c.drawScene(s.placeImageXY(fork6.draw(), 250, 250)) && c.show(); // sanity chekcs
  }

  RectangleImage drawnSea = new RectangleImage(50, 10, "solid", Color.RED);

  Fork DELTA1FORK = new Fork(30, 30, 10, 10, 135, 40, sea, sea);

  Stream stream1Stream = new Stream(40, 10, 90, DELTA1);
  Stream stream2Stream = new Stream(50, 10, 90, DELTA2);

  // Tests the pinholeXOffset method in the Fork class
  boolean testPinholeXOffsetFork(Tester jon) {
    return jon.checkInexact(fork2.pinholeXOffset(1, 90), 0.0, EPS)
        && jon.checkInexact(fork2.pinholeXOffset(10, 135), 7.071, EPS);
  }

  // Tests the pinholeYOffset method in the Fork class
  boolean testPinholeYOffsetFork(Tester jon) {
    return jon.checkInexact(fork2.pinholeYOffset(1, 90), 1.0, EPS)
        && jon.checkInexact(fork2.pinholeYOffset(10, 135), 7.071, EPS);
  }

  // Tests the pinholeXOffset method in the Stream class
  boolean testPinholeXOffsetStream(Tester jon) {
    return jon.checkInexact(stream1Stream.pinholeXOffset(1, 90), 0.0, EPS)
        && jon.checkInexact(stream2Stream.pinholeXOffset(10, 135), 7.071, EPS);
  }

  // Tests the pinholeYOffset method in the Stream class
  boolean testPinholeYOffsetStream(Tester jon) {
    return jon.checkInexact(stream1Stream.pinholeYOffset(1, 90), 1.0, EPS)
        && jon.checkInexact(stream1Stream.pinholeYOffset(10, 135), 7.071, EPS);
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
        && jon.checkInexact(stream4.getWidth(), 35.355, EPS)
        && jon.checkInexact(sea.getWidth(), 0.0, EPS)
        && jon.checkInexact(DELTA2.getWidth(), 25.357, EPS)
        && jon.checkInexact(fork6.getWidth(), 72.981, EPS);
  }

  // Tests the draw method in the IRiverDelta interface
  boolean testDraw(Tester jon) {
    return jon.checkExpect(stream4.draw(),
        new OverlayImage(new RotateImage(
            new RectangleImage(50, 10, "solid", Color.BLUE).movePinholeTo(new Posn(25, 0)), 315),
            new RectangleImage(50, 10, "solid", Color.RED)).movePinhole(
                (-50 * Math.cos(Math.toRadians(45))), (50 * Math.sin(Math.toRadians(45)))))
        && jon
            .checkExpect(delta1Left.draw(),
                new OverlayImage(
                    new RotateImage(new RectangleImage(30, 10, "solid", Color.BLUE)
                        .movePinholeTo(new Posn(15, 0)), 225),
                    new RectangleImage(50, 10, "solid", Color.RED)).movePinhole(
                        (-30 * Math.cos(Math.toRadians(135))),
                        (30 * Math.sin(Math.toRadians(135)))))
        && jon
            .checkExpect(delta1Right.draw(),
                new OverlayImage(
                    new RotateImage(new RectangleImage(30, 10, "solid", Color.BLUE)
                        .movePinholeTo(new Posn(15, 0)), 320),
                    new RectangleImage(50, 10, "solid", Color.RED)).movePinhole(
                        (-30 * Math.cos(Math.toRadians(40))), (30 * Math.sin(Math.toRadians(40)))))
        && jon.checkExpect(sea.draw(), drawnSea);
  }
}