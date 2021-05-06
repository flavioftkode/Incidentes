package ipvc.estg.incidentes.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.navigation.NavigationHost
import kotlinx.android.synthetic.main.in_filter_fragment.*
import kotlinx.android.synthetic.main.in_filter_fragment.view.*


class FilterFragment : BottomSheetDialogFragment() {

    companion object {
        private const val TAG = "Filter"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) dismiss()
        val view = inflater.inflate(R.layout.in_filter_fragment, container, false)

        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "FILTERMAP",
            Context.MODE_PRIVATE
        )
        view.checkbox_1.isChecked = preferences.getBoolean("0", true)
        view.checkbox_2.isChecked = preferences.getBoolean("1", true)
        view.checkbox_3.isChecked = preferences.getBoolean("2", true)
        view.checkbox_4.isChecked = preferences.getBoolean("3", true)
        view.checkbox_5.isChecked = preferences.getBoolean("4", true)
        view.radius.setText((preferences.getInt("radius", 0)).toString())

        view.checkbox_1.setOnCheckedChangeListener { buttonView, isChecked ->
            val preferences: SharedPreferences = context!!.getSharedPreferences(
                "FILTERMAP",
                Context.MODE_PRIVATE
            )
            val editor = preferences.edit()
            editor.putBoolean("0", isChecked)
            editor.apply()
        }

        view.checkbox_2.setOnCheckedChangeListener { buttonView, isChecked ->
            val preferences: SharedPreferences = context!!.getSharedPreferences(
                "FILTERMAP",
                Context.MODE_PRIVATE
            )
            val editor = preferences.edit()
            editor.putBoolean("1", isChecked)
            editor.apply()
        }

        view.checkbox_3.setOnCheckedChangeListener { buttonView, isChecked ->
            val preferences: SharedPreferences = context!!.getSharedPreferences(
                "FILTERMAP",
                Context.MODE_PRIVATE
            )
            val editor = preferences.edit()
            editor.putBoolean("2", isChecked)
            editor.apply()
        }

        view.checkbox_4.setOnCheckedChangeListener { buttonView, isChecked ->
            val preferences: SharedPreferences = context!!.getSharedPreferences(
                "FILTERMAP",
                Context.MODE_PRIVATE
            )
            val editor = preferences.edit()
            editor.putBoolean("3", isChecked)
            editor.apply()
        }

        view.checkbox_5.setOnCheckedChangeListener { buttonView, isChecked ->
            val preferences: SharedPreferences = context!!.getSharedPreferences(
                "FILTERMAP",
                Context.MODE_PRIVATE
            )
            val editor = preferences.edit()
            editor.putBoolean("4", isChecked)
            editor.apply()
        }


        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog? ?: return
                val behavior = dialog.behavior
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            dismiss()
                        }
                    }
                })
            }
        })

        filterClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        var myRadius = 0;
        if((radius).text.isNotEmpty()){
            myRadius = (radius).text.toString().toInt()
        }

        val preferences: SharedPreferences = context!!.getSharedPreferences("FILTERMAP", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("radius", myRadius)
        editor.apply()

        val fragment: HomeFragment = activity!!.supportFragmentManager.findFragmentByTag("home") as HomeFragment
        fragment.getMarkers()
    }

    fun filter(): FilterFragment {
        return this
    }

    /**
     * Shows color sheet
     */
    fun show(fragmentManager: FragmentManager) {
        this.show(fragmentManager, TAG)
    }
}