package ipvc.estg.incidentes.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.navigation.NavigationHost
import kotlinx.android.synthetic.main.in_consent_fragment.view.*

class ConsentFragment : Fragment() {
    private var isAllChecked:Boolean = false
    var btnConsent:CircularProgressButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.in_consent_fragment, container, false)
        btnConsent = view.findViewById(R.id.btn_consent)
        setClickListeners(view)
        return view
    }

    private fun failed(){
        btnConsent!!.isEnabled = true
        btnConsent!!.doneLoadingAnimation(R.color.transparent, BitmapFactory.decodeResource(resources, R.drawable.error))

        Handler(Looper.getMainLooper()).postDelayed({
            btnConsent!!.revertAnimation();
            btnConsent!!.setBackgroundResource(R.drawable.shape);
            (activity as NavigationHost).customToaster(title = getString(R.string.toast_error), message = getString(R.string.consent_needed), type= "error");
        }, 10 * 100)
    }

    private fun success(){
        btnConsent!!.isEnabled = true
        btnConsent!!.doneLoadingAnimation(R.color.transparent, BitmapFactory.decodeResource(resources, R.drawable.done))

        Handler(Looper.getMainLooper()).postDelayed({
            btnConsent!!.revertAnimation();
            btnConsent!!.setBackgroundResource(R.drawable.shape);
            (activity as NavigationHost).setConsent();
            (activity as NavigationHost).navigateTo(HomeFragment(), addToBackstack = false, animate = true, "home")
        }, 10 * 100)
    }

    private fun setClickListeners(view: View){
        view.btn_consent.setOnClickListener {
            btnConsent!!.isEnabled = false
            btnConsent!!.startAnimation();
            if(isAllChecked){
                success()
            }else{
                failed()
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