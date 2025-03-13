package climate;

import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered 
 * linked list structure that contains USA communitie's Climate and Economic information.
 * 
 * @author Navya Sharma
 */

public class ClimateEconJustice {

    private StateNode firstState;
    
    /*
    * Constructor
    * 
    * **** DO NOT EDIT *****
    */
    public ClimateEconJustice() {
        firstState = null;
    }

    /*
    * Get method to retrieve instance variable firstState
    * 
    * @return firstState
    * 
    * **** DO NOT EDIT *****
    */ 
    public StateNode getFirstState () {
        // DO NOT EDIT THIS CODE
        return firstState;
    }

    /**
     * Creates 3-layered linked structure consisting of state, county, 
     * and community objects by reading in CSV file provided.
     * 
     * @param inputFile, the file read from the Driver to be used for
     * @return void
     * 
     * **** DO NOT EDIT *****
     */
    public void createLinkedStructure ( String inputFile ) {
        
        // DO NOT EDIT THIS CODE
        StdIn.setFile(inputFile);
        StdIn.readLine();
        
        // Reads the file one line at a time
        while ( StdIn.hasNextLine() ) {
            // Reads a single line from input file
            String line = StdIn.readLine();
            // IMPLEMENT these methods
            addToStateLevel(line);
            addToCountyLevel(line);
            addToCommunityLevel(line);
        }
    }

    /*
    * Adds a state to the first level of the linked structure.
    * Do nothing if the state is already present in the structure.
    * 
    * @param inputLine a line from the input file
    */
    public void addToStateLevel ( String inputLine ) {
        String state = inputLine.split(",")[2];
        if (firstState == null){
            firstState = new StateNode(state, null, null);
        }
        else{
            for (StateNode pntr = firstState; pntr != null && (!pntr.getName().equals(state)); pntr = pntr.next){
                if (pntr.next == null){
                    pntr.next = new StateNode(state,null,null);
                }
            }
        }
        
    }

    /*
    * Adds a county to a state's list of counties.
    * 
    * Access the state's list of counties' using the down pointer from the State class.
    * Do nothing if the county is already present in the structure.
    * 
    * @param inputFile a line from the input file
    */
    public void addToCountyLevel ( String inputLine ) {
        String state = inputLine.split(",")[2];
        String county = inputLine.split(",")[1];
        for (StateNode i = firstState; i != null; i = i.next) {
            if (i.getName().equals(state)) {
                if (i.down == null) {
                    i.down = new CountyNode(county, null, null);
                }
                else {
                    for (CountyNode j = i.getDown(); j != null && (!j.getName().equals(county)); j = j.next) {
                        if (j.next == null) {
                            j.next = new CountyNode(county,null,null);
                        }
                    }
                }
            }
        }
    }

    /*
    * Adds a community to a county's list of communities.
    * 
    * Access the county through its state
    *      - search for the state first, 
    *      - then search for the county.
    * Use the state name and the county name from the inputLine to search.
    * 
    * Access the state's list of counties using the down pointer from the StateNode class.
    * Access the county's list of communities using the down pointer from the CountyNode class.
    * Do nothing if the community is already present in the structure.
    * 
    * @param inputFile a line from the input file
    */
    public void addToCommunityLevel ( String inputLine ) {
        String state = inputLine.split(",")[2];
        String county = inputLine.split(",")[1];
        String community = inputLine.split(",")[0];
        double prcntAfrican = Double.parseDouble(inputLine.split(",")[3]);
        double prcntNative = Double.parseDouble(inputLine.split(",")[4]);
        double prcntAsian = Double.parseDouble(inputLine.split(",")[5]);
        double prcntWhite = Double.parseDouble(inputLine.split(",")[8]);
        double prcntHispanic = Double.parseDouble(inputLine.split(",")[9]);
        String disadvantaged = inputLine.split(",")[19];
        double PMLvl = Double.parseDouble(inputLine.split(",")[49]);
        double chanceOfFlood = Double.parseDouble(inputLine.split(",")[37]);
        double povertyLine = Double.parseDouble(inputLine.split(",")[121]);
        Data data = new Data(prcntAfrican,prcntNative,prcntAsian,prcntWhite,prcntHispanic,disadvantaged,PMLvl,chanceOfFlood,povertyLine);
        for (StateNode i = firstState; i != null; i = i.next) {
            for (CountyNode j = i.down; j != null; j = j.next) {
                if (i.getName().equals(state)) {
                    if (j.getName().equals(county)) {
                        if (j.down == null) {
                            j.down = new CommunityNode(community, null, data);
                        }
                        else {
                            for (CommunityNode k = j.getDown(); k != null && (!k.getName().equals(community)); k = k.next) {
                                if (k.next == null) {
                                    k.next = new CommunityNode(community,null,data);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Given a certain percentage and racial group inputted by user, returns
     * the number of communities that have that said percentage or more of racial group  
     * and are identified as disadvantaged
     * 
     * Percentages should be passed in as integers for this method.
     * 
     * @param userPrcntage the percentage which will be compared with the racial groups
     * @param race the race which will be returned
     * @return the amount of communities that contain the same or higher percentage of the given race
     */
    public int disadvantagedCommunities ( double userPrcntage, String race ) {
        int count = 0;
        for (StateNode i = firstState; i != null; i = i.next) {
            for (CountyNode j = i.down; j != null; j = j.next) {
                for (CommunityNode k = j.down; k != null; k = k.next) {
                    if(race.equals("African American")){
                        if (k.getInfo().getAdvantageStatus().equals("True") && k.getInfo().getPrcntAfricanAmerican() * 100 >= userPrcntage) {
                            count++;
                        }
                    }
                    else if (race.equals("Native American")){
                        if (k.getInfo().getAdvantageStatus().equals("True") && k.getInfo().getPrcntNative() * 100 >= userPrcntage) {
                            count++;                           
                        }
                    }
                    else if (race.equals("Asian American")) {
                        if (k.getInfo().getAdvantageStatus().equals("True") && k.getInfo().getPrcntAsian() * 100 >= userPrcntage) {
                            count++;                         
                        }
                    }
                    else if (race.equals("White American")) {
                        if (k.getInfo().getAdvantageStatus().equals("True") && k.getInfo().getPrcntWhite() * 100 >= userPrcntage) {
                            count++;                           
                        }
                    }
                    else if (race.equals("Hispanic American")) {
                        if (k.getInfo().getAdvantageStatus().equals("True") && k.getInfo().getPrcntHispanic() * 100 >= userPrcntage) {
                            count++;                            
                        }
                    }
                }
            }

        }
        return count; // replace this line
    }

    /**
     * Given a certain percentage and racial group inputted by user, returns
     * the number of communities that have that said percentage or more of racial group  
     * and are identified as non disadvantaged
     * 
     * Percentages should be passed in as integers for this method.
     * 
     * @param userPrcntage the percentage which will be compared with the racial groups
     * @param race the race which will be returned
     * @return the amount of communities that contain the same or higher percentage of the given race
     */
    public int nonDisadvantagedCommunities ( double userPrcntage, String race ) {
        int count = 0;
        for (StateNode i = firstState; i != null; i = i.next) {
            for (CountyNode j = i.down; j != null; j = j.next) {
                for (CommunityNode k = j.down; k != null; k = k.next) {
                    if(race.equals("African American")){
                        if (k.getInfo().getAdvantageStatus().equals("False") && k.getInfo().getPrcntAfricanAmerican() * 100 >= userPrcntage) {
                            count++;
                        }
                    }
                    else if (race.equals("Native American")){
                        if (k.getInfo().getAdvantageStatus().equals("False") && k.getInfo().getPrcntNative() * 100 >= userPrcntage) {
                            count++;                           
                        }
                    }
                    else if (race.equals("Asian American")) {
                        if (k.getInfo().getAdvantageStatus().equals("False") && k.getInfo().getPrcntAsian() * 100 >= userPrcntage) {
                            count++;                         
                        }
                    }
                    else if (race.equals("White American")) {
                        if (k.getInfo().getAdvantageStatus().equals("False") && k.getInfo().getPrcntWhite() * 100 >= userPrcntage) {
                            count++;                           
                        }
                    }
                    else if (race.equals("Hispanic American")) {
                        if (k.getInfo().getAdvantageStatus().equals("False") && k.getInfo().getPrcntHispanic() * 100 >= userPrcntage) {
                            count++;                            
                        }
                    }
                }
            }

        }
        return count; // replace this line
    }
    
    /** 
     * Returns a list of states that have a PM (particulate matter) level
     * equal to or higher than value inputted by user.
     * 
     * @param PMlevel the level of particulate matter
     * @return the States which have or exceed that level
     */ 
    public ArrayList<StateNode> statesPMLevels ( double PMlevel ) {
        ArrayList<StateNode> states = new ArrayList<StateNode>();
        for (StateNode i = firstState; i != null; i = i.next) {
            for (CountyNode j = i.down; j != null; j = j.next) {
                for (CommunityNode k = j.down; k != null; k = k.next) {
                    if(k.getInfo().getPMlevel() >= PMlevel) {
                        if (states.isEmpty()) {
                            states.add(i);
                        }
                        else {
                            boolean duplicate = false;
                            for (int l = 0; l < states.size(); l++) {
                                if (states.get(l) == i) {
                                    duplicate = true;
                                }
                            }
                            if (!duplicate) {
                                states.add(i);
                            }
                        }
                    }
                }
            }

        }
        return states; // replace this line
    }

    /**
     * Given a percentage inputted by user, returns the number of communities 
     * that have a chance equal to or higher than said percentage of
     * experiencing a flood in the next 30 years.
     * 
     * @param userPercntage the percentage of interest/comparison
     * @return the amount of communities at risk of flooding
     */
    public int chanceOfFlood ( double userPercntage ) {
        int count = 0;
        for (StateNode i = firstState; i != null; i = i.next) {
            for (CountyNode j = i.down; j != null; j = j.next) {
                for (CommunityNode k = j.down; k != null; k = k.next) {
                    if (k.getInfo().getChanceOfFlood() >= userPercntage) {
                        count++;
                    }
                }
            }
        }
        return count; // replace this line
    }

    /** 
     * Given a state inputted by user, returns the communities with 
     * the 10 lowest incomes within said state.
     * 
     *  @param stateName the State to be analyzed
     *  @return the top 10 lowest income communities in the State, with no particular order
    */
    public ArrayList<CommunityNode> lowestIncomeCommunities ( String stateName ) {
        ArrayList<CommunityNode> coms = new ArrayList<CommunityNode>();
        for (StateNode i = firstState; i != null; i = i.next) {
            if (i.getName().equals(stateName)) {
                for (CountyNode j = i.down; j != null; j = j.next) {
                    for (CommunityNode k = j.down; k != null; k = k.next) {
                        if (coms.size() < 10) {
                            coms.add(k);
                        }
                        else {
                            double min = coms.get(0).getInfo().getPercentPovertyLine();
                            int replace = 0;
                            for (int l = 0; l < 10; l++) {
                                if (min > coms.get(l).getInfo().getPercentPovertyLine()) {
                                    min = coms.get(l).getInfo().getPercentPovertyLine();
                                    replace = l;
                                }
                            }
                            if (k.getInfo().getPercentPovertyLine() > min) {
                                coms.set(replace, k);
                            }
                        }
                    }
                }
            }
        }

        return coms; // replace this line
    }
}
    
