package com.example.mvvmnewsapplication.data

import androidx.room.TypeConverter
import com.example.mvvmnewsapplication.models.Source

/*
* using to can Room Database converts class in source
* becose he count no the source in parameter */

class Converts {

    @TypeConverter
    fun fromSource(source:Source):String{
        return source.name
    }

    @TypeConverter
    fun toSource(name:String):Source{

        return Source(name,name)
    }
}