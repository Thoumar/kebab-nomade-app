package com.thoumar.kebabnomade.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.crowdfire.cfalertdialog.CFAlertDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.Task
import com.google.maps.android.clustering.ClusterManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.activities.RestaurantActivity
import com.thoumar.kebabnomade.adapters.restaurants.RestaurantsAdapter
import com.thoumar.kebabnomade.entities.Restaurant
import com.thoumar.kebabnomade.others.OnSnapPositionChangeListener
import com.thoumar.kebabnomade.others.RestaurantClusterRenderer
import com.thoumar.kebabnomade.others.SnapOnScrollListener
import com.thoumar.kebabnomade.viewmodels.RestaurantsViewModel
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import java.io.IOException
import java.lang.Math.sin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sqrt

class MapFragment : Fragment(), OnMapReadyCallback, PermissionListener {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    // Theme
    private var isDark = true

    // Map and location
    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var lastLocation: Location
    private var clusterManager: ClusterManager<Restaurant>? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Data
    private lateinit var restaurants: List<Restaurant>
    private lateinit var restaurantsViewModel: RestaurantsViewModel

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)

        initViewmodel()
        initMap(savedInstanceState, v)
        initLocationProcess(v)
        initListeners(v)
        return v
    }


    private fun initViewmodel() {
        restaurantsViewModel = ViewModelProvider(this).get(RestaurantsViewModel::class.java)
    }

    @SuppressLint("VisibleForTests")
    private fun initMap(savedInstanceState: Bundle?, view: View) {
        MapsInitializer.initialize(activity?.applicationContext)
        map = view.exploreMap
        map.onCreate(savedInstanceState)
        map.onResume()
        map.getMapAsync(this)
    }

    private fun initListeners(v: View) {
        v.btn_toggle_style_map.setOnClickListener { toggleMapTheme() }
        v.btn_go_to_location.setOnClickListener { goToUserLocation() }
    }

    private fun initLocationProcess(v: View) {
        fusedLocationProviderClient = FusedLocationProviderClient(this.requireActivity())
    }

    private fun toggleMapTheme() {
        if (isDark) {
            btn_toggle_style_map.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_round_button_shape_dark
                )
            )
            btn_toggle_style_map.imageTintList =
                ColorStateList.valueOf(Color.WHITE)
            btn_go_to_location.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_round_button_shape_dark
                )
            )
            btn_go_to_location.imageTintList =
                ColorStateList.valueOf(Color.WHITE)
            setMapStyle(R.raw.style_light)
            isDark = !isDark
         } else {
             btn_toggle_style_map.setBackgroundDrawable(
                 ContextCompat.getDrawable(
                     requireContext(),
                     R.drawable.bg_round_button_shape_light
                 )
             )
            btn_toggle_style_map.imageTintList =
                ColorStateList.valueOf(Color.parseColor("#242529"))
            btn_go_to_location.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_round_button_shape_light
                )
            )
            btn_go_to_location.imageTintList =
                ColorStateList.valueOf(Color.parseColor("#242529"))
            setMapStyle(R.raw.style_dark)
            isDark = !isDark
        }
    }

    private fun onRcRestaurantClicked(restaurant: Restaurant) {
        startActivity(
            Intent(context, RestaurantActivity::class.java)
                .putExtra("RESTAURANT", restaurant)
        )
    }

    private fun setMapStyle(styleId: Int) =
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.activity, styleId))

    private fun goToUserLocation() {
        if (this::lastLocation.isInitialized) {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        lastLocation.latitude,
                        lastLocation.longitude
                    ), 15f
                )
            )
        } else {
            Toast.makeText(
                requireContext(),
                "Vous n\'avez pas encore été localisé",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun animateCamera(coordinates: LatLng) {
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 15f)
        )
    }

    private fun RecyclerView.smoothSnapToPosition(
        position: Int,
        snapMode: Int = LinearSmoothScroller.SNAP_TO_START
    ) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun onMapRestaurantClicked(restaurant: Restaurant): Boolean {
        val indexOfSelectedPlace = restaurants.lastIndexOf(restaurant)
        if (indexOfSelectedPlace != -1) restaurantsRcView.smoothSnapToPosition(indexOfSelectedPlace)
        animateCamera(LatLng(restaurant.latitude, restaurant.longitude))
        return true
    }


    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        setMapStyle(R.raw.style_dark)

        googleMap.setPadding(
            0,
            0,
            0,
            (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                200f,
                resources.displayMetrics
            )).toInt()
        )
        addAllMarkers()

        checkPermission()
//        setOnMarkerClickListener()
//        addSydneyMarker()
//        setSnapHelperBehaviorForRecyclerView()
        googleMap.setMaxZoomPreference(15.0f)
        googleMap.setMinZoomPreference(6.0f)
    }

    private fun addAllMarkers() {

        clusterManager = ClusterManager<Restaurant>(requireContext(), googleMap)
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
        clusterManager!!.setOnClusterItemClickListener { onMapRestaurantClicked(it) }

        // Is connected
        if (isOnline()) {

            // Get places
            restaurantsViewModel.getRestaurants()
                .observe(this, { restaurantList: List<Restaurant> ->
                    if (this::lastLocation.isInitialized) {
                        restaurantList.map { restaurant ->
                            restaurant.range = getDistanceFromLatLonInKm(
                                LatLng(
                                    lastLocation.latitude,
                                    lastLocation.longitude
                                ), restaurant.position
                            )
                        }
                        restaurants = restaurantList.sortedBy { place -> place.range }
                    } else {
                        restaurants = restaurantList
                    }

                    restaurantsRcView.layoutManager = LinearLayoutManager(
                        this@MapFragment.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    restaurantsRcView.adapter = RestaurantsAdapter("map", restaurants) { place ->
                        onRcRestaurantClicked(place)
                    }
                    restaurants.forEach { place -> clusterManager?.addItem(place) }
                    clusterManager!!.cluster()
                    setSnapHelperBehaviorForRecyclerView()
                })
        } else {
            val builder: CFAlertDialog.Builder = CFAlertDialog.Builder(activity)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setIcon(R.drawable.ic_no_internet)
                .setTitle("Internet indisponible")
                .setMessage("Vérifiez votre connection à internet et réessayez")
                .addButton(
                    "OK",
                    -1,
                    -1,
                    CFAlertDialog.CFAlertActionStyle.DEFAULT,
                    CFAlertDialog.CFAlertActionAlignment.CENTER
                ) { dialog, _ ->
                    dialog.dismiss()
                    activity?.finish()
                }
            builder.show()
        }

        clusterManager?.renderer =
            RestaurantClusterRenderer(requireActivity(), googleMap, clusterManager!!)
        checkPermission()
    }


    @Throws(InterruptedException::class, IOException::class)
    fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    private fun getDistanceFromLatLonInKm(latLngOne: LatLng, latLngTwo: LatLng): Double {
        val lat1 = latLngOne.latitude
        val lon1 = latLngOne.longitude
        val lat2 = latLngTwo.latitude
        val lon2 = latLngTwo.longitude
        val r = 6371
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) + cos(deg2rad(lat1)) * cos(
            deg2rad(lat2)
        ) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return round(r * c * 10.0) / 10.0
    }

    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    @SuppressLint("MissingPermission")
    private fun checkPermission() {
        if (isPermissionGiven()) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            googleMap.uiSettings.isZoomControlsEnabled = false
            getCurrentLocation()
        } else {
            givePermission()
        }
    }

    private fun setSnapHelperBehaviorForRecyclerView() {
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(restaurantsRcView)
        val behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL
        val onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                val restaurant = restaurants[position]
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            restaurant.latitude,
                            restaurant.longitude
                        ), 15f
                    )
                )
            }
        }
        val snapOnScrollListener = SnapOnScrollListener(helper, behavior, onSnapPositionChangeListener)
        restaurantsRcView.addOnScrollListener(snapOnScrollListener)
    }

    @SuppressLint("RestrictedApi")
    private fun getCurrentLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(this.requireActivity()).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this.activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result!!
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(lastLocation.latitude, lastLocation.longitude))
                    .zoom(15f)
                    .build()
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else Toast.makeText(this.requireContext(), "No current location found", Toast.LENGTH_LONG).show()
        }
    }

    private fun givePermission() = Dexter.withActivity(this.activity)
        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        .withListener(this)
        .check()

    private fun isPermissionGiven(): Boolean = ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override fun onPermissionGranted(response: PermissionGrantedResponse?) = getCurrentLocation()

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) = token!!.continuePermissionRequest()

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> if (resultCode == Activity.RESULT_OK) getCurrentLocation()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}