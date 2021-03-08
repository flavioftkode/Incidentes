package ipvc.estg.incidentes.constructors

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 *
 * Criado por Flávio Fernandes a 12/12/2019
 *
 */
class MyMarker(// 2
    val id: Int,
    val latLng: LatLng,
    val status: Int,
    val location: String,
    val number: String,
    val date: String,
    val time: String,
    val description: String,
    val photo: String,
    val photo_finish: String
) :
    ClusterItem {

    override fun getPosition(): LatLng {  // 1
        return latLng
    }

    val snippet: String
        get() = ""

}