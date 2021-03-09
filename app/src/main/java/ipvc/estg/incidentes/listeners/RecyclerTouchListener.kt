package ipvc.estg.incidentes.listeners

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import ipvc.estg.incidentes.fragments.NotesFragment

/**
 * Created by Flávio on 12/11/2017.
 *
 */
class RecyclerTouchListener(
    context: Context?,
    recyclerView: RecyclerView?,
    clickListener: NotesFragment.ClickListener?
) :
    OnItemTouchListener {
    private val gestureDetector: GestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            //long click desativado (FF)
            /*  View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null) {
                clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
            }*/
        }
    })
    private val clickListener: NotesFragment.ClickListener? = clickListener
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        val TAG = "RecyclerTouchListener"
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            try {
                clickListener.onClick()
            } catch (a: IndexOutOfBoundsException) {
                Log.d(TAG, "Exception $e")
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

}