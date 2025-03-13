package spiderman;
import java.util.*;

/**
 * Steps to implement this class main method:
 * 
 * Step 1:
 * DimensionInputFile name is passed through the command line as args[0]
 * Read from the DimensionsInputFile with the format:
 * 1. The first line with three numbers:
 *      i.    a (int): number of dimensions in the graph
 *      ii.   b (int): the initial size of the cluster table prior to rehashing
 *      iii.  c (double): the capacity(threshold) used to rehash the cluster table 
 * 2. a lines, each with:
 *      i.    The dimension number (int)
 *      ii.   The number of canon events for the dimension (int)
 *      iii.  The dimension weight (int)
 * 
 * Step 2:
 * SpiderverseInputFile name is passed through the command line as args[1]
 * Read from the SpiderverseInputFile with the format:
 * 1. d (int): number of people in the file
 * 2. d lines, each with:
 *      i.    The dimension they are currently at (int)
 *      ii.   The name of the person (String)
 *      iii.  The dimensional signature of the person (int)
 * 
 * Step 3:
 * SpotInputFile name is passed through the command line as args[2]
 * Read from the SpotInputFile with the format:
 * Two integers (line seperated)
 *      i.    Line one: The starting dimension of Spot (int)
 *      ii.   Line two: The dimension Spot wants to go to (int)
 * 
 * Step 4:
 * TrackSpotOutputFile name is passed in through the command line as args[3]
 * Output to TrackSpotOutputFile with the format:
 * 1. One line, listing the dimenstional number of each dimension Spot has visited (space separated)
 * 
 * @author Seth Kelley
 */

public class TrackSpot {
    
    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.TrackSpot <dimension INput file> <spiderverse INput file> <spot INput file> <trackspot OUTput file>");
                return;
        }

        String dimensionInput = args[0];
        // String peopleInput = args[1];
        String spotFile = args[2];
        String outputFile = args[3];
        
        // String dimensionInput = "dimension.in";
        // String peopleInput = "spiderverse.in";
        // String spotFile = "spot.in";
        // String outputFile = "trackspot.out";

        Clusters table = new Clusters();
        table.clusterGraph = table.buildClusterGraph(dimensionInput);
        Collider adj = new Collider();
        HashMap<Integer, Dimension> aList = adj.createAdjList(table.clusterGraph);
        // People[] peopleArray = Collider.makePeopleArray(peopleInput);
        ArrayList<Integer> visitedDimensions = createPath(spotFile, aList);
        printVisited(visitedDimensions, outputFile);
    }

    public static ArrayList<Integer> createPath(String inputFile, HashMap<Integer, Dimension> aList) {
        StdIn.setFile(inputFile);
        ArrayList<Integer> visitedDimensions = new ArrayList<Integer>();
        HashSet<Integer> visDim = new HashSet<Integer>();
        int start = StdIn.readInt();
        int target = StdIn.readInt();

        dfs(aList, start, target, visitedDimensions, visDim);

        return visitedDimensions;
    }

    public static void dfs(HashMap<Integer, Dimension> aList, int currentNode, int target, ArrayList<Integer> visitedDimensions, HashSet<Integer> visDim) {
        visDim.add(currentNode);
        Dimension next = aList.get(currentNode).getNextDimension();
        visitedDimensions.add(currentNode);
        if (currentNode == target) {
            return;
        }
        while (next != null) {
            if (!visDim.contains(next.getNumber())) {
                dfs(aList, next.getNumber(), target, visitedDimensions, visDim);
            }
            if (visitedDimensions.get(visitedDimensions.size() - 1) == target) {
                return;
            }
            next = next.getNextDimension();
        }
    }

    public static void printVisited(ArrayList<Integer> visitedDimensions, String outputFile) {
        StdOut.setFile(outputFile);
        for (int i = 0; i < visitedDimensions.size(); i++) {
            StdOut.print(visitedDimensions.get(i)+ " ");
        }
    }
}
