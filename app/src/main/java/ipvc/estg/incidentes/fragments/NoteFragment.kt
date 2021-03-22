package ipvc.estg.incidentes.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.Note
import ipvc.estg.incidentes.navigation.NavigationHost
import ipvc.estg.incidentes.vmodel.NotesViewModel
import kotlinx.android.synthetic.main.in_note_fragment.*
import kotlinx.android.synthetic.main.in_note_fragment.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NoteFragment : Fragment() {

    var selectedColor: Int? = 0
    var note:EditText? = null
    private lateinit var noteViewModel: NotesViewModel
    var colorHex :String = "#e8e8e8"
    var destination:String? = "create"
    var id:Int? = null
    var note_title:EditText? = null
    var note_body:EditText? = null
    var alarm: Boolean = false
    var myView : View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.in_note_fragment, container, false)
        myView = view
        note = view.findViewById(R.id.note_body)
        note!!.setBackgroundColor(Color.parseColor("#F2F2F2"))
        (activity as AppCompatActivity).setSupportActionBar(view!!.close_note)
        noteViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        note_title = view.findViewById(R.id.note_title)
        note_body = view.findViewById(R.id.note_body)

        val bundle = this.arguments
        if (bundle != null) {
            destination = bundle.getString("destination")
            id = bundle.getInt("id")
        }

        when (destination) {
            "update" -> {
                (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                    getString(R.string.note_edit)
                inputState(view, true);
                getNote(id, view);
            }
            "view" -> {
                (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                    getString(R.string.note_view)
                view!!.save_note.visibility = View.GONE;
                inputState(view, false);
                getNote(id, view);
            }
            else -> {
                (activity as AppCompatActivity?)!!.supportActionBar!!.title = getString(R.string.note_create)
                inputState(view, true);
                view.note_title_layout.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_grey)
            }
        }

        setClickListeners(view)

        return view

    }


    private fun inputState(view: View?, state: Boolean){

        view!!.note_title.isFocusable = state;
        view!!.note_title.isEnabled = state;
        view!!.note_title.isCursorVisible = state;

        view!!.note_body.isFocusable = state;
        view!!.note_body.isEnabled = state;
        view!!.note_body.isCursorVisible = state;
    }

    private fun getNote(id: Int?, view: View?){

        noteViewModel.getNoteById(id!!).observe(viewLifecycleOwner, Observer { note ->
            view!!.note_title.setText(note.title)
            view!!.note_body.setText(note.description)
            view!!.note_body!!.setBackgroundColor(Color.parseColor(note.color))

            selectedColor = note.colorId
            colorHex = note.color
            alarm = note.notification
            activity!!.invalidateOptionsMenu();

            changeBackground(colorHex)
        })


    }

    fun changeBackground(colorHex: String){
        when (colorHex) {
            "#B2EDFF" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_blue)
            }
            "#D6FF99" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_green)
            }
            "#F2F2F2" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_grey)
            }
            "#BAFF8A" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_lime)
            }
            "#F6BBFF" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_pink)
            }
            "#F9648A" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_purple)
            }
            "#F17957" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_red)
            }
            "#5E92E5" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_royal)
            }
            "#FEF399" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_yellow)
            }
            "#F3F765" ->{
                note_title_layout!!.background = ContextCompat.getDrawable(context!!, R.drawable.sticky_yellow_darker)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.in_note_toolbar, menu)

        menu.findItem(R.id.note_edit).isVisible = !(destination == "edit" || destination == "create" || destination == "update")
        menu.findItem(R.id.note_color).isVisible = destination != "view"
        menu.findItem(R.id.note_edit_cancel).isVisible = destination == "update"

        if(destination == "view"){
            menu.findItem(R.id.note_notification_set).isVisible = !alarm
            menu.findItem(R.id.note_notification_unset).isVisible = alarm
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.note_notification_set -> {
                (activity as NavigationHost).timePickers(id!!,(note_title!!.text.toString()),(note_body!!.text.toString()));
                true
            }
            R.id.note_notification_unset -> {
                (activity as NavigationHost).cancelNotification(id!!);
                true
            }
            R.id.note_color -> {
                (activity as NavigationHost).showColors(selectedColor);
                true
            }
            R.id.note_edit -> {
                val bundle = Bundle()
                bundle.putString("destination", "update")
                bundle.putInt("id", id!!)
                (activity as NavigationHost).navigateToWithData(
                    NoteFragment(),
                    addToBackstack = false,
                    animate = true,
                    "note",
                    bundle
                )
                true
            }

            R.id.note_edit_cancel -> {
                val bundle = Bundle()
                bundle.putString("destination", "view")
                bundle.putInt("id", id!!)
                (activity as NavigationHost).navigateToWithData(
                    NoteFragment(),
                    addToBackstack = false,
                    animate = true,
                    "note",
                    bundle
                )
                true
            }
            else -> false
        }
    }

    private fun setClickListeners(view: View?) {
        noteViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        view!!.close_note.setNavigationOnClickListener{
            activity?.onBackPressed();
        }

        view!!.save_note.setOnClickListener {

            if(note_title!!.text!!.isEmpty()){
                note_title!!.error = getString(R.string.no_string_title)
            }else{
                val dateFormat: DateFormat = SimpleDateFormat("dd MMMM")
                val hourFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
                val date = Date()

                if(destination == "create"){
                    val note = Note(
                        title = note_title!!.text.toString(),
                        description = note_body!!.text.toString(),
                        date = dateFormat.format(date).toString(),
                        hour = hourFormat.format(date).toString(),
                        color = colorHex,
                        colorId = selectedColor!!
                    )
                    noteViewModel.insert(note)
                }else if(destination == "update"){
                    activity!!.invalidateOptionsMenu();
                    noteViewModel.updateNote(
                        id = id!!,
                        title = note_title!!.text.toString(),
                        description = note_body!!.text.toString(),
                        color = colorHex,
                        colorId = selectedColor!!
                    )
                }
                activity?.onBackPressed();
            }

        }
    }

    public fun setColorFun(color: Int, colorFormat: String){
        selectedColor = color
        colorHex = colorFormat

        if(colorHex == "#FFFFFF" || colorHex == ""){
            colorHex = "#F2F2F2"
        }
        note!!.setBackgroundColor(Color.parseColor(colorHex))
        Log.e("COLOR",colorHex)
        changeBackground(colorHex)
    }

    fun setNotification(){
        noteViewModel.setNotification(id!!,true)
    }

    fun unsetNotification(){
        noteViewModel.setNotification(id!!,false)
    }

}