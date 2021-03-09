package ipvc.estg.incidentes.db

import androidx.lifecycle.LiveData
import ipvc.estg.incidentes.dao.NotesDao
import ipvc.estg.incidentes.entities.Note

class NoteRepository(private val notesDao: NotesDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allNotes: LiveData<List<Note>> = notesDao.getAllNotes()

    suspend fun insert(note: Note) {
        notesDao.insert(note)
    }

    suspend fun deleteAll(){
        notesDao.deleteAll()
    }

    suspend fun deleteById(id: Int){
        notesDao.deleteById(id)
    }

   /* fun getCitiesByCountry(country: String): LiveData<List<Note>> {
        return notesDao.getCitiesByCountry(country)
    }

    fun getCountryFromCity(city: String): LiveData<Note> {
        return notesDao.getCountryFromCity(city)
    }*/

    /*suspend fun insert(city: Note) {
        notesDao.insert(city)
    }

    suspend fun deleteAll(){
        notesDao.deleteAll()
    }*/

   /* suspend fun deleteByCity(city: String){
        notesDao.deleteByCity(city)
    }*/

   /* suspend fun updateCity(city: City) {
        cityDao.updateCity(city)
    }

    suspend fun updateCountryFromCity(city: String, country: String){
        cityDao.updateCountryFromCity(city, country)
    }*/
}