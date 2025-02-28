import tester.Tester;

//represents a list of contents
interface ILoContent {
  // returns the total number of megabytes in the contents
  double totalMegabytes();

  // returns the picture info associated with the contents
  String pictureInfo();

  // returns the picture info associated with the contents with a comma separator
  // before the first picture info
  String pictureInfoComma();
}

//represents an empty list of contents
class MtLoContent implements ILoContent {
  /*
   * Template
   * 
   * Methods
   * this.totalMegabytes()--double
   * this.pictureInfo()--String
   */

  // returns the total number of megabytes in the contents in this empty list, 0
  public double totalMegabytes() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return 0;
  }

  // returns the picture info for all the pictures in this cons list, always
  // returning ""
  public String pictureInfo() {
    /*
    * Template
    * 
    * Everything in the Class Template
    */
    return "";
  }

  // returns the picture info associated with Text, "", with no comma separator
  // because the empty list contains no picture info
  public String pictureInfoComma() {
    /*
    * Template
    * 
    * Everything in the Class Template
    */
    return "";
  }
}

//represents a non-empty list of contents with the first element and the rest of the list
class ConsLoContent implements ILoContent {
  IContent first;
  ILoContent rest;

  ConsLoContent(IContent first, ILoContent rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template
   * 
   * Fields
   * this.first--IContent
   * this.rest.--ILoContent
   * 
   * Methods
   * this.totalMegabytes()--double
   * this.pictureInfo()--String
   * 
   * Methods on Fields
   * this.first.totalMegabytes()--double
   * this.first.pictureInfo()--String
   * this.rest.totalMegabytes()--double
   * this.rest.pictureInfo()--String
   */

  // returns the total number of megabytes in the contents in this cons list
  public double totalMegabytes() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this.first.totalMegabytes() + this.rest.totalMegabytes();
  }

  // returns the picture info for all the pictures in this cons list
  public String pictureInfo() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    String firstPictureInfo = this.first.pictureInfo();
    if (firstPictureInfo.equals("")) {
      return this.rest.pictureInfo();
    }
    else {
      return this.first.pictureInfo() + this.rest.pictureInfoComma();
    }
  }

  // returns the picture info for all the pictures in this cons list with a comma
  // before the first pictureInfo
  public String pictureInfoComma() {
    /*
    * Template
    * 
    * Everything in the Class Template
    */
    String firstPictureInfo = this.first.pictureInfo();
    if (firstPictureInfo.equals("")) {
      return this.rest.pictureInfoComma();
    }
    else {
      return ", " + this.first.pictureInfo() + this.rest.pictureInfoComma();
    }
  }

}

//Represents a piece of Content for a webpage
interface IContent {
  // returns the total megabytes associated with the content
  double totalMegabytes();

  // returns the picture info associated with the content
  String pictureInfo();
}

//Represents a piece of text with a name, number of lines and whether it is in markdown
class Text implements IContent {
  String name;
  int numLines;
  boolean inMarkdown;

  Text(String name, int numLines, boolean inMarkdown) {
    this.name = name;
    this.numLines = numLines;
    this.inMarkdown = inMarkdown;
  }
  /*
   * Template
   * 
   * Fields 
   * this.name--String
   * this.numLines--int
   * this.inMarkdown--boolean
   * 
   * Methods
   * totalMegabytes()--double
   * pictureInfo()--String
   */

  // returns the total number of megabytes in the contents in this empty list, 0
  public double totalMegabytes() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return 0;
  }

  // returns the picture info associated with Text, ""
  public String pictureInfo() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return "";
  }
}

//Represents a picture with a description, name and size in megabytes
class Picture implements IContent {
  String name;
  String description;
  double megabytes;
  /*
   * Template
   * 
   * Fields 
   * this.name--String
   * this.description--String
   * this.megabytes--double
   * 
   * Methods
   * totalMegabytes()--double
   * pictureInfo()--String
   */

  Picture(String name, String description, double megabytes) {
    this.name = name;
    this.description = description;
    this.megabytes = megabytes;
  }

  // returns the total number of megabytes of this picture
  public double totalMegabytes() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this.megabytes;
  }

  // returns the picture info associated with this picture: the name and
  // description
  public String pictureInfo() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this.name + " (" + this.description + ")";
  }
}

//Represents a Hyperlink with a name and webpage it is pointing to
class Hyperlink implements IContent {
  String text;
  Webpage destination;
  /*
   * Template
   * 
   * Fields 
   * this.name--String
   * this.destination--Webpage
   * 
   * Methods
   * totalMegabytes()--double
   * pictureInfo()--String
   * 
   * Methods on Fields
   * this.destination.totalCredits()--int
   * this.destination.totalMegabytes()--double
   * this.destination.pictureInfo()--String
   */

  Hyperlink(String text, Webpage destination) {
    this.text = text;
    this.destination = destination;
  }

  // returns the total number of megabytes of the content that this Hyperlink is
  // pointing to
  public double totalMegabytes() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this.destination.totalMegabytes();
  }

  // returns the info of all the pictures that this Hyperlink is pointing to
  public String pictureInfo() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this.destination.pictureInfo();
  }
}

//Represents a webpage with a name and contents
class Webpage {
  String name;
  ILoContent content;

  Webpage(String name, ILoContent content) {
    this.name = name;
    this.content = content;
  }
  /*
   * Template
   * 
   * Fields 
   * this.name--String
   * this.contentILo--Content
   * 
   * Methods
   * this.totalCredits()--int
   * this.totalMegabytes()--double
   * this.pictureInfo()--String
   * 
   * Methods on Fields
   * this.content.totalMegabytes()--double
   * this.content.pictureInfo()--String
   */

  // returns the total credits it costs to build this website
  int totalCredits() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return 50 * (int) Math.ceil(this.totalMegabytes());
  }

  // returns the total megabytes for this pictures in this website
  double totalMegabytes() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this.content.totalMegabytes();
  }

  // returns the info for all pictures in this website
  String pictureInfo() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this.content.pictureInfo();
  }
}

class ExamplesWebpages {
  // Our website, Fundies News Network (FNN), contains a homepage and two
  // articles. The homepage links to two articles,
  // which each link to the next article. The second article's
  // Next Article button links to malware.
  Webpage malware = new Webpage("Hot Babes In Your Area", new MtLoContent());

  Webpage article2 = new Webpage("Next Semester To Feature \"Super-Accelerated\" Fundies",
      new ConsLoContent(new Text("Article Text", 10, true),
          new ConsLoContent(new Picture("Going Fast", "Super-Accelerated student", 1),
              new ConsLoContent(new Hyperlink("Next Article", malware), new MtLoContent()))));

  Webpage article1 = new Webpage("Ben Lerner Invents New Language",
      new ConsLoContent(new Text("Article Text", 5000, true),
          new ConsLoContent(new Picture("Ben Lerner", "Lerner inventing", 400),
              new ConsLoContent(new Hyperlink("Next Article", article2), new MtLoContent()))));

  Webpage newsHomepage = new Webpage("FNN Home", new ConsLoContent(
      new Hyperlink("Lerner Invents Language", article1),
      new ConsLoContent(new Hyperlink("Super Accelerated Fundies", article2), new MtLoContent())));

  // Fundies 2 site
  Webpage assignment1 = new Webpage("Assignment 1", new ConsLoContent(
      new Picture("Submission", "submission screenshot", 13.7), new MtLoContent()));

  Webpage syllabus = new Webpage("Syllabus",
      new ConsLoContent(new Picture("Java", "HD Java logo", 4), new ConsLoContent(
          new Text("Week 1", 10, true),
          new ConsLoContent(new Hyperlink("First Assignment", assignment1), new MtLoContent()))));

  Webpage assignments = new Webpage("Assignments",
      new ConsLoContent(new Text("Pair Programming", 10, false), new ConsLoContent(
          new Text("Expectations", 15, false),
          new ConsLoContent(new Hyperlink("First Assignment", assignment1), new MtLoContent()))));

  Webpage homepage = new Webpage("Fundies 2 Homepage",
      new ConsLoContent(new Text("Course Goals", 5, true),
          new ConsLoContent(new Text("Instructor Contact", 1, false),
              new ConsLoContent(new Picture("Eclipse", "Eclipse logo", 0.13),
                  new ConsLoContent(
                      new Picture("Coding Background", "digital rain from the Matrix", 30.2),
                      new ConsLoContent(new Hyperlink("Course Syllabus", syllabus),
                          new ConsLoContent(new Hyperlink("Course Assignments", assignments),
                              new MtLoContent())))))));
  double EPS = 0.001;

  // tests the method totalCredits for class Webpage
  boolean testWebpageTotalCredits(Tester t) {
    return t.checkExpect(assignment1.totalCredits(), 700)
        && t.checkExpect(syllabus.totalCredits(), 900)//
        && t.checkExpect(homepage.totalCredits(), 3100);
  }

  // tests the method totalMegabytes for class Webpage
  boolean testWebpageTotalMegabytes(Tester t) {
    return t.checkInexact(assignment1.totalMegabytes(), 13.7, EPS)
        && t.checkInexact(syllabus.totalMegabytes(), 17.7, EPS)
        && t.checkInexact(homepage.totalMegabytes(), 61.73, EPS);
  }

  // tests the method pictureIngo for class Webpage
  boolean testWebpagePictureInfo(Tester t) {
    return t.checkExpect(assignment1.pictureInfo(), "Submission (submission screenshot)")
        && t.checkExpect(assignments.pictureInfo(), "Submission (submission screenshot)")
        && t.checkExpect(homepage.pictureInfo(),
            "Eclipse (Eclipse logo), Coding Background (digital rain from the Matrix),"
                + " Java (HD Java logo), Submission (submission screenshot),"
                + " Submission (submission screenshot)");
  }

  Text text = new Text("Something", 31, true);

  Picture picture = new Picture("some title", "some description", 12.4);

  Hyperlink linkToAssignment = new Hyperlink("link", assignment1);

  // tests the method totalMegabytes for interface IContentTotalMegabytes
  boolean testIContentTotalMegabytes(Tester t) {
    return t.checkExpect(text.totalMegabytes(), 0.0)//
        & t.checkExpect(picture.totalMegabytes(), 12.4)
        & t.checkExpect(linkToAssignment.totalMegabytes(), 13.7);
  }

  // tests the method pictureInfo for interface IContentPictureInfo
  boolean testIContentPictureInfo(Tester t) {
    return t.checkExpect(text.pictureInfo(), "")
        & t.checkExpect(picture.pictureInfo(), "some title (some description)")
        & t.checkExpect(linkToAssignment.pictureInfo(), "Submission (submission screenshot)");
  }

  ConsLoContent syllabusContent = new ConsLoContent(new Picture("Java", "HD Java logo", 4),
      new ConsLoContent(new Text("Week 1", 10, true),
          new ConsLoContent(new Hyperlink("First Assignment", assignment1), new MtLoContent())));

  MtLoContent empty = new MtLoContent();

  // tests the method totalMegabytes for interface ILoContent
  boolean testILoContentTotalMegabytes(Tester t) {
    return t.checkInexact(empty.totalMegabytes(), 0.0, EPS)
        && t.checkInexact(syllabusContent.totalMegabytes(), 17.7, EPS);
  }

  // tests the method pictureInfo for interface ILoContent
  boolean testILoContentPictureInfo(Tester t) {
    return t.checkExpect(empty.pictureInfo(), "") && t.checkExpect(syllabusContent.pictureInfo(),
        "Java (HD Java logo), Submission (submission screenshot)");
  }

  // tests the method pictureInfoComma for interface ILoContent
  boolean testILoContentPictureInfoComma(Tester t) {
    return t.checkExpect(empty.pictureInfoComma(), "")
        && t.checkExpect(syllabusContent.pictureInfoComma(),
            ", Java (HD Java logo), Submission (submission screenshot)");
  }
}

// The reason our methods double-count is because a piece of content could be hyperlinked to
// and also present in the list, or hyperlinked to multiple times. This occurs in  totalMegabytes 
// and pictureInfo (and by extension, totalCredits and pictureInfoComma)
