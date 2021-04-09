package ipvc.estg.incidentes.navigation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.webkit.WebStorage
import androidx.fragment.app.Fragment
import java.time.Duration


/**
 * A host (typically an `Activity`} that can display fragments and knows how to respond to
 * navigation events.
 */
interface NavigationHost {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    fun navigateTo(fragment: Fragment, addToBackstack: Boolean, animate: Boolean,tag:String = "")

    fun navigateToWithData(fragment: Fragment, addToBackstack: Boolean, animate: Boolean,tag:String = "",data: Bundle)

    fun navigateToShared(fragment: Fragment, addToBackstack: Boolean, animate: Boolean, view: View?)

    fun logout(fragment: Fragment)

    fun getRememberMe(): String?

    fun showColors(selectedColor:Int?)

    fun showFilters()

    fun timePickers(id:Int,title:String,body:String)

    fun cancelNotification(id:Int)

    fun customToaster(message:String,drawable: String,duration: Int)

    fun isUserLogged(): Boolean?

    fun getAuthenticationUserId(): Int?

    fun getAuthenticationToken(): String?
}
