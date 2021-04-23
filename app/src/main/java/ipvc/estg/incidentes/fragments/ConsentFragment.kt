package ipvc.estg.incidentes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.navigation.NavigationHost
import kotlinx.android.synthetic.main.in_consent_fragment.view.*

class ConsentFragment : Fragment() {
    private var isAllChecked:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.in_consent_fragment, container, false)
        setClickListeners(view)
        return view
    }

    private fun setClickListeners(view: View){
        view.btn_consent.setOnClickListener {
            if(isAllChecked){
                (activity as NavigationHost).setConsent();
                (activity as NavigationHost).navigateTo(HomeFragment(), addToBackstack = false, animate = true, "home")
            }else{
                (activity as NavigationHost).customToaster(
                    getString(R.string.consent_needed),
                    "ic_error_small",
                    Toast.LENGTH_LONG
                );
            }
        }

        view.checkbox_consent_1.setOnClickListener{
            onCheckboxClicked(view)
        }

        view.checkbox_consent_2.setOnClickListener{
            onCheckboxClicked(view)
        }
    }

    private fun onCheckboxClicked(view: View) {
        val checked1 = (view.findViewById<View>(R.id.checkbox_consent_1) as CheckBox).isChecked
        val checked2 = (view.findViewById<View>(R.id.checkbox_consent_2) as CheckBox).isChecked
        isChecked(checked1, checked2)
    }

    private fun isChecked(checked_1: Boolean, checked_2: Boolean) {
        isAllChecked = checked_1 && checked_2
    }
}