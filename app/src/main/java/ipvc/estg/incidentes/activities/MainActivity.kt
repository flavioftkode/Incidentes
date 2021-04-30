package ipvc.estg.incidentes.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.allyants.notifyme.NotifyMe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.fragments.*
import ipvc.estg.incidentes.navigation.NavigationHost
import kotlinx.android.synthetic.main.in_backdrop.*
import kotlinx.android.synthetic.main.in_backdrop.view.*
import www.sanju.motiontoast.MotionToast
import java.util.*


class MainActivity : AppCompatActivity(), NavigationHost, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private var colors: IntArray? = null
    var now: Calendar = Calendar.getInstance()
    var tpd: TimePickerDialog? = null
    var dpd: DatePickerDialog? = null
    var id: Int? = null
    var title: String? = null
    var body: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.in_main_activity)
        val notification = intent.getStringExtra("notification")
        if (notification != null) {
            if (notification == "fromNotification") {
                val bundle = Bundle()
                bundle.putString("key", intent.getStringExtra("key"))
                var fragment =   NotesFragment()
                fragment.arguments = bundle
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, fragment,"notes")
                    .commit()
            }
        }else{
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, HomeFragment(),"home")
                .commit()
        }



        colors = resources.getIntArray(R.array.colors)

    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    override fun navigateTo(
        fragment: androidx.fragment.app.Fragment,
        addToBackstack: Boolean,
        animate: Boolean,
        tag: String,
    ) {

        val transaction = supportFragmentManager
            .beginTransaction()/*.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)*/

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        if(animate){
            transaction/*.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);*/
            .setCustomAnimations(
                R.animator.slide_in_up,//enter
                R.animator.slide_out_down,//exit
                R.animator.slide_in_up,//popEnter
                R.animator.slide_out_up
            )//popExit
        }

        transaction.replace(R.id.container, fragment, tag).commit()
    }

    override fun navigateToShared(
        fragment: androidx.fragment.app.Fragment,
        addToBackstack: Boolean,
        animate: Boolean,
        view: View?
    ) {

        val transaction = supportFragmentManager
            .beginTransaction().addSharedElement(view!!, view.transitionName)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        if(animate){
            /*transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);*/
        }

        transaction.replace(R.id.container, fragment).commit()
    }

    override fun navigateToWithData(
        fragment: Fragment,
        addToBackstack: Boolean,
        animate: Boolean,
        tag: String,
        data: Bundle
    ) {
        val transaction = supportFragmentManager
            .beginTransaction()

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        if(animate){
            transaction
                .setCustomAnimations(
                    R.animator.slide_in_up,//enter
                    R.animator.slide_out_down,//exit
                    R.animator.slide_in_up,//popEnter
                    R.animator.slide_out_up//popExit
                )
        }

        fragment.arguments = data
        transaction.replace(R.id.container, fragment, tag).commit()
    }

    override fun getRememberMe(): String? {
        val sharedPref: SharedPreferences = this.getSharedPreferences(
            "REMEMBER",
            Context.MODE_PRIVATE
        )
        return sharedPref.getString("username", null)
    }

    override fun isUserLogged(): Boolean? {
        val sharedPref: SharedPreferences = this.getSharedPreferences(
            "AUTHENTICATION",
            Context.MODE_PRIVATE
        )

        if(sharedPref.getInt("iduser", 0) != 0){
            return true
        }
        return false
    }

    override fun logout(fragment: Fragment) {
        MaterialAlertDialogBuilder(this)
            .setTitle("logout"/*getString(R.string.logout)*/)
            .setMessage("Sair"/*getString(R.string.leave)*/)
            .setPositiveButton("Sim"/*getString(R.string.yes)*/) { dialog, which ->
                val settings = getSharedPreferences("REMEMBER", Context.MODE_PRIVATE)
                settings.edit().clear().apply()

                val auth = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)
                auth.edit().clear().apply()
               /* supportFragmentManager
                    .beginTransaction()
                    .detach(NotesFragment())
                    .add(R.id.container, NotesFragment())
                    .commit()*/
                navigateTo(fragment, addToBackstack = false, animate = true)
            }
            .setNegativeButton("Não"/*getString(R.string.no)*/) { dialog, which -> }
            .show()
    }

    override fun showColors(selectedColor: Int?) {
        ColorSheet().colorPicker(
            colors = colors!!,
            noColorOption = true,
            selectedColor = selectedColor,
            listener = { checkedColor ->

                val fragment: NoteFragment =
                    supportFragmentManager.findFragmentByTag("note") as NoteFragment
                fragment.setColorFun(checkedColor, ColorSheetUtils.colorToHex(checkedColor))
                return@colorPicker
            }).show(supportFragmentManager)
    }

    override fun showFilters(){
        FilterFragment().filter().show(supportFragmentManager)
    }


    override fun timePickers(idN: Int, titleN: String, bodyN: String){
        id = idN
        title = titleN
        body = bodyN
        dpd = DatePickerDialog.newInstance(
            this@MainActivity,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )

        tpd = TimePickerDialog.newInstance(
            this@MainActivity,
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            now.get(Calendar.SECOND),
            true
        );

        dpd!!.show(fragmentManager, "Datepickerdialog");
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        tpd!!.show(fragmentManager, "Timepickerdialog");
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        now[Calendar.HOUR_OF_DAY] = hourOfDay
        now[Calendar.MINUTE] = minute
        now[Calendar.SECOND] = 0

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("notification", "fromNotification")
        notificationIntent.putExtra("key", id.toString())

        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        Log.e("now", now.toString())
        val notifyMe: NotifyMe = NotifyMe.Builder(applicationContext)
            .title(title)
            .content(body)
            .color(255, 0, 0, 255)
            .led_color(255, 255, 255, 255)
            .time(now)
            .addAction(notificationIntent, resources.getString(R.string.notification_open), false)
            .key(id.toString())
            .addAction(Intent(), resources.getString(R.string.notification_close), true, false)
            .addAction(notificationIntent, resources.getString(R.string.notification_open_close))
            .large_icon(R.drawable.ic_logo_round)
            .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")
            .build()
        Log.e("notifyMe", notifyMe.toString())

        val fragment: NoteFragment =
            supportFragmentManager.findFragmentByTag("note") as NoteFragment
        fragment.setNotification();
        return
    }

     override fun cancelNotification(id: Int){
        NotifyMe.cancel(applicationContext, id.toString());
         val fragment: NoteFragment =
             supportFragmentManager.findFragmentByTag("note") as NoteFragment
         fragment.unsetNotification();
    }

    override fun customToaster(message: String,title: String,type: String){
        /*val inflater = layoutInflater
        val layout: View = inflater.inflate(
            R.layout.custom_toast,
            findViewById<LinearLayout>(R.id.custom_toast_container)
        )
        val text = layout.findViewById<View>(R.id.text) as TextView
        text.text = message
        val resources: Resources = applicationContext.resources
        val resourceId: Int = resources.getIdentifier(
            drawable,
            "drawable",
            applicationContext.packageName
        )
        text.setCompoundDrawablesWithIntrinsicBounds(resourceId, 0, 0, 0);
        val toast = Toast(applicationContext)
        toast.duration = duration
        toast.view = layout
        toast.show()*/
        var toastType =  MotionToast.TOAST_SUCCESS;
        if(type == "success"){
            toastType = MotionToast.TOAST_SUCCESS;
        }else if(type == "error"){
            toastType = MotionToast.TOAST_ERROR;
        }else if(type == "warning"){
            toastType = MotionToast.TOAST_WARNING;
        }else if(type == "info"){
            toastType = MotionToast.TOAST_INFO;
        }else if(type == "delete"){
            toastType = MotionToast.TOAST_DELETE;
        }else if(type == "connection"){
            toastType = MotionToast.TOAST_NO_INTERNET;
        }

        MotionToast.darkToast( this,
            title,
            message,
            toastType,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.helvetica_regular))
    }

    override fun getAuthenticationToken(): String? {
        val sharedPref = getSharedPreferences("AUTHENTICATION", MODE_PRIVATE)
        return sharedPref.getString("_token", null)
    }

    override fun getAuthenticationUserId(): Int? {
        val sharedPref = getSharedPreferences("AUTHENTICATION", MODE_PRIVATE)
        return sharedPref.getInt("iduser", 0)
    }

    override fun setConsent() {
        val preferences: SharedPreferences = getSharedPreferences("CONSENT", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("isConsentCheck", 1)
        editor.apply()
    }

    override fun getConsentStatus(): Boolean {
        val sharedPref = getSharedPreferences("CONSENT", MODE_PRIVATE)
        return sharedPref.getInt("isConsentCheck", 0) == 1
    }

    override fun reloadMarkers() {

    }
}
