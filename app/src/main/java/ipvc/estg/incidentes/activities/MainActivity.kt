package ipvc.estg.incidentes.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.fragments.NoteFragment
import ipvc.estg.incidentes.fragments.HomeFragment
import ipvc.estg.incidentes.navigation.NavigationHost


class MainActivity : AppCompatActivity(), NavigationHost {
    var me: String? = null
    var colors: IntArray? = null
    var selectedColor: Int? = null
    var noColorOption: Boolean? = null

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

        colors = resources.getIntArray(R.array.colors)
       /* selectedColor = savedInstanceState?.getInt(COLOR_SELECTED) ?: colors!!.first()
        setColor(selectedColor!!)

        noColorOption = savedInstanceState?.getBoolean(NO_COLOR_OPTION) ?: false*/

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

        transaction.replace(R.id.container, fragment,tag).commit()
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
        transaction.replace(R.id.container, fragment,tag).commit()
    }

    override fun getRememberMe(): String? {
        val sharedPref: SharedPreferences = this.getSharedPreferences(
            "REMEMBER",
            Context.MODE_PRIVATE
        )
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

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
        private const val NO_COLOR_OPTION = "noColorOption"
    }

    private fun setColor(@ColorInt color: Int) {
        /*if (color != ColorSheet.NO_COLOR) {
            colorBackground.setBackgroundColor(color)
            colorSelectedText.text = ColorSheetUtils.colorToHex(color)
        } else {
            val primaryColor = ContextCompat.getColor(this, R.color.colorPrimary)
            colorBackground.setBackgroundColor(primaryColor)
            colorSelectedText.text = getString(R.string.no_color)
        }*/
    }

    override fun showColors(selectedColor:Int?) {
        ColorSheet().colorPicker(
            colors = colors!!,
            noColorOption = true,
            selectedColor = selectedColor,
            listener = { checkedColor ->

                val fragment: NoteFragment = supportFragmentManager.findFragmentByTag("note") as NoteFragment
                fragment.setColorFun(checkedColor,ColorSheetUtils.colorToHex(checkedColor))
                return@colorPicker
            }).show(supportFragmentManager)
    }
}
