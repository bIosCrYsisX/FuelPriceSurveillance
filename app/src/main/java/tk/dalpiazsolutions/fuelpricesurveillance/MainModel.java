package tk.dalpiazsolutions.fuelpricesurveillance;

/**
 * Created by Christoph on 08.06.2018.
 */

public class MainModel {

    private FuelService fuelService;
    private MainActivity mainActivity;
    private String completeSite;
    private float price;
    private int counter = 0;

    public MainModel(FuelService fuelService)
    {
        this.fuelService = fuelService;
    }

    public MainModel(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    public String getCompleteSite() {
        return completeSite;
    }

    public void setCompleteSite(String completeSite) {
        this.completeSite = completeSite;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
