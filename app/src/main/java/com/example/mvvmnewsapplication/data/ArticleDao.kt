package com.example.mvvmnewsapplication.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mvvmnewsapplication.models.Article

@Dao
interface ArticleDao {

    //remove Suspend from fun in Dao

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun upSet(article: Article):Long

    @Query("SELECT * FROM articles")
    fun getAllArticle():LiveData<List<Article>>

    @Delete
     fun deleteData(article: Article)
}