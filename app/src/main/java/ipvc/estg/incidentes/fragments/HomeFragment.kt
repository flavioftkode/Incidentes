package ipvc.estg.incidentes.fragments

import android.Manifest
import android.R.attr.*
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.Algorithm
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import ipvc.estg.incidentes.R
import ipvc.estg.incidentes.entities.MyMarker
import ipvc.estg.incidentes.listeners.NavigationIconClickListener
import ipvc.estg.incidentes.navigation.NavigationHost
import ipvc.estg.incidentes.retrofit.EndPoints
import ipvc.estg.incidentes.retrofit.ServiceBuilder
import ipvc.estg.incidentes.services.GPSTracker2
import ipvc.estg.incidentes.services.MarkerClusterRenderer
import kotlinx.android.synthetic.main.in_backdrop.*
import kotlinx.android.synthetic.main.in_backdrop.view.*
import kotlinx.android.synthetic.main.in_event_fragment.*
import kotlinx.android.synthetic.main.in_filter_fragment.view.*
import kotlinx.android.synthetic.main.in_home_fragment.*
import kotlinx.android.synthetic.main.in_home_fragment.view.*
import kotlinx.android.synthetic.main.in_login_fragment.*
import kotlinx.android.synthetic.main.in_main_activity.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.Result.Companion.failure
import kotlin.system.exitProcess


/**
 *
 * Criado por Flávio Fernandes a 11/12/2019
 *
 */
class HomeFragment : Fragment(), View.OnClickListener {
    var clusterManager: ClusterManager<MyMarker?>? = null
    var gps: GPSTracker2? = null
    private var _token: String? = null
    private var _userId: Int? = null
    private var mMapView: MapView? = null
    private var mMap: GoogleMap? = null
    var btnTrash: CircularProgressButton? = null
    private var mMarker: Marker? = null
    var url:String? = null
    var options: PolygonOptions? = null
    var dragLat = 0.0
    var dragLon = 0.0
    private var logged: Boolean? = false
    private var clusterManagerAlgorithm: Algorithm<MyMarker>? = null
    var myRadius : Circle? = null

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
    private lateinit var sensorManager: SensorManager
    lateinit var geofencingClient: GeofencingClient
    var geofenceList = arrayListOf<Geofence>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*SET OPTIONS MENU*/
        setHasOptionsMenu(true)
        /*PREPER SENSORMANAGER*/
        sensorManager = activity!!.getSystemService(SENSOR_SERVICE) as SensorManager
        geofencingClient = LocationServices.getGeofencingClient(activity)

        geofenceList.add(Geofence.Builder()
            .setRequestId("1")
            .setCircularRegion(
                38.7071159,
                -9.1639664,
                4000f
            )
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(1000)
            .build())

    }

    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().setInitialTrigger(0).addGeofences(listOf(geofence)).build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, HomeFragment::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.in_map_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.event_filter -> {
                /*OPEN BOTTOM SHEET FRAGMENT*/
                (activity as NavigationHost).showFilters();
                true
            }
            else -> false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*GET AUTH BEARER TOKEN AND LOGGED USER*/
        _token = authenticationToken
        _userId = authenticationUserId
        val view = inflater.inflate(R.layout.in_home_fragment, container, false)
        logged = (activity as NavigationHost).isUserLogged()

        if(logged!!){
            view.in_auth.text = getString(R.string.navigation_logout)
        }else{
            view.in_auth.text = getString(R.string.navigation_login)
        }
        // Set up the tool bar
        setToolbar(view)
        /*PROMPT USER TO GPS*/
        if (!isLocationEnabled(context)) {
            promptTurnGPS()
        }
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        declareItems(view)
        setClickListeners(view)
        /*DECLARE MAP*/
        mMapView = view!!.findViewById<View>(R.id.map) as MapView
        mMapView!!.onCreate(savedInstanceState)
        mMapView!!.onResume() // needed to get the map to display immediately

        try {
            val zoomIn: View = mMapView!!.findViewWithTag("GoogleMapZoomInButton")
            val zoomInOut = zoomIn.parent as View
            if (zoomInOut != null) {
                moveView(zoomInOut, left, top, right, bottom, false, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }
    /*NAVIGATION MENU*/
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
    /*MOVE MAP VIEW*/
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
    /*PERMISSIONS RESULT*/
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
        /*LIGHTSENSOR*/
        val lightSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        /*REGISTER LIGHTSENSOR LISTENER*/
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.e("lightSensorListener", "lightSensorListener")
        activity!!.invalidateOptionsMenu();
        /*START GETTING LOCATION UPDATES*/
        if(gps != null){
            gps!!.startUsingGPS();
        }

        if(!(activity as NavigationHost).getConsentStatus()!! && (_token != null && _token != null && _userId != 0)){
            /*ASK FOR CONSENT*/
            (activity as NavigationHost).navigateTo(
                ConsentFragment(),
                addToBackstack = false,
                animate = true,
                tag = "consent"
            )
        }
        if (mMap != null) {
            myMarkers.clear()
            /*GET ALL MARKERS IF MAP WAS PREVIOUSLY INITIALIZED*/
            getMarkers()
        }
    }

    override fun onPause() {
        super.onPause()
        //activity!!.unregisterReceiver(mNotificationReceiver)
        sensorManager.unregisterListener(lightSensorListener)
        if(gps != null){
            gps!!.stopUsingGPS();
        }

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
                (activity as NavigationHost).logout(HomeFragment(), "home")
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

    private fun declareItems(view: View?) {
        btnTrash = view!!.findViewById(R.id.call_trash)
        btnTrash!!.setBackgroundResource(R.drawable.shapeleft);
        context?.let { ContextCompat.getColor(it, R.color.cpb_blue_light) }?.let {
            view.refresh_home!!.setColorSchemeColors(
                it,
                ContextCompat.getColor(context!!, R.color.cpb_orange_light),
                ContextCompat.getColor(context!!, R.color.cpb_red_light),
                ContextCompat.getColor(context!!, R.color.cpb_green_light)
            )
        }
        view.refresh_home.setOnRefreshListener {
            getMarkers()
        }
        myLocation


    }

    private fun failed(){
        btnTrash!!.isEnabled = true
        btnTrash!!.doneLoadingAnimation(
            R.color.transparent, BitmapFactory.decodeResource(
                resources,
                R.drawable.error
            )
        )

        Handler(Looper.getMainLooper()).postDelayed(
            {
                btnTrash!!.revertAnimation();
                btnTrash!!.setBackgroundResource(R.drawable.shapeleft);
            }, 10 * 100
        )
    }

    private fun success(){
        btnTrash!!.isEnabled = true
        btnTrash!!.doneLoadingAnimation(
            R.color.transparent, BitmapFactory.decodeResource(
                resources,
                R.drawable.done
            )
        )

        Handler(Looper.getMainLooper()).postDelayed(
            {
                btnTrash!!.revertAnimation();
                btnTrash!!.setBackgroundResource(R.drawable.shapeleft);
            }, 10 * 100
        )
    }

    override fun onClick(v: View) {
        if (v === btnTrash) {

            Log.e("location", LatLng(gps!!.getLatitude(), gps!!.getLongitude()).toString())
            btnTrash!!.isEnabled = false
            btnTrash!!.startAnimation();
            //btnTrash!!.showLoading()
            Log.e("token", _token.toString())
            Log.e("_userId", _userId.toString())
            if (_token == null || _userId == 0) {
                (activity as NavigationHost).customToaster(
                    title = getString(R.string.toast_info),
                    message = getString(R.string.no_login),
                    type = "info"
                );
                success()
                goToLogin()
            } else {
                val bundle = Bundle()
                /*val intent = Intent(context, EventActivity::class.java)*/
                if (mMarker != null) {
                    bundle.putDouble("mMarker_lat", mMarker!!.position.latitude)
                    bundle.putDouble("mMarker_long", mMarker!!.position.longitude)
                    bundle.putBoolean("mMarker", true)
                    success()
                    (activity as NavigationHost).navigateToWithData(
                        EventFragment(),
                        addToBackstack = true,
                        animate = true,
                        "event",
                        bundle
                    )

                   /* intent.putExtra("mMarker_lat", mMarker.getPosition().latitude)
                    intent.putExtra("mMarker_long", mMarker.getPosition().longitude)
                    intent.putExtra("mMarker", true)
                    startActivity(intent)*/
                } else if (isLocationEnabled(context)) {
                    bundle.putBoolean("mMarker", false)
                    if (mMap != null && PolyUtil.containsLocation(
                            LatLng(
                                gps!!.getLatitude(),
                                gps!!.getLongitude()
                            ), hole, true
                        )) {
                        mMarker = mMap!!.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    gps!!.getLatitude(),
                                    gps!!.getLongitude()
                                )
                            ).draggable(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                        )
                        /*Toast.makeText(context, "placed"*//*getString("placed")*//*, Toast.LENGTH_LONG).show()*/
                        bundle.putDouble("mMarker_lat", mMarker!!.position.latitude)
                        bundle.putDouble("mMarker_long", mMarker!!.position.longitude)
                        bundle.putBoolean("mMarker", true)
                        success()
                        (activity as NavigationHost).navigateToWithData(
                            EventFragment(),
                            addToBackstack = true,
                            animate = true,
                            "event",
                            bundle
                        )
                    } else if (mMarker != null) {
                        bundle.putDouble("mMarker_lat", mMarker!!.position.latitude)
                        bundle.putDouble("mMarker_long", mMarker!!.position.longitude)
                        bundle.putBoolean("mMarker", true)
                        success()
                        (activity as NavigationHost).navigateToWithData(
                            EventFragment(),
                            addToBackstack = true,
                            animate = true,
                            "event",
                            bundle
                        )
                    } else {
                        (activity as NavigationHost).customToaster(
                            title = getString(R.string.toast_warning),
                            message = getString(R.string.marker_not_in_area),
                            type = "warning"
                        );
                        failed()
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
        return (activity as NavigationHost).getAuthenticationToken()
    }

    private val authenticationUserId: Int? get() {
        return (activity as NavigationHost).getAuthenticationUserId()
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
                    clusterManagerAlgorithm = NonHierarchicalDistanceBasedAlgorithm<MyMarker>()

                    // Set this local algorithm in clusterManager
                    clusterManager!!.algorithm = clusterManagerAlgorithm as Algorithm<MyMarker?>
                    clusterManager!!.clearItems()
                    options = PolygonOptions()
                    options!!.addAll(points).fillColor(Color.argb(100, 60, 63, 65)).strokeWidth((0).toFloat()).strokeColor(Color.TRANSPARENT)
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
                    mMap = googleMap
                    mMap!!.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.map_in_day
                        )
                    );
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
                            (activity as NavigationHost).customToaster(
                                title = getString(R.string.toast_warning),
                                message = getString(R.string.marker_not_in_area),
                                type = "warning"
                            );
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
                                (activity as NavigationHost).customToaster(
                                    title = getString(R.string.toast_warning),
                                    message = getString(R.string.marker_not_in_area),
                                    type = "warning"
                                );
                            }
                        }
                    })
                    /*mMap!!.setOnCameraChangeListener { camera ->
                        Log.e("zoom", camera.zoom.toString())
                    }*/

                    mMap!!.setOnCameraMoveStartedListener {
                        val preferences: SharedPreferences = context!!.getSharedPreferences("MAPZOOM", Context.MODE_PRIVATE)
                        val editor = preferences.edit()
                        editor.putFloat("zoom", mMap!!.cameraPosition.zoom)
                        editor.apply()
                    }

                    /*map_loading.visibility = View.GONE;*/
                }
            })
        }

    private fun setUpClusterManager() {
        if (myMarkers.isEmpty()) {
            if(!refresh_home!!.isRefreshing){
                refresh_home!!.isRefreshing = true
                getMarkers()
            }
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }


    /*GET ALL MARKERS*/
    fun getMarkers(){
        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "FILTERMAP",
            Context.MODE_PRIVATE
        )
        val radius = preferences.getInt("radius", 0)
        val obj = JSONObject()
        obj.put("0", preferences.getBoolean("0", true));
        obj.put("1", preferences.getBoolean("1", true));
        obj.put("2", preferences.getBoolean("2", true));
        obj.put("3", preferences.getBoolean("3", true));
        obj.put("4", preferences.getBoolean("4", true));
        val payload = Base64.encodeToString(
            obj.toString().toByteArray(charset("UTF-8")),
            Base64.DEFAULT
        )

        if(mMarker != null){
            mMarker!!.remove()
            mMarker = mMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(mMarker!!.position.latitude, mMarker!!.position.longitude))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
            )
        }

        geofencingClient.addGeofences(buildGeofencingRequest(geofenceList[0]), geofencePendingIntent)

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getCluster(payload)

        call.enqueue(object : Callback<List<MyMarker>> {
            override fun onResponse(
                call: Call<List<MyMarker>>?,
                response: Response<List<MyMarker>>?
            ) {
                myMarkers.clear()
                if (myRadius != null) {
                    myRadius!!.remove()
                }
                Log.e("----", "----")
                Log.e("response", response!!.body().toString())
                if (response.isSuccessful) {
                    try {
                        if (radius != 0) {
                            myRadius = mMap!!.addCircle(
                                CircleOptions()
                                    .center(LatLng(gps!!.getLatitude(), gps!!.getLongitude()))
                                    .radius(radius.toDouble())
                                    .strokeColor(R.color.cpb_light_purple)
                                    .fillColor(Color.TRANSPARENT)
                            )
                        }

                        response.body().forEach {

                            val myMarker = MyMarker(
                                id = it.id,
                                latitude = it.latitude,
                                longitude = it.longitude,
                                latLng = LatLng(it.latitude, it.longitude),
                                status = it.status,
                                location = it.location,
                                number = it.location,
                                date = it.date,
                                time = it.time,
                                description = it.description,
                                photo = it.photo,
                                photo_finish = it.photo,
                                user_id = it.user_id,
                                type = it.type
                            )


                            if (radius != 0) {
                                val locationA = Location("me")

                                locationA.latitude = gps!!.getLatitude()
                                locationA.longitude = gps!!.getLongitude()

                                val locationB = Location("marker")

                                locationB.latitude = myMarker.latitude
                                locationB.longitude = myMarker.longitude
                                val distance: Float = locationA.distanceTo(locationB)
                                Log.e("distance", distance.toString())
                                Log.e("locationA", locationA.toString())
                                Log.e("locationB", locationB.toString())
                                if (distance < radius) {
                                    myMarkers.add(myMarker)
                                }
                            } else {
                                myMarkers.add(myMarker)
                            }


                        }
                    } catch (e: Exception) {
                        Log.e("catch", e.toString())
                    }

                    clusterManager!!.renderer = MarkerClusterRenderer(
                        context!!,
                        mMap!!,
                        clusterManager!!
                    )
                    mMap!!.setOnCameraIdleListener(clusterManager)
                    clusterManager!!.clearItems()
                    clusterManager!!.addItems(myMarkers as Collection<MyMarker?>?) // 4
                    clusterManager!!.cluster()
                } else {
                    (activity as NavigationHost).customToaster(
                        title = getString(R.string.toast_error),
                        message = getString(R.string.general_error),
                        type = "error"
                    );
                }
                refresh_home!!.isRefreshing = false
            }

            override fun onFailure(call: Call<List<MyMarker>>?, t: Throwable?) {
                if (!t!!.localizedMessage.contains("End of input at line")) {
                    (activity as NavigationHost).customToaster(
                        title = getString(R.string.toast_error),
                        message = getString(R.string.general_error),
                        type = "connection"
                    );
                }

                refresh_home!!.isRefreshing = false
            }

        })
    }

    private val myLocation: LatLng
        get() {
            gps = GPSTracker2(context!!)
            return LatLng(gps!!.getLatitude(), gps!!.getLongitude())
        }



    companion object {
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

    private val lightSensorListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.e("onAccuracyChanged", "onAccuracyChanged")
            if (sensor.type == Sensor.TYPE_LIGHT) {

            }
        }

        override fun onSensorChanged(event: SensorEvent) {
            Log.e("onSensorChanged", "onSensorChanged")
            if (event.sensor.type == Sensor.TYPE_LIGHT) {
                if(event!!.values[0] < 20000.0){
                    mMap!!.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.map_in_night
                        )
                    );
                }else{
                    mMap!!.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.map_in_day
                        )
                    );
                }
            }
        }
    }
}