package ipvc.estg.incidentes.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.Event
import ipvc.estg.incidentes.navigation.NavigationHost
import ipvc.estg.incidentes.retrofit.EndPoints
import ipvc.estg.incidentes.retrofit.ServiceBuilder
import kotlinx.android.synthetic.main.in_event_detail_fragment.view.*
import kotlinx.android.synthetic.main.in_event_detail_fragment.view.address
import kotlinx.android.synthetic.main.in_event_detail_fragment.view.close_event
import kotlinx.android.synthetic.main.in_event_detail_fragment.view.description
import kotlinx.android.synthetic.main.in_event_detail_fragment.view.static_spinner
import kotlinx.android.synthetic.main.in_event_fragment.*
import kotlinx.android.synthetic.main.in_event_fragment.view.*
import kotlinx.android.synthetic.main.in_login_fragment.*
import kotlinx.android.synthetic.main.in_note_fragment.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class EventDetailFragment: Fragment(), OnMapReadyCallback {

    var URL: String? = null
    var position: String? = null
    var location:String? = null
    var date:String? = null
    var time:String? = null
    var description:String? = null
    var number:String? = null
    var photo:String? = null
    var photo_finish:String? = null
    var id:Int? = 0
    var status:Int = 0
    var photoView: ImageView? = null
    var noImage: TextView? = null
    var locationItem:TextView? = null
    var descriptionItem:TextView? = null
    var numberItem:TextView? = null
    var statusItem:TextView? = null
    var dateItem:TextView? = null
    var latitude = 0.0
    var longitude:kotlin.Double = 0.0
    private var mMap: GoogleMap? = null
    var action: String? = null
    var owner: Boolean = false
    var idUser: Int? = null
    var type: Int = 0
    var staticSpinner: Spinner? = null
    var btnSave: CircularProgressButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.in_event_detail_fragment, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view!!.close_event)
        val bundle = this.arguments
        if (bundle != null) {
         /*   position = intent.getStringExtra("position");*/
            latitude =   bundle.getDouble("latitude", 0.0)
            longitude = bundle.getDouble("longitude", 0.0)
            location = bundle.getString("location")
            date = bundle.getString("date")
            time = bundle.getString("time")
            status = bundle.getInt("status", 4)
            description = bundle.getString("description")
            id = bundle.getInt("id", 0)
            number = bundle.getString("number")
            photo = bundle.getString("photo")
            photo_finish = bundle.getString("photo_finish")
            action = bundle.getString("action")
            owner = bundle.getBoolean("owner")
            idUser = bundle.getInt("idUser")
            type = bundle.getInt("type")
            location = getCompleteAddressString(latitude, longitude)
            view!!.address.text = location
        }

        declareItems(view)
        setClickListeners(view)
        setItems(view)

        when (action) {
            "update" -> {
                /*  (activity as AppCompatActivity?)!!.supportActionBar!!.title = getString(R.string.note_edit)*/
                inputState(view, true);
                view.save_event.visibility = View.VISIBLE
            }
            "view" -> {
                inputState(view, false);
                view.save_event.visibility = View.GONE
            }
        }
        setMiniMap()

        return view
    }

    private fun inputState(view: View?, state: Boolean){
        view!!.description.isFocusable = state
        view!!.description.isEnabled = state
        view!!.description.isCursorVisible = state

        view!!.static_spinner.isFocusable = state
        view!!.static_spinner.isEnabled = state
    }

    private fun setClickListeners(view: View?) {
        view!!.close_event.setNavigationOnClickListener{
            activity?.onBackPressed();
        }

        view.save_event.setOnClickListener {
            btnSave!!.startAnimation();
            val obj = JSONObject()
            obj.put("id", id)
            obj.put("user_id", idUser)
            obj.put("type", staticSpinner!!.selectedItemPosition)
            obj.put("description", descriptionItem!!.text)

            val payload = Base64.encodeToString(
                obj.toString().toByteArray(charset("UTF-8")),
                Base64.DEFAULT
            )
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


            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.updateEvent(payload = payload, token!!)

            call.enqueue(object : Callback<Event> {
                override fun onResponse(call: Call<Event>?, response: Response<Event>?) {

                    if (response!!.isSuccessful && response.body().id != 0) {
                        val data = response!!.body();
                        val event = Event(
                            id = data.id,
                            latitude = data.latitude,
                            longitude = data.longitude,
                            location = data.location,
                            date = data.date,
                            time = data.time,
                            status = data.status,
                            type = data.type,
                            user_id = data.user_id,
                            description = data.description,
                            number = data.location,
                            photo = data.photo,
                            photo_finish = data.photo,
                            latLng = LatLng(
                                data.latitude,
                                data.longitude
                            )
                        )
                        activity!!.invalidateOptionsMenu();
                        val bundle = Bundle()
                        bundle.putInt("id", event.id!!)
                        bundle.putDouble("latitude", event.latitude)
                        bundle.putDouble("longitude", event.longitude)
                        bundle.putString("date", event.date)
                        bundle.putString("time", event.time)
                        bundle.putInt("status", event.status)
                        bundle.putString("description", event.description)
                        bundle.putString("number", event.number)
                        bundle.putString("photo", event.photo)
                        bundle.putString("photo_finish", event.photo_finish)
                        bundle.putBoolean("owner", event.user_id == idUser)
                        bundle.putString("action", "view")
                        bundle.putInt("idUser", event.user_id!!)
                        bundle.putInt("type", event.type)

                        success(true,bundle)


                    } else {

                        (activity as NavigationHost).customToaster(
                            title = getString(R.string.toast_error),
                            message = getString(R.string.general_error),
                            type = "error"
                        );
                        failed()
                    }
                }

                override fun onFailure(call: Call<Event>?, t: Throwable?) {
                    (activity as NavigationHost).customToaster(
                        title = getString(R.string.toast_error),
                        message = getString(R.string.general_error),
                        type = "connection"
                    );
                    failed()
                }
            })
        }
    }

    fun declareItems(view: View) {
        photoView = view.findViewById<ImageView>(R.id.image_camera)
        noImage = view.findViewById<TextView>(R.id.no_image)
        locationItem = view.findViewById<TextView>(R.id.address)
        descriptionItem = view.findViewById<TextView>(R.id.description)
        numberItem = view.findViewById<TextView>(R.id.number)
        statusItem = view.findViewById<TextView>(R.id.status)
        dateItem = view.findViewById<TextView>(R.id.date)

        staticSpinner = view.findViewById(R.id.static_spinner) as Spinner
        val staticAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.type_array,
            android.R.layout.simple_spinner_item
        )
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        staticSpinner!!.adapter = staticAdapter
        staticSpinner!!.setSelection(type)

        btnSave = view.findViewById(R.id.save_event)
        btnSave!!.setBackgroundResource(R.drawable.shapeleft);
    }

    fun setMiniMap() {
        val mapFragment = childFragmentManager!!.findFragmentById(R.id.map_lite) as SupportMapFragment?
        Objects.requireNonNull(mapFragment!!.view)!!.isClickable = false
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 7f))
        mMap = googleMap
        mMap!!.isBuildingsEnabled = true
        mMap!!.uiSettings.isMapToolbarEnabled = true
        makeMarker()
    }

    fun onPointerCaptureChanged(hasCapture: Boolean) {}

    private fun makeMarker() {
        val marker = LatLng(latitude, longitude)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 17f))
        if (status == 1) {
            mMap!!.addMarker(
                MarkerOptions().position(marker).icon(
                    BitmapDescriptorFactory.fromResource(
                        R.drawable.ic_green_pin
                    )
                )
            )
        } else if (status == 2 || status == 3) {
            mMap!!.addMarker(
                MarkerOptions().position(marker).icon(
                    BitmapDescriptorFactory.fromResource(
                        R.drawable.ic_yellow_pin
                    )
                )
            )
        } else if (status == 4) {
            mMap!!.addMarker(
                MarkerOptions().position(marker).icon(
                    BitmapDescriptorFactory.fromResource(
                        R.drawable.ic_red_pin
                    )
                )
            )
        } else {
            mMap!!.addMarker(
                MarkerOptions().position(marker).icon(
                    BitmapDescriptorFactory.fromResource(
                        R.drawable.ic_grey_pin
                    )
                )
            )
        }
    }

    private fun decodeBase64(input: String?): Bitmap? {
        val decodedByte = Base64.decode(input, Base64.DEFAULT)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size, options)
    }

    fun setItems(view: View) {
        if (photo != null && !photo!!.isEmpty()) {
            try{
                val bitmap = decodeBase64(photo)
                photoView!!.setImageBitmap(bitmap)
            }catch (e: java.lang.Exception){}
        } else {
            noImage!!.visibility = View.VISIBLE
        }
        locationItem!!.text = location
        descriptionItem!!.text = description
        numberItem!!.text = number
        if (status == 1) {
            view.findViewById<View>(R.id.number_color).setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.success
                )
            )
            statusItem!!.text = getString(R.string.status_completed)
            statusItem!!.setTextColor(getColor(context!!, R.color.success))
        } else if (status == 2 || status == 3) {
            view.findViewById<View>(R.id.number_color).setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.warning
                )
            )
            statusItem!!.text = getString(R.string.status_in_progress)
            statusItem!!.setTextColor(getColor(context!!, R.color.warning))
        } else if (status == 4) {
            view.findViewById<View>(R.id.number_color).setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.danger
                )
            )
            statusItem!!.text = getString(R.string.status_received)
            statusItem!!.setTextColor(getColor(context!!, R.color.danger))
        } else {
            view.findViewById<View>(R.id.number_color).setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.cpb_grey
                )
            )
            statusItem!!.text = getString(R.string.status_error)
            statusItem!!.setTextColor(getColor(context!!, R.color.cpb_grey))
        }
        try {
            val date_time = SimpleDateFormat("dd/MM/yyyy").parse(date)
            val formattedDate = SimpleDateFormat("dd MMMM yyyy").format(date_time)
            dateItem!!.text = "$formattedDate $time"
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.in_event_toolbar, menu)

        menu.findItem(R.id.event_edit).isVisible = (action == "view" && owner)
        menu.findItem(R.id.event_edit_cancel).isVisible = (action == "update" && owner)
        menu.findItem(R.id.event_cancel).isVisible = (owner && status == 4)
    }

    private fun failed(){
        btnSave!!.isEnabled = true
        btnSave!!.doneLoadingAnimation(R.color.transparent, BitmapFactory.decodeResource(resources, R.drawable.error))

        Handler().postDelayed(Runnable
        {
            btnSave!!.revertAnimation();
            btnSave!!.setBackgroundResource(R.drawable.shape);
        }, 10 * 100)
    }

    private fun success(back:Boolean,bundle:Bundle){
        btnSave!!.isEnabled = true
        btnSave!!.doneLoadingAnimation(R.color.transparent, BitmapFactory.decodeResource(resources, R.drawable.done))
        Handler().postDelayed(Runnable
        {
            btnSave!!.revertAnimation();
            btnSave!!.setBackgroundResource(R.drawable.shape);
            if(back){
                (activity as NavigationHost).navigateToWithData(EventDetailFragment(), addToBackstack = false, animate = true, "event_detail", bundle)
                (activity as NavigationHost).customToaster(title = getString(R.string.toast_success), message = getString(R.string.event_updated), type = "success");
            }
        }, 10 * 100)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.event_cancel -> {
                MaterialAlertDialogBuilder(context!!, R.style.MaterialAlertDialog_rounded)
                    .setTitle(getString(R.string.remove_event))
                    .setMessage(getString(R.string.remove_event_body))
                    .setPositiveButton(getString(R.string.yes_remove)) { dialog, which ->
                        val token = (activity as NavigationHost).getAuthenticationToken()
                        val request = ServiceBuilder.buildService(EndPoints::class.java)
                        val call = request.deleteEvent(id.toString(), idUser.toString(), token!!)
                        call.enqueue(object : Callback<Event> {
                            override fun onResponse(call: Call<Event>?, response: Response<Event>?) {
                                if (response!!.isSuccessful) {
                                    (activity as NavigationHost).customToaster(
                                        title = getString(R.string.toast_success),
                                        message = getString(R.string.event_deleted),
                                        type = "success"
                                    );
                                    activity?.onBackPressed();
                                }else{
                                    (activity as NavigationHost).customToaster(
                                        title = getString(R.string.toast_error),
                                        message = getString(R.string.general_error),
                                        type = "error"
                                    );
                                }
                            }

                            override fun onFailure(call: Call<Event>?, t: Throwable?) {
                                (activity as NavigationHost).customToaster(
                                    title = getString(R.string.toast_error),
                                    message = getString(R.string.general_error),
                                    type = "connection"
                                );
                            }
                        });
                    }
                    .setNegativeButton(getString(R.string.no_remove)) { dialog, which -> }
                    .show()
                true
            }
            R.id.event_edit -> {
                activity!!.invalidateOptionsMenu();
                val bundle = Bundle()
                bundle.putInt("id", id!!)
                /* bundle.putCharSequence("position", position)*/
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
                bundle.putString("date", date)
                bundle.putString("time", time)
                bundle.putInt("status", status)
                bundle.putString("description", description)
                bundle.putString("number", number)
                bundle.putString("photo", photo)
                bundle.putString("photo_finish", photo_finish)
                bundle.putBoolean("owner", owner)
                bundle.putString("action", "update")
                bundle.putInt("idUser", idUser!!)
                bundle.putInt("type", type)
                (activity as NavigationHost).navigateToWithData(
                    EventDetailFragment(),
                    addToBackstack = false,
                    animate = true,
                    "event_detail",
                    bundle
                )
                true
            }
            R.id.event_edit_cancel -> {
                activity!!.invalidateOptionsMenu();
                val bundle = Bundle()
                bundle.putInt("id", id!!)
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
                bundle.putString("date", date)
                bundle.putString("time", time)
                bundle.putInt("status", status)
                bundle.putString("description", description)
                bundle.putString("number", number)
                bundle.putString("photo", photo)
                bundle.putString("photo_finish", photo_finish)
                bundle.putBoolean("owner", owner)
                bundle.putString("action", "view")
                bundle.putInt("idUser", idUser!!)
                bundle.putInt("type", type)
                (activity as NavigationHost).navigateToWithData(
                    EventDetailFragment(),
                    addToBackstack = false,
                    animate = true,
                    "event_detail",
                    bundle
                )
                true
            }
            else -> false
        }
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

}