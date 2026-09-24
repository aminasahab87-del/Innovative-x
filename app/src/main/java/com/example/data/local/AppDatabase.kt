package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.LiveClassSession
import com.example.data.model.NotificationItem
import com.example.data.model.PaymentRequest
import com.example.data.model.Project
import com.example.data.model.Review
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Project::class,
        NotificationItem::class,
        Review::class,
        LiveClassSession::class,
        PaymentRequest::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao
    abstract fun liveClassDao(): LiveClassDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = try {
                    buildDatabase(context)
                } catch (t: Throwable) {
                    try {
                        context.deleteDatabase("innovatex_v7.db")
                    } catch (_: Throwable) {}
                    buildDatabase(context)
                }
                INSTANCE = db
                db
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "innovatex_v7.db"
            )
                .fallbackToDestructiveMigration(true)
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .build()
        }
    }
}
