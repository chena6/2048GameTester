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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The main class of the Game 2048.
 * 
 * @author Marko Laban, Arthur Chen, Karim Guirguis
 * Project initially by: Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class Board implements Cloneable {
    /**
     * The size of the board
     */
    public static final int BOARD_SIZE = 4;
    
    /**
     * The maximum combination in which the game terminates
     */
    public static int targetPoints;
    
    public static int n;
    
    /**
     * The theoretical minimum win score until the target point is reached
     */
    public static int minimumWinScore;
    
    /**
     * The score so far
     */
    private int score=0;
    
    /**
     * The board values
     */
    private int[][] boardArray;
    
    /**
     * Random Generator which is used in the creation of random cells
     */
    private final Random randomGenerator;
    
    /**
     * It caches the number of empty cells
     */
    private Integer cache_emptyCells=null;
    
    /**
     * Constructor without arguments. It initializes randomly the Board
     */
    public Board(int tp) {
    	targetPoints = tp;
    	n = (int) (Math.log(targetPoints)/Math.log(2));
    	minimumWinScore = (targetPoints * (n-1)) - targetPoints;
        boardArray = new int[BOARD_SIZE][BOARD_SIZE]; // Initialize square board
        randomGenerator = new Random(System.currentTimeMillis()); // Initialize Random generator

        // Create 2 cells in the board
        addRandomCell();
        addRandomCell();
        
    }
    
    public int getMinimumScore(){
    	return minimumWinScore;
    }
    
    /**
     * Deep clone
     * 
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException { // Returns a new Board that is a copy of 'this' one
        Board copy = (Board)super.clone();
        copy.boardArray = clone2dArray(boardArray);
        return copy;
    }
    
    /**
     * Getter for score attribute
     * 
     * @return 
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Getter for BoardArray
     * @return 
     */
    public int[][] getBoardArray() {
        return clone2dArray(boardArray);
    }
    
    /**
     * Getter for RandomGenerator field
     * 
     * @return 
     */
    public Random getRandomGenerator() {
        return randomGenerator;
    }
    
    /**
     * Performs one move (up, down, left or right).
     * 
     * @param direction
     * @return 
     */
    public int move(Direction direction) {    
        int points = 0;
        
        //rotate the board to make simplify the merging algorithm
        if(direction==Direction.UP) {
            rotateLeft();
        }
        else if(direction==Direction.RIGHT) {
            rotateLeft();
            rotateLeft();
        }
        else if(direction==Direction.DOWN) {
            rotateRight();
        }
        
        for(int i=0;i<BOARD_SIZE;++i) { //loop for all columns
            int lastMergePosition=0; // remember last position merged, to avoid merging twice
            for(int j=1;j<BOARD_SIZE;++j) { //loop for all rows
                if(boardArray[i][j]==0) {
                    continue; //skip moving zeros
                }
                
                int previousPosition = j-1;
                while(previousPosition>lastMergePosition && boardArray[i][previousPosition]==0) { //skip all the zeros
                    --previousPosition;
                }
                
                if(previousPosition==j) {
                    //we can't move this at all
                }
                else if(boardArray[i][previousPosition]==0) {
                    //move to empty value
                    boardArray[i][previousPosition]=boardArray[i][j];
                    boardArray[i][j]=0;
                }
                else if(boardArray[i][previousPosition]==boardArray[i][j]){
                    //merge with matching value
                    boardArray[i][previousPosition]*=2;
                    boardArray[i][j]=0;
                    points+=boardArray[i][previousPosition];
                    lastMergePosition=previousPosition+1;
                    
                }
                else if(boardArray[i][previousPosition]!=boardArray[i][j] && previousPosition+1!=j){
                    boardArray[i][previousPosition+1]=boardArray[i][j];
                    boardArray[i][j]=0;
                }
            }
        }
        
        
        score+=points;
        
        //reverse back the board to the original orientation
        if(direction==Direction.UP) {
            rotateRight();
        }
        else if(direction==Direction.RIGHT) {
            rotateRight();
            rotateRight();
        }
        else if(direction==Direction.DOWN) {
            rotateLeft();
        }
        
        return points;
    }
    
    /**
     * Returns the Ids of the empty cells. The cells are numbered by row.
     * 
     * @return 
     */
    public List<Integer> getEmptyCellIds() {
        List<Integer> cellList = new ArrayList<>();
        
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                if(boardArray[i][j]==0) {
                    cellList.add(BOARD_SIZE*i+j);
                }
            }
        }
        
        return cellList;
    }
    
    /**
     * Counts the number of empty cells
     * 
     * @return 
     */
    public int getNumberOfEmptyCells() {
        if(cache_emptyCells==null) {
            cache_emptyCells = getEmptyCellIds().size();
        }
        return cache_emptyCells;
    }
    
    /**
     * Checks if any of the cells in the board has value equal or larger than the
     * target.
     * 
     * @return 
     */
    public boolean hasWon() {
        if(score<minimumWinScore) { //speed optimization
            return false;
        }
        for(int i=0;i<BOARD_SIZE;++i) { // check all columns
            for(int j=0;j<BOARD_SIZE;++j) { // check all rows
                if(boardArray[i][j]>=targetPoints) {
                    return true; // if there is a 2048 cell then game is won
                }
            }
        }
        
        return false; // otherwise, game is not won
    }
    
    /**
     * Checks whether the game is terminated
     * 
     * @return 
     * @throws java.lang.CloneNotSupportedException 
     */
    public boolean isGameTerminated() throws CloneNotSupportedException {
        boolean terminated=false;
        
        if(hasWon()==true) {
            terminated=true; // if the game is won, then it has ended
        }
        else {
            if(getNumberOfEmptyCells()==0) { //if no more available cells
                Board copyBoard = (Board) this.clone();
                                
                if(copyBoard.move(Direction.UP)==0 
                   && copyBoard.move(Direction.RIGHT)==0 
                   && copyBoard.move(Direction.DOWN)==0 
                   && copyBoard.move(Direction.LEFT)==0) {
                    terminated=true; // if there are no available moves (score for all moves is 0), then game has ended
                }
                
                //copyBoard=null;
            }
        }
        
        return terminated; // return state of the game
    }
    
    /**
     * Performs an Up, Right, Down or Left move
     * 
     * @param direction
     * @return 
     * @throws java.lang.CloneNotSupportedException 
     */
    public ActionStatus action(Direction direction) throws CloneNotSupportedException {
        ActionStatus result = ActionStatus.CONTINUE;
        
        int[][] currBoardArray = getBoardArray(); // board before move
        int newPoints = move(direction); // move in direction
        int[][] newBoardArray = getBoardArray(); // board after move
        
        //add random cell
        boolean newCellAdded = false;
        
        if(!isEqual(currBoardArray, newBoardArray)) { // if board is changed, then a cell has been added
            newCellAdded = addRandomCell();
        }
        
        if(newPoints==0 && newCellAdded==false) { // no points added, or new cell added
            if(isGameTerminated()) { // if game ended then there are no more moves
                result = ActionStatus.NO_MORE_MOVES;
            }
            else { // if not ended, then invalid move
                result = ActionStatus.INVALID_MOVE;
            }
        }
        else { // if board is changed
            if(newPoints>=targetPoints) { // if points are greater than or eaual to target, then game has been won
                result = ActionStatus.WIN;
            }
            else {
                if(isGameTerminated()) { // if game is terminated, then there are no more moves
                    result = ActionStatus.NO_MORE_MOVES;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Sets the value to an empty cell. 
     * 
     * @param i
     * @param j
     * @param value 
     */
    public void setEmptyCell(int i, int j, int value) {
        if(boardArray[i][j]==0) {
            boardArray[i][j]=value;
            cache_emptyCells=null;
        }
    }
    
    /**
     * Rotates the board on the left
     */
    private void rotateLeft() {
        int[][] rotatedBoard = new int[BOARD_SIZE][BOARD_SIZE];
        
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                rotatedBoard[BOARD_SIZE-j-1][i] = boardArray[i][j];
            }
        }
        
        boardArray=rotatedBoard;
    }
    
    /**
     * Rotates the board on the right
     */
    private void rotateRight() {
        int[][] rotatedBoard = new int[BOARD_SIZE][BOARD_SIZE];
        
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                rotatedBoard[i][j]=boardArray[BOARD_SIZE-j-1][i];
            }
        }
        
        boardArray=rotatedBoard;
    }
    
    /**
     * Creates a new Random Cell
     */
    private boolean addRandomCell() {
        List<Integer> emptyCells = getEmptyCellIds();
        
        int listSize=emptyCells.size();
        
        if(listSize==0) {
            return false;
        }
        
        int randomCellId=emptyCells.get(randomGenerator.nextInt(listSize));
        int randomValue=(randomGenerator.nextDouble()< 0.9)?2:4; // 90% chance of a 2 cell, 10% chance of a 4 cell
        
        int i = randomCellId/BOARD_SIZE;
        int j = randomCellId%BOARD_SIZE;
        
        setEmptyCell(i, j, randomValue);
        
        return true;
    }
    
    /**
     * Clones a 2D array
     * 
     * @param original
     * @return 
     */
    private int[][] clone2dArray(int[][] original) { // copy the board (into an array)
        int[][] copy = new int[original.length][];
        for(int i=0;i<original.length;++i) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
    

    
    /**
     * Checks whether the two input boards are same.
     * 
     * @param currBoardArray, newBoardArray
     * @return 
     */
    public boolean isEqual(int[][] currBoardArray, int[][] newBoardArray) {

    	boolean equal = true;
        
        for(int i=0;i<currBoardArray.length;i++) {
            for(int j=0;j<currBoardArray.length;j++) {
                if(currBoardArray[i][j]!= newBoardArray[i][j]) {
                    equal = false; //The two boards are not same.
                    return equal;
                }
            }
        }
        
        return equal;
    }
}
