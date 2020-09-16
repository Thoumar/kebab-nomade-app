package com.thoumar.kebabnomade.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
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
import com.thoumar.kebabnomade.others.SnapOnScrollListener
import com.thoumar.kebabnomade.viewmodels.RestaurantsViewModel
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*

class MapFragment : Fragment(), OnMapReadyCallback, PermissionListener {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    private var isDark = true
    private lateinit var lastLocation: Location
    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var restaurants: List<Restaurant>
    private lateinit var restaurantsViewModel: RestaurantsViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v= inflater.inflate(R.layout.fragment_map, container, false)

        // View Model
        restaurantsViewModel = ViewModelProvider(this).get(RestaurantsViewModel::class.java)

        // Map
        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fusedLocationProviderClient = FusedLocationProviderClient(this.requireActivity())

        map = v.findViewById(R.id.exploreMap) as MapView
        map.onCreate(savedInstanceState)
        map.onResume()
        map.getMapAsync(this)

        // Listeners
        v.btn_toggle_style_map.setOnClickListener { toggleMapTheme() }
        v.btn_go_to_location.setOnClickListener {
            if(this::lastLocation.isInitialized) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lastLocation.latitude, lastLocation.longitude)))
            } else {
                Toast.makeText(requireContext(), "Vous n'avez pas encore était loalisé", Toast.LENGTH_LONG).show()
            }
        }
        return v
    }

    private fun toggleMapTheme() {
         if(isDark) {
             btn_toggle_style_map.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_round_button_shape_dark))
             btn_toggle_style_map.imageTintList =
                 ColorStateList.valueOf(Color.WHITE)

             btn_go_to_location.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_round_button_shape_dark))
             btn_go_to_location.imageTintList =
                 ColorStateList.valueOf(Color.WHITE)


             setStyle(R.raw.style_light)
            isDark = !isDark
         } else {
             btn_toggle_style_map.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_round_button_shape_light))
             btn_toggle_style_map.imageTintList =
                 ColorStateList.valueOf(Color.parseColor("#242529"))

             btn_go_to_location.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_round_button_shape_light))
             btn_go_to_location.imageTintList =
                 ColorStateList.valueOf(Color.parseColor("#242529"))

            setStyle(R.raw.style_dark)
            isDark = !isDark
        }
    }

    private fun setStyle(styleId: Int) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.activity, styleId))
    }

    private fun onRestaurantClick(restaurant: Restaurant) {
        val intent = Intent(context, RestaurantActivity::class.java)
        intent.putExtra("RESTAURANT", restaurant)
        startActivity(intent)
    }

    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        setStyle(R.raw.style_dark)
        addAllMarkers()
        checkPermission()
        setOnMarkerClickListener()
        addSydneyMarker()
        setSnapHelperBehaviorForRecyclerView()
    }

    private fun addAllMarkers() {
        val restaurantsLiveData: LiveData<List<Restaurant>> = restaurantsViewModel.getRestaurants()

        restaurantsLiveData.observe(this.requireActivity(), {
            restaurants = it
            setRestaurants(restaurantsRcView)
            populateMap()
        })
    }

    private fun setRestaurants(rc: RecyclerView) = rc.apply {
        layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        adapter = RestaurantsAdapter("map", restaurants) { restaurant -> onRestaurantClick(restaurant) }
    }

    @SuppressLint("Recycle")
    private fun populateMap() = restaurants.forEach { restaurant ->

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(restaurant.latitude, restaurant.longitude))
                .title(restaurant.name)
                .snippet(restaurant.description)
        )
    }

    private fun addSydneyMarker() {
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    @SuppressLint("MissingPermission")
    private fun checkPermission() {
        if (isPermissionGiven()){
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
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(restaurant.latitude, restaurant.longitude)))
            }
        }
        val snapOnScrollListener = SnapOnScrollListener(helper, behavior, onSnapPositionChangeListener)
        restaurantsRcView.addOnScrollListener(snapOnScrollListener)
    }

    private fun setOnMarkerClickListener() {
        googleMap.setOnMarkerClickListener {
            true
        }
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
                    .zoom(17f)
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

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) = getCurrentLocation()

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) = token!!.continuePermissionRequest()

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this.context, "Permission required for showing location", Toast.LENGTH_LONG).show()
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> if (resultCode == Activity.RESULT_OK) getCurrentLocation()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}