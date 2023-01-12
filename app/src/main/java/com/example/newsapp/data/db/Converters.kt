package com.example.newsapp.data.db

import androidx.room.TypeConverter
import com.example.newsapp.data.model.Source

class Converters {
    @TypeConverter
    fun fromSource(source: Source) = source.name

    @TypeConverter
    fun toSource(name: String) = Source(null, name)
}