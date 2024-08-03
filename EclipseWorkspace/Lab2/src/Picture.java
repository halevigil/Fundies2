//import Math;
interface IPicture {
  int getWidth();
};

class Shape implements IPicture {
  String kind;
  int size;

  Shape(String kind, int size) {
    this.kind = kind;
    this.size = size;
  }

  public int getWidth() {
    return this.size;
  }

}

class Combo implements IPicture {
  String name;
  IOperation operation;

  Combo(String name, IOperation operation) {
    this.name = name;
    this.operation = operation;
  }

  public int getWidth() {
    return this.operation.getWidth();
  }
}

interface IOperation {
  int getWidth();
}

class Scale implements IOperation {
  IPicture picture;

  Scale(IPicture picture) {
    this.picture = picture;
  }

  public int getWidth() {
    return 2*this.picture.getWidth()
  }
}

class Beside implements IOperation {
  IPicture picture1;
  IPicture picture2;

  Beside(IPicture picture1, IPicture picture2) {
    this.picture1 = picture1;
    this.picture2 = picture2;
  }

  public int getWidth() {
    return this.picture1.getWidth() + this.picture2.getWidth();
  }
}

class Overlay implements IOperation {
  IPicture bottom;
  IPicture top;

  Overlay(IPicture bottom, IPicture top) {
    this.bottom = bottom;
    this.top = top;
  }

  public int getWidth() {
    Math.max(this.bottom.getWidth(), this.top.getWidth())
  }
}

class ExamplesPicture {
  IPicture circle = new Shape("circle", 20);
  IPicture square = new Shape("square", 30);
  IPicture bigCircle = new Combo("big circle", new Scale(circle));
  IPicture squareOnCircle = new Combo("square on circle", new Overlay(bigCircle, square));
  IPicture doubledSquareOnCircle = new Combo("douled square on circle",
      new Beside(squareOnCircle, squareOnCircle));
}