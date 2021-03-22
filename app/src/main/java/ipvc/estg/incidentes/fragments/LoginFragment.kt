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
import kotlinx.android.synthetic.main.in_login_fragment.*
import kotlinx.android.synthetic.main.in_login_fragment.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.in_login_fragment, container, false)
        declareItems(view)
        setClickListeners(view)
        return view
    }

    fun declareItems(view: View) {
        btnLogin = view.findViewById<View>(R.id.btn_login) as ConstraintLayout
        btnRegister = view.findViewById(R.id.btn_register)
        btnResetPassword = view.findViewById(R.id.btn_reset_password)
        rememberMe = view.findViewById(R.id.remember_me)
        if (me != null) {
            view.username_edit_text.setText(me)
            rememberMe?.isChecked = true
        }
    }

    private fun setClickListeners(view: View?) {
        view!!.close_login.setNavigationOnClickListener{
            activity?.onBackPressed();
        }

        view!!.btn_login.setOnClickListener {
            progress_bar_frame.visibility = View.VISIBLE
            btnLogin!!.isEnabled = false
            username = view.username_edit_text.text.toString().toLowerCase()
            password = view.password_edit_text.text.toString()
            username_text_input.error = null
            password_text_input.error = null
            if (username!!.isEmpty()) {
                username_text_input.error = getString(R.string.invalid_username)
                btnLogin!!.isEnabled = true
                progress_bar_frame.visibility = View.GONE
            } else if (password!!.isEmpty() || password == " ") {
                password_text_input.error = getString(R.string.invalid_password)
                btnLogin!!.isEnabled = true
                progress_bar_frame.visibility = View.GONE
            } else {

                val obj = JSONObject()
                obj.put("username", username);
                obj.put("password", password);
                payload = obj.toString()
                payload = Base64.encodeToString(payload?.toByteArray(charset("UTF-8")), Base64.DEFAULT)

                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.loginUser(payload=payload!!)

                call.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>?, response: Response<User>?) {
                        if (response!!.isSuccessful) {

                            (activity as NavigationHost).customToaster(getString(R.string.login_success), "logo_small",Toast.LENGTH_LONG);

                            if (rememberMe!!.isChecked) {
                                val rememberMe: SharedPreferences = context!!.getSharedPreferences("REMEMBER", Context.MODE_PRIVATE)
                               rememberMe.edit().putString("username", "response").apply()
                            } else {
                                val rememberMe: SharedPreferences = context!!.getSharedPreferences("REMEMBER", Context.MODE_PRIVATE)
                                rememberMe.edit().clear().apply()
                            }
                            val rememberMe: SharedPreferences = context!!.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)
                            rememberMe.edit().putInt("iduser", response.body().id).apply()
                            rememberMe.edit().putString("_token", response.body()._token).apply()
                            activity?.onBackPressed()
                        }else{
                            if(response.code() == 403 && response.message() == "login_fail"){
                                username_text_input.error = getString(R.string.wrong_user_info)
                                password_text_input.error = getString(R.string.wrong_user_info)
                            }else{
                                (activity as NavigationHost).customToaster(getString(R.string.login_fail), "ic_error_small",Toast.LENGTH_LONG);
                            }
                        }
                        progress_bar_frame.visibility = View.GONE
                        btnLogin!!.isEnabled = true
                    }

                    override fun onFailure(call: Call<User>?, t: Throwable?) {
                        progress_bar_frame.visibility = View.GONE
                        btnLogin!!.isEnabled = true
                        (activity as NavigationHost).customToaster(getString(R.string.login_fail), "ic_error_small",Toast.LENGTH_LONG);
                    }
                })
            }
        }
    }
}