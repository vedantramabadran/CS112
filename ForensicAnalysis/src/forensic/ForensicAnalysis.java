package forensic;

/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
    */
    public Profile createSingleProfile() {
        int n = StdIn.readInt();
        STR[] strs = new STR[n];
        for (int i = 0; i < n; i++) {
            strs[i] = new STR(StdIn.readString(), StdIn.readInt());
        }
        Profile output = new Profile(strs);
        return output;
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {
        TreeNode newPerson = new TreeNode(name, newProfile, null, null);
        TreeNode ptr = treeRoot;
        if (treeRoot == null) {
            treeRoot = newPerson;
        }
        else {
            while (ptr != null) {
                if ((name.compareTo(ptr.getName()) > 0)) {
                    if (ptr.getRight() == null) {
                        ptr.setRight(newPerson); break;
                    }
                    else {
                        ptr = ptr.getRight();
                    } 
                }
                else if ((name.compareTo(ptr.getName()) < 0)) {
                    if (ptr.getLeft() == null) {
                        ptr.setLeft(newPerson); break;
                    }
                    else {
                        ptr = ptr.getLeft();
                    } 
                }
            }
        }
    }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */
    public int getMatchingProfileCount(boolean isOfInterest) {
        int[] count = new int[1];
        inOrderTraversal(treeRoot, count, isOfInterest);
        return count[0]; // update this line
    }

    public void inOrderTraversal(TreeNode n, int[] count, boolean isOfInterest) {
        if (n == null) return;
        inOrderTraversal(n.getLeft(), count, isOfInterest);
        if (n.getProfile().getMarkedStatus() == isOfInterest) count[0]++;
        inOrderTraversal(n.getRight(), count, isOfInterest);
    }

    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        // DO NOT EDIT THIS CODE
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    public void flagProfilesOfInterest() {
        inOrderTraversalFlag(treeRoot);
    }

    public void inOrderTraversalFlag(TreeNode n) {
        if (n == null) return;
        flag(n);
        inOrderTraversalFlag(n.getLeft());
        inOrderTraversalFlag(n.getRight());
    }

    public void flag(TreeNode n) {
        int count = 0;
        for (int i = 0; i < n.getProfile().getStrs().length; i++) {
            int occurrences = n.getProfile().getStrs()[i].getOccurrences();
            int firstOccurences = numberOfOccurrences(firstUnknownSequence, n.getProfile().getStrs()[i].getStrString());
            int secondOccurences = numberOfOccurrences(secondUnknownSequence, n.getProfile().getStrs()[i].getStrString());
            if (occurrences == (firstOccurences+secondOccurences)) count++;
        }
        if (count >= Math.ceil(n.getProfile().getStrs().length / 2.0)) n.getProfile().setInterestStatus(true);
    }

    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {
        String[] unmarked = new String[getMatchingProfileCount(false)];
        Queue<TreeNode> order = new Queue<TreeNode>();
        order.enqueue(treeRoot);
        int i = 0;
        while (!order.isEmpty()) {
            TreeNode node = order.dequeue();
            if (!node.getProfile().getMarkedStatus()) {
                unmarked[i] = node.getName();
                i++;
            }
            if (node.getLeft() != null) order.enqueue(node.getLeft());
            if (node.getRight() != null) order.enqueue(node.getRight());
        }
        return unmarked; // update this line
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    public void removePerson(String fullName) {
        TreeNode prev = null;
        TreeNode ptr = treeRoot;
        boolean isLeft = false;
        // Traverse the tree
        while (!ptr.getName().equals(fullName)) {
            if (ptr.getName().compareTo(fullName) > 0) {
                if (ptr.getLeft() == null) {
                    return;
                }
                prev = ptr;
                ptr = ptr.getLeft();
                isLeft = true;

            }
            else {
                if (ptr.getRight() == null) {
                    return;
                }
                prev = ptr;
                ptr = ptr.getRight();
                isLeft = false;
            }
        }
        //delete the node based on the case
        //case 1, node to delete is the root
        if (ptr == treeRoot) {
            //case 1a, tree only has root
            if (ptr.getLeft() == null && ptr.getRight() == null) {
                treeRoot = null;
                return;
            }
            //case 1b, root has either left or right
            else if (ptr.getLeft() == null && ptr.getRight() != null) {
                treeRoot = ptr.getRight();
                return;
            }
            else if (ptr.getLeft() != null && ptr.getRight() == null) {
                treeRoot = ptr.getLeft();
                return;
            }
            //case 1c, root has both left and right
            else if (ptr.getLeft() != null && ptr.getRight() != null) {
                TreeNode successor = ptr.getRight();
                while (successor.getLeft() != null) {
                    successor = successor.getLeft();
                }
                ptr.setProfile(successor.getProfile());
                removePerson(successor.getName());
                ptr.setName(successor.getName());
            }
        }
        //case 2, ptr has no children
        else if (ptr.getLeft() == null && ptr.getRight() == null) {
            if (isLeft) {
                prev.setLeft(null);
            }
            else {
                prev.setRight(null);
            }
        }
        //case 3, ptr has 1 child
        else if (ptr.getLeft() == null && ptr.getRight() != null) {
            if (isLeft) {
                prev.setLeft(ptr.getRight());
            }
            else {
                prev.setRight(ptr.getRight());
            }
        }
        else if (ptr.getLeft() != null && ptr.getRight() == null) {
            if (isLeft) {
                prev.setLeft(ptr.getLeft());
            }
            else {
                prev.setRight(ptr.getLeft());
            }
        }
        //case 4, ptr has 2 children
        else if (ptr.getLeft() != null && ptr.getRight() != null) {
            TreeNode successor = ptr.getRight();
                while (successor.getLeft() != null) {
                    successor = successor.getLeft();
                }
                ptr.setProfile(successor.getProfile());
                removePerson(successor.getName());
                ptr.setName(successor.getName());
        }
    }

    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        String[] unmarked = getUnmarkedPeople();
        for (int i = 0; i < unmarked.length; i++) {
            removePerson(unmarked[i]);
        }

    }

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
