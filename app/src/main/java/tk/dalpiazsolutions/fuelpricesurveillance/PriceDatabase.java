package tk.dalpiazsolutions.fuelpricesurveillance;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tk.dalpiazsolutions.fuelpricesurveillance.dao.ItemDAO;
import tk.dalpiazsolutions.fuelpricesurveillance.models.Item;

@Database(entities = {Item.class}, version = 1)
public abstract class PriceDatabase extends RoomDatabase {
    public abstract ItemDAO getItemDAO();
}
