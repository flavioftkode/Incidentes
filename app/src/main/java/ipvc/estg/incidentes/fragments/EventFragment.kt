package ipvc.estg.incidentes.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.incidentes.BuildConfig
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.Event
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
    var staticSpinner: Spinner? = null
    var bitmap: Bitmap? = null
    var btnSend: CircularProgressButton? = null

    @RequiresApi(Build.VERSION_CODES.O)
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
        staticSpinner = view.findViewById(R.id.static_spinner) as Spinner
        val staticAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.type_array,
            android.R.layout.simple_spinner_item
        )
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        staticSpinner!!.adapter = staticAdapter
        staticSpinner!!.setSelection(0)

        view.add_image.setText(R.string.event_add_image)

        btnSend = view.findViewById<CircularProgressButton>(R.id.bnt_send)
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
                val frag: Fragment = this
                frag.startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            bitmap = BitmapFactory.decodeFile(imageFilePath)
            image_camera.setImageBitmap(bitmap)
            no_image.visibility = View.GONE
            add_image.setText(R.string.event_edit_image)
            required.visibility = View.GONE
        }
        add_image.isEnabled = true
    }

    private fun failed(){
        bnt_send!!.isEnabled = true
        bnt_send!!.doneLoadingAnimation(
            R.color.transparent, BitmapFactory.decodeResource(
                resources,
                R.drawable.error
            )
        )

        Handler().postDelayed(Runnable
        {
            bnt_send!!.revertAnimation();
            bnt_send!!.setBackgroundResource(R.drawable.shape);
        }, 10 * 100)
    }

    private fun success(){
        bnt_send!!.isEnabled = true
        bnt_send!!.doneLoadingAnimation(
            R.color.transparent, BitmapFactory.decodeResource(
                resources,
                R.drawable.done
            )
        )

        Handler().postDelayed(Runnable
        {
            bnt_send!!.revertAnimation();
            bnt_send!!.setBackgroundResource(R.drawable.shape);
            (activity as NavigationHost).customToaster(title = getString(R.string.toast_success), message = getString(R.string.event_created), type = "success");
            activity?.onBackPressed()
        }, 10 * 100)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickListeners(view: View?) {
        view!!.close_event.setNavigationOnClickListener{
            activity?.onBackPressed();
        }

        view.add_image.setOnClickListener {
            openCameraIntent()
        }

        view.bnt_send.setOnClickListener{
            btnSend!!.startAnimation();
            view.required.visibility = View.GONE

            if(description.text!!.isEmpty() && bitmap == null){
                failed()
                view.required.visibility = View.VISIBLE
                description.error = getString(R.string.field_required)
                return@setOnClickListener
            }else if(description.text!!.isEmpty()){
                failed()
                description.error = getString(R.string.field_required)
                return@setOnClickListener
            }else if(bitmap == null){
                failed()
                view.required.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val request = ServiceBuilder.buildService(EndPoints::class.java)

            val obj = JSONObject()
            obj.put("user_id", (activity as NavigationHost).getAuthenticationUserId());
            obj.put("location", location);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            obj.put("description", description.text);
            obj.put("type", staticSpinner!!.selectedItemPosition)

            if (bitmap != null) {
                val bos = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, bos)
                val image:ByteArray = bos.toByteArray()
                val base64Encoded = java.util.Base64.getEncoder().encodeToString(image)
                obj.put("photo", base64Encoded)
            }

            val payload = Base64.encodeToString(obj.toString().toByteArray(charset("UTF-8")), Base64.DEFAULT)
            val token = (activity as NavigationHost).getAuthenticationToken()
            if(token == null){
                (activity as NavigationHost).customToaster(
                    title = getString(R.string.toast_info),
                    message = getString(R.string.no_login),
                    type = "info"
                );
                (activity as NavigationHost).navigateTo(
                    LoginFragment(),
                    addToBackstack = true,
                    animate = true
                )
            }

            val call = request.insertEvent(payload = payload, token!!)

            call.enqueue(object : Callback<Event> {
                override fun onResponse(call: Call<Event>?, response: Response<Event>?) {
                    Log.e("response", response.toString())
                    if (response!!.isSuccessful) {
                        success()
                    } else {
                        failed()
                        (activity as NavigationHost).customToaster(
                            title = getString(R.string.toast_error),
                            message = getString(R.string.general_error),
                            type = "error"
                        );
                    }
                }

                override fun onFailure(call: Call<Event>?, t: Throwable?) {
                    Log.e("error",t!!.localizedMessage)
                    failed()
                    if(!t!!.localizedMessage.contains("Expected BEGIN_OBJECT")){
                        (activity as NavigationHost).customToaster(
                            title = getString(R.string.toast_error),
                            message = getString(R.string.general_error),
                            type = "connection"
                        );
                    }
                }
            })
        }
    }
}