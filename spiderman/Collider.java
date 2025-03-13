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
 * ColliderOutputFile name is passed in through the command line as args[2]
 * Output to ColliderOutputFile with the format:
 * 1. e lines, each with a different dimension number, then listing
 *       all of the dimension numbers connected to that dimension (space separated)
 * 
 * @author Seth Kelley
 */

public class Collider {

    public Dimension head;
    public Dimension lastNode;

    public Collider() {
        head = null;
        lastNode = null;
    }

    public static void main(String[] args) {

        if ( args.length < 3 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.Collider <dimension INput file> <spiderverse INput file> <collider OUTput file>");
                return;
        }
        String dimensionInput = args[0];
        // String peopleInput = args[1];
        String outputFile = args[2];

        // String dimensionInput = "dimension.in";
        // String peopleInput = "spiderverse.in";
        // String outputFile = "collider.out";
        
        // HashMap<String, People> peopleArray = makePeopleArray(peopleInput);

        Clusters table = new Clusters();
        table.clusterGraph = table.buildClusterGraph(dimensionInput);
        Collider adj = new Collider();
        HashMap<Integer, Dimension> aList = adj.createAdjList(table.clusterGraph);
        adj.printHashMap(aList, outputFile);
        

    }

    public HashMap<Integer, Dimension> createAdjList(Dimension[] graph) {
        HashMap<Integer, Dimension> aList = new HashMap<Integer, Dimension>();
        for (int i = 0; i < graph.length; i++) {
            head = graph[i];
            //new Dimension(head.getNumber(), head.getCanonEvents(), head.getWeight(), null);
            Dimension newHead = new Dimension(head.getNumber(), head.getCanonEvents(), head.getWeight(), null);
            insert(newHead, aList);
            Dimension ptr = graph[i].getNextDimension();
            while (ptr != null) {
                Dimension nextPtr = ptr.getNextDimension();
                Dimension ptrClone = new Dimension(ptr.getNumber(), ptr.getCanonEvents(), ptr.getWeight(), null);
                insert(ptrClone,aList);
                hashLink(newHead, ptrClone, aList);
                ptr = nextPtr;
            }
        
        }
        return aList;
    }

    public void insert(Dimension ptr, HashMap<Integer, Dimension> aList) {
        lastNode = ptr;
            if (!aList.containsKey(ptr.getNumber())) {
                aList.put(ptr.getNumber(), ptr);
            }
            else {
                while (lastNode.getNextDimension() != null) {
                    lastNode = lastNode.getNextDimension();
                }
            }
    }

    public void hashLink(Dimension one, Dimension two, HashMap<Integer, Dimension> aList) {
        Dimension oneClone = new Dimension(one.getNumber(), one.getCanonEvents(), one.getWeight(), null);
        Dimension twoClone = new Dimension(two.getNumber(), two.getCanonEvents(), two.getWeight(), null);
        Dimension ptr1 = aList.get(one.getNumber());
        while (ptr1.getNextDimension() != null) {
            ptr1 = ptr1.getNextDimension();
        }
        Dimension ptr2 = aList.get(two.getNumber());
        while (ptr2.getNextDimension() != null) {
            ptr2 = ptr2.getNextDimension();
        }
        ptr1.setNextDimension(twoClone);
        ptr2.setNextDimension(oneClone);
    }

    public void printHashMap(HashMap<Integer, Dimension> aList, String outputFile) {
        StdOut.setFile(outputFile);
        for (Dimension ptr : aList.values()) {
            while (ptr != null) {
                StdOut.print(ptr.getNumber() + " ");
                ptr = ptr.getNextDimension();
            }
            StdOut.println();
        }
    }

    public HashMap<String, People> makePeopleArray(String inputFile) {
        StdIn.setFile(inputFile);
        int a = StdIn.readInt();
        HashMap<String, People>peopleArray = new HashMap<String, People>();
        for (int i = 0; i < a; i++) {
            StdIn.readLine();
            int currentDimension = StdIn.readInt();
            String name = StdIn.readString();
            int homeDimension = StdIn.readInt();
            People persontoAdd = new People(name, currentDimension, homeDimension, false);
            peopleArray.put(name, persontoAdd);
        }
        return peopleArray;
    }

    
}