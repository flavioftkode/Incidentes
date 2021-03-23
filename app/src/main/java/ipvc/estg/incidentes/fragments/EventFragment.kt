package ipvc.estg.incidentes.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.incidentes.BuildConfig
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.Event
import ipvc.estg.incidentes.entities.User
import ipvc.estg.incidentes.navigation.NavigationHost
import ipvc.estg.incidentes.retrofit.EndPoints
import ipvc.estg.incidentes.retrofit.ServiceBuilder
import ipvc.estg.incidentes.services.GPSTracker
import kotlinx.android.synthetic.main.in_backdrop.view.*
import kotlinx.android.synthetic.main.in_event_fragment.*
import kotlinx.android.synthetic.main.in_event_fragment.view.*
import kotlinx.android.synthetic.main.in_login_fragment.*
import kotlinx.android.synthetic.main.in_login_fragment.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EventFragment : Fragment() {

    private var latitude:Double = 0.0
    private  var longitude:Double = 0.0
    var mMarker = false
    private val REQUEST_IMAGE_CAPTURE = 1
    var imageFilePath:String? = null
    var gps: GPSTracker? = null
    var location: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.in_event_fragment, container, false)
        declareItems(view)
        setClickListeners(view)

        setMiniMap()
        val bundle = this.arguments
        if (bundle != null) {
            latitude = bundle.getDouble("mMarker_lat", 0.0)
            longitude = bundle.getDouble("mMarker_long", 0.0)
            location = getCompleteAddressString(latitude, longitude)
            Log.e("lat", latitude.toString())
            Log.e("long", longitude.toString())
            view!!.address.text = location
            mMarker =bundle.getBoolean("mMarker", false)
        }

        if (mMarker) {
            view.no_image.visibility = View.VISIBLE
            view.add_image.visibility = View.VISIBLE
        } else {
            openCameraIntent()
        }
        return view
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = image.absolutePath
        return image
    }

    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (activity!!.getExternalFilesDir(null) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    Objects.requireNonNull(context!!),
                    BuildConfig.APPLICATION_ID.toString() + ".provider",
                    photoFile
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    pictureIntent,
                    REQUEST_IMAGE_CAPTURE
                )
            }
        }
    }

    fun setMiniMap() {
        val mapFragment = childFragmentManager!!.findFragmentById(R.id.map_lite) as SupportMapFragment?
        //val mapFragment = parentFragmentManager.findFragmentById(R.id.map_lite) as SupportMapFragment
        Objects.requireNonNull(mapFragment!!.view)!!.isClickable = false
        mapFragment.getMapAsync(OnMapReadyCallback {
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 14.3f))
            it.addMarker(
                MarkerOptions().position(LatLng(latitude, longitude)).draggable(true).icon(
                    BitmapDescriptorFactory.fromResource(
                        R.drawable.ic_map_marker
                    )
                )
            )
        });
    }

    fun declareItems(view: View) {

    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("ADDRESS", strReturnedAddress.toString())
            } else {
                Log.w("NO_ADDRESS", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("CANT_LOCATION", "Cannot get Address!")
        }
        return strAdd
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            /*gps = GPSTracker(context!!)
            if (!mMarker && gps!!.canGetLocation()) {
                latitude = gps!!.getLatitude()
                longitude = gps!!.getLongitude()
                location = getCompleteAddressString(latitude, longitude)
                address.text = location
                //makeMarker()
            }*/
            Glide.with(this).load(imageFilePath).into(image_camera)
            no_image.visibility = View.GONE
            add_image.visibility = View.GONE
        } else {
            if (image_camera.drawable == null && mMarker) {
                no_image.visibility = View.VISIBLE
                add_image.visibility = View.VISIBLE
            } else {
                //finish()
            }
        }
        add_image.isEnabled = true
    }

    private fun setClickListeners(view: View?) {
        view!!.close_event.setNavigationOnClickListener{
            activity?.onBackPressed();
        }

        view.add_image.setOnClickListener {
            openCameraIntent()
        }

        view.bnt_send.setOnClickListener{
            val request = ServiceBuilder.buildService(EndPoints::class.java)

            val obj = JSONObject()
            obj.put("user_id", (activity as NavigationHost).getAuthenticationUserId());
            obj.put("location", location);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            obj.put("description", description.text);

            if (imageFilePath != null) {
                obj.put("photo", getFileToByte(imageFilePath))


                obj.put("image", System.currentTimeMillis().toString() + ".jpg")
            }

            var payload = Base64.encodeToString(obj.toString().toByteArray(charset("UTF-8")), Base64.DEFAULT)
            Log.e("payload", payload)

            val call = request.insertEvent(payload = payload)

            call.enqueue(object : Callback<Event> {
                override fun onResponse(call: Call<Event>?, response: Response<Event>?) {
                    if (response!!.isSuccessful) {

                        activity?.onBackPressed()
                    } else {
                        if (response.code() == 403 && response.message() == "login_fail") {

                        } else {

                        }
                    }

                }

                override fun onFailure(call: Call<Event>?, t: Throwable?) {
                    Log.e("failerr", t!!.message!!)
                }
            })
        }

    }

    fun getFileToByte(filePath: String?): String? {
        var bmp: Bitmap? = null
        var bos: ByteArrayOutputStream? = null
        var bt: ByteArray? = null
        var encodeString: String? = null
        try {
            bmp = BitmapFactory.decodeFile(filePath)
            bos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bt = bos.toByteArray()
            encodeString = Base64.encodeToString(bt, Base64.DEFAULT)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        Log.d("IMG", encodeString!!)
        return encodeString
    }

}