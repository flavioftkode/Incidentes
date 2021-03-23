package ipvc.estg.incidentes.entities

import com.google.android.gms.maps.model.LatLng

class Event(
    val id: Int,
    val latLng: LatLng,
    var status: Int,
    val location: String,
    val number: String,
    val date: String,
    val time: String,
    val description: String,
    val photo: String,
    val photo_finish: String,
    var latitude: Double,
    var longitude: Double
)
