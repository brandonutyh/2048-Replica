import java.util.*;
import java.io.*;
/* Allows you to use out.println() or out.print() instead of System.out.println() or System.out.print() */
import static java.lang.System.*;


public class Game
{
  // declare instance variables
  private File bestScoresFile;
  private final char EMDASH = 8212;
  private final char VERTLINE = 9474;
  private String name;
  private String bestPlayer;
  private int score;
  private String input;
  private Scanner sc = new Scanner(System.in);
  private Scanner fileScanner;
  private int [][] board, prev;
  private boolean gameOver;
  private boolean hasMove, testMode;
  private ArrayList<Integer> available;
  private int highest;
  private int bestScore;
  private FileWriter outputFile;
  private boolean hasT;
  private Map<Integer,Boolean> mergeMap;
  private boolean ask;


  
  // constructor
  public Game(File f) throws IOException
  {
    bestScoresFile = f;
    // add code below
    score=0;
    board= new int[4][4];
    prev=new int[4][4];
    gameOver=false;
    hasMove=false;
    fileScanner = new Scanner(f);
    bestPlayer = fileScanner.nextLine();
    bestScore = fileScanner.nextInt();
    available= new ArrayList<Integer>();
    for(int i=0;i<4;i++){
      for(int j=0;j<4;j++){
        available.add((i*10)+j);
      }
    }
    hasT=false;
    testMode=false;
    ask=false;
    mergeMap=new HashMap<Integer, Boolean>();
    System.out.println("Please input your name: ");
    name=sc.nextLine();
    intro();

  }

  //intro to game and instructions
  public void intro ()throws IOException{
    System.out.println("Do you want to enter the debug mode? Type y for yes and n for no");
    String i=sc.nextLine();
    while(!i.equals("y")&&!i.equals("n")){
      System.out.println("Please input y or n: ");
      i=sc.next();
    }
    if(i.equals("y")){
      testMode=true;
    }
    System.out.println("Combine tiles of the same number to make a number of a higher power of 2. The goal is to create a 2048 tile.");
    System.out.println("WASD Controls: ");
    System.out.println("Move Up: W + ENTER \nMove Right: A + ENTER \nMove Down: S + ENTER \nMove Left: D + ENTER \nTo quit game: q + ENTER \nTo enter debug mode: debug + ENTER \n");
    System.out.println("Highest score owned by: " + bestPlayer);
    System.out.println("Highest Score: " + bestScore + "\n");
    play();
  }

  //controls the turn by turn gameplay 
 public void play()throws IOException{
    
    if(testMode){
      for(int i=0; i<4; i++){
        for(int j=0;j<4;j++){
          System.out.println("Input a number: ");
        String t=sc.next();
        board[i][j]=validateT(t);
        }
      }
      for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
          if(board[i][j]!=0){
          available.remove(available.indexOf((i*10)+j));
        }
        }
      }
    }
   if(!testMode){
      spawn();
    spawn();
   }
    printBoard();
    while(!gameOver){
      validate("Enter a move, type q to quit, type u to undo, or type debug to enter debug mode: ");
      System.out.println();
      if(input.equals("q")){
        break;
      }
      if(input.equals("debug")){
        System.out.println("You have entered the debug mode");
        for(int i=0; i<4; i++){
        for(int j=0;j<4;j++){
          System.out.println("Input a number: ");
        String t=sc.next();
        board[i][j]=validateT(t);
        }
      }
      available= new ArrayList<Integer>();
      for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
          if(board[i][j]==0){
          available.add(((i*10)+j));
        }
        }
      }
      }
      if(input.equals("u")){
        for(int r=0;r<4;r++){
        for(int c=0;c<4;c++){
          board[r][c]=prev[r][c];
        }
      }
         available= new ArrayList<Integer>();
        for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
          if(prev[i][j]==0){
            available.add((i*10)+j);
          }
        }
      }
      }
      for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
          prev[i][j]=board[i][j];
        }
      }
      
      for(int i=0;i<3;i++){
        move(input);
      }
      
      merge(input);
      move(input);
      move(input);
      if(hasMove){
        spawn();
      }
      
      printBoard();
      hasMove=false;
      if(isFull()&&!hasMerge()){
        break;
      }
      if(ask){
          gameOver=cont();
          hasT=true;
        ask=false;
      }
      mergeMap=new HashMap<Integer,Boolean>();
    }
   
  outputFile = new FileWriter("bestScore.dat");
  outputFile.append(bestPlayer + "\n" + bestScore);
  outputFile.flush();
  outputFile.close();
   
   if(gameOver){
     System.out.println("You won");
   }
   playAgain();
  }

  //moves tiles based on the input direction
  public void move(String s){
    if(s.equals("w")){
      for(int i=1;i<4;i++){
        for(int j=0;j<4;j++){
          if(board[i][j]!=0){
            if(board[i-1][j]==0){
            board[i-1][j]=board[i][j];
            board[i][j]=0;
            hasMove=true;
            available.add((i*10)+j);
            available.remove(available.indexOf(((i-1)*10)+j));
          }
          }
        }
      }
    }
    if(s.equals("a")){
      for(int i=0;i<4;i++){
        for(int j=1;j<4;j++){
          if(board[i][j]!=0){
            if(board[i][j-1]==0){
            board[i][j-1]=board[i][j];
            board[i][j]=0;
            hasMove=true;
            available.add((i*10)+j);
            available.remove(available.indexOf(((i)*10)+(j-1)));
          }
          }
        }
      }
    }
    if(s.equals("s")){
      for(int i=0;i<3;i++){
        for(int j=0;j<4;j++){
          if(board[i][j]!=0){
            if(board[i+1][j]==0){
            board[i+1][j]=board[i][j];
            board[i][j]=0;
            hasMove=true;
            available.add((i*10)+j);
            available.remove(available.indexOf(((i+1)*10)+j));
          }
          }  
        }
      }
    }
    if(s.equals("d")){
      for(int i=0;i<4;i++){
        for(int j=0;j<3;j++){
          if(board[i][j]!=0){
            if(board[i][j+1]==0){
            board[i][j+1]=board[i][j];
            board[i][j]=0;
            hasMove=true;
            available.add((i*10)+j);
            available.remove(available.indexOf(((i)*10)+(j+1)));
          }
          }
          
        }
      }
    }
  }

  //merges like tiles in the direction specified
public void merge(String s){
  if(s.equals("w")){
    for(int i=1;i<4;i++){
        for(int j=0;j<4;j++){
          if(board[i][j]!=0){
            if(board[i-1][j]==board[i][j]){
              board[i-1][j]=board[i][j]*2;
              board[i][j]=0;
              available.add((i*10)+j);
              highest = board[i-1][j];
              score+=highest;
              hasMove=true;
                if(bestScore<=score){
                     bestScore=score;
                     bestPlayer=name;
                }
                  if(!hasT){
                    if(highest>=2048){
                    ask=true;
                  }
                    
                  }
                  
              }
            }
          }
        }
      } 
  
  if(s.equals("a")){
    for(int i=0;i<4;i++){
     for(int j=1;j<4;j++){
      if(board[i][j]!=0){
        if(board[i][j-1]==board[i][j]){
              board[i][j-1]=board[i][j]*2;
              board[i][j]=0;
              available.add((i*10)+j);
              highest = board[i][j-1];
              score+=highest;
              hasMove=true;
              if(bestScore<=score){
                  bestScore=score;
                  bestPlayer=name;
              }
                if(!hasT){
                    if(highest>=2048){
                      ask=true;
                  }
                  }
            }
      }
     }
    }
  }
  if(s.equals("s")){
  for(int i=2;i>-1;i--){
     for(int j=0;j<4;j++){
      if(board[i][j]!=0){
        if(board[i+1][j]==board[i][j]){
              if(mergeMap.putIfAbsent(((i+1)*10)+j,false)==null){
                board[i+1][j]=board[i][j]*2;
              board[i][j]=0;
              available.add((i*10)+j);
              highest = board[i+1][j];
              score+=highest;
              hasMove=true;
               if(bestScore<=score){
                   bestScore=score;
                   bestPlayer=name;
               }                
              if(!hasT){
                    if(highest>=2048){
                      ask=true;
                  }
                }
              }
            mergeMap.put(((i*10)+j),false);
            }
        }
       }
      }
    }
  
   if(s.equals("d")){
      for(int i=0;i<4;i++){
        for(int j=2;j>-1;j--){
          if(board[i][j]!=0){
            if(board[i][j+1]==board[i][j]){
              if(mergeMap.putIfAbsent((i*10)+(j+1),false)==null){
                board[i][j+1]=board[i][j]*2;
              board[i][j]=0;
              available.add((i*10)+j);
              highest = board[i][j+1];
              score+=highest;
              hasMove=true;
               if(bestScore<=score){
                   bestScore=score;
                   bestPlayer=name;
               }                
              if(!hasT){
                    if(highest>=2048){
                      ask=true;
                  }
                }
              }
            mergeMap.put(((i*10)+j+1),false);
            }
            }
          }
        }
     }
}

  //randomly spawns a 2 or 4 at an available spot in the grid
public void spawn(){
  int space=new Random().nextInt(available.size());
  int row=available.get(space)/10;
  int col=available.get(space)%10;
  int rand= new Random().nextInt(10);
  if(rand==0){
    board[row][col]=4;
  }
  else{
    board[row][col]=2;
  }
  available.remove(space);
}

  //asks the player if they want to play again and resets all the variables if they do
public void playAgain()throws IOException{
  System.out.println("Do you want to play again? Type y for yes or n for no.");
  String ans=sc.nextLine();
    while(!ans.equals("y")&&!ans.equals("n")){
      System.out.println("Please input y or n: ");
      ans=sc.next();
    }
    if(ans.equals("y")){
      score=0;
    board= new int[4][4];
    gameOver=false;
    hasMove=false;
    prev=new int[4][4];
    available= new ArrayList<Integer>();
    for(int i=0;i<4;i++){
      for(int j=0;j<4;j++){
        available.add((i*10)+j);
      }
    }
    hasT=false;
    ask=false;
    testMode=false;
    mergeMap=new HashMap<Integer, Boolean>();
    
    intro();
    }
  else{
    System.out.println("Good Game!");
  }
}

  //makes sure input is a move,q,u, or debug and assigns the variable input to that
public void validate(String s){
    System.out.print(s);
     input = sc.next().toLowerCase(); 
    if(!input.equals("w")&&!input.equals("a")&&!input.equals("s")&&!input.equals("d")&&!input.equals("q")&&!input.equals("debug")&&!input.equals("u")){
      validate("Please enter w, a, s, d, q, or debug: ");
    }
  }

  //makes sure that the input is positive and a number
public int validateT(String t){
  boolean flag=false;
  while(!flag){
    try{
            
             Integer.parseInt(t);
          if(Integer.parseInt(t)>=0){
           flag=true;
            return Integer.parseInt(t); 
          }
      else{
        System.out.println("Please input a positive number: ");
        t=sc.next();
      }
           
             
        }catch (NumberFormatException ex){
            System.out.println("Please input a number: ");
            t=sc.next();
            flag=false;
        }
  }
  return 0;
}

  //prints board in 4x4 grid
public void printBoard(){
  printScore();
  System.out.println();
  for(int i=0;i<25;i++){
   System.out.print(EMDASH); 
  }
  System.out.println();
  printW();
  System.out.print(VERTLINE);
  for(int j=0;j<4;j++){
        if(String.valueOf(board[3][j]).length()==5){
      System.out.println(board[3][j]+VERTLINE);
    }
    else if(String.valueOf(board[0][j]).length()==4){
       System.out.print(" "+board[0][j]+VERTLINE);
    }
    else if(String.valueOf(board[0][j]).length()==3){
      System.out.print(" "+board[0][j]+" "+VERTLINE);
    }
    else if(String.valueOf(board[0][j]).length()==2){
      System.out.print(" "+board[0][j]+"  "+VERTLINE);
    }
    else if(String.valueOf(board[0][j]).length()==1){
      if(board[0][j]!=0){
       System.out.print("  "+board[0][j]+"  "+VERTLINE);   
      }
      else{
        System.out.print("     "+VERTLINE);
      }
    }
  }
  System.out.println();
  printW();
  for(int i=0;i<25;i++){
   System.out.print(EMDASH); 
  }
  System.out.println();
  printW();
  System.out.print(VERTLINE);
  for(int j=0;j<4;j++){
    if(String.valueOf(board[3][j]).length()==5){
      System.out.println(board[3][j]+VERTLINE);
    }
    else if(String.valueOf(board[1][j]).length()==4){
       System.out.print(" "+board[1][j]+VERTLINE);
    }
    else if(String.valueOf(board[1][j]).length()==3){
      System.out.print(" "+board[1][j]+" "+VERTLINE);
    }
    else if(String.valueOf(board[1][j]).length()==2){
      System.out.print(" "+board[1][j]+"  "+VERTLINE);
    }
    else if(String.valueOf(board[1][j]).length()==1){
      if(board[1][j]!=0){
        System.out.print("  "+board[1][j]+"  "+VERTLINE);
      }
      else{
        System.out.print("     "+VERTLINE);
      }
    }
  }
  System.out.println();
  printW();
  for(int i=0;i<25;i++){
   System.out.print(EMDASH); 
  }
  System.out.println();
  printW();
  System.out.print(VERTLINE);
  for(int j=0;j<4;j++){
    if(String.valueOf(board[3][j]).length()==5){
      System.out.println(board[3][j]+VERTLINE);
    }
    else if(String.valueOf(board[2][j]).length()==4){
       System.out.print(" "+board[2][j]+VERTLINE);
    }
    else if(String.valueOf(board[2][j]).length()==3){
      System.out.print(" "+board[2][j]+" "+VERTLINE);
    }
    else if(String.valueOf(board[2][j]).length()==2){
      System.out.print(" "+board[2][j]+"  "+VERTLINE);
    }
    else if(String.valueOf(board[2][j]).length()==1){
      if(board[2][j]!=0){
        System.out.print("  "+board[2][j]+"  "+VERTLINE);
      }
      else{
        System.out.print("     "+VERTLINE);
      }
    }
  }
  System.out.println();
  printW();
  for(int i=0;i<25;i++){
   System.out.print(EMDASH); 
  }
  System.out.println();
  printW();
  System.out.print(VERTLINE);
  for(int j=0;j<4;j++){
    if(String.valueOf(board[3][j]).length()==5){
      System.out.println(board[3][j]+VERTLINE);
    }
    else if(String.valueOf(board[3][j]).length()==4){
       System.out.print(" "+board[3][j]+VERTLINE);
    }
    else if(String.valueOf(board[3][j]).length()==3){
      System.out.print(" "+board[3][j]+" "+VERTLINE);
    }
    else if(String.valueOf(board[3][j]).length()==2){
      System.out.print(" "+board[3][j]+"  "+VERTLINE);
    }
    else if(String.valueOf(board[3][j]).length()==1){
      if(board[3][j]!=0){
        System.out.print("  "+board[3][j]+"  "+VERTLINE);
      }
      else{
        System.out.print("     "+VERTLINE);
      }
    }
  }
  System.out.println();
  printW();
  for(int i=0;i<25;i++){
   System.out.print(EMDASH); 
  }
  System.out.println();
}

  //prints current score and best score in two boxes
public void printScore(){
  for(int i=0; i<10; i++){
   System.out.print(EMDASH);
  }
  System.out.print("     ");
   for(int i=0; i<10; i++){
    System.out.print(EMDASH);
  }
  System.out.println();
  out.print(VERTLINE+" SCORE  "+VERTLINE);
  out.print("     ");
  out.println(VERTLINE+"  BEST  "+VERTLINE);
  out.print(VERTLINE);
  if(score>=10000){
    out.print(" "+score+"  "+VERTLINE);
  }
  else if(score>=1000){
    out.print("  "+score+"  "+VERTLINE);
  }
  else if(score>=100){
    out.print("  "+score+"   "+VERTLINE);
  }
  else if(score>=10){
    out.print("   "+score+"   "+VERTLINE);
  }
  else{
    out.print("   "+score+"    "+VERTLINE);
  }
  out.print("     "+VERTLINE);
  if(bestScore>=10000){
    out.print(" "+bestScore+"  "+VERTLINE);
  }
  else if(bestScore>=1000){
    out.print("  "+bestScore+"  "+VERTLINE);
  }
  else if(bestScore>=100){
    out.print("  "+bestScore+"   "+VERTLINE);
  }
  else if(bestScore>=10){
    out.print("   "+bestScore+"   "+VERTLINE);
  }
  else{
    out.print("   "+bestScore+"    "+VERTLINE);
  }
  out.println();
  for(int i=0; i<10; i++){
   System.out.print(EMDASH);
  }
  System.out.print("     ");
   for(int i=0; i<10; i++){
    System.out.print(EMDASH);
  }
}

  //helper method to print white space used above and below the number
public void printW(){
  System.out.print(VERTLINE);
  for(int i=0;i<5;i++){
    System.out.print(" ");
  }
  System.out.print(VERTLINE);
  for(int i=0;i<5;i++){
    System.out.print(" ");
  }
  System.out.print(VERTLINE);
  for(int i=0;i<5;i++){
    System.out.print(" ");
  }
  System.out.print(VERTLINE);
  for(int i=0;i<5;i++){
    System.out.print(" ");
  }
  System.out.println(VERTLINE);
}

  //checks if the board has any available merges
public boolean hasMerge(){
  boolean flag=false;
  for(int i=1;i<4;i++){
        for(int j=0;j<4;j++){
          if(board[i][j]!=0){
            if(board[i-1][j]==board[i][j]){
              flag=true;
            }
            }
          }
      }
  for(int i=0;i<4;i++){
     for(int j=1;j<4;j++){
      if(board[i][j]!=0){
        if(board[i][j-1]==board[i][j]){
          flag=true;
        }
     }
  }
 }
  return flag;
}

  //checks if the board is full
public boolean isFull(){
  boolean flag=true;
  for(int i=0;i<4;i++){
    for(int j=0;j<4;j++){
      if(board[i][j]==0){
        flag=false;
      }
    }
  }
  return flag;
}

  //asks if the player wants to continue
public boolean cont(){
  System.out.println("Do you want to continue? Enter y for yes and n for no");
  String i=sc.nextLine();
    while(!i.equals("y")&&!i.equals("n")){
      System.out.println("Please input y or n: ");
      i=sc.next();
    }
    if(i.equals("n")){
      return true;
    }
  else {
    return false;
  }
}
}