package ipvc.estg.incidentes.entities

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyMarker(// 2
    val id: Int,
    val latLng: LatLng,
    val latitude: Double,
    val longitude: Double,
   /* val status: Int,*/
    val location: String,
    val number: String,
    val date: String,
    val time: String,
    val description: String,
    val photo: String,
    val photo_finish: String,
    val status: Status,
    val user_id: Int,
    val type: Int,
) :
    ClusterItem {

    override fun getPosition(): LatLng {  // 1
        return latLng
    }

    val snippet: String
        get() = ""

}

data class Status(
    val id: Int,
    val status: String,
)