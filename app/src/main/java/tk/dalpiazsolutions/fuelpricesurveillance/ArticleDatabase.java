package tk.dalpiazsolutions.fuelpricesurveillance;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tk.dalpiazsolutions.fuelpricesurveillance.dao.ArticleDAO;
import tk.dalpiazsolutions.fuelpricesurveillance.models.Article;

@Database(entities = {Article.class}, version = 1)
public abstract class ArticleDatabase extends RoomDatabase {
    public abstract ArticleDAO getArticleDAO();
}
