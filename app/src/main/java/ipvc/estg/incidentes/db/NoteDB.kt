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

@Database(entities = arrayOf(Note::class), version = 14, exportSchema = false)
abstract class NoteDB : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                  /* var noteDao = database.notesDao()
                    noteDao.deleteAll()

                    //val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    val dateFormat: DateFormat = SimpleDateFormat("dd MMMM")
                    val hourFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
                    val date = Date()


                    var city = Note(1, "Ir as compras", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#B2EDFF",0)
                    noteDao.insert(city)
                    city = Note(2, "Não passar ali ao lado", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#D6FF99",0)
                    noteDao.insert(city)
                    city = Note(3, "Pagar a conta da luz", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#F2F2F2",0)
                    noteDao.insert(city)
                    city = Note(4, "Talho", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#BAFF8A",0)
                    noteDao.insert(city)
                    city = Note(5, "Comprar Arroz", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#F6BBFF",0)
                    noteDao.insert(city)
                    city = Note(6, "Instalar o Windows", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#F9648A",0)
                    noteDao.insert(city)
                    city = Note(7, "Fazer desporto", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#F17957",0)
                    noteDao.insert(city)
                    city = Note(8, "Roubar", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#5E92E5",0)
                    noteDao.insert(city)
                    city = Note(9, "Aniversário do jaquim", "Portugal",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#FEF399",0)
                    noteDao.insert(city)
                    city = Note(10, "Nota em kotlin para lembrar de nada", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Felis bibendum ut tristique et egestas. Amet justo donec enim diam vulputate ut. Habitasse platea dictumst quisque sagittis. Arcu ac tortor dignissim convallis aenean et tortor at risus. Pellentesque pulvinar pellentesque habitant morbi tristique senectus et. Eget aliquet nibh praesent tristique magna sit amet purus gravida. Vel fringilla est ullamcorper eget nulla facilisi etiam. Egestas egestas fringilla phasellus faucibus scelerisque eleifend. Ac felis donec et odio pellentesque. Scelerisque purus semper eget duis at tellus. Vitae justo eget magna fermentum iaculis eu.\n" +
                            "\n" +
                            "Fringilla ut morbi tincidunt augue. Massa enim nec dui nunc mattis enim ut. Porttitor eget dolor morbi non. Sed arcu non odio euismod. Ut porttitor leo a diam sollicitudin tempor id eu nisl. Posuere sollicitudin aliquam ultrices sagittis orci a scelerisque purus semper. Imperdiet proin fermentum leo vel. Sem nulla pharetra diam sit amet nisl suscipit adipiscing. Integer quis auctor elit sed vulputate mi sit amet mauris. Quis imperdiet massa tincidunt nunc. Sit amet mattis vulputate enim nulla aliquet porttitor lacus. Pellentesque id nibh tortor id aliquet lectus. Sit amet tellus cras adipiscing enim. Interdum velit laoreet id donec ultrices tincidunt arcu non. Placerat vestibulum lectus mauris ultrices eros in cursus. Pellentesque habitant morbi tristique senectus et netus et malesuada. Bibendum arcu vitae elementum curabitur vitae nunc sed. Imperdiet sed euismod nisi porta lorem mollis.\n" +
                            "\n" +
                            "Nunc sed augue lacus viverra vitae congue eu. A diam sollicitudin tempor id eu nisl. Fringilla ut morbi tincidunt augue interdum. Ut porttitor leo a diam. Pellentesque nec nam aliquam sem et. Vehicula ipsum a arcu cursus vitae. Ut eu sem integer vitae justo eget. Duis tristique sollicitudin nibh sit amet commodo. Sem fringilla ut morbi tincidunt augue. Augue mauris augue neque gravida in fermentum et. Enim nulla aliquet porttitor lacus luctus accumsan tortor posuere. Urna cursus eget nunc scelerisque viverra mauris in aliquam sem. Vel elit scelerisque mauris pellentesque pulvinar pellentesque habitant morbi. Eu mi bibendum neque egestas. Et egestas quis ipsum suspendisse. Dolor sit amet consectetur adipiscing elit duis. Pellentesque habitant morbi tristique senectus.\n" +
                            "\n" +
                            "Sodales ut etiam sit amet nisl purus. Mauris rhoncus aenean vel elit scelerisque mauris pellentesque. Turpis egestas pretium aenean pharetra magna. Volutpat commodo sed egestas egestas fringilla phasellus faucibus scelerisque eleifend. Tortor vitae purus faucibus ornare suspendisse. Nunc aliquet bibendum enim facilisis gravida neque convallis a. Congue quisque egestas diam in arcu. Etiam sit amet nisl purus in mollis. Bibendum arcu vitae elementum curabitur vitae. A diam sollicitudin tempor id eu nisl nunc mi. Lectus magna fringilla urna porttitor rhoncus dolor purus non enim. Dolor sit amet consectetur adipiscing elit ut aliquam. Maecenas pharetra convallis posuere morbi leo urna molestie at. Eu feugiat pretium nibh ipsum consequat nisl vel pretium lectus. Accumsan in nisl nisi scelerisque eu. Lectus magna fringilla urna porttitor rhoncus dolor purus non. Aliquam sem et tortor consequat.\n" +
                            "\n" +
                            "At lectus urna duis convallis convallis. Tristique senectus et netus et malesuada fames ac. Amet consectetur adipiscing elit ut aliquam purus sit amet. Ut placerat orci nulla pellentesque dignissim enim. Leo duis ut diam quam nulla porttitor massa id. Lacus sed viverra tellus in hac habitasse platea dictumst. Interdum varius sit amet mattis vulputate enim nulla aliquet porttitor. Quis ipsum suspendisse ultrices gravida. Auctor elit sed vulputate mi sit amet mauris commodo. Libero nunc consequat interdum varius sit. Sed euismod nisi porta lorem mollis aliquam ut porttitor. Maecenas accumsan lacus vel facilisis volutpat est. Viverra tellus in hac habitasse platea dictumst. Metus aliquam eleifend mi in nulla posuere sollicitudin aliquam. Non enim praesent elementum facilisis leo vel fringilla est. Lectus quam id leo in vitae.\n" +
                            "\n" +
                            "Iaculis nunc sed augue lacus. Pellentesque habitant morbi tristique senectus et netus et. Donec ac odio tempor orci dapibus ultrices in. Aliquam sem et tortor consequat. Egestas fringilla phasellus faucibus scelerisque. Eget arcu dictum varius duis at. Quisque sagittis purus sit amet volutpat consequat mauris. Morbi tincidunt augue interdum velit. Eu ultrices vitae auctor eu augue ut. Enim ut sem viverra aliquet eget sit. Est pellentesque elit ullamcorper dignissim cras tincidunt. Nisi est sit amet facilisis magna etiam tempor orci eu. Hac habitasse platea dictumst quisque. Tristique magna sit amet purus gravida. Blandit aliquam etiam erat velit. Dui faucibus in ornare quam viverra.\n" +
                            "\n" +
                            "Et egestas quis ipsum suspendisse. Phasellus egestas tellus rutrum tellus. Velit ut tortor pretium viverra. Volutpat ac tincidunt vitae semper quis lectus nulla at volutpat. Ut tellus elementum sagittis vitae et. Netus et malesuada fames ac turpis egestas sed tempus urna. Viverra tellus in hac habitasse. Pretium nibh ipsum consequat nisl vel pretium lectus. Morbi enim nunc faucibus a pellentesque sit amet porttitor eget. Iaculis urna id volutpat lacus. Tincidunt dui ut ornare lectus sit. Interdum velit laoreet id donec ultrices tincidunt arcu.\n" +
                            "\n" +
                            "Commodo viverra maecenas accumsan lacus vel facilisis volutpat est. Porta nibh venenatis cras sed. Magna ac placerat vestibulum lectus mauris ultrices eros in. Vitae tortor condimentum lacinia quis vel eros donec ac. Purus in massa tempor nec feugiat. Elementum curabitur vitae nunc sed. Semper feugiat nibh sed pulvinar proin gravida hendrerit lectus a. Morbi non arcu risus quis varius quam quisque. Massa enim nec dui nunc. Nibh praesent tristique magna sit amet. Proin nibh nisl condimentum id venenatis. Nisi porta lorem mollis aliquam ut porttitor leo. Auctor urna nunc id cursus metus aliquam. Eros in cursus turpis massa tincidunt dui ut ornare lectus.\n" +
                            "\n" +
                            "Eget nunc lobortis mattis aliquam faucibus purus. Scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus et. Commodo viverra maecenas accumsan lacus. Nibh mauris cursus mattis molestie a. Sit amet consectetur adipiscing elit ut. Aliquam etiam erat velit scelerisque in dictum non consectetur. Sollicitudin nibh sit amet commodo nulla facilisi. Et netus et malesuada fames ac turpis egestas sed. Suspendisse in est ante in. Non tellus orci ac auctor augue mauris. Id faucibus nisl tincidunt eget. Aliquam etiam erat velit scelerisque in. Ac turpis egestas integer eget aliquet nibh praesent tristique magna. Nulla aliquet porttitor lacus luctus accumsan tortor posuere. Vel orci porta non pulvinar neque laoreet suspendisse interdum. Amet cursus sit amet dictum sit. Felis donec et odio pellentesque diam volutpat commodo sed. Tempus iaculis urna id volutpat. Dui vivamus arcu felis bibendum ut. Amet venenatis urna cursus eget nunc scelerisque viverra mauris in.\n" +
                            "\n" +
                            "Auctor elit sed vulputate mi sit amet mauris commodo quis. Diam maecenas sed enim ut. Aliquet sagittis id consectetur purus. Dolor morbi non arcu risus quis varius quam quisque id. Risus commodo viverra maecenas accumsan lacus vel facilisis volutpat. Non curabitur gravida arcu ac tortor. Morbi tempus iaculis urna id volutpat lacus laoreet non. Volutpat odio facilisis mauris sit amet. Euismod quis viverra nibh cras pulvinar mattis. Diam maecenas ultricies mi eget mauris pharetra. Id nibh tortor id aliquet lectus proin nibh nisl condimentum. Diam donec adipiscing tristique risus nec. Nisi est sit amet facilisis magna etiam tempor orci. Donec et odio pellentesque diam volutpat commodo sed egestas. Turpis nunc eget lorem dolor sed viverra ipsum nunc aliquet. Odio morbi quis commodo odio aenean sed adipiscing diam. Vestibulum lectus mauris ultrices eros in cursus turpis.\n" +
                            "\n" +
                            "In pellentesque massa placerat duis. Cursus metus aliquam eleifend mi in nulla posuere. Blandit volutpat maecenas volutpat blandit. Sagittis purus sit amet volutpat consequat mauris nunc congue. Sem nulla pharetra diam sit amet nisl suscipit adipiscing bibendum. Pellentesque eu tincidunt tortor aliquam nulla. Sed nisi lacus sed viverra tellus. Vivamus arcu felis bibendum ut tristique et egestas quis ipsum. Risus pretium quam vulputate dignissim suspendisse in. Imperdiet proin fermentum leo vel. Aliquet lectus proin nibh nisl condimentum id. Quam quisque id diam vel quam.\n" +
                            "\n" +
                            "Sollicitudin ac orci phasellus egestas tellus rutrum tellus. Sed id semper risus in hendrerit gravida. Eu ultrices vitae auctor eu. Quisque id diam vel quam elementum pulvinar etiam. Dui sapien eget mi proin sed libero enim. Tincidunt tortor aliquam nulla facilisi cras fermentum odio. Egestas diam in arcu cursus euismod quis viverra nibh cras. Eget mauris pharetra et ultrices neque. Tellus at urna condimentum mattis pellentesque id nibh tortor id. Maecenas accumsan lacus vel facilisis. Mattis aliquam faucibus purus in massa tempor nec feugiat. Risus nec feugiat in fermentum posuere urna nec. Viverra tellus in hac habitasse platea dictumst vestibulum rhoncus est. Id velit ut tortor pretium viverra suspendisse potenti. Sed libero enim sed faucibus turpis. Malesuada bibendum arcu vitae elementum curabitur vitae. Ac turpis egestas sed tempus urna. Id diam vel quam elementum. Ipsum consequat nisl vel pretium lectus quam id leo in.\n" +
                            "\n" +
                            "Et odio pellentesque diam volutpat commodo sed. Mauris commodo quis imperdiet massa tincidunt nunc. Nunc consequat interdum varius sit amet mattis vulputate enim nulla. Ultrices gravida dictum fusce ut placerat orci. Id aliquet risus feugiat in ante metus dictum. Consectetur adipiscing elit pellentesque habitant morbi tristique senectus. Nam libero justo laoreet sit amet cursus sit amet dictum. Quisque non tellus orci ac auctor augue mauris augue neque. Sodales ut etiam sit amet nisl. Cursus mattis molestie a iaculis at erat pellentesque adipiscing. Pellentesque habitant morbi tristique senectus et netus et malesuada fames. Eget dolor morbi non arcu risus quis varius quam. Ornare arcu dui vivamus arcu felis bibendum ut tristique. Aliquet sagittis id consectetur purus. Pellentesque nec nam aliquam sem et tortor consequat. Ut tellus elementum sagittis vitae et leo duis ut diam. Viverra nam libero justo laoreet sit amet. Vel orci porta non pulvinar neque.\n" +
                            "\n" +
                            "In nisl nisi scelerisque eu. Urna duis convallis convallis tellus id interdum velit. At varius vel pharetra vel turpis. Semper eget duis at tellus at urna condimentum mattis pellentesque. Nulla porttitor massa id neque aliquam. Elementum nisi quis eleifend quam. Fermentum et sollicitudin ac orci phasellus egestas tellus. Pulvinar elementum integer enim neque volutpat ac tincidunt. Pellentesque habitant morbi tristique senectus et netus et malesuada fames. Amet consectetur adipiscing elit duis tristique. Tellus cras adipiscing enim eu turpis egestas pretium. Porttitor leo a diam sollicitudin. Quis eleifend quam adipiscing vitae proin sagittis nisl rhoncus mattis. Volutpat lacus laoreet non curabitur gravida arcu ac tortor. Blandit aliquam etiam erat velit scelerisque in.\n" +
                            "\n" +
                            "Odio pellentesque diam volutpat commodo. Lorem dolor sed viverra ipsum nunc aliquet bibendum enim. Ultrices eros in cursus turpis massa tincidunt dui ut ornare. Lacus suspendisse faucibus interdum posuere lorem ipsum dolor sit. Ipsum dolor sit amet consectetur adipiscing elit. Lobortis scelerisque fermentum dui faucibus in ornare quam viverra orci. Est ullamcorper eget nulla facilisi etiam dignissim diam quis enim. Nunc sed augue lacus viverra vitae congue eu consequat. Nulla facilisi morbi tempus iaculis. Cursus risus at ultrices mi. Tincidunt eget nullam non nisi est sit. Pellentesque id nibh tortor id. Viverra nam libero justo laoreet sit amet cursus sit amet. Viverra maecenas accumsan lacus vel facilisis volutpat. Id aliquet risus feugiat in ante metus dictum. Tristique senectus et netus et malesuada fames ac turpis. Facilisi etiam dignissim diam quis enim lobortis scelerisque. Tincidunt arcu non sodales neque sodales ut etiam sit amet. Sed odio morbi quis commodo odio aenean sed.",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#F3F765",0)
                    noteDao.insert(city)
                    city = Note(11, "Nota bastante grande que até o cumprimento sai da lista em que o titulo nem devia ser assim tao grande porque assim da estouro e sai fora e fica mal", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",dateFormat.format(date).toString(),hourFormat.format(date).toString(),"#F6BBFF",0)
                    noteDao.insert(city)*/
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