/**
 * @file   AtroposState.java
 * @author Kyle Burke <paithan@cs.bu.edu>
 * @date   
 * 
 * @brief  This is an empty java file
 * 
 */
//package something;
 
import java.lang.*;
import java.io.*;
import java.util.*;

public class WeiPlayer {
  
  //Declare the variables
  private static final int positiveNumber = 1000; //maximum number possible
  private static final int negativeNumber = -1000; //minimum number possible
  private static final int MAX_DEPTH = 7; //How many levels deep to go on the AB Pruning

  //instance variables

  /**
    * Array of AtroposCircles
    *
    * The first coordinate is the height, the second is the distance from the left.
    */
  private AtroposCircle[][] circles;

  /**
    * Last-colored circle.
    */
  private AtroposCircle lastPlay;
  
  //constants
  
  /**
    * Blank Color.
    */
  private static final int UNCOLORED = 0;

  /**
    * Color Red.
    */
  public static final int RED = 1;
  
  /**
    * Color Blue.
    */
  public static final int BLUE = 2;
  
  /**
    * Color Green.
    */
  public static final int GREEN = 3;
  
  
  //public methods
  
  /**
   * Class constructor.
   *
   * @param circles   The laid-out circles.
   * @param lastPlay  The last play on the board.
   */
  public WeiPlayer(AtroposCircle[][] circles, AtroposCircle lastPlay) {
    this.circles = new AtroposCircle[circles.length][circles.length];
    for (int i = 0; i < circles.length; i++) {
      for (int j = 0; j < circles[i].length; j++) {
        if (circles[i][j] != null) {
          this.circles[i][j] = circles[i][j].clone();
        }
      }
    }
    this.lastPlay = null;
    if (lastPlay != null) {
      this.lastPlay = lastPlay.clone();
    }
  }
  
  /**
   * Constructor by size
   */
  public WeiPlayer(String string) {
    
    //Split the string passed in into tokens
    String delims = "\\[|\\]";
    String[] tokens = string.split(delims);
    
    //determine the size of the matrix
    int size = (tokens.length - 1) / 2 ;
    this.circles = new AtroposCircle[size][size];
    
    for (int i = 0; i < size; i++){
      for( int j = 0; j < tokens[2*i + 1 ].length(); j++){
        String x = tokens[2 * i + 1].substring(j,j+1);
        if( i == (size -1)){
          this.circles[0][j+1] = new AtroposCircle(Integer.parseInt(x),size - 1 - i, j+1,size - j - 1);
        }
        else{
        this.circles[size - 1 - i][j] = new AtroposCircle(Integer.parseInt(x),size - 1 - i,j, tokens[2*i+1].length() - j - 1);
        }
      }     
   }
    
    // determine the last play
    delims = ":";
    String[] player = string.split(delims);
    String play = player[player.length-1];
    
    // if last play is null
    if (play.equals("null")){
      this.lastPlay = null;}
    
    else{
      delims = "\\(|\\)|\\,";
      String[] tokens2 = play.split(delims);
      AtroposCircle lastPlay = new AtroposCircle(Integer.parseInt(tokens2[1]),Integer.parseInt(tokens2[2]),Integer.parseInt(tokens2[3]),Integer.parseInt(tokens2[4]));
      this.lastPlay = lastPlay.clone();
      //this.isLegalPlay(lastPlay))
    }
    int [][] avail = this.availmove(size);
    AtroposCircle bestMove = this.bestmove(avail);
    printString(bestMove);
  }
  
  public String printString (AtroposCircle bestMove){
    int height = bestMove.height();
    int leftdistance = bestMove.leftDistance();
    int rightdistance = bestMove.rightDistance();
    int color = bestMove.getColor();
    String string = "(";
    string = string + Integer.toString(color) + ",";
    string = string + Integer.toString(height) + ",";
    string = string + Integer.toString(leftdistance) + ",";
    string = string + Integer.toString(rightdistance) + ")";
    String bestmove = string;
    System.out.println(bestmove);
    return bestmove;
  }
  
  
  
  // Find all the available moves and put it in a AtroposState circle
  public int [][] availmove(int size) {
    int [][] avail = new int [size][size];
    // check to see if the lastplay is not null
    if (this.lastPlay != null){
      // get the index of the move
      int height = this.lastPlay.height();
      int leftdistance = this.lastPlay.leftDistance();
      // go through every possibility to find the corrdinates
      // above right
      if(this.circles[height+1][leftdistance].getColor() == 0){
        avail[height+1][leftdistance] = 1;}
      else{
        avail[height+1][leftdistance] = 0;}
      // below left
      if(this.circles[height-1][leftdistance].getColor() == 0){
        avail[height-1][leftdistance] = 1;}
      else{
        avail[height-1][leftdistance] = 0;}
      // left
      if(this.circles[height][leftdistance-1].getColor() == 0){
        avail[height][leftdistance-1] = 1;}
      else{
        avail[height][leftdistance-1] = 0;}
      // right
      if(this.circles[height][leftdistance+1].getColor() == 0){
        avail[height][leftdistance+1] = 1;} 
      else{
        avail[height][leftdistance+1] = 0;}
      // above left
      if(this.circles[height+1][leftdistance-1].getColor() == 0){
        avail[height+1][leftdistance-1] = 1;}
      else{
        avail[height+1][leftdistance-1] = 0;}
      // below right
      if(this.circles[height-1][leftdistance+1].getColor() == 0){
        avail[height-1][leftdistance+1] = 1;}
      else{
        avail[height-1][leftdistance+1] = 0;}
      
      // if there is no adjacent circles, choose the next available circle
      if(avail[height+1][leftdistance] == 0 && avail[height-1][leftdistance] == 0 && avail[height][leftdistance-1] == 0 && avail[height][leftdistance+1] == 0 && avail[height+1][leftdistance-1] == 0 && avail[height-1][leftdistance+1] == 0){
        for (int i = 1;i<size - 1;i++){
          for(int j = 1; j < size - i;j++){
            if(this.circles[i][j].getColor() == 0){
              avail[i][j] = 1;}
          }
        }
      }
      return avail;
    }
    // if lastplay is null, do nothing.
    else {
      return avail;}
  }
  
 
  // go through all the available moves and find the best move
  public AtroposCircle bestmove (int [][] avail){
      
      // initialize max to a loss
      int max = negativeNumber;
      int score = max;
      // make a clone of the current circle
      WeiPlayer clone = this.clone();
      // declare new variable to set to (0,0,0,0)
      AtroposCircle bestMove = new AtroposCircle(0,0,0,0);
      
      // Find all available moves based on the last move
      // go through each option
      int size = avail[0].length;
      if (this.lastPlay != null){
      int height = this.lastPlay.height();
      int leftdistance = this.lastPlay.leftDistance();
      int rightdistance = this.lastPlay.rightDistance();
      int c = 0;
      int h = 0;
      int l = 0;
      for(int i = 1; i<= size-1;i++){
        for (int j = 1; j<= size-1;j++){
          //go through each color
          for(int color = 1; color < 4; color++){
            //if coordinates are valid
            if(avail[i][j] == 1){
              if (clone.makePlay(i,j,color) == true && clone.isFinished() == false){
                c = color;
                h = i;
                l = j;
                //System.out.println("test another option in bestMove");
                //System.out.println(clone);
                score = Math.max(score,minMax(clone,MAX_DEPTH,negativeNumber,positiveNumber,size));
                //If a win, set as best move and remove the color
                if(clone.eval(size) == negativeNumber){
                  bestMove = new AtroposCircle(color,i,j,size - i - j);
                  clone.circles[i][j] = new AtroposCircle(0,i,j,size-i-j);
                  clone.lastPlay = this.lastPlay;
                }
                //reset max and bestMove according to highest value
                if (score > max){
                  bestMove = new AtroposCircle(color,i,j,size - i - j);
                  max = score;
                }
              }
              // Reset the board position to 0
              clone.circles[i][j] = new AtroposCircle(0,i,j,size-i-j);
              clone.lastPlay = this.lastPlay;
            }
          }
        }
      }
      if (bestMove.getColor() == 0){
        bestMove = new AtroposCircle(c,h,l,size-h-l);}
      return bestMove;}
      else{
        int height = (int)(Math.random() * (size-3) + 1);
        int leftdistance = (int)(Math.random() * (size-height-1) +1 );
        int color = (int) (Math.random()  * 3 + 1);
        bestMove = new AtroposCircle(color,height,leftdistance,size - height - leftdistance);
        clone.makePlay(bestMove);
          while (clone.isFinished() == true){
          clone.circles[height][leftdistance] = new AtroposCircle(0,height,leftdistance,size-height-leftdistance);
          clone.lastPlay = this.lastPlay;
          height = (int)(Math.random() * (size-3) + 1);
          leftdistance = (int)(Math.random() * (size-height-1) +1 );
          color = (int) (Math.random()  * 3 + 1);
          bestMove = new AtroposCircle(color,height,leftdistance,size - height - leftdistance);
          clone.makePlay(bestMove);
        }
        clone.circles[height][leftdistance] = new AtroposCircle(0,height,leftdistance,size-height-leftdistance);
        clone.lastPlay = this.lastPlay;
        return bestMove;}
  }
  
  //evaluate the AtroposState per move
  public int eval(int size) {
    //if you lose
    if(this.isFinished()){
      return negativeNumber;
    }
    
    //get the height and leftdistance of the last move
    int height = this.lastPlay.height();
    int leftdistance = this.lastPlay.leftDistance();
    int middlecolor = this.circles[height][leftdistance].getColor();
    int leftcolor = this.circles[height][leftdistance-1].getColor();
    int rightcolor = this.circles[height][leftdistance+1].getColor();
    int rightupcolor = this.circles[height+1][leftdistance].getColor();
    int leftupcolor = this.circles[height+1][leftdistance-1].getColor();
    int rightdowncolor = this.circles[height-1][leftdistance+1].getColor();
    int leftdowncolor = this.circles[height-1][leftdistance].getColor();
    
      
    // see how many of the other options are filled. The more that are filled, the higher the score of this move
    //similar approach to finding all options
    int score = 0;
    //Above right
    if(this.circles[height+1][leftdistance].getColor() != 0){
      score += 10;
    }
    //Below left
    if(this.circles[height-1][leftdistance].getColor() != 0){
      score += 10;
    }
    //left
    if(this.circles[height][leftdistance-1].getColor() != 0){
      score += 10;
    }
    //right
    if(this.circles[height][leftdistance+1].getColor() != 0){
      score += 10;
    }
    //above left
    if(this.circles[height+1][leftdistance-1].getColor() != 0){
      score += 10;
    }
    //below right
    if(this.circles[height-1][leftdistance+1].getColor() != 0){
      score += 10;
    }
    
    // if the neighbour has a different color
    if (leftcolor != leftupcolor){
      score += 5;}
    if (leftcolor != leftdowncolor){
      score += 5;}
    if (leftdowncolor != rightdowncolor){
      score += 5;}
    if (rightdowncolor != rightcolor){
      score += 5;}
    if(rightcolor != rightupcolor){
      score += 5;}
    if (rightupcolor != leftupcolor){
      score += 5;}
    
    // if the neighbours has the same color with the current circle
    if (leftupcolor == middlecolor){
      score --;}
    if (leftcolor == middlecolor){
      score -- ;}
    if (leftdowncolor == middlecolor){
      score -- ;}
    if (rightdowncolor == middlecolor){
      score --;}
    if (rightcolor == middlecolor){
      score --;}
    if (rightupcolor == middlecolor){
      score --;}
    
    return score;
  }


  //MinMax method with AB Pruning
  public int minMax(WeiPlayer clone, int depth, int alpha, int beta,int size){
    // if the playerMove is done, one winner has won, or it has reach the max depth
    // then return the evaulation
    //System.out.println("depth");
    //System.out.println(depth);
    if(depth == 0 || clone.eval(size) == negativeNumber){
       //clone.eval(size) == negativeNumber || clone.eval(size) == positiveNumber){
      return clone.eval(size);
    }
    //if it's the AI's turn 
    else if (depth % 2 == 0){
      //System.out.println("AI's move");
      int [][] availboard = clone.availmove(size);
      int score = negativeNumber;
      AtroposCircle lastplay = clone.lastPlay;
      for( int i = 1; i< size -1 ;i++){
        for(int j = 0; j< size; j++){
          for(int color = 1; color < 4; color ++){
            if (availboard[i][j] == 1){
              if (clone.makePlay(i,j,color) == true && clone.isFinished() == false){
                //System.out.println(clone);
                score = Math.max(score,minMax(clone,depth-1,negativeNumber,positiveNumber,size));
                //set alpha
                alpha = Math.max(alpha,score);
                clone.circles[i][j] = new AtroposCircle(0,i,j,size-i-j);
                clone.lastPlay = lastplay;
                //if alpha and beta overlap, then finish the recurrence
                if(beta < alpha){
                  clone.circles[i][j] = new AtroposCircle(0,i,j,size-i-j);
                  clone.lastPlay = lastplay;
                  return alpha;
                }
              }
              clone.circles[i][j] = new AtroposCircle(0,i,j,size-i-j);
              clone.lastPlay = lastplay;
            }
          }
        }
      }
          //System.out.println("alpha");
        return alpha;
    }
    else{
      //System.out.println("player's move");
      int [][] availboard = clone.availmove(size);
      int score = positiveNumber;
      AtroposCircle lastplay = clone.lastPlay;
      for( int i = 1; i< size -1;i++){
        for(int j = 0; j< size; j++){
          for(int color = 1; color < 4; color ++){
            if(availboard[i][j] == 1){
              if (clone.makePlay(i,j,color) == true && clone.isFinished() == false){
                //System.out.println(clone);
                score = Math.min(score,minMax(clone,depth-1,negativeNumber,positiveNumber,size));
                //set alpha
                beta = Math.min(beta,score);
                clone.circles[i][j] = new AtroposCircle(0,i,j,size-i-j);
                clone.lastPlay = lastplay;
                //if alpha and beta overlap, then finish the recurrence
                if(beta < alpha){
                  clone.circles[i][j] = new AtroposCircle(0,i,j,size-i-j);
                  clone.lastPlay = lastplay;
                  return beta;
                }
              }
              clone.circles[i][j] = new AtroposCircle(0,i,j,size-i-j);
              clone.lastPlay = lastplay;
            }
          }
        }
      }
       return beta;
     }
  }
                  

      
      

  /**
   * Makes a deep clone of this object.
   */
  public WeiPlayer clone() {
    WeiPlayer clone;
    if (this.lastPlay != null) {
      clone = new WeiPlayer(this.circles, this.lastPlay.clone());
    } else {
      clone = new WeiPlayer(this.circles, null);
    }
    return clone;
  }
  
  /**
   * Checks to see whether a circle is a valid move.
   *
   */
  public boolean isLegalPlay(AtroposCircle play) {
    //make sure the color is legal
    int color = play.getColor();
    if (color < 1 || color > 3) {
      return false;
    }
    //check that the dimensions add up
    return this.isLegalPlayLocation(play.height(), play.leftDistance());
  }
  
  /**
   * Performs a move on the board.
   *
   * @param play  Next move to make.
   */
  public boolean makePlay(AtroposCircle play) {
    return this.makePlay(play.height(), play.leftDistance(), play.getColor());
  }
  
  /**
   * Performs a move on the board.
   *
   * @param height        Height of the circle to play.
   * @param leftDistance  Distance of the circle from the left.
   * @param color         Color to play.
   */
  public boolean makePlay(int height, int leftDistance, int color) {
    if (this.isFinished()) {
      return false;
    }
    /*
    if (!this.isLegalPlayLocation(height, leftDistance)) {
      System.err.println("This is not a legal move!");
      return false;
    }
    */
    this.colorCircle(height, leftDistance, color);
    this.lastPlay = this.circleAt(height, leftDistance);
    return true;
  }
  
  /**
   * Determines whether the game is over.
   */
  public boolean isFinished() {
    if (this.lastPlay == null) {
      return false;
    }
    //System.out.println("start checking if is finished");
    int middleColor = this.lastPlay.getColor();
    //System.out.println(middleColor);
    int height = this.lastPlay.height();
    int leftDistance = this.lastPlay.leftDistance();
    int leftUpColor = this.circles[height + 1][leftDistance - 1].getColor();
    //System.out.println(leftUpColor);
    int leftColor = this.circles[height][leftDistance - 1].getColor();
    //System.out.println(leftColor);
    int leftDownColor = this.circles[height - 1][leftDistance].getColor();
    //System.out.println(leftDownColor);
    int rightDownColor = this.circles[height - 1][leftDistance + 1].getColor();
    //System.out.println(rightDownColor);
    int rightColor = this.circles[height][leftDistance + 1].getColor();
    //System.out.println(rightColor);
    int rightUpColor = this.circles[height + 1][leftDistance].getColor();
    //System.out.println(rightUpColor);
    
    return ((this.colorConflict(middleColor, leftUpColor) &&
              this.colorConflict(middleColor, leftColor) &&
              this.colorConflict(leftUpColor, leftColor)) ||
            (this.colorConflict(middleColor, leftColor) &&
              this.colorConflict(middleColor, leftDownColor) &&
              this.colorConflict(leftColor, leftDownColor)) ||
            (this.colorConflict(middleColor, leftDownColor) &&
              this.colorConflict(middleColor, rightDownColor) &&
              this.colorConflict(leftDownColor, rightDownColor)) ||
            (this.colorConflict(middleColor, rightDownColor) &&
              this.colorConflict(middleColor, rightColor) &&
              this.colorConflict(rightDownColor, rightColor)) ||
            (this.colorConflict(middleColor, rightColor) &&
              this.colorConflict(middleColor, rightUpColor) &&
              this.colorConflict(rightColor, rightUpColor)) ||
            (this.colorConflict(middleColor, rightUpColor) &&
              this.colorConflict(middleColor, leftUpColor) &&
              this.colorConflict(rightUpColor, leftUpColor)));
  }
  
  /**
   * Options for the next play
   */
  public Iterator<AtroposCircle> playableCircles() {
    Vector<AtroposCircle> vector = new Vector<AtroposCircle>();
    for (int height = 1; height < this.circles.length; height++) {
      for (int leftDistance = 1; 
           leftDistance < this.circles.length - height; 
           leftDistance++) {
        if (this.isLegalPlayLocation(height, leftDistance)) {
          vector.add(this.circleAt(height, leftDistance));
        }
      }
    }
    return vector.iterator();
  }
  
  
  //private methods
  
  
  /**
   * Checks to see whether a location is a valid place to make the next move.
   *
   */
  private boolean isLegalPlayLocation(int height, int leftDistance) {
    if (this.isFinished()) {
      System.out.println("isfinished");
      return false;
    }
    AtroposCircle circle = this.circleAt(height, leftDistance);
    if (!circle.insideBoardOfSize(this.circles.length)) {
      System.out.println(this.circles.length);
      System.out.println("outofboardofsize");
      return false;
    }
    if (circle.isColored()) {
      System.out.println("iscolored");
      return false;
    }
    if (this.canPlayAnywhere()) {
      return true;
    } else {
      return circle.adjacentTo(this.lastPlay);
    }
  }
  
  /**
   * Checks to see if the next play can be anywhere.
   */
  private boolean canPlayAnywhere() {
    if (this.isFinished()) {
      return false;
    }
    if (this.lastPlay == null) {
      return true;
    }
    int height = this.lastPlay.height();
    int leftDistance = this.lastPlay.leftDistance();
    int rightDistance = this.lastPlay.rightDistance();
    return ((this.circles[height - 1]
                         [leftDistance].isColored()) &&
            (this.circles[height - 1]
                         [leftDistance + 1].isColored()) &&
            (this.circles[height]
                         [leftDistance + 1].isColored()) &&
            (this.circles[height + 1]
                         [leftDistance].isColored()) &&
            (this.circles[height + 1]
                         [leftDistance - 1].isColored()) &&
            (this.circles[height]
                         [leftDistance - 1].isColored()));
  }
  
  /** Determines whether the two colors are not equal if both are colored. */
  private boolean colorConflict(int colorOne, int colorTwo) {
    return ((colorOne != this.UNCOLORED) &&
            (colorTwo != this.UNCOLORED) &&
            (colorOne != colorTwo));
  }
  
  /**
   * Finds the circle at a certain location.
   */
  private AtroposCircle circleAt(int height, int leftDistance) {
    return this.circles[height][leftDistance];
  }
  
  /**
   * Determines whether a circle is colored.
   *
   * @param height        Height of the circle.
   * @param leftDistance  Distance of the circle from the left.
   */
  private boolean circleIsColored(int height, int leftDistance) {
    return this.circles[height][leftDistance].isColored();
  }
  
  /**
   * Colors a given circle.
   *
   * @param height        Height of the circle.
   * @param leftDistance  Distance of the circle from the left.
   * @param color         New color for the circle.
   */
  private void colorCircle(int height, int leftDistance, int color) {
    if (this.circles[height][leftDistance].isColored()) {
      System.out.println(height);
      System.out.println(leftDistance);
      System.err.println("Error!  This circle is already colored!");
      return;
    }
    if (color < 0 || color > 3) {
      System.err.println("Error!  This is not a legal color!");
    }
    this.circles[height][leftDistance].color(color);
  }
  
  
  //toString
  
  /**
   * Returns a string version of this.
   *
   * @param indent  Indentation string.
   */
  public String toString(String indent){
    String string = "";
    for (int i = this.circles.length - 1; i >=0 ; i --) {
      //set up some nice spacing.
      for (int space = 0; space < 2*(i - 1); space++) {
        string += " ";
      }
      if (i == 0) {
        string += "  ";
      }
      string +="[";
      for (int j = 0; j < this.circles.length; j++) {
        if (this.circles[i][j] != null) {
          string += this.circles[i][j].getColor();
          if (j + i < this.circles.length) {
            if (i != 0 || j != this.circles.length - 1) {
              string += "   ";
            }
          }
        }
      }
      string += "]\n";
    }
    string += "Last Play: ";
    if (this.lastPlay == null) {
      string += "null";
    } else {
 string += this.lastPlay.getColorLocationString();
    }
    string += "\n";
    return string;
  }
  
  /**
   * Returns a string version of this.
   */
  public String toString() {
    return this.toString("");
  }
   
  /**
   * Main method for testing.
   */
  public static void main(String[] args) { 
    WeiPlayer atropos = new WeiPlayer(args[0]);
    /*
    String string = args[0];
    String delims = "\\[|\\]";
    String[] tokens = string.split(delims);
    int size = (tokens.length - 1) / 2 ;
    
    int [][] avail = atropos.availmove(7);
    
    AtroposCircle bestMove = atropos.bestmove(avail);
    
    atropos.makePlay(bestMove);
    System.out.println(atropos);
    */
  }
   
   
   
   
   
   
} //end of AtroposState.java
