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
 * HubInputFile name is passed through the command line as args[2]
 * Read from the SpotInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * AnomaliesInputFile name is passed through the command line as args[3]
 * Read from the AnomaliesInputFile with the format:
 * 1. e (int): number of anomalies in the file
 * 2. e lines, each with:
 *      i.   The Name of the anomaly which will go from the hub dimension to their home dimension (String)
 *      ii.  The time allotted to return the anomaly home before a canon event is missed (int)
 * 
 * Step 5:
 * ReportOutputFile name is passed in through the command line as args[4]
 * Output to ReportOutputFile with the format:
 * 1. e Lines (one for each anomaly), listing on the same line:
 *      i.   The number of canon events at that anomalies home dimensionafter being returned
 *      ii.  Name of the anomaly being sent home
 *      iii. SUCCESS or FAILED in relation to whether that anomaly made it back in time
 *      iv.  The route the anomaly took to get home
 * 
 * @author Seth Kelley
 */

public class GoHomeMachine {

    public HashMap<Integer, Integer> shortestDistance;
    public HashMap<Integer, Integer> previous;
    public int[] canonEvents;
    public String[] names;
    public String[] status;
    public ArrayList<ArrayList<Integer>> paths;

    public GoHomeMachine() {
        shortestDistance = null;
        previous = null;
        canonEvents = null;
        names = null;
        status = null;
        paths = null;
    }


    public static void main(String[] args) {

        if ( args.length < 5 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.GoHomeMachine <dimension INput file> <spiderverse INput file> <hub INput file> <anomalies INput file> <report OUTput file>");
                return;
        }

        String dimensionInput = args[0];
        String peopleInput = args[1];
        String hubFile = args[2];
        String anomaliesFile = args[3];
        String outputFile = args[4];
        
        // String dimensionInput = "dimension.in";
        // String peopleInput = "spiderverse.in";
        // String hubFile = "hub.in";
        // String anomaliesFile = "anomalies.in";
        // String outputFile = "report.out";

        Clusters table = new Clusters();
        table.clusterGraph = table.buildClusterGraph(dimensionInput);
        Collider adj = new Collider();
        HashMap<Integer, Dimension> aList = adj.createAdjList(table.clusterGraph);
        HashMap<String, People> peopleArray = adj.makePeopleArray(peopleInput);
        CollectAnomalies collector = new CollectAnomalies();
        @SuppressWarnings("unused")
        ArrayList<ArrayList<Integer>> paths = collector.createAnomalyMap(peopleArray, hubFile, aList);
        int hub = collector.getHub(hubFile);
        GoHomeMachine ghm = new GoHomeMachine();
        ghm.createReport(anomaliesFile, peopleArray, aList, hub);
        ghm.printReport(outputFile);
    }


    public void Dijkstra1(HashMap<Integer, Dimension> aList, int hub) {
        shortestDistance = new HashMap<Integer, Integer>();
        previous = new HashMap<Integer, Integer>();
        HashSet<Integer> unvisited = new HashSet<Integer>();
        PriorityQueue<Integer> pQ = new PriorityQueue<>(Comparator.comparingInt(shortestDistance::get));

        for (Dimension ptr : aList.values()) {
            shortestDistance.put(ptr.getNumber(), Integer.MAX_VALUE);
            unvisited.add(ptr.getNumber());
        }

        shortestDistance.put(hub, 0);
        pQ.offer(hub);

        while (!unvisited.isEmpty()) {
            int currentDimNum = pQ.poll();
            Dimension current = aList.get(currentDimNum);
            Dimension ptr = current.getNextDimension();
            while (ptr != null) {
                int distanceFromStart = shortestDistance.get(currentDimNum) + calculateEdgeWeight(current, ptr);
                if (distanceFromStart < shortestDistance.get(ptr.getNumber())) {
                    shortestDistance.put(ptr.getNumber(), distanceFromStart);
                    previous.put(ptr.getNumber(), currentDimNum);
                    pQ.offer(ptr.getNumber());
                }
                ptr = ptr.getNextDimension();
            }
            unvisited.remove(currentDimNum);
        }
    }

    public int calculateEdgeWeight(Dimension a, Dimension b) {
        return a.getWeight() + b.getWeight();
    }

    public ArrayList<Integer> Dijkstra2(HashMap<Integer, Integer> previous, int targetDim, int hub) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        Stack<Integer> s = new Stack<Integer>();
        int ptr = targetDim;
        while (previous.get(ptr) != null) {
            s.add(ptr);
            ptr = previous.get(ptr);
        }
        s.add(hub);
        while (!s.isEmpty()) {
            path.add(s.pop());
        }
        return path;
    }

    public void createReport(String inputFile, HashMap<String, People> peopleArray, HashMap<Integer, Dimension> aList, int hub) {
        StdIn.setFile(inputFile);
        int size = StdIn.readInt();
        canonEvents = new int[size];
        names = new String[size];
        status = new String[size];
        paths = new ArrayList<>();

        Dijkstra1(aList, hub);
        for (int i = 0; i < size; i++) {
            StdIn.readLine();
            People ptr = peopleArray.get(StdIn.readString());
            names[i] = ptr.getName();
            int timeAlloted = StdIn.readInt();
            int timeUsed = shortestDistance.get(ptr.getHomeDimension());
            Dimension dim = aList.get(ptr.getHomeDimension());
            if (timeUsed > timeAlloted) {
                status[i] = "FAILED";
                dim.setCanonEvents(dim.getCanonEvents()-1);
            }
            else {
                status[i] = "SUCCESS";
            }
            canonEvents[i] = dim.getCanonEvents();
            ArrayList<Integer> path = Dijkstra2(previous, ptr.getHomeDimension(), hub);
            paths.add(path);
        }
    }

    public void printReport(String outputFile) {
        StdOut.setFile(outputFile);
        int n = canonEvents.length;
        for (int i = 0; i < n; i++) {
            StdOut.print(canonEvents[i] + " " + names[i] + " " + status[i] + " ");
            ArrayList<Integer> path = paths.get(i);
            for (int j : path) {
                StdOut.print(j + " ");
            }
            StdOut.println();
        }
    }
}