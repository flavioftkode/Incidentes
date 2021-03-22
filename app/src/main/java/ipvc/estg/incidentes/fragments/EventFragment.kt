package ipvc.estg.incidentes.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.User
import ipvc.estg.incidentes.navigation.NavigationHost
import ipvc.estg.incidentes.retrofit.EndPoints
import ipvc.estg.incidentes.retrofit.ServiceBuilder
import kotlinx.android.synthetic.main.in_backdrop.view.*
import kotlinx.android.synthetic.main.in_event_fragment.view.*
import kotlinx.android.synthetic.main.in_login_fragment.*
import kotlinx.android.synthetic.main.in_login_fragment.view.*
import kotlinx.android.synthetic.main.in_login_fragment.view.close_login
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventFragment : Fragment() {

    var btnRegister: Button? = null
    var btnLogin: ConstraintLayout? = null
    var btnResetPassword: Button? = null
    var rememberMe: CheckBox? = null
    var me: String? = null
    var payload: String? = null
    var username: String? = null
    var password: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.in_event_fragment, container, false)
        declareItems(view)
        setClickListeners(view)
        return view
    }

    fun declareItems(view: View) {

    }

    private fun setClickListeners(view: View?) {
        view!!.close_event.setNavigationOnClickListener{
            activity?.onBackPressed();
        }
    }
}