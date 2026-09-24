package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.LiveClassSession
import com.example.data.model.NotificationItem
import com.example.data.model.PaymentConfig
import com.example.data.model.PaymentRequest
import com.example.data.model.Project
import com.example.data.model.Review
import com.example.data.model.User

@Database(
    entities = [
        User::class,
        Project::class,
        NotificationItem::class,
        Review::class,
        LiveClassSession::class,
        PaymentRequest::class,
        PaymentConfig::class
    ],
    version = 9,
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

        private const val DB_NAME = "innovatex_v9.db"

        private val MIGRATION_6_9 = object : Migration(6, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `reviews` (`id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `reviewerId` TEXT NOT NULL, `reviewerName` TEXT NOT NULL, `action` TEXT NOT NULL, `studentFeedback` TEXT NOT NULL, `privateNotes` TEXT NOT NULL, `badgeAwarded` TEXT, `rating` INTEGER NOT NULL, `reviewerRole` TEXT NOT NULL, `constructiveTip` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `live_classes` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `instructorName` TEXT NOT NULL, `subject` TEXT NOT NULL, `dateTimeText` TEXT NOT NULL, `zoomMeetingId` TEXT NOT NULL, `zoomPassword` TEXT NOT NULL, `zoomLink` TEXT NOT NULL, `description` TEXT NOT NULL, `priceText` TEXT NOT NULL, `isLiveNow` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `scheduledTimestamp` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }

        private val MIGRATION_7_9 = object : Migration(7, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `reviews` (`id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `reviewerId` TEXT NOT NULL, `reviewerName` TEXT NOT NULL, `action` TEXT NOT NULL, `studentFeedback` TEXT NOT NULL, `privateNotes` TEXT NOT NULL, `badgeAwarded` TEXT, `rating` INTEGER NOT NULL, `reviewerRole` TEXT NOT NULL, `constructiveTip` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Schema identical between 8 and 9
            }
        }

        private fun createLegacyMigration(from: Int, to: Int): Migration = object : Migration(from, to) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `reviews` (`id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `reviewerId` TEXT NOT NULL, `reviewerName` TEXT NOT NULL, `action` TEXT NOT NULL, `studentFeedback` TEXT NOT NULL, `privateNotes` TEXT NOT NULL, `badgeAwarded` TEXT, `rating` INTEGER NOT NULL, `reviewerRole` TEXT NOT NULL, `constructiveTip` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `live_classes` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `instructorName` TEXT NOT NULL, `subject` TEXT NOT NULL, `dateTimeText` TEXT NOT NULL, `zoomMeetingId` TEXT NOT NULL, `zoomPassword` TEXT NOT NULL, `zoomLink` TEXT NOT NULL, `description` TEXT NOT NULL, `priceText` TEXT NOT NULL, `isLiveNow` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `scheduledTimestamp` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE != null) return INSTANCE!!
                var db: AppDatabase
                try {
                    db = buildDatabase(context)
                    // Trigger writableDatabase open synchronously to safely catch and recover from migration errors
                    db.openHelper.writableDatabase
                } catch (t: Throwable) {
                    android.util.Log.e("AppDatabase", "Database initialization/migration failed, recovering with clean database: ${t.message}", t)
                    try {
                        context.deleteDatabase(DB_NAME)
                        context.deleteDatabase("innovatex_v8.db")
                    } catch (_: Throwable) {}
                    db = buildDatabase(context)
                    try {
                        db.openHelper.writableDatabase
                    } catch (t2: Throwable) {
                        android.util.Log.e("AppDatabase", "Second attempt fallback: ${t2.message}", t2)
                    }
                }
                INSTANCE = db
                db
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .addMigrations(
                    MIGRATION_6_9,
                    MIGRATION_7_9,
                    MIGRATION_8_9,
                    createLegacyMigration(1, 9),
                    createLegacyMigration(2, 9),
                    createLegacyMigration(3, 9),
                    createLegacyMigration(4, 9),
                    createLegacyMigration(5, 9)
                )
                .fallbackToDestructiveMigration(true)
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .build()
        }
    }
}
