package tk.dalpiazsolutions.fuelpricesurveillance.dao;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import tk.dalpiazsolutions.fuelpricesurveillance.models.Article;
import tk.dalpiazsolutions.fuelpricesurveillance.models.Item;

@Dao
public interface ArticleDAO {
    @Insert
    public void insert(Article... articles);
    @Update
    public void update(Article... articles);
    @Delete
    public void delete(Article article);
    @Query("SELECT * FROM articles")
    public List<Article> getArticles();
    @Query("SELECT * FROM articles WHERE id = :id")
    public Article getArticleById(Long id);
    @Query("DELETE FROM articles")
    public void nukeTable();
}
