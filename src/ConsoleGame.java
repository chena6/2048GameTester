
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Marko Laban, Arthur Chen, Karim Guirguis
 * Project initially by: Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class ConsoleGame {

    /**
     * Main function of the game.
     * 
     * @param args
     * @throws CloneNotSupportedException 
     */
    public static void main(String[] args) throws CloneNotSupportedException {
        
        System.out.println("The 2048 Game in JAVA!");
        System.out.println("======================");
        System.out.println();
        
        Scanner in = new Scanner(System.in);
        
        while(true) {
            printMenu();
            int choice;
            try {
                choice = in.nextInt();
         
                // Choices for the user to select
                switch (choice) { 		
                    case 1:  calculateAccuracy();	//meant to initiate the AI to try and solve 10 games
                             break;
                    case 2:  changeVar(in);
                    		 break;
                    case 3: System.out.println("Bye");
                    		return;	//exit
                    default: throw new Exception();
                }
            }
            //if user enters a number that is not 1-3
            catch(Exception e) {
                System.out.println(e);
            }
        }
    }
    
    
    /**
     * Prints main menu
     */
    public static void printMenu() {
        System.out.println();	//print empty line
        System.out.println("Choices:");	
        System.out.println("1. Estimate the Accuracy of AI Solver");
        System.out.println("2. Change numbers");
        System.out.println("3. Quit");
        System.out.println();
        System.out.println("Enter numbers 1-3:");
    }
    
    public static void changeVar(Scanner in) throws FileNotFoundException{
    	System.out.print("\n\n Enter new depth (negative number to not change): ");
    	DataModel.setDepth(in.nextInt());
    	
    	System.out.print("\n Enter new number of games (negative number to not change): ");
    	DataModel.setNumOfGames(in.nextInt());
    	
    	System.out.print("\n Enter new max cell (negative number to not change): ");
    	DataModel.setMaxCell(in.nextInt());
    }
    
    /**
     * Estimates the accuracy of the AI solver by running multiple games.
     * 
     * @throws CloneNotSupportedException 
     * @throws FileNotFoundException 
     */
    public static void calculateAccuracy() throws CloneNotSupportedException, FileNotFoundException {
        int wins=0;
        int total = DataModel.getNumOfGames();
        double sum=0;
        double temp=0;
        double score=0;
        System.out.println("Running "+total+" games to estimate the accuracy:");
        Stopwatch timer;	
        ArrayList<Double> Numbers = new ArrayList<Double>();
    
        for(int i=0;i<total;++i) {
        	timer = new Stopwatch(); //initializes the stopwatch
            int hintDepth = DataModel.getDepth(); //declare the depth which the minimax algorithm will use
            Board theGame = new Board(DataModel.getMaxCell());	//initializes a new board object
            Direction hint = AIsolver.findBestMove(theGame, hintDepth);	//uses the class Direction to decide which is the best move
            ActionStatus result=ActionStatus.CONTINUE;	//checks with enum if it can continue 
            while(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE) {	//if it can continue or it is currently in the state of invalid move
                result=theGame.action(hint);	
               
                if(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE ) {
                    hint = AIsolver.findBestMove(theGame, hintDepth);	//finds the best move if any of the above conditions are met
                }
            }
            Numbers.add(timer.elapsedTime());	//appends the elapsed time to the array list
            if(result == ActionStatus.WIN) {	//if won
                ++wins;	//increase win counter
                System.out.println();	//print empty line
                System.out.println("Game "+(i+1)+" - won" + " in " + timer.elapsedTime() + " Seconds" +  " Score = " + theGame.getScore());	//prints result
                System.out.println("Over the min score by: "+ (theGame.getScore() - theGame.getMinimumScore()));
            }
            else { //if lost
            	System.out.println();	//print empty line
                System.out.println("Game "+(i+1)+" - lost" + " in " + timer.elapsedTime() + " Seconds" +   " Score = " + theGame.getScore());	//prints result
                System.out.println("Under the min score by: "+ (theGame.getMinimumScore() - theGame.getScore()));
            }
            score+= theGame.getScore(); 
        }
        
        for(int i =0; i< Numbers.size(); i++){	//interate through the arraylist
        	sum += Numbers.get(i);	//sum of all entries in the arraylist
        	}
        temp = temp + Math.round((((double)wins/total)*100.0) * 100.0)/100.0;
        System.out.println();	//print empty line
        System.out.println(wins+" wins out of "+total+" games.");	
        System.out.println();	//print empty line
        System.out.println("completed " + total + " games in an avg time of:" + " " + Math.round(sum/Numbers.size() *100.0)/100.0 + " Seconds");
        System.out.println();	//print empty line
        System.out.println("completed " + total + " games with an avg score of:" + " " + Math.round(score/(double) total  *100.0)/100.0);
        System.out.println();
        System.out.println("completed " + total + " games with a success rate of: " + Math.round((((double)wins/total)*100.0) * 100.0)/100.0 + "%");
        System.out.println();
        System.out.println();
        wins=0;
    }
}
