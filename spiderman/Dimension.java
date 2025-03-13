package spiderman;

public class Dimension {
    private int number; //Dimension Number
    private int canonEvents; //number of canon events
    private int weight; //dimension weight
    private Dimension next; //next dimension



    //constructor
    public Dimension (int number, int canonEvents, int weight, Dimension next) {
        this.number = number;
        this.canonEvents = canonEvents;
        this.weight = weight;
        this.next = next;
    }

    //getter methods
    public int getNumber() { return number; }
    public int getCanonEvents() { return canonEvents; }
    public int getWeight() { return weight; }
    public Dimension getNextDimension() { return next; }

    //setter methods
    public void setNumber(int number) { this.number = number; }
    public void setCanonEvents(int canonEvents) { this.canonEvents = canonEvents; }
    public void setWeight(int weight) { this.weight = weight; }
    public void setNextDimension(Dimension next) { this.next = next; }
    
}
