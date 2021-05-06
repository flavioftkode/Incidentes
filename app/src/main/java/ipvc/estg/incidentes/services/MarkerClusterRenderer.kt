package ipvc.estg.incidentes.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.MyMarker
import ipvc.estg.incidentes.fragments.EventDetailFragment
import ipvc.estg.incidentes.fragments.HomeFragment
import ipvc.estg.incidentes.navigation.NavigationHost
import java.text.ParseException
import java.text.SimpleDateFormat


@SuppressLint("InflateParams")
class MarkerClusterRenderer(
    private val mycontext: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MyMarker?>
) : DefaultClusterRenderer<MyMarker>(mycontext, map, clusterManager), OnInfoWindowClickListener {
    private val googleMap: GoogleMap
    private val layoutInflater: LayoutInflater
    private val iconGenerator: IconGenerator = IconGenerator(mycontext)
    private val markerImageView: ImageView = ImageView(mycontext)
    private val logged: Boolean = (mycontext as NavigationHost).isUserLogged() == false
    private val idUser: Int = (mycontext as NavigationHost).getAuthenticationUserId()!!
    override fun onBeforeClusterItemRendered(item: MyMarker, markerOptions: MarkerOptions) {
        if (item.status.id == 1) {
            if(logged && idUser == item.user_id){

            }else{
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_pin))
            }
        } else if (item.status.id == 2 || item.status.id == 3) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_yellow_pin))
        } else if (item.status.id == 4) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pin))
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_pin))
        }
    }

    override fun onClusterItemRendered(clusterItem: MyMarker, marker: Marker) {
        marker.tag = clusterItem
    }

    override fun onInfoWindowClick(marker: Marker) {
        val myMarker = marker.tag as MyMarker?
        val bundle = Bundle()
        bundle.putString("destination", "view")
        bundle.putInt("id", myMarker!!.id)
        bundle.putCharSequence("position", myMarker.position.toString())
        bundle.putDouble("latitude", myMarker.position.latitude)
        bundle.putDouble("longitude", myMarker.position.longitude)
        bundle.putString("date", myMarker.date)
        bundle.putString("time", myMarker.time)
        bundle.putInt("status", myMarker.status.id)
        bundle.putString("description", myMarker.description)
        bundle.putString("number", myMarker.number)
        bundle.putString("photo", myMarker.photo)
        bundle.putString("photo_finish", myMarker.photo_finish)
        bundle.putBoolean("owner", myMarker.user_id == idUser)
        bundle.putInt("idUser", idUser)
        bundle.putString("action", "view")
        bundle.putInt("type", myMarker.type)

        (mycontext as NavigationHost).navigateToWithData(
            EventDetailFragment(),
            addToBackstack = true,
            animate = true,
            data = bundle,
            tag = "event_detail"
        )
    }


    override fun shouldRenderAsCluster(cluster: Cluster<MyMarker>?): Boolean {
        val preferences: SharedPreferences = mycontext.getSharedPreferences("MAPZOOM", Context.MODE_PRIVATE)
        val zoom = preferences.getFloat("zoom", 0.0f)
        if(zoom >= 19.5f && zoom != 0.0f){
            return false
        }
        return cluster!!.size > 1
    }


    private inner class MyCustomClusterItemInfoView internal constructor() : InfoWindowAdapter {
        private val clusterItemView: View

        override fun getInfoWindow(marker: Marker): View {
            val myMarker = marker.tag as MyMarker? ?: return clusterItemView
            val status = clusterItemView.findViewById<TextView>(R.id.status)
            if (myMarker.status.id == 1) {
                clusterItemView.findViewById<View>(R.id.number_color).setBackgroundColor(
                    ContextCompat.getColor(
                        mycontext,
                        R.color.success
                    )
                )
                status.setText(R.string.status_completed)
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.success))
            } else if (myMarker.status.id == 2 || myMarker.status.id == 3) {
                clusterItemView.findViewById<View>(R.id.number_color).setBackgroundColor(
                    ContextCompat.getColor(
                        mycontext,
                        R.color.warning
                    )
                )
                status.setText(R.string.status_in_progress)
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.warning))
            } else if (myMarker.status.id == 4) {
                clusterItemView.findViewById<View>(R.id.number_color).setBackgroundColor(
                    ContextCompat.getColor(
                        mycontext,
                        R.color.danger
                    )
                )
                status.setText(R.string.status_received)
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.danger))
            } else {
                clusterItemView.findViewById<View>(R.id.number_color).setBackgroundColor(
                    ContextCompat.getColor(
                        mycontext,
                        R.color.cpb_grey
                    )
                )
                status.setText(R.string.status_error)
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_grey))
            }
            val name = clusterItemView.findViewById<TextView>(R.id.name)
            val date = clusterItemView.findViewById<TextView>(R.id.date)
            val location = clusterItemView.findViewById<TextView>(R.id.location)
            val myReport = clusterItemView.findViewById<LinearLayout>(R.id.my_report)
            name.text = myMarker.location
            location.text = myMarker.location

            if(myMarker.user_id == idUser){
                myReport.visibility = View.VISIBLE
            }else{
                myReport.visibility = View.GONE
            }

            try {
                val creationDate = SimpleDateFormat("dd/MM/yyyy").parse(myMarker.date)
                val day = SimpleDateFormat("dd MMMM").format(creationDate)
                val year = SimpleDateFormat("yyyy").format(creationDate)
                date.text = mycontext.resources.getString(R.string.date, day, year, myMarker.time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return clusterItemView
        }

        override fun getInfoContents(marker: Marker): View? {
            return null
        }

        init {
            clusterItemView = layoutInflater.inflate(R.layout.custominfowindow, null)
            clusterItemView
        }
    }

    companion object {
        private const val MARKER_DIMENSION = 48
    }

    init {
        markerImageView.layoutParams = ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION)
        iconGenerator.setContentView(markerImageView)
        googleMap = map
        layoutInflater = LayoutInflater.from(mycontext)
        googleMap.setInfoWindowAdapter(clusterManager.markerManager)
        googleMap.setOnInfoWindowClickListener(this)
        clusterManager.markerCollection.setOnInfoWindowAdapter(MyCustomClusterItemInfoView())
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
    }
}