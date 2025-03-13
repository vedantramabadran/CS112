package spiderman;

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
 * 
 * Step 2:
 * ClusterOutputFile name is passed in through the command line as args[1]
 * Output to ClusterOutputFile with the format:
 * 1. n lines, listing all of the dimension numbers connected to 
 *    that dimension in order (space separated)
 *    n is the size of the cluster table.
 * 
 * @author Seth Kelley
 */

public class Clusters {

    public Dimension[] clusterGraph;
    public int clusterGraphSize;

    public Clusters() {
        clusterGraph = null;
        clusterGraphSize = 0;
    }

    public Dimension[] buildClusterGraph(String inputFile) {
        StdIn.setFile(inputFile);
        int dimensions = StdIn.readInt(); // number of dimensions
        clusterGraphSize = StdIn.readInt(); // initial size
        double threshold = StdIn.readDouble(); // threshold
        clusterGraph = new Dimension[clusterGraphSize];
        int count = 0;
        for (int i = 0; i < dimensions; i++) {
            StdIn.readLine();
            count = i + 1;
            int dimensionNum = StdIn.readInt();
            int events = StdIn.readInt();
            int weight = StdIn.readInt();

            Dimension newDimension = new Dimension(dimensionNum, events, weight, null);
            hash(clusterGraph, newDimension);
            if ((double) count / clusterGraphSize >= threshold) {
                clusterGraph = rehash(clusterGraph);
            }
        }
        for (int j = 0; j < clusterGraphSize; j++) {
            Dimension last = clusterGraph[j];
            while (last.getNextDimension() != null) {
                last = last.getNextDimension();
            }
            int index1 = (j + clusterGraphSize - 1) % clusterGraphSize;
            int index2 = (j + clusterGraphSize - 2) % clusterGraphSize;
            Dimension node1 = new Dimension(clusterGraph[index1].getNumber(), clusterGraph[index1].getCanonEvents(), clusterGraph[index1].getWeight(), null);
            Dimension node2 = new Dimension(clusterGraph[index2].getNumber(), clusterGraph[index2].getCanonEvents(), clusterGraph[index2].getWeight(), null);
            last.setNextDimension(node1);
            last.getNextDimension().setNextDimension(node2);
        }
        return clusterGraph;
    }

    public void hash(Dimension[] clusterGraph, Dimension newDimension) {
        int dimensionNum = newDimension.getNumber();
        if (clusterGraph[dimensionNum % clusterGraphSize] == null) {
            clusterGraph[dimensionNum % clusterGraphSize] = newDimension;
            clusterGraph[dimensionNum % clusterGraphSize].setNextDimension(null);
        } else {
            newDimension.setNextDimension(clusterGraph[dimensionNum % clusterGraphSize]);
            clusterGraph[dimensionNum % clusterGraphSize] = newDimension;
        }
    }

    public Dimension[] rehash(Dimension[] clusterGraph) {
        Dimension[] newGraph = new Dimension[clusterGraphSize * 2];
        clusterGraphSize *= 2;
        for (int i = 0; i < clusterGraphSize / 2; i++) {
            Dimension ptr = clusterGraph[i];
            while (ptr != null) {
                Dimension nextPtr = ptr.getNextDimension();
                hash(newGraph, ptr);
                ptr = nextPtr;
            }
        }
        return newGraph;
    }

    public void printClusterGraph(Dimension[] clusterGraph, String outputFile) {
        StdOut.setFile(outputFile);
        for (int i = 0; i < clusterGraphSize; i++) {
            Dimension ptr = clusterGraph[i];
            while (ptr != null) {
                StdOut.print(ptr.getNumber() + " ");
                ptr = ptr.getNextDimension();
            }
            StdOut.println();
        }
    }

    public static void main(String[] args) {
        Clusters table = new Clusters();
        if (args.length < 2) {
            StdOut.println("Execute: java -cp bin spiderman.Clusters <dimension INput file> <collider OUTput file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];
        table.clusterGraph = table.buildClusterGraph(inputFile);
        table.printClusterGraph(table.clusterGraph, outputFile);
    }
}