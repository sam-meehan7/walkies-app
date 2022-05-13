package sam.app.walkies.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import sam.app.walkies.R
import sam.app.walkies.adapters.walkiesLocationAdapter
import sam.app.walkies.adapters.walkiesLocationListener
import sam.app.walkies.databinding.ActivityWalkiesLocationListBinding
import sam.app.walkies.main.MainApp
import sam.app.walkies.models.WalkiesLocationModel

class WalkiesLocationsActivity : AppCompatActivity(), walkiesLocationListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityWalkiesLocationListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapsIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkiesLocationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        registerMapCallback()
        firebaseAuth = FirebaseAuth.getInstance()


        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = walkiesLocationAdapter(app.walkiesLocations.findAll(),this)
        loadWalkiesLocations()
        registerRefreshCallback()
    }

    private fun loadWalkiesLocations() {
        showWalkiesLocations(app.walkiesLocations.findAll())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, WalkiesActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
            R.id.item_map -> {
                val launcherIntent = Intent(this, WalkiesMapsActivity::class.java)
                mapsIntentLauncher.launch(launcherIntent)
            }
            R.id.miLogout -> {
                firebaseAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onwalkiesLocationClick(walkiesLocation: WalkiesLocationModel) {
        val launcherIntent = Intent(this, WalkiesActivity::class.java)
        launcherIntent.putExtra("walkiesLocation_edit", walkiesLocation)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadWalkiesLocations() }
    }

    private fun registerMapCallback() {
        mapsIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { }
    }

    private fun showWalkiesLocations (walkiesLocations: List<WalkiesLocationModel>) {
        binding.recyclerView.adapter = walkiesLocationAdapter(walkiesLocations, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

}