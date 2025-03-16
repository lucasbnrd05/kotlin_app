package com.example.imctrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CoordinatesEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coordinatesDao(): ICoordinatesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coordinates_database"
                ).fallbackToDestructiveMigration().addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                val lieuDao = database.coordinatesDao()
                                val initialData = listOf(
                                    CoordinatesEntity(name = "Tennis", latitude = 40.38779608214728, longitude = -3.627687914352839),
                                    CoordinatesEntity(name = "Futsal outdoors", latitude = 40.38788595319803, longitude = -3.627048250272035),
                                    CoordinatesEntity(name = "Fashion and design", latitude = 40.3887315224542, longitude = -3.628643539758645),
                                    CoordinatesEntity(name = "Topos", latitude = 40.38926842612264, longitude = -3.630067893975619),
                                    CoordinatesEntity(name = "Teleco", latitude = 40.38956358584258, longitude = -3.629046081389352),
                                    CoordinatesEntity(name = "ETSISI", latitude = 40.38992125672989, longitude = -3.6281366497769714),
                                    CoordinatesEntity(name = "Library", latitude = 40.39037466191718, longitude = -3.6270256763598447),
                                    CoordinatesEntity(name = "CITSEM", latitude = 40.389855884803005, longitude = -3.626782180787362)
                                )
                                lieuDao.insertAll(initialData)
                            }
                        }
                    }
                })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}