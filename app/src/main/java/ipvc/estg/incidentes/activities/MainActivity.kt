package ipvc.estg.incidentes.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.fragments.HomeFragment
import ipvc.estg.incidentes.fragments.LoginFragment
import ipvc.estg.incidentes.navigation.NavigationHost


class MainActivity : AppCompatActivity(), NavigationHost {
    var me: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.in_main_activity)

        me = getRememberMe()

        if (me != null) {
           /* supportFragmentManager
                .beginTransaction()
                .add(R.id.container, FeaturedFragment())
                .commit()*/
        }else if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, HomeFragment())
                    .commit()
        }
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    override fun navigateTo(fragment: androidx.fragment.app.Fragment, addToBackstack: Boolean, animate: Boolean) {

        val transaction = supportFragmentManager
            .beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        if(animate){
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        transaction.replace(R.id.container, fragment).commit()
    }

    override fun navigateToShared(fragment: androidx.fragment.app.Fragment, addToBackstack: Boolean, animate: Boolean, view:View?) {

        val transaction = supportFragmentManager
            .beginTransaction().addSharedElement(view!!,view.transitionName)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        if(animate){
            /*transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);*/
        }

        transaction.replace(R.id.container, fragment).commit()
    }

    override fun navigateToWithData(fragment: Fragment, addToBackstack: Boolean, animate: Boolean, data: Bundle) {
        val transaction = supportFragmentManager
            .beginTransaction()

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        if(animate){
           /* transaction.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_in_down);*/
        }

        fragment.arguments = data
        transaction.replace(R.id.container, fragment).commit()
    }

    override fun getRememberMe(): String? {
        val sharedPref: SharedPreferences = this.getSharedPreferences("REMEMBER", Context.MODE_PRIVATE)
        return sharedPref.getString("username", null)
    }

    override fun logout() {
       /* MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.leave))
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                val settings = getSharedPreferences("REMEMBER", Context.MODE_PRIVATE)
                settings.edit().clear().apply()
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, LoginFragment())
                    .commit()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, which -> }
            .show()*/
    }

    override fun paymentEnd() {
       /* MaterialAlertDialogBuilder(this,R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(R.string.payment_title)
            .setMessage(R.string.payment_body)
            .setPositiveButton(R.string.payment_confirm) { dialog, which ->
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, FeaturedFragment())
                    .commit()
            }
            .show()*/
    }
}
