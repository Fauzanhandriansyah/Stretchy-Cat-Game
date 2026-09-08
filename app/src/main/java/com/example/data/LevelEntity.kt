package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "level_progress")
data class LevelProgress(
    @PrimaryKey val levelId: Int,
    val isCompleted: Boolean,
    val stars: Int,
    val bestTimeSeconds: Int
)

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress ORDER BY levelId ASC")
    fun getAllProgress(): Flow<List<LevelProgress>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId LIMIT 1")
    suspend fun getProgressById(levelId: Int): LevelProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LevelProgress)

    @Query("DELETE FROM level_progress")
    suspend fun clearAllProgress()
}

@Database(entities = [LevelProgress::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun levelProgressDao(): LevelProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stretchy_cat_database_v2"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class LevelRepository(private val dao: LevelProgressDao) {
    val allProgress: Flow<List<LevelProgress>> = dao.getAllProgress()

    suspend fun getProgressById(levelId: Int): LevelProgress? {
        return dao.getProgressById(levelId)
    }

    suspend fun saveProgress(progress: LevelProgress) {
        dao.saveProgress(progress)
    }

    suspend fun clearAllProgress() {
        dao.clearAllProgress()
    }
}
