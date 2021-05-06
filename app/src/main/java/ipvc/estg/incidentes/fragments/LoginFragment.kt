package ipvc.estg.incidentes.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.User
import ipvc.estg.incidentes.navigation.NavigationHost
import ipvc.estg.incidentes.retrofit.EndPoints
import ipvc.estg.incidentes.retrofit.ServiceBuilder
import kotlinx.android.synthetic.main.in_login_fragment.*
import kotlinx.android.synthetic.main.in_login_fragment.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    var btnRegister: Button? = null
    var btnLogin: CircularProgressButton? = null
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
        btnLogin = view.findViewById<View>(R.id.btn_login) as CircularProgressButton
        btnRegister = view.findViewById(R.id.btn_register)
        btnResetPassword = view.findViewById(R.id.btn_reset_password)
        rememberMe = view.findViewById(R.id.remember_me)
        if (me != null) {
            view.username_edit_text.setText(me)
            rememberMe?.isChecked = true
        }
    }

    private fun failed(){
        btnLogin!!.isEnabled = true
        btnLogin!!.doneLoadingAnimation(R.color.transparent, BitmapFactory.decodeResource(resources, R.drawable.error))

        Handler(Looper.getMainLooper()).postDelayed({
            btnLogin!!.revertAnimation();
            btnLogin!!.setBackgroundResource(R.drawable.shape);
        }, 10 * 100)
    }

    private fun success(){
        btnLogin!!.isEnabled = true
        btnLogin!!.doneLoadingAnimation(R.color.transparent, BitmapFactory.decodeResource(resources, R.drawable.done))

        Handler(Looper.getMainLooper()).postDelayed({
            btnLogin!!.revertAnimation();
            btnLogin!!.setBackgroundResource(R.drawable.shape);
            (activity as NavigationHost).customToaster(title = getString(R.string.toast_success), message = getString(R.string.login_success), type = "success");
            activity?.onBackPressed()
        }, 10 * 100)
    }

    private fun setClickListeners(view: View?) {
        view!!.close_login.setNavigationOnClickListener{
            activity?.onBackPressed();
        }

        view!!.btn_login.setOnClickListener {
          /*  progress_bar_frame.visibility = View.VISIBLE*/
            btnLogin!!.isEnabled = false
            btnLogin!!.startAnimation();
            username = view.username_edit_text.text.toString().toLowerCase()
            password = view.password_edit_text.text.toString()
            username_text_input.error = null
            password_text_input.error = null
            if (username!!.isEmpty()) {
                username_text_input.error = getString(R.string.invalid_username)
                failed();
            } else if (password!!.isEmpty() || password == " ") {
                password_text_input.error = getString(R.string.invalid_password)
                failed();
            } else {

                val obj = JSONObject()
                obj.put("username", username);
                obj.put("password", password);
                payload = obj.toString()
                payload = Base64.encodeToString(
                    payload?.toByteArray(charset("UTF-8")),
                    Base64.DEFAULT
                )

                Log.e("payload", payload.toString())
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.loginUser(payload = payload!!)

                call.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>?, response: Response<User>?) {

                        if (response!!.isSuccessful) {
                            if (rememberMe!!.isChecked) {
                                val rememberMe: SharedPreferences = context!!.getSharedPreferences(
                                    "REMEMBER",
                                    Context.MODE_PRIVATE
                                )
                                rememberMe.edit().putString("username", "response").apply()
                            } else {
                                val rememberMe: SharedPreferences = context!!.getSharedPreferences(
                                    "REMEMBER",
                                    Context.MODE_PRIVATE
                                )
                                rememberMe.edit().clear().apply()
                            }
                            val rememberMe: SharedPreferences = context!!.getSharedPreferences(
                                "AUTHENTICATION",
                                Context.MODE_PRIVATE
                            )
                            rememberMe.edit().putInt("iduser", response.body().id).apply()
                            rememberMe.edit().putString(
                                "_token",
                                "Bearer " + response.body()._token
                            ).apply()

                            success()

                        } else {
                            failed()
                            if (response.code() == 403 && response.message() == "login_fail") {
                                username_text_input.error = getString(R.string.wrong_user_info)
                                password_text_input.error = getString(R.string.wrong_user_info)
                            } else {
                                (activity as NavigationHost).customToaster(
                                    title = getString(R.string.toast_error),
                                    message = getString(R.string.general_error),
                                    type = "connection"
                                );
                            }

                        }
                        /*progress_bar_frame.visibility = View.GONE*/

                        btnLogin!!.isEnabled = true
                    }

                    override fun onFailure(call: Call<User>?, t: Throwable?) {
                        /* progress_bar_frame.visibility = View.GONE*/
                        btnLogin!!.isEnabled = true

                        val rememberMe: SharedPreferences = context!!.getSharedPreferences(
                            "AUTHENTICATION",
                            Context.MODE_PRIVATE
                        )
                        rememberMe.edit().putInt("iduser", 0).apply()
                        rememberMe.edit().putString("_token", "").apply()
                       /* activity?.onBackPressed()*/

                        (activity as NavigationHost).customToaster(
                            title = getString(R.string.toast_error),
                            message = getString(R.string.general_error),
                            type = "connection"
                        );
                        failed()
                    }
                })
            }
        }
    }
}