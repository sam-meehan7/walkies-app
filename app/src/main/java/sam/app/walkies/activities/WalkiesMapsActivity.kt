package sam.app.walkies.activities


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import sam.app.walkies.models.WalkiesLocationModel

import sam.app.walkies.main.MainApp
import sam.app.walkies.readImageFromPath
import sam.app.walkies.R
import sam.app.walkies.databinding.ActivityWalkiesMapsBinding
import sam.app.walkies.databinding.WalkiesLocationContentBinding
import timber.log.Timber


class WalkiesMapsActivity : AppCompatActivity() , GoogleMap.OnMarkerClickListener{

    private lateinit var binding: ActivityWalkiesMapsBinding
    private lateinit var contentBinding: WalkiesLocationContentBinding
    lateinit var map: GoogleMap
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp
        binding = ActivityWalkiesMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        contentBinding = WalkiesLocationContentBinding.bind(binding.root)
        contentBinding.mapView.onCreate(savedInstanceState)
        contentBinding.mapView.getMapAsync{
            map = it
            configureMap()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.miBack -> {
                val intent = Intent(this, WalkiesLocationsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val currentTitle: TextView = findViewById(R.id.currentTitle)
        val currentDescription: TextView = findViewById(R.id.currentDescription)
        val currentImage: ImageView = findViewById(R.id.currentImage)
        currentTitle.text = marker.title
        currentDescription.text = marker.snippet
        return false
    }

    fun showwalkiesLocationImage(walkiesLocation: WalkiesLocationModel) {
        val currentImage: ImageView = findViewById(R.id.currentImage)
        currentImage.setImageBitmap(readImageFromPath(this, walkiesLocation.image.toString()))
    }


    private fun configureMap() {
        map.uiSettings.isZoomControlsEnabled = true
        app.walkiesLocations.findAll().forEach{
            val loc = LatLng(it.lat, it.lng)
            val options = MarkerOptions().title(it.title).snippet(it.description).position(loc).icon(BitmapDescriptorFactory.fromResource(R.drawable.dog_icon))
            map.addMarker(options).tag = it.id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))
        }
        map.setOnMarkerClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener() { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_CYAN)))
            poiMarker.showInfoWindow()
        }
    }

}


