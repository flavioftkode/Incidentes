package ipvc.estg.incidentes.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.Note
import kotlinx.android.synthetic.main.in_note_fragment.*
import kotlinx.android.synthetic.main.in_note_fragment.view.*
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
        var alarm: ImageView
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
            alarm = view.findViewById(R.id.note_alarm)


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
        holder.color.setBackgroundColor(Color.parseColor(note.color))

        if(note.notification){
            holder.alarm.visibility = View.VISIBLE
        }

        when (note.color) {
            "#B2EDFF" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_blue)
            }
            "#D6FF99" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_green)
            }
            "#F2F2F2" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_grey)
            }
            "#BAFF8A" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_lime)
            }
            "#F6BBFF" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_pink)
            }
            "#F9648A" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_purple)
            }
            "#F17957" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_red)
            }
            "#5E92E5" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_royal)
            }
            "#FEF399" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_yellow)
            }
            "#F3F765" ->{
                holder.viewForeground.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_yellow_darker)
            }
        }

        var height = Resources.getSystem().displayMetrics.heightPixels.toString()
        var screen = convertPxToDp(context,(height.toFloat())) - 100
        var recycler = ((position +1) * 80)

        Log.e("screen", screen.toString())
        Log.e("recycler", recycler.toString())

        if(recycler > screen){
            addLastChildMargin(holder, 200, notesListFiltered.size, position)
        }


    }

    fun convertPxToDp(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
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
                        if (row.title.toLowerCase(Locale.ROOT).contains(charSequence)||row.description.toLowerCase(
                                Locale.ROOT
                            ).contains(charSequence) || row.date.toLowerCase(Locale.ROOT).contains(
                                charSequence
                            )
                            || row.date.toLowerCase(Locale.ROOT).contains(charSequence) || row.hour.toLowerCase(
                                Locale.ROOT
                            ).contains(charSequence)) {
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

    fun getItemById(id: Int): Note {
        for (i in 0 until notesListFiltered.size) {
            if(notesListFiltered[i].id == id){
                return notesListFiltered[i];
            }
        }
        return notesListFiltered[0]
    }

    fun removeItem(position: Int) {
        notesListFiltered.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun addLastChildMargin(
        holder: RecyclerView.ViewHolder,
        margin: Int,
        recyclerListSize: Int,
        position: Int
    ) {
        if (recyclerListSize - 1 == position) {
            //holder.itemView.setPadding(0, 0, 0, padding)
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = margin
            holder.itemView.layoutParams = params
        }
    }

    init {

        setHasStableIds(true)
        this.listener = listener
        this.notesList = notesList!!
        notesListFiltered = notesList!!
    }
}