package ipvc.estg.incidentes.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.Note
import java.util.*

class NotesAdapter(notesList: MutableList<Note>, listener: NotesAdapterListener, context: Context) : RecyclerView.Adapter<NotesAdapter.MyViewHolder>(), Filterable {
    private var notesList: MutableList<Note>
    private var notesListFiltered: MutableList<Note>
    private val listener: NotesAdapterListener
    var context : Context = context

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
      /*  var content: TextView*/
        var viewBackground: RelativeLayout
        var viewForeground: RelativeLayout
       /* var image: ImageView*/
        var color: View
        var date: TextView
        var time: TextView

        init {
            title = view.findViewById(R.id.title)

          /*  content = view.findViewById(R.id.content)*/
            color = view.findViewById(R.id.priority)
            date = view.findViewById(R.id.date)
            time = view.findViewById(R.id.time)
            viewBackground = view.findViewById(R.id.view_background)
            viewForeground = view.findViewById(R.id.view_foreground)
           /* image = view.findViewById(R.id.image)*/


            view.setOnClickListener { // send selected contact in callback
                listener.onNoteSelected(notesListFiltered[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapter.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotesAdapter.MyViewHolder, position: Int) {
        val note = notesListFiltered[position]
        holder.title.text = note.title
        holder.date.text = note.date
        holder.time.text = note.hour
        holder.viewForeground.setBackgroundColor(Color.parseColor(note.color))
        when (note.color) {
            "#DFC5F3" -> {
                holder.color.setBackgroundResource(R.color.cpb_purple)
            }
            "#F1CCD9" -> {
                holder.color.setBackgroundResource(R.color.cpb_pink)
            }
            "#B2DDEC" -> {
                holder.color.setBackgroundResource(R.color.cpb_blue)
            }
            "#FFADAD" -> {
                holder.color.setBackgroundResource(R.color.cpb_red)
            }
            "#E3F1B9" -> {
                holder.color.setBackgroundResource(R.color.cpb_green)
            }
            "#EDC0B3" -> {
                holder.color.setBackgroundResource(R.color.cpb_orange)
            }
            "#EDDCA8" -> {
                holder.color.setBackgroundResource(R.color.cpb_yellow)
            }
            else -> {
                holder.color.setBackgroundResource(R.color.cpb_grey)
            }

        }
    }

    override fun getItemCount(): Int {
        return notesListFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                notesListFiltered = if (charString.isEmpty()) {
                    notesList
                } else {
                    val filteredList: MutableList<Note> =
                        ArrayList()
                    for (row in notesList) {
                        if (row.title.toLowerCase(Locale.ROOT).contains(charSequence)||row.description.toLowerCase(Locale.ROOT).contains(charSequence) || row.date.toLowerCase(Locale.ROOT).contains(charSequence)
                            || row.date.toLowerCase(Locale.ROOT).contains(charSequence) || row.hour.toLowerCase(Locale.ROOT).contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = notesListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                notesListFiltered = filterResults.values as MutableList<Note>
                notifyDataSetChanged()
            }
        }
    }

    interface NotesAdapterListener {
        fun onNoteSelected(note: Note?)
    }

    fun getItem(position: Int): Note {
        return notesListFiltered[position]
    }

    fun removeItem(position: Int) {
        notesListFiltered.removeAt(position)
        notifyItemRemoved(position)
    }

    init {

        setHasStableIds(true)
        this.listener = listener
        this.notesList = notesList!!
        notesListFiltered = notesList!!
    }
}