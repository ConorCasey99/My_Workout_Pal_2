package ie.wit.myworkoutpal.fragments

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import ie.wit.myworkoutpal.main.MainApp
import ie.wit.myworkoutpal.helpers.getAllRoutines
import ie.wit.myworkoutpal.helpers.setMapMarker
import ie.wit.myworkoutpal.helpers.trackLocation


class MapsFragment : SupportMapFragment(), OnMapReadyCallback {

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        app.mMap = googleMap
        app.mMap.isMyLocationEnabled = true
        app.mMap.uiSettings.isZoomControlsEnabled = true
        app.mMap.uiSettings.setAllGesturesEnabled(true)
        app.mMap.clear()
        trackLocation(app)
        setMapMarker(app)
        getAllRoutines(app)
    }
}