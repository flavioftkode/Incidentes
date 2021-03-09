package ipvc.estg.incidentes.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ipvc.estg.incidentes.entities.Note


@Dao
interface NotesDao {

    @Query("SELECT * from notes")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()

    @Query("DELETE FROM notes where id == :id")
    suspend fun deleteById(id: Int)
}