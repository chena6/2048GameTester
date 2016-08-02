/* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The AIsolver class that uses Artificial Intelligence to estimate the next move.
 * 
 * 
 */
public class AIsolver {
    
    /**
     * Player vs Computer enum class
     */
    public enum Player {
        /**
         * Computer
         */
        COMPUTER, 

        /**
         * User
         */
        USER
    }
    
    /**
     * Method that finds the best next move.
     * 
     * @param theBoard
     * @param depth
     * @return
     * @throws CloneNotSupportedException 
     */
    public static Direction findBestMove(Board theBoard, int depth) throws CloneNotSupportedException {
        //Map<String, Object> result = minimax(theBoard, depth, Player.USER);
        
        Map<String, Object> result = alphabeta(theBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, Player.USER);
        
        return (Direction)result.get("Direction");
    }
    
    /**
     * Finds the best move by using the Alpha-Beta pruning algorithm.
     * 
     * @param theBoard
     * @param depth
     * @param alpha
     * @param beta
     * @param player
     * @return
     * @throws CloneNotSupportedException 
     */
    private static Map<String, Object> alphabeta(Board theBoard, int depth, int alpha, int beta, Player player) throws CloneNotSupportedException {
        Map<String, Object> result = new HashMap<>();
        
        Direction bestDirection = null;
        int bestScore;
        
        if(theBoard.isGameTerminated()) {
            if(theBoard.hasWon()) {
                bestScore=Integer.MAX_VALUE; //highest possible score
            }
            else {
                bestScore=Math.min(theBoard.getScore(), 1); //lowest possible score
            }
        }
        else if(depth==0) {
            bestScore=heuristicScore(theBoard.getScore(),theBoard.getNumberOfEmptyCells(),calculateClusteringScore(theBoard.getBoardArray()));  //highest heuristic score when depth is 0
        }
        else {
            if(player == Player.USER) {
                for(Direction direction : Direction.values()) {       
                    Board newBoard = (Board) theBoard.clone();       //creates new board

                    int points=newBoard.move(direction);            //moves board in direction
                    
                    if(points==0 && newBoard.isEqual(theBoard.getBoardArray(), newBoard.getBoardArray())) {  //checks if new board is equal to old board
                    	continue;        
                    }
                    
                    Map<String, Object> currentResult = alphabeta(newBoard, depth-1, alpha, beta, Player.COMPUTER);   //uses alpha beta pruning to get best score
                    int currentScore=((Number)currentResult.get("Score")).intValue();
                                        
                    if(currentScore>alpha) { //maximize score
                        alpha=currentScore;          
                        bestDirection=direction;    //best move   
                    }
                    
                    if(beta<=alpha) {
                        break; //beta cutoff
                    }
                }
                
                bestScore = alpha;
            }
            else {                  //same as minimax algorithm code
                List<Integer> moves = theBoard.getEmptyCellIds();
                int[] possibleValues = {2, 4};

                int i,j;
                abloop: for(Integer cellId : moves) {
                    i = cellId/Board.BOARD_SIZE;
                    j = cellId%Board.BOARD_SIZE;

                    for(int value : possibleValues) {
                        Board newBoard = (Board) theBoard.clone();
                        newBoard.setEmptyCell(i, j, value);

                        Map<String, Object> currentResult = alphabeta(newBoard, depth-1, alpha, beta, Player.USER);
                        int currentScore=((Number)currentResult.get("Score")).intValue();
                        if(currentScore<beta) { //minimize best score
                            beta=currentScore;
                        }
                        
                        if(beta<=alpha) {
                            break abloop; //alpha cutoff
                        }
                    }
                }
                
                bestScore = beta;
                
                if(moves.isEmpty()) {
                    bestScore=0;
                }
            }
        }
        
        result.put("Score", bestScore);                  //final score
        result.put("Direction", bestDirection);          //final move      
        
        return result;
    }
    
    /**
     * Estimates a heuristic score by taking into account the real score, the
     * number of empty cells and the clustering score of the board.
     * 
     * @param actualScore
     * @param numberOfEmptyCells
     * @param clusteringScore
     * @return 
     */
    private static int heuristicScore(int actualScore, int numberOfEmptyCells, int clusteringScore) {
        int score = (int) (actualScore+Math.log(actualScore)*numberOfEmptyCells -clusteringScore);
        return Math.max(score, Math.min(actualScore, 1));
    }
    
    /**
     * Calculates a heuristic variance-like score that measures how clustered the
     * board is.
     * 
     * @param boardArray
     * @return 
     */
    private static int calculateClusteringScore(int[][] boardArray) {
        int clusteringScore=0;
        
        int[] neighbors = {-1,0,1};
        
        for(int i=0;i<boardArray.length;++i) {            //iterates through the board
            for(int j=0;j<boardArray.length;++j) {
                if(boardArray[i][j]==0) {
                    continue; //ignore empty cells
                }
                
                //clusteringScore-=boardArray[i][j];
                
                //for every pixel find the distance from each neightbors
                int numOfNeighbors=0; 
                int sum=0;
                for(int k : neighbors) {
                    int x=i+k;
                    if(x<0 || x>=boardArray.length) { //looks through board rows
                        continue;
                    }
                    for(int l : neighbors) {
                        int y = j+l;
                        if(y<0 || y>=boardArray.length) {  //looks through board columns
                            continue;
                        }
                        
                        if(boardArray[x][y]>0) {
                            ++numOfNeighbors;
                            sum+=Math.abs(boardArray[i][j]-boardArray[x][y]); //number of non empty cells increases score  
                        }
                        
                    }
                }
                
                clusteringScore+=sum/numOfNeighbors;  //final clustering score
            }
        }
        
        return clusteringScore;
    }

}
