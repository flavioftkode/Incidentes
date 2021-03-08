package ipvc.estg.incidentes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.navigation.NavigationHost
import kotlinx.android.synthetic.main.in_login_fragment.view.*

class NotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.in_login_fragment, container, false)

        setClickListeners(view)


        return view
    }

    private fun setClickListeners(view: View?) {
        view!!.close_login.setNavigationOnClickListener{
            activity?.onBackPressed();
        }

        view!!.close_login.setOnClickListener {
            (activity as NavigationHost).navigateTo(NotesFragment(), true,false)
        }
    }
}