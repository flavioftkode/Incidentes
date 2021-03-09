package ipvc.estg.incidentes.dao

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ipvc.estg.incidentes.entities.Note


@Dao
interface NotesDao {

    @Query("SELECT * from notes order by id desc")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()

    @Query("DELETE FROM notes where id == :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM notes WHERE id == :id")
    fun getNoteById(id: Int): LiveData<Note>

    @Query("UPDATE notes SET colorId = :colorId,color = :color,description=:description,title=:title WHERE id == :id")
    suspend fun updateNote(id: Int,title:String,description:String,color: String,colorId:Int)
}