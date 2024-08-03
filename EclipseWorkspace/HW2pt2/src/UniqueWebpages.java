import tester.Tester;

//represents a list of contents
interface ILoContent {
  // returns the total number of megabytes in the contents
  double totalMegabytes();

  boolean containsPictureInfo(String info);

  // returns the picture info associated with the contents
  String pictureInfo();

  // returns the picture info associated with the contents with a comma separator
  // before the first picture info
  String pictureInfoComma();

  ILoContent append(ILoContent other);

  ILoContent flattenHyperlinks();

  ILoContent getUniquePictures();

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

  public boolean containsPictureInfo(String info) {
    return false;
  }

  // returns the picture info for all the pictures in this empty list, always
  // returning ""
  public String pictureInfo() {
    /*
     * Template
     * 
     * Everything in the Class Template
     * 
     * Parameters
     * 
     * info -- String
     */
    return "";
  }

  // returns the picture info associated with this empty list with no comma
  // separator because the empty list contains no picture info. Always returns "".
  public String pictureInfoComma() {
    /*
    * Template
    * 
    * Everything in the Class Template
    */
    return "";
  }

  public ILoContent append(ILoContent other) {
    /*
     * Template
     * 
     * Everything in the Class Template
     * 
     * Parameters
     * 
     * other -- ILoContent
     * 
     * Methods on Parameters
     * 
     * other.totalMegabytes() -- double
     * other.containsPictureInfo(String) -- boolean
     * other.pictureInfo() -- String
     * other.pictureInfoComma() -- String
     * other.append(ILoContent) -- ILoContent
     * other.flattenHyperlinks(ILoContent) -- ILoContent
     * other.getUniquePictures(ILoContent) -- ILoContent
     */
    return other;
  }

  public ILoContent flattenHyperlinks() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this;
  }

  public ILoContent getUniquePictures() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this;
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
   * this.rest--ILoContent
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
    return first.totalMegabytes() + rest.totalMegabytes();
  }

  public boolean containsPictureInfo(String info) {
    /*
    * Template
    * 
    * Everything in the Class Template
    * 
    * Parameters
    * 
    * info -- String
    */
    return first.containsPictureInfo(info) || rest.containsPictureInfo(info);
  }

  // returns the picture info for all the pictures in this cons list
  public String pictureInfo() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    String firstPictureInfo = first.pictureInfo();
    if (firstPictureInfo.equals("")) {
      return rest.pictureInfo();
    }
    else {
      return first.pictureInfo() + rest.pictureInfoComma();
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
    String firstPictureInfo = first.pictureInfo();
    if (firstPictureInfo.equals("")) {
      return rest.pictureInfoComma();
    }
    else {
      return ", " + first.pictureInfo() + rest.pictureInfoComma();
    }
  }

  // Appends other to the end of this list
  public ILoContent append(ILoContent other) {
    return new ConsLoContent(this.first, this.rest.append(other));
  }

  // Flattens all hyperlinks in the list, extracting their content
  public ILoContent flattenHyperlinks() {
    /*
    * Template
    * 
    * Everything in the Class Template
    */
    return this.first.flattenHyperlinks().append(this.rest.flattenHyperlinks());
  }

  // Gets all unique pictures in a flattened list (ignoring hyperlinks, assuming
  // they've already been flattened)
  public ILoContent getUniquePictures() {
    /*
    * Template
    * 
    * Everything in the Class Template
    */
    return this.first.getUniquePictures(this.rest);
  }

}

//Represents a piece of Content for a webpage
interface IContent {
  // returns the total megabytes associated with the content
  double totalMegabytes();

  boolean containsPictureInfo(String info);

  // returns the picture info associated with this content
  String pictureInfo();

  ILoContent flattenHyperlinks();

  ILoContent getUniquePictures(ILoContent other);
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

  public boolean containsPictureInfo(String info) {
    return false;
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

  public ILoContent flattenHyperlinks() {
    return new ConsLoContent(this, new MtLoContent());
  }

  public ILoContent getUniquePictures(ILoContent other) {
    return other.getUniquePictures();
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

  public boolean containsPictureInfo(String info) {
    return this.pictureInfo().equals(info);
  }

  public double totalMegabytesHelp(ILoContent otherContent) {
    if (otherContent.containsPictureInfo(this.pictureInfo()))
      return 0;
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

  // If this picture is not in the list of content, returns the picture info
  // associated with this picture: the name and description. Else returns "".
  public String pictureInfoHelp(ILoContent other) {
    /*
    * Template
    * 
    * Everything in the Class Template
    */
    if (other.containsPictureInfo(this.pictureInfo())) {
      return "";
    }
    else {
      return this.name + " (" + this.description + ")";
    }
  }

  public ILoContent flattenHyperlinks() {
    return new ConsLoContent(this, new MtLoContent());
  }

  public ILoContent getUniquePictures(ILoContent other) {
    if (other.containsPictureInfo(this.pictureInfo())) {
      return other.getUniquePictures();
    }
    else {
      return new ConsLoContent(this, other.getUniquePictures());
    }
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

  public boolean containsPictureInfo(String info) {
    return this.destination.containsPictureInfo(info);
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

  public ILoContent flattenHyperlinks() {
    return this.destination.flattenHyperlinks();
  }

  public ILoContent getUniquePictures(ILoContent other) {
    return other.getUniquePictures();
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
    return this.content.flattenHyperlinks().getUniquePictures().totalMegabytes();
  }

  boolean containsPictureInfo(String info) {
    return this.content.containsPictureInfo(info);
  }

  // returns the info for all pictures in this website
  String pictureInfo() {
    /*
     * Template
     * 
     * Everything in the Class Template
     */
    return this.content.flattenHyperlinks().getUniquePictures().pictureInfo();
  }

  public ILoContent flattenHyperlinks() {
    return this.content.flattenHyperlinks();
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

  Picture myPic = new Picture("sth", "sthElse", 12.3);

  ILoContent homepagePictures = new ConsLoContent(new Picture("Eclipse", "Eclipse logo", 0.13),
      new ConsLoContent(new Picture("Coding Background", "digital rain from the Matrix", 30.2),
          new ConsLoContent(new Picture("Java", "HD Java logo", 4.0), new ConsLoContent(
              new Picture("Submission", "submission screenshot", 13.7), new MtLoContent()))));

  // Tests the getUniquePictures method in the Webpage class
  boolean testGetUniquePicturesWebpage(Tester t) {
    return t.checkExpect(homepage.flattenHyperlinks().getUniquePictures(), homepagePictures);
  }

  // Tests the getUniquePictures method in the ILoContent class
  boolean testGetUniquePicturesILoContent(Tester t) {
    return t.checkExpect(homepage.content.flattenHyperlinks().getUniquePictures(),
        homepagePictures);
  }

//Tests the getUniquePictures method in the ILoContent class
  boolean testGetUniquePicturesIContent(Tester t) {
    return t.checkExpect(text.getUniquePictures(homepage.content.flattenHyperlinks()),
        homepagePictures)
        && t.checkExpect(linkToAssignment.getUniquePictures(homepage.content.flattenHyperlinks()),
            homepagePictures)
        && t.checkExpect(picture.getUniquePictures(homepage.content.flattenHyperlinks()),
            new ConsLoContent(picture, homepagePictures));
  }

  Webpage hyperLinkAndPicture = new Webpage("hyperLinkAndPicture",
      new ConsLoContent(myPic,
          new ConsLoContent(
              new Hyperlink("myPic",
                  new Webpage("show myPic", new ConsLoContent(myPic, new MtLoContent()))),
              new MtLoContent())));

  // Tests the totalMegabytes method in the Webpage class
  boolean testWebpageTotalMegabytes(Tester t) {
    return t.checkInexact(assignment1.totalMegabytes(), 13.7, EPS)
        && t.checkInexact(syllabus.totalMegabytes(), 17.7, EPS)
        && t.checkInexact(homepage.totalMegabytes(), 48.03, EPS)
        && t.checkExpect(hyperLinkAndPicture.totalMegabytes(), 12.3);
  }

  // Tests the totalCredits method in the Webpage class
  boolean testWebpageTotalCredits(Tester t) {
    return t.checkExpect(assignment1.totalCredits(), 700)
        && t.checkExpect(syllabus.totalCredits(), 900)//
        && t.checkExpect(homepage.totalCredits(), 2450);
  }

  // Tests the pictureInfo method in the Webpage class
  boolean testWebpagePictureInfo(Tester t) {
    return t.checkExpect(assignment1.pictureInfo(), "Submission (submission screenshot)")
        && t.checkExpect(assignments.pictureInfo(), "Submission (submission screenshot)")
        && t.checkExpect(homepage.pictureInfo(),
            "Eclipse (Eclipse logo), Coding Background (digital rain from the Matrix),"
                + " Java (HD Java logo), Submission (submission screenshot)");
  }

  Text text = new Text("Something", 31, true);

  Picture picture = new Picture("some title", "some description", 12.4);

  Hyperlink linkToAssignment = new Hyperlink("link", assignment1);

  // Tests the totalMegabytes method in the IContent class
  boolean testIContentTotalMegabytes(Tester t) {
    return t.checkExpect(text.totalMegabytes(), 0.0)//
        & t.checkExpect(picture.totalMegabytes(), 12.4)
        & t.checkExpect(linkToAssignment.totalMegabytes(), 13.7);
  }

  // Tests the pictureInfo method in the IContent class
  boolean testIContentPictureInfo(Tester t) {
    return t.checkExpect(text.pictureInfo(), "")
        & t.checkExpect(picture.pictureInfo(), "some title (some description)")
        & t.checkExpect(linkToAssignment.pictureInfo(), "Submission (submission screenshot)");
  }

  ConsLoContent syllabusContent = new ConsLoContent(new Picture("Java", "HD Java logo", 4),
      new ConsLoContent(new Text("Week 1", 10, true),
          new ConsLoContent(new Hyperlink("First Assignment", assignment1), new MtLoContent())));

  MtLoContent empty = new MtLoContent();

  // Tests the totalMegabytes method in the ILoContent class
  boolean testILoContentTotalMegabytes(Tester t) {
    return t.checkInexact(empty.totalMegabytes(), 0.0, EPS)
        && t.checkInexact(syllabusContent.totalMegabytes(), 17.7, EPS);
  }

  // Tests the pictureInfo method in the ILoContent class
  boolean testILoContentPictureInfo(Tester t) {
    return t.checkExpect(empty.pictureInfo(), "") && t.checkExpect(syllabusContent.pictureInfo(),
        "Java (HD Java logo), Submission (submission screenshot)");
  }

  // Tests the pictureInfoComma method in the ILoContent interface
  boolean testILoContentPictureInfoComma(Tester t) {
    return t.checkExpect(empty.pictureInfoComma(), "")
        && t.checkExpect(syllabusContent.pictureInfoComma(),
            ", Java (HD Java logo), Submission (submission screenshot)");
  }

  // Tests the containsPictureInfo method in the ILoContent and IContent
  // interfaces
  boolean testContainsPictureInfoIContent(Tester t) {
    return t.checkExpect(picture.containsPictureInfo("some title (some description)"), true)
        && t.checkExpect(picture.containsPictureInfo("wrong title (some description)"), false)
        && t.checkExpect(text.containsPictureInfo("sth (sth else)"), false)//
        && t.checkExpect(linkToAssignment.containsPictureInfo("Submission (submission screenshot)"),
            true)
        && t.checkExpect(linkToAssignment.containsPictureInfo("Submission (wrooong screenshot)"),
            false);
  }

  // Tests the containsPictureInfo method in the ILoContent interfaces
  boolean testContainsPictureInfoILoContent(Tester t) {
    return t.checkExpect(empty.containsPictureInfo("Java (HD Java logo)"), false)
        && t.checkExpect(syllabusContent.containsPictureInfo("Java (HD Java logo)"), true)
        && t.checkExpect(homepage.content.containsPictureInfo("Java (HD Java logo)"), true)
        && t.checkExpect(hyperLinkAndPicture.content.containsPictureInfo("sth (sthElse)"), true)
        && t.checkExpect(hyperLinkAndPicture.content.containsPictureInfo("sth (sth else)"), false);
  }

  // Tests the containsPictureInfo method in the Webpage class
  boolean testContainsPictureInfoWebpage(Tester t) {
    return t.checkExpect(homepage.containsPictureInfo("Java (HD Java logo)"), true)
        && t.checkExpect(homepage.containsPictureInfo("Java (wroong)"), false);
  }

  ConsLoContent consPicture = new ConsLoContent(picture, empty);

  // Tests the append method in the ILoContent class
  boolean testAppend(Tester t) {
    return t.checkExpect(empty.append(consPicture), consPicture)
        && t.checkExpect(consPicture.append(new ConsLoContent(new Picture("hi", "hi", 0), empty)),
            new ConsLoContent(picture, new ConsLoContent(new Picture("hi", "hi", 0), empty)));
  }

  // Tests the flattenHyperlinks method in the IContent inteface
  boolean testIContentFlattenHyperlinks(Tester t) {
    return t.checkExpect(linkToAssignment.flattenHyperlinks(),
        new ConsLoContent(new Picture("Submission", "submission screenshot", 13.7), empty))
        && t.checkExpect(myPic.flattenHyperlinks(), new ConsLoContent(myPic, empty))
        && t.checkExpect(text.flattenHyperlinks(), new ConsLoContent(text, empty));
  }

  ConsLoContent syllabusAssignmentsContentF = new ConsLoContent(
      new Picture("Java", "HD Java logo", 4),
      new ConsLoContent(new Text("Week 1", 10, true),
          new ConsLoContent(new Picture("Submission", "submission screenshot", 13.7),
              new ConsLoContent(new Text("Pair Programming", 10, false),
                  new ConsLoContent(new Text("Expectations", 15, false),
                      new ConsLoContent(new Picture("Submission", "submission screenshot", 13.7),
                          new MtLoContent()))))));

  // Tests the flattenHyperlinks method in the Webpage class
  boolean testWebpageFlattenHyperlinks(Tester t) {
    return t.checkExpect(hyperLinkAndPicture.flattenHyperlinks(),
        new ConsLoContent(myPic, new ConsLoContent(myPic, new MtLoContent())))
        && t.checkExpect(homepage.flattenHyperlinks(),
            new ConsLoContent(new Text("Course Goals", 5, true),
                new ConsLoContent(new Text("Instructor Contact", 1, false),
                    new ConsLoContent(new Picture("Eclipse", "Eclipse logo", 0.13),
                        new ConsLoContent(
                            new Picture("Coding Background", "digital rain from the Matrix", 30.2),
                            syllabusAssignmentsContentF)))));
  }

  // Tests the flattenHyperlinks method in the ILoContent inteface
  boolean testILoContentFlattenHyperlinks(Tester t) {
    return t.checkExpect(empty.flattenHyperlinks(), empty)
        && t.checkExpect(homepage.content.flattenHyperlinks(),
            new ConsLoContent(new Text("Course Goals", 5, true),
                new ConsLoContent(new Text("Instructor Contact", 1, false),
                    new ConsLoContent(new Picture("Eclipse", "Eclipse logo", 0.13),
                        new ConsLoContent(
                            new Picture("Coding Background", "digital rain from the Matrix", 30.2),
                            syllabusAssignmentsContentF)))));
  }
}
