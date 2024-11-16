package com.example.muzz.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database for storing messages.
 *
 * This class provides the database instance and ensures thread-safe access
 * via a singleton pattern. It includes the DAO for performing database operations.
 */
@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class MessageDatabase : RoomDatabase() {

    /**
     * Provides access to the DAO for message-related database operations.
     */
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDatabase? = null

        /**
         * Returns the singleton instance of the database.
         *
         * - Uses a `synchronized` block to ensure only one instance of the database is created,
         *   even when accessed from multiple threads.
         * - The `@Volatile` annotation ensures that changes to `INSTANCE` are immediately visible
         *   to all threads.
         *
         * @param context The application context, used to initialize the database.
         */
        fun getDatabase(context: Context): MessageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context to avoid memory leaks
                    MessageDatabase::class.java, // The database class
                    "message_database" // Name of the database file
                ).build()
                INSTANCE = instance // Cache the instance for future access
                instance
            }
        }
    }
}