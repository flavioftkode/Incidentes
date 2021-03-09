package ipvc.estg.incidentes.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.adapters.NotesAdapter
import ipvc.estg.incidentes.entities.Note
import ipvc.estg.incidentes.listeners.NavigationIconClickListener
import ipvc.estg.incidentes.listeners.RecyclerItemTouchHelper
import ipvc.estg.incidentes.listeners.RecyclerTouchListener
import ipvc.estg.incidentes.navigation.NavigationHost
import ipvc.estg.incidentes.vmodel.NotesViewModel
import kotlinx.android.synthetic.main.in_backdrop.view.*
import kotlinx.android.synthetic.main.in_main_activity.view.*
import kotlinx.android.synthetic.main.in_notes_fragment.view.*
import java.util.*


class NotesFragment : Fragment(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
    NotesAdapter.NotesAdapterListener {

    private var mAdapter: NotesAdapter? = null
    private var notesList: MutableList<Note> = ArrayList<Note>()
    var mLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var noteViewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.in_notes_fragment, container, false)
        setClickListeners(view)
        setToolbar(view)
        setRecycleView(view)
        setAdapter()


        /*view.recycler_loading.visibility = View.GONE;*/
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {

                true
            }
            else -> false
        }
    }

    private fun setRecycleView(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = mLayoutManager
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
        recyclerView?.itemAnimator = DefaultItemAnimator()
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        recyclerView?.addOnItemTouchListener(
            RecyclerTouchListener(activity, recyclerView, ClickListener())
        )
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun setAdapter() {

        noteViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)



        noteViewModel.allNotes.observe(viewLifecycleOwner, Observer { notes ->
            //Log.e("notes", notesList[0].title.toString())
            // Update the cached copy of the words in the adapter.
            /*notes?.let { mAdapter!!.setNotes(it) }*/
            notesList = notes as MutableList<Note>

            mAdapter = context?.let { NotesAdapter(notesList, this, it) }
            mAdapter!!.setHasStableIds(true)

            mAdapter!!.notifyItemRangeInserted(0, notesList.size - 1)
            recyclerView!!.adapter = mAdapter
        })

      /*  mAdapter = context?.let { NotesAdapter(notesList, this, it) }
        mAdapter!!.setHasStableIds(true)*/

    }

    class ClickListener {
        fun onClick() {}
    }

    private fun setClickListeners(view: View?) {
        view!!.in_main.setOnClickListener{
            (activity as NavigationHost).navigateTo(
                HomeFragment(),
                addToBackstack = false,
                animate = true
            )
        }
    }

    private fun setToolbar(view: View?){
        (activity as AppCompatActivity).setSupportActionBar(view!!.app_bar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = getString(R.string.notes)
        view.app_bar.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!, view.notes_grid,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(
                    context!!, R.drawable.in_menu
                ), // Menu open icon
                ContextCompat.getDrawable(context!!, R.drawable.in_close)
            )
        ) // Menu close icon
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)

        menuInflater.inflate(R.menu.in_notes_toolbar, menu)

        val searchView = SearchView(((activity as AppCompatActivity?)!!.supportActionBar?.themedContext ?: context)!!)
        val searchEditText: EditText = searchView.findViewById<View>(R.id.search_src_text) as EditText
        searchEditText.setTextColor(ContextCompat.getColor(context!!, R.color.cpb_white))
        searchEditText.setHintTextColor(ContextCompat.getColor(context!!, R.color.cpb_white))
        menu.findItem(R.id.search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mAdapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mAdapter?.filter?.filter(newText)
                return false
            }
        })
        searchView.setOnClickListener { view -> }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is NotesAdapter.MyViewHolder) {
            val note = mAdapter!!.getItem(position);
            noteViewModel.deleteById(mAdapter!!.getItem(position).id!!)
            mAdapter!!.removeItem(position)
            Snackbar.make(
                activity!!.findViewById(android.R.id.content), getString(R.string.note_deleted), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.note_restore)) {
                    noteViewModel.insert(note)
                    val success = Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.note_restored), Snackbar.LENGTH_SHORT)
                    success.show()
                }.show()
        }
    }

    override fun onNoteSelected(note: Note?) {
        Log.e("NOTE", note!!.title)
    }
}