package spiderman;

public class People {
    private String name;
    private int currentDimension;
    private int homeDimension;
    private boolean isSpider;

    public People (String name, int currentDimension, int homeDimension, boolean isSpider) {
        this.name = name;
        this.currentDimension = currentDimension;
        this.homeDimension = homeDimension;
        this.isSpider = isSpider;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCurrentDim() { return currentDimension; }
    public void setCurrentDimension(int currentDimension) { this.currentDimension = currentDimension; }

    public int getHomeDimension() { return homeDimension;}

    public boolean getSpider() { return isSpider; }
    public void setSpider(boolean isSpider) { this.isSpider = isSpider; }
}
