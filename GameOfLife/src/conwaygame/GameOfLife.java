package conwaygame;
import java.util.ArrayList;
/**
 * Conway's Game of Life Class holds various methods that will
 * progress the state of the game's board through it's many iterations/generations.
 *
 * Rules 
 * Alive cells with 0-1 neighbors die of loneliness.
 * Alive cells with >=4 neighbors die of overpopulation.
 * Alive cells with 2-3 neighbors survive.
 * Dead cells with exactly 3 neighbors become alive by reproduction.

 * @author Seth Kelley 
 * @author Maxwell Goldberg
 */
public class GameOfLife {

    // Instance variables
    private static final boolean ALIVE = true;
    private static final boolean  DEAD = false;

    private boolean[][] grid;    // The board has the current generation of cells
    private int totalAliveCells; // Total number of alive cells in the grid (board)

    /**
    * Default Constructor which creates a small 5x5 grid with five alive cells.
    * This variation does not exceed bounds and dies off after four iterations.
    */
    public GameOfLife() {
        grid = new boolean[5][5];
        totalAliveCells = 5;
        grid[1][1] = ALIVE;
        grid[1][3] = ALIVE;
        grid[2][2] = ALIVE;
        grid[3][2] = ALIVE;
        grid[3][3] = ALIVE;
    }

    /**
    * Constructor used that will take in values to create a grid with a given number
    * of alive cells
    * @param file is the input file with the initial game pattern formatted as follows:
    * An integer representing the number of grid rows, say r
    * An integer representing the number of grid columns, say c
    * Number of r lines, each containing c true or false values (true denotes an ALIVE cell)
    */
    public GameOfLife (String file) {
        StdIn.setFile(file);
        int n = StdIn.readInt();
        int m = StdIn.readInt();
        grid = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                grid[i][j] = StdIn.readBoolean();
                if (grid[i][j] == true) {
                    totalAliveCells += 1;
                }
            }
        }
    }

    /**
     * Returns grid
     * @return boolean[][] for current grid
     */
    public boolean[][] getGrid () {
        return grid;
    }
    
    /**
     * Returns totalAliveCells
     * @return int for total number of alive cells in grid
     */
    public int getTotalAliveCells () {
        return totalAliveCells;
    }

    /**
     * Returns the status of the cell at (row,col): ALIVE or DEAD
     * @param row row position of the cell
     * @param col column position of the cell
     * @return true or false value "ALIVE" or "DEAD" (state of the cell)
     */
    public boolean getCellState (int row, int col) {
        return grid[row][col];// update this line, provided so that code compiles
    }

    /**
     * Returns true if there are any alive cells in the grid
     * @return true if there is at least one cell alive, otherwise returns false
     */
    public boolean isAlive () {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == true) {
                    return true;
                }
            }
        }
        return false; // update this line, provided so that code compiles
    }

    /**
     * Determines the number of alive cells around a given cell.
     * Each cell has 8 neighbor cells which are the cells that are 
     * horizontally, vertically, or diagonally adjacent.
     * 
     * @param col column position of the cell
     * @param row row position of the cell
     * @return neighboringCells, the number of alive cells (at most 8).
     */
    public int numOfAliveNeighbors (int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    if (grid[Math.floorMod(i+row,grid.length)][Math.floorMod(j+col,grid[0].length)] == true) {
                        count += 1;
                    }
                }
            }
        }
        return count; // update this line, provided so that code compiles
    }

    /**
     * Creates a new grid with the next generation of the current grid using 
     * the rules for Conway's Game of Life.
     * 
     * @return boolean[][] of new grid (this is a new 2D array)
     */
    public boolean[][] computeNewGrid () {
        boolean[][] newGrid = new boolean[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                /** RULES
                 * 1) Alive cells with no neighbors or one neighbor die of loneliness.
                 * 2) Dead cells with exactly three neighbors become alive by reproduction.
                 * 3) Alive cells with two or three neighbors survive.
                 * 4) Alive cells with four or more neighbors die of overpopulation. 
                 */
                if ((getCellState(i,j) == true) && (numOfAliveNeighbors(i,j) == 2 || numOfAliveNeighbors(i,j) == 3)) { //rule 2
                    newGrid[i][j] = true;
                }
                else if ((getCellState(i,j) == false) && (numOfAliveNeighbors(i,j) == 3)) { //rule 3
                    newGrid[i][j] = true;
                }
            }
        }
        return newGrid;
    }

    /**
     * Updates the current grid (the grid instance variable) with the grid denoting
     * the next generation of cells computed by computeNewGrid().
     * 
     * Updates totalAliveCells instance variable
     */
    public void nextGeneration () {
        totalAliveCells = 0;
        boolean[][] arr = computeNewGrid();
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[0].length; j++) {
                grid[i][j] = arr[i][j];
                if (grid[i][j] == true) {
                    totalAliveCells += 1;
                }
            }
        }
    }

    /**
     * Updates the current grid with the grid computed after multiple (n) generations. 
     * @param n number of iterations that the grid will go through to compute a new grid
     */
    public void nextGeneration (int n) {
        for (int i = 0; i < n; i++) {
            nextGeneration();
        }
    }

    /**
     * Determines the number of separate cell communities in the grid
     * @return the number of communities in the grid, communities can be formed from edges
     */
    public int numOfCommunities() {
        WeightedQuickUnionUF WUF = new WeightedQuickUnionUF(grid.length, grid[0].length);

        //sets each cells parent
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == true){
                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            if (!(x == 0 && y == 0)) {
                                if (grid[Math.floorMod(x+i,grid.length)][Math.floorMod(y+j,grid[0].length)] == true) {
                                    WUF.union(i,j,Math.floorMod(x+i,grid.length),Math.floorMod(y+j,grid[0].length));
                                }
                            }
                        }
                    }
                }
                
            }
        }

        //finding unique roots
        int total = 0;
        int root = 0;
        boolean dups = true;
        int[] parents = new int[grid.length * grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == true) {
                    root = WUF.find(i,j);
                    dups = true;
                    for (int x = 0; x < (grid.length * grid[0].length); x++) {
                        if (parents[x] == root) {
                            dups = false;
                        }
                    }
                    if (dups == true) {
                        parents[total] = root;
                        total += 1;
                    }

                }
            }
        }

        if ((total == 0) && (isAlive() == true)) {
            return 1;
        }
        return total;
    }
}