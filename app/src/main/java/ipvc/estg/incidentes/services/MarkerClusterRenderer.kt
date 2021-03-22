package ipvc.estg.incidentes.services

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.MyMarker
import java.text.ParseException
import java.text.SimpleDateFormat


@SuppressLint("InflateParams")
class MarkerClusterRenderer(
    private val mycontext: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MyMarker>
) :
    DefaultClusterRenderer<MyMarker>(mycontext, map, clusterManager), OnInfoWindowClickListener {
    private val googleMap: GoogleMap
    private val layoutInflater: LayoutInflater
    private val iconGenerator: IconGenerator = IconGenerator(mycontext)
    private val markerImageView: ImageView = ImageView(mycontext)
    override fun onBeforeClusterItemRendered(item: MyMarker, markerOptions: MarkerOptions) {
        if (item.status == 1) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))
        } else if (item.status == 2 || item.status == 3) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))
        } else if (item.status == 0) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))
        }
    }

    override fun onClusterItemRendered(clusterItem: MyMarker, marker: Marker) {
        marker.tag = clusterItem
    }

    override fun onInfoWindowClick(marker: Marker) {
        val myMarker = marker.tag as MyMarker?
        /* Intent intent = new Intent(mycontext, SingleActivity.class);
        intent.putExtra("position",myMarker.getPosition());
        intent.putExtra("latitude",myMarker.getPosition().latitude);
        intent.putExtra("longitude",myMarker.getPosition().longitude);
        intent.putExtra("location",myMarker.getLocation());
        intent.putExtra("date",myMarker.getDate());
        intent.putExtra("time",myMarker.getTime());
        intent.putExtra("status",myMarker.getStatus());
        intent.putExtra("description",myMarker.getDescription());
        intent.putExtra("id",myMarker.getId());
        intent.putExtra("number",myMarker.getNumber());
        intent.putExtra("photo",myMarker.getPhoto());
        intent.putExtra("photo_finish",myMarker.getPhoto_finish());

        mycontext.startActivity(intent);*/
    }

    private inner class MyCustomClusterItemInfoView internal constructor() : InfoWindowAdapter {
        private val clusterItemView: View
        override fun getInfoWindow(marker: Marker): View {
            val myMarker = marker.tag as MyMarker? ?: return clusterItemView
            val status = clusterItemView.findViewById<TextView>(R.id.status)
            Log.d("STATUS", myMarker.status.toString())
            if (myMarker.status == 1) {
                clusterItemView.findViewById<View>(R.id.number_color)
                    .setBackgroundColor(ContextCompat.getColor(mycontext, R.color.cpb_green))
                status.text = "completo"
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_blue))
            } else if (myMarker.status == 2 || myMarker.status == 3) {
                clusterItemView.findViewById<View>(R.id.number_color)
                    .setBackgroundColor(ContextCompat.getColor(mycontext, R.color.cpb_red))
                status.text = "progress"
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_red))
            } else if (myMarker.status == 0) {
                clusterItemView.findViewById<View>(R.id.number_color)
                    .setBackgroundColor(ContextCompat.getColor(mycontext, R.color.cpb_green_dark))
                status.text = "recived"
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_red_dark))
            } else {
                clusterItemView.findViewById<View>(R.id.number_color)
                    .setBackgroundColor(ContextCompat.getColor(mycontext, R.color.cpb_grey))
                status.text = "error"
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_grey))
            }
            val name = clusterItemView.findViewById<TextView>(R.id.name)
            val date = clusterItemView.findViewById<TextView>(R.id.date)
            val location = clusterItemView.findViewById<TextView>(R.id.location)
            /*     TextView description = clusterItemView.findViewById(R.id.description);*/name.text =
                myMarker.number
            location.text = myMarker.location
            /*    description.setText(myMarker.getDescription());*/try {
                val date_time = SimpleDateFormat("dd/MM/yyyy").parse(myMarker.date)
                val formattedDate = SimpleDateFormat("dd MMMM yyyy").format(date_time)
                date.text = formattedDate + " " + myMarker.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return clusterItemView
        }

        override fun getInfoContents(marker: Marker?): View? {
            return null
        }

        init {
            clusterItemView = layoutInflater.inflate(R.layout.custominfowindow, null)
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