package tk.dalpiazsolutions.fuelpricesurveillance.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "articles")
public class Article {
    @PrimaryKey
    private Long id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "date")
    private String date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
