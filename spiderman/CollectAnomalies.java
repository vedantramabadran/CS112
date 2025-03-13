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
 * Read from the HubInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * CollectedOutputFile name is passed in through the command line as args[3]
 * Output to CollectedOutputFile with the format:
 * 1. e Lines, listing the Name of the anomaly collected with the Spider who
 *    is at the same Dimension (if one exists, space separated) followed by 
 *    the Dimension number for each Dimension in the route (space separated)
 * 
 * @author Seth Kelley
 */

public class CollectAnomalies {
    
    public ArrayList<People> spiders;
    public ArrayList<People> anomalies;
    public String[] anom;
    public String[] spid;

    public CollectAnomalies() {
        spiders = null;
        anomalies = null;
        anom = null;
        spid = null;
    }

    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.CollectAnomalies <dimension INput file> <spiderverse INput file> <hub INput file> <collected OUTput file>");
                return;
        }

        String dimensionInput = args[0];
        String peopleInput = args[1];
        String hubFile = args[2];
        String outputFile = args[3];
        
        // String dimensionInput = "dimension.in";
        // String peopleInput = "spiderverse.in";
        // String hubFile = "hub.in";
        // String outputFile = "collected.out";


        Clusters table = new Clusters();
        table.clusterGraph = table.buildClusterGraph(dimensionInput);
        Collider adj = new Collider();
        HashMap<Integer, Dimension> aList = adj.createAdjList(table.clusterGraph);
        HashMap<String, People> peopleArray = adj.makePeopleArray(peopleInput);
        CollectAnomalies collector = new CollectAnomalies();
        ArrayList<ArrayList<Integer>> paths = collector.createAnomalyMap(peopleArray, hubFile, aList);
        collector.printPaths(collector.anom, collector.spid, paths, outputFile);

    }


    public ArrayList<ArrayList<Integer>> createAnomalyMap(HashMap<String, People> peopleArray, String hubFile, HashMap<Integer, Dimension> aList) {
        StdIn.setFile(hubFile);
        int hub = StdIn.readInt();
        ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
        sortAnomalies(peopleArray, hub);
        anom = new String[anomalies.size()];
        spid = new String[anomalies.size()];
        int i = 0;
        for (People ptr : anomalies) {
            People spider = findSpider(ptr.getCurrentDim(), spiders);
            if (spider == null) {
                ArrayList<Integer> path = createPathWithoutSpider(hub, ptr.getCurrentDim(), aList);
                anom[i] = ptr.getName();
                paths.add(path);
            }
            else {
                ArrayList<Integer> path = createPathWithSpider(hub, ptr.getCurrentDim(), aList);
                anom[i] = ptr.getName();
                spid[i] = spider.getName();
                paths.add(path);
                spider.setCurrentDimension(hub);
            }
            i++;
            ptr.setCurrentDimension(hub);
        }
        return paths;
    }

    public void sortAnomalies(HashMap<String, People> peopleArray, int hub) {
        spiders = new ArrayList<People>();
        anomalies = new ArrayList<People>();
        for (People ptr : peopleArray.values()) {
            if (ptr.getCurrentDim() != hub) {
                if (ptr.getCurrentDim() == ptr.getHomeDimension()) {
                    ptr.setSpider(true);
                    spiders.add(ptr);
                }
                else {
                    anomalies.add(ptr);
                }
            }
        }
    }


    public HashMap<Integer, Integer> findPath(int start, HashMap<Integer, Dimension> aList) {
        //create the Previous HashMap for the start node
        Queue<Integer> q = new LinkedList<>();
        HashSet<Integer> visited = new HashSet<Integer>();
        HashMap<Integer, Integer> prev = new HashMap<Integer,Integer>();
        q.offer(start);
        visited.add(start);
        prev.put(start, null);
        while (!q.isEmpty()) {
            int current = q.poll();
            Dimension ptr = aList.get(current).getNextDimension();
            while (ptr != null) {
                int dimNum = ptr.getNumber();
                if (!visited.contains(dimNum)) {
                    q.offer(dimNum);
                    visited.add(dimNum);
                    prev.put(dimNum, current);
                }
                ptr = ptr.getNextDimension();
            }
        }
        return prev;
    }

    public ArrayList<Integer> createPathWithoutSpider(int start, int location, HashMap<Integer, Dimension> aList) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        Stack<Integer> s = new Stack<Integer>();
        Queue<Integer> q = new LinkedList<>();
        HashMap<Integer, Integer> prev1 = findPath(start, aList);

        int current = location;
        path.add(start);
        while (current != start) {
            s.push(current);
            q.offer(current);
            current = prev1.get(current);
        }
        q.offer(start);
        q.poll();
        while (!s.isEmpty()) {
            path.add(s.pop());
        }
        while (!q.isEmpty()) {
            path.add(q.poll());
        }

        return path;
    }

    public ArrayList<Integer> createPathWithSpider(int start, int location, HashMap<Integer, Dimension> aList) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        Queue<Integer> s = new LinkedList<Integer>();
        HashMap<Integer, Integer> prev1 = findPath(start, aList);

        int current = location;
        while (current != start) {
            s.offer(current);
            current = prev1.get(current);
        }

        while (!s.isEmpty()) {
            path.add(s.poll());
        }
        path.add(start);
        return path;
    }
    
    public People findSpider(int currentDimension, ArrayList<People> spiders) {
        for (People ptr : spiders) {
            if ((ptr.getCurrentDim() == currentDimension) && ptr.getSpider()) {
                return ptr;
            }
        }
        return null;
    } 

    public void printPaths(String[] anom, String[] spid, ArrayList<ArrayList<Integer>> paths, String outputFile) {
        StdOut.setFile(outputFile);
        int n = anom.length;
        for (int i = 0; i < n; i++) {
            if (spid[i] == null) {
                StdOut.print(anom[i] + " ");
            }
            else {
                StdOut.print(anom[i] + " " + spid[i] + " ");
            }
            ArrayList<Integer> path = paths.get(i);
            for (int j :path) {
                StdOut.print(j + " ");
            }
            StdOut.println();
        }
    }

    public int getHub(String hubFile) {
        StdIn.setFile(hubFile);
        return StdIn.readInt();
    }




}
