package com.example.citiessweather.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.citiessweather.cities.City;

@Database(entities = {City.class}, version = 1)
public abstract class DataManager extends RoomDatabase {

    private static DataManager INSTANCE;

    public static DataManager getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(
                            context.getApplicationContext(),
                            DataManager.class, "db"
                    ).build();
        }
        return INSTANCE;
    }

    public abstract CitiesDao getCitiesDao();
}