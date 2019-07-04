package tk.dalpiazsolutions.fuelpricesurveillance.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "prices")
public class Item {
    @PrimaryKey
    private Long id;

    @ColumnInfo(name = "price")
    private float price;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "tankName")
    private String tankName;

    public boolean isNotExact() {
        return notExact;
    }

    public void setNotExact(boolean notExact) {
        this.notExact = notExact;
    }

    @ColumnInfo(name = "notExact")
    private boolean notExact;

    public String getTankName() {
        return tankName;
    }

    public void setTankName(String tankName) {
        this.tankName = tankName;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
