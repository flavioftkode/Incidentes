package ipvc.estg.incidentes.fragments

import android.Manifest
import android.R.attr.*
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import com.google.maps.android.PolyUtil
import com.google.maps.android.clustering.ClusterManager
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.MyMarker
import ipvc.estg.incidentes.listeners.NavigationIconClickListener
import ipvc.estg.incidentes.navigation.NavigationHost
import ipvc.estg.incidentes.retrofit.EndPoints
import ipvc.estg.incidentes.retrofit.ServiceBuilder
import ipvc.estg.incidentes.services.GPSTracker
import ipvc.estg.incidentes.services.MarkerClusterRenderer
import kotlinx.android.synthetic.main.in_backdrop.view.*
import kotlinx.android.synthetic.main.in_home_fragment.view.*
import kotlinx.android.synthetic.main.in_login_fragment.*
import kotlinx.android.synthetic.main.in_main_activity.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess


/**
 *
 * Criado por Flávio Fernandes a 11/12/2019
 *
 */
class HomeFragment : Fragment(), View.OnClickListener {
    var clusterManager: ClusterManager<MyMarker>? = null
    var gps: GPSTracker? = null
    private var _token: String? = null
    private var _userId: String? = null
    private var mMapView: MapView? = null
    private var mMap: GoogleMap? = null
    var btnTrash: MaterialButton? = null
    private var mMarker: Marker? = null
    var url:String? = null
    var options: PolygonOptions? = null
    var dragLat = 0.0
    var dragLon = 0.0
    private var logged: Boolean? = false

    private val delta = 0.1f
    private val points: List<LatLng> = listOf<LatLng>(
        LatLng(90.0, -180.0),
        LatLng(-90.0 + delta, -180.0 + delta),
        LatLng(-90.0 + delta, 0.0),
        LatLng(-90.0 + delta, 180.0 - delta),
        LatLng(0.0, 180.0 - delta),
        LatLng(90.0 - delta, 180.0 - delta),
        LatLng(90.0 - delta, 0.0),
        LatLng(90.0 - delta, -180.0 + delta),
        LatLng(0.0, -180.0 + delta)
    )
    private val hole: List<LatLng> = listOf<LatLng>(
        LatLng(38.6987916, -9.1759297),
        LatLng(38.698702, -9.1759008),
        LatLng(38.6986523, -9.1759037),
        LatLng(38.6987057, -9.1755862),
        LatLng(38.698109, -9.1754178),
        LatLng(38.6980933, -9.1755172),
        LatLng(38.6978989, -9.1754642),
        LatLng(38.7001286, -9.1632581),
        LatLng(38.7004133, -9.1633265),
        LatLng(38.7008601, -9.1625508),
        LatLng(38.7010617, -9.162604),
        LatLng(38.7011967, -9.1618713),
        LatLng(38.7020117, -9.1573168),
        LatLng(38.7021014, -9.1570351),
        LatLng(38.7021993, -9.1570597),
        LatLng(38.702218, -9.1571812),
        LatLng(38.7019842, -9.1592504),
        LatLng(38.7018588, -9.1611756),
        LatLng(38.7021096, -9.1615327),
        LatLng(38.7021102, -9.1618556),
        LatLng(38.702422, -9.1618525),
        LatLng(38.7024176, -9.1614072),
        LatLng(38.7024175, -9.161397),
        LatLng(38.7025831, -9.1612074),
        LatLng(38.7025831, -9.1612074),
        LatLng(38.7025564, -9.1592445),
        LatLng(38.7033711, -9.1590694),
        LatLng(38.7037529, -9.1569364),
        LatLng(38.7037529, -9.1569364),
        LatLng(38.7042586, -9.157253),
        LatLng(38.7044799, -9.1560134),
        LatLng(38.7035984, -9.1557482),
        LatLng(38.7037528, -9.1549038),
        LatLng(38.7037528, -9.1549038),
        LatLng(38.7044546, -9.1551118),
        LatLng(38.7046741, -9.1549413),
        LatLng(38.7051941, -9.1520473),
        LatLng(38.7065567, -9.1524407),
        LatLng(38.7069194, -9.1524382),
        LatLng(38.7091786, -9.1531231),
        LatLng(38.7112807, -9.1536318),
        LatLng(38.7112807, -9.1536318),
        LatLng(38.7117563, -9.1538551),
        LatLng(38.7116092, -9.1535556),
        LatLng(38.7122406, -9.1528057),
        LatLng(38.7140004, -9.153409),
        LatLng(38.7142411, -9.1535313),
        LatLng(38.7150071, -9.1540936),
        LatLng(38.7151962, -9.1542302),
        LatLng(38.7144551, -9.1565723),
        LatLng(38.7144159, -9.1575158),
        LatLng(38.7151142, -9.1572657),
        LatLng(38.715772, -9.1586894),
        LatLng(38.7155356, -9.1606997),
        LatLng(38.7156157, -9.1609155),
        LatLng(38.7163488, -9.1615825),
        LatLng(38.7161572, -9.1620205),
        LatLng(38.7154889, -9.1637539),
        LatLng(38.7146555, -9.1642788),
        LatLng(38.7137119, -9.1647378),
        LatLng(38.7134084, -9.1648323),
        LatLng(38.7128134, -9.1648858),
        LatLng(38.7127289, -9.1652022),
        LatLng(38.7128408, -9.1657611),
        LatLng(38.7128338, -9.1661439),
        LatLng(38.7125911, -9.1671119),
        LatLng(38.7126753, -9.1675649),
        LatLng(38.7125336, -9.1682125),
        LatLng(38.7125754, -9.1683717),
        LatLng(38.7129873, -9.1686478),
        LatLng(38.7151569, -9.1695036),
        LatLng(38.7151116, -9.1699398),
        LatLng(38.7157928, -9.1721219),
        LatLng(38.7144109, -9.1730207),
        LatLng(38.7139561, -9.1732197),
        LatLng(38.7136206, -9.1732971),
        LatLng(38.7131632, -9.1733123),
        LatLng(38.7131555, -9.1732191),
        LatLng(38.7123311, -9.1729878),
        LatLng(38.712115, -9.1733876),
        LatLng(38.712115, -9.1733876),
        LatLng(38.7121401, -9.1751393),
        LatLng(38.7108695, -9.1745352),
        LatLng(38.7100166, -9.1741112),
        LatLng(38.7091511, -9.1737677),
        LatLng(38.7085808, -9.1736746),
        LatLng(38.7080854, -9.1737118),
        LatLng(38.7063682, -9.1739154),
        LatLng(38.7043472, -9.1744874),
        LatLng(38.7014608, -9.1754405),
        LatLng(38.6990938, -9.1749084),
        LatLng(38.6989299, -9.1759008),
        LatLng(38.6987916, -9.1759297)
    )
    private var myMarkers: MutableList<MyMarker> = ArrayList<MyMarker>()
    var received = true
    var finished = false
    var progress = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        _token = authenticationToken
        _userId = authenticationUserId?.toString()
        val view = inflater.inflate(R.layout.in_home_fragment, container, false)

        logged = (activity as NavigationHost).getLoggedUser()

        if(logged!!){
            view.in_auth.text = getString(R.string.navigation_logout)
        }else{
            view.in_auth.text = getString(R.string.navigation_login)
        }


        //url = "getString(R.string.main_url) + getString(R.string.url_geslixo_server_api) + getString(R.string.url_get_incidente)"

        // Set up the tool bar
        setToolbar(view);

       /* map_loading?.visibility = View.GONE;*/
        if (!isLocationEnabled(context)) {
            promptTurnGPS()
        }
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        declareItems(view)
        setClickListeners(view)
        mMapView = view!!.findViewById<View>(R.id.map) as MapView
        mMapView!!.onCreate(savedInstanceState)
        mMapView!!.onResume() // needed to get the map to display immediately

        try {
            val zoomIn: View = mMapView!!.findViewWithTag("GoogleMapZoomInButton")

            // we need the parent view of the zoomin/zoomout buttons - it didn't have a tag
            // so we must get the parent reference of one of the zoom buttons

            // we need the parent view of the zoomin/zoomout buttons - it didn't have a tag
            // so we must get the parent reference of one of the zoom buttons
            val zoomInOut = zoomIn.parent as View

            if (zoomInOut != null) {
                moveView(zoomInOut, left, top, right, bottom, false, false)
            }
            /*   MapsInitializer.initialize(getActivity().getApplicationContext());*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    private fun setToolbar(view: View?){
        (activity as AppCompatActivity).setSupportActionBar(view!!.app_bar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = getString(R.string.home)
        view.app_bar.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!, view.map_grid,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(
                    context!!, R.drawable.in_menu
                ), // Menu open icon
                ContextCompat.getDrawable(context!!, R.drawable.in_close)
            )
        ) // Menu close icon
    }

    private fun moveView(
        view: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        horizontal: Boolean,
        vertical: Boolean
    ) {
        try {
            assert(view != null)

            // replace existing layout params
            val rlp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            if (left >= 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
            }
            if (top >= 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            }
            if (right >= 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
            }
            if (bottom >= 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            }
            if (horizontal) {
                rlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            }
            if (vertical) {
                rlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
            }
            rlp.setMargins(left, top, right, bottom)
            view!!.layoutParams = rlp
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map
                } else {
                    activity!!.finish()
                    exitProcess(0)
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /*map_loading.visibility = View.VISIBLE;*/
        activity!!.registerReceiver(mNotificationReceiver, IntentFilter("FILTER"))
        if (mMap != null) {
            mMap!!.clear()
            myMarkers.clear()
            map
        }
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(mNotificationReceiver)
    }

    private val mNotificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            received = intent.extras!!.getBoolean("RECEIVED")
            progress = intent.extras!!.getBoolean("PROGRESS")
            finished = intent.extras!!.getBoolean("FINISHED")
            if (mMap != null) {
                clusterManager!!.clearItems()
                mMap!!.clear()
                myMarkers.clear()
                map
            }
        }
    }

    private fun promptTurnGPS() {
        val alertDialog = AlertDialog.Builder(
            context!!
        )
        alertDialog.setTitle("titulo")
        alertDialog.setMessage("body")
        alertDialog.setPositiveButton("positive") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activity!!.startActivity(intent)
        }
        alertDialog.setNegativeButton("negative") { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.show()
    }

    private fun setClickListeners(view: View?) {
        btnTrash!!.setOnClickListener(this)
        view!!.in_auth.setOnClickListener {
            if(logged!!){
                (activity as NavigationHost).logout(HomeFragment())
            }else{
                (activity as NavigationHost).navigateTo(
                    LoginFragment(),
                    addToBackstack = true,
                    animate = true
                )
            }
        }

        view.in_notes.setOnClickListener {
            (activity as NavigationHost).navigateTo(
                NotesFragment(),
                addToBackstack = false,
                animate = true
            )
        }
    }

    private fun declareItems(root: View?) {
        btnTrash = root!!.findViewById(R.id.call_trash)
    }

    override fun onClick(v: View) {
        if (v === btnTrash) {
            btnTrash!!.isEnabled = false
            //btnTrash!!.showLoading()
            if (authenticationToken == null || authenticationToken!!.isEmpty()) {
                Toast.makeText(context, "no login", Toast.LENGTH_SHORT).show()
                goToLogin()
            } else {
                val bundle = Bundle()
                /*val intent = Intent(context, EventActivity::class.java)*/
                if (mMarker != null) {
                    bundle.putDouble("mMarker_lat", mMarker!!.position.latitude)
                    bundle.putDouble("mMarker_long", mMarker!!.position.longitude)
                    bundle.putBoolean("mMarker", true)
                    (activity as NavigationHost).navigateToWithData(EventFragment(), addToBackstack = true, animate = true, "event", bundle)

                   /* intent.putExtra("mMarker_lat", mMarker.getPosition().latitude)
                    intent.putExtra("mMarker_long", mMarker.getPosition().longitude)
                    intent.putExtra("mMarker", true)
                    startActivity(intent)*/
                } else if (isLocationEnabled(context)) {
                    bundle.putBoolean("mMarker", false)
                    if (mMap != null && PolyUtil.containsLocation(myLocation, hole, true)) {
                        myLocation
                        mMarker = mMap!!.addMarker(MarkerOptions().position(myLocation).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)))
                        Toast.makeText(context, "placed"/*getString("placed")*/, Toast.LENGTH_LONG)
                            .show()
                    } else if (mMarker != null) {
                        bundle.putDouble("mMarker_lat", mMarker!!.position.latitude)
                        bundle.putDouble("mMarker_long", mMarker!!.position.longitude)
                        bundle.putBoolean("mMarker", true)
                        (activity as NavigationHost).navigateToWithData(EventFragment(), addToBackstack = true, animate = true, "event", bundle)
                    } else {
                        Toast.makeText(
                            context,
                            /*getString(R.string.not_in_the_area)*/"not in area",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    promptTurnGPS()
                }
            }
            //btnTrash!!.hideLoading()
            btnTrash!!.isEnabled = true
        }
    }

    private val authenticationToken: String? get() {
            val sharedPref = context!!.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)
            return sharedPref.getString("_token", null)
        }

    private val authenticationUserId: Int? get() {
            val sharedPref = context!!.getSharedPreferences(
                "AUTHENTICATION",
                Context.MODE_PRIVATE
            )
            return sharedPref.getInt("iduser", 0)
        }

    private fun goToLogin() {
        /*startActivity(Intent(context, LoginActivity::class.java))*/
        (activity as NavigationHost).navigateTo(
            LoginFragment(),
            addToBackstack = true,
            animate = true
        )
    }

    /* LatLng portugal = new LatLng( 39.477658, -8.141747);*/
    private val map: // 3
            Unit
        get() {
            mMapView!!.getMapAsync(object : OnMapReadyCallback {
                override fun onMapReady(googleMap: GoogleMap) {
                    /* LatLng portugal = new LatLng( 39.477658, -8.141747);*/
                    val portugal = LatLng(38.7071159, -9.1639664)
                    clusterManager = ClusterManager(context, googleMap) // 3
                    clusterManager!!.clearItems()
                    options = PolygonOptions()
                    options!!.addAll(points).fillColor(Color.argb(100, 60, 63, 65))
                        .strokeWidth((0).toFloat())
                        .strokeColor(
                            Color.TRANSPARENT
                        )
                    options!!.addHole(hole)
                    val poly = PolygonOptions()
                    poly.addAll(hole).strokeWidth((5).toFloat()).strokeColor(R.color.white)
                    googleMap.addPolygon(options)
                    googleMap.addPolygon(poly)
                    googleMap.isBuildingsEnabled = true
                    if (ActivityCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    googleMap.isMyLocationEnabled = true
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = true
                    googleMap.uiSettings.isMapToolbarEnabled = false
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(portugal, 14.3f))
                    myLocation
                    mMap = googleMap
                    setUpClusterManager()
                    mMap!!.setOnMapClickListener { point ->
                        if (PolyUtil.containsLocation(point, hole, true)) {
                            if (mMarker != null) {
                                mMarker!!.remove()
                            }
                            mMarker = mMap!!.addMarker(
                                MarkerOptions().position(point!!).draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                            )
                            mMap!!.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    point,
                                    mMap!!.cameraPosition.zoom
                                )
                            )
                            dragLat = mMarker!!.position.latitude
                            dragLon = mMarker!!.position.longitude
                        } else {
                            Toast.makeText(context, "invalid", Toast.LENGTH_SHORT).show()
                        }
                    }
                    mMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                        override fun onMarkerClick(marker: Marker): Boolean {
                            if (marker == mMarker) {
                                marker.remove()
                                mMarker = null
                                return true
                            }
                            return false
                        }
                    })
                    mMap!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                        override fun onMarkerDragStart(marker: Marker?) {}
                        override fun onMarkerDrag(marker: Marker?) {}
                        override fun onMarkerDragEnd(marker: Marker) {
                            if (PolyUtil.containsLocation(marker.position, hole, true)) {
                                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
                                mMarker = marker
                                dragLat = mMarker!!.position.latitude
                                dragLon = mMarker!!.position.longitude
                            } else {
                                marker.remove()
                                mMarker = mMap!!.addMarker(
                                    MarkerOptions().position(
                                        LatLng(
                                            dragLat,
                                            dragLon
                                        )
                                    ).draggable(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                                )
                                mMap!!.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        mMarker!!.position,
                                        mMap!!.cameraPosition.zoom
                                    )
                                )
                                Toast.makeText(context, "invalide", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })

                    /*map_loading.visibility = View.GONE;*/
                }
            })
        }

    private fun setUpClusterManager() {
        if (_token != null && _userId != null && myMarkers.isEmpty()) {
            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.getCluster()

            call.enqueue(object : Callback<List<MyMarker>> {
                override fun onResponse(
                    call: Call<List<MyMarker>>?,
                    response: Response<List<MyMarker>>?
                ) {

                    if (response!!.isSuccessful) {

                        /*response.body().forEach {
                            *//*Log.e("response",it.photo.toString())*//*
                            //val decodedString: String = Base64.decode(it.photo?.toByteArray(charset("UTF-8")), Base64.DEFAULT).toString()

                            *//*val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)*//*

                            val myMarker = MyMarker(
                                id = it.id,
                                latitude = it.latitude,
                                longitude = it.longitude,
                                latLng = LatLng(it.latitude,it.longitude),
                                status = 1,
                                location = it.location,
                                number = it.location,
                                date = it.location,
                                time = it.location,
                                description = it.description,
                                photo = decodedString,
                                photo_finish = decodedString
                            )

                            //myMarkers.add(myMarker)
                        }*/

                        myMarkers = response.body() as MutableList<MyMarker>
                        /*val myMarker = MyMarker(
                            id= response.body().id,
                            latLng = LatLng(2.2, 2.2),
                            status = 1,
                            location = response.body().location,
                            number = response.body().location,
                            date = response.body().location,
                            time = response.body().location,
                            description = response.body().description,
                            photo = response.body().photo,
                            photo_finish = response.body().photo
                        )*/

                        //myMarkers = response.body() as MutableList<MyMarker>

                        clusterManager!!.renderer = MarkerClusterRenderer(
                            context!!,
                            mMap!!,
                            clusterManager!!
                        )
                        mMap!!.setOnCameraIdleListener(clusterManager)
                        clusterManager!!.clearItems()
                        clusterManager!!.addItems(myMarkers) // 4
                        clusterManager!!.cluster()
                        (activity as NavigationHost).customToaster(
                            response.toString(),
                            "ic_error_small",
                            Toast.LENGTH_LONG
                        );
                    } else {
                        if (response.code() == 403 && response.message() == "login_fail") {
                            username_text_input.error = getString(R.string.wrong_user_info)
                            password_text_input.error = getString(R.string.wrong_user_info)
                        } else {
                            (activity as NavigationHost).customToaster(
                                "fail resp",
                                "ic_error_small",
                                Toast.LENGTH_LONG
                            );
                        }
                    }
                }

                override fun onFailure(call: Call<List<MyMarker>>?, t: Throwable?) {
                    (activity as NavigationHost).customToaster(
                        t!!.message.toString(),
                        "ic_error_small",
                        Toast.LENGTH_LONG
                    );
                    Log.e("responseerr", t!!.message.toString())
                }

            })
        }
    }

    private val myLocation: LatLng
        get() {
            gps = GPSTracker(context!!)
            return LatLng(gps!!.getLatitude(), gps!!.getLongitude())
        }

   /* private fun volleyService(queue: RequestQueue) {
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, URL,
            Response.Listener { response ->
                try {
                    myMarkers.clear()
                    val `val` = JSONObject(response)
                    val jsonMainNode = `val`.getJSONArray("data")
                    eventCounter = 0
                    while (eventCounter < jsonMainNode.length()) {
                        val jsonFirstIndex = jsonMainNode.getJSONObject(eventCounter)
                        val id = jsonFirstIndex.optInt("id_incidente")
                        val latitude = jsonFirstIndex.optDouble("latitude")
                        val longitude = jsonFirstIndex.optDouble("longitude")
                        val status = jsonFirstIndex.optInt("estado")
                        val location = jsonFirstIndex.optString("localizacao")
                        val n_incidente = jsonFirstIndex.optString("n_incidente")
                        val date = jsonFirstIndex.optString("data_incidente")
                        val time = jsonFirstIndex.optString("hora_incidente")
                        val description = jsonFirstIndex.optString("descricao")
                        val photo = jsonFirstIndex.optString("foto")
                        val photo_finish = jsonFirstIndex.optString("foto_recolha")
                        Log.d("JSON", jsonFirstIndex.toString())
                        val myMarker = MyMarker(
                            id,
                            LatLng(latitude, longitude),
                            status,
                            location,
                            n_incidente,
                            date,
                            time,
                            description,
                            photo,
                            photo_finish
                        )
                        if (received && status == 0) {
                            myMarkers.add(myMarker)
                        } else if (finished && status == 1) {
                            myMarkers.add(myMarker)
                        } else if (progress && status == 2 || progress && status == 3) {
                            myMarkers.add(myMarker)
                        }
                        eventCounter++
                    }
                    clusterManager.setRenderer(MarkerClusterRenderer(context, mMap, clusterManager))
                    mMap.setOnCameraIdleListener(clusterManager)
                    clusterManager.clearItems()
                    clusterManager.addItems(myMarkers) // 4
                    clusterManager.cluster()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError ->
                var message = ""
                if (volleyError is NetworkError) {
                    message = getString(R.string.network_error)
                } else if (volleyError is ServerError) {
                    message = getString(R.string.server_error)
                } else if (volleyError is AuthFailureError) {
                    message = getString(R.string.auth_failure_error)
                    clearAuthentication()
                    disconnectFromFacebook()
                    activity!!.finish()
                    startActivity(activity!!.intent)
                } else if (volleyError is ParseError) {
                    message = getString(R.string.parsing_error)
                } else if (volleyError is NoConnectionError) {
                    message = getString(R.string.no_connection_error)
                } else if (volleyError is TimeoutError) {
                    message = getString(R.string.time_out_error)
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["token"] = _token!!
                params["method"] = "GET_BY_CIDADAO"
                params["id_cidadao"] = _userId!!
                params["user_id"] = _userId!!
                return params
            }
        }
        stringRequest.retryPolicy =
            DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(stringRequest)
    }*/

    companion object {
        private var URL: String? = null
        var eventCounter = 0
        private fun isLocationEnabled(context: Context?): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val lm = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                lm.isLocationEnabled
            } else {
                val mode = Settings.Secure.getInt(
                    context!!.contentResolver, Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                mode != Settings.Secure.LOCATION_MODE_OFF
            }
        }
    }
}