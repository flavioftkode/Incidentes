package ipvc.estg.incidentes.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ipvc.estg.incidentes.dao.NotesDao
import ipvc.estg.incidentes.entities.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

// Annotates class to be a Room Database with a table (entity) of the City class

// Note: When you modify the database schema, you'll need to update the version number and define a migration strategy
//For a sample, a destroy and re-create strategy can be sufficient. But, for a real app, you must implement a migration strategy.

@Database(entities = arrayOf(Note::class), version = 13, exportSchema = false)
public abstract class NoteDB : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                   var noteDao = database.notesDao()
                    noteDao.deleteAll()

                    //val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    val dateFormat: DateFormat = SimpleDateFormat("dd MMMM")
                    val hourFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
                    val date = Date()


                    var city = Note(1, "Nota teste 1", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString())
                    noteDao.insert(city)
                    city = Note(2, "Nota teste 2", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#e8e8e8",0)
                    noteDao.insert(city)
                    city = Note(3, "Nota teste 3", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#DFC5F3",0)
                    noteDao.insert(city)
                    city = Note(4, "Nota teste 4", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#F1CCD9",0)
                    noteDao.insert(city)
                    city = Note(5, "Nota teste 5", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#B2DDEC",0)
                    noteDao.insert(city)
                    city = Note(6, "Nota teste 6", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#FFADAD",0)
                    noteDao.insert(city)
                    city = Note(7, "Nota teste 7", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#E3F1B9",0)
                    noteDao.insert(city)
                    city = Note(8, "Nota teste 8", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#EDC0B3",0)
                    noteDao.insert(city)
                    city = Note(9, "Nota teste 9", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#EDDCA8",0)
                    noteDao.insert(city)
                    city = Note(10, "Nota teste 10", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#FFADAD",0)
                    noteDao.insert(city)
                    city = Note(11, "Nota teste 11", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#F1CCD9",0)
                    noteDao.insert(city)
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NoteDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NoteDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDB::class.java,
                    "notes_database",
                )
                    //estratégia de destruição
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}