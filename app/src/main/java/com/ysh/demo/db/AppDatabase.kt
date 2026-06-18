package com.ysh.demo.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ysh.demo.MainApplication


@Database(entities = [MinioInfo::class], version = 1) abstract class AppDatabase : RoomDatabase() {
    abstract fun minioDao(): MinioDao

    companion object {

        private var instance: AppDatabase? = null
        @JvmStatic
        fun getDatabase(): AppDatabase {
            if (instance == null || !instance!!.isOpen) {
                instance = synchronized(this) {
                    Room.databaseBuilder(MainApplication.instance, AppDatabase::class.java,
                        "xcsz.db").build()
                }
            }
            return instance!!
        }
    }
}