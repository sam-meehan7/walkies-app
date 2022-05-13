package sam.app.walkies.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import sam.app.walkies.main.MainApp
import sam.app.walkies.models.Location
import sam.app.walkies.models.WalkiesLocationModel
import sam.app.walkies.showImagePicker
import sam.app.walkies.R
import sam.app.walkies.databinding.ActivityWalkiesLocationBinding
import timber.log.Timber.i

class WalkiesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalkiesLocationBinding
    var walkiesLocation = WalkiesLocationModel()
    lateinit var app: MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>


    var location = Location(52.245696, -7.139102, 15f)
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityWalkiesLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        setupNumberPickerForStringValues()

        app = application as MainApp

        i("Walkies Activity started...")

        if (intent.hasExtra("walkiesLocation_edit")) {
            edit = true
            walkiesLocation = intent.extras?.getParcelable("walkiesLocation_edit")!!
            binding.walkiesLocationTitle.setText(walkiesLocation.title)
            binding.description.setText(walkiesLocation.description)
            binding.difficultyPicker.value = walkiesLocation.difficulty.toInt()
            Picasso.get()
                .load(walkiesLocation.image)
                .into(binding.walkiesLocationImage)
            if (walkiesLocation.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_walkiesLocation_image)
            }
        }

        binding.btnAdd.setOnClickListener() {
            walkiesLocation.title = binding.walkiesLocationTitle.text.toString()
            walkiesLocation.description =  binding.description.text.toString()
            walkiesLocation.difficulty = binding.difficultyPicker.value.toString()
            if (walkiesLocation.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_walkiesLocation_title, Snackbar.LENGTH_LONG)
                        .show()
            }

            if (walkiesLocation.lat.equals(0.0) || walkiesLocation.lng.equals(0.0)) {
                Snackbar.make(it,R.string.location_error_message, Snackbar.LENGTH_LONG)
                    .show()
            }

            else {
                if (edit) {
                    app.walkiesLocations.update(walkiesLocation.copy())
                    setResult(RESULT_OK)
                    finish()
                } else {
                    app.walkiesLocations.create(walkiesLocation.copy())
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        binding.walkiesLocationLocation.setOnClickListener {
            var location = Location(52.31732006318825, -6.562802158646359, 12f)
            if (walkiesLocation.zoom != 0f) {
                location.lat =  walkiesLocation.lat
                location.lng = walkiesLocation.lng
                location.zoom = walkiesLocation.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        registerImagePickerCallback()
        registerMapCallback()
    }

    private fun setupNumberPickerForStringValues() {
        val difficultyPicker = binding.difficultyPicker
        val values = arrayOf("Easy", "Medium", "Hard")
        difficultyPicker.minValue = 0
        difficultyPicker.maxValue = values.size - 1
        difficultyPicker.displayedValues = values
        difficultyPicker.wrapSelectorWheel = true
        difficultyPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val text = "Changed from " + values[oldVal] + " to " + values[newVal]
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupNumberPicker() {
        val numberPicker = binding.difficultyPicker
        numberPicker.minValue = 0
        numberPicker.maxValue = 10
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val text = "Changed from $oldVal to $newVal"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_walkies, menu)
        if (edit && menu != null) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.item_delete -> {
                app.walkiesLocations.delete(walkiesLocation)
                finish()
            }

            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            walkiesLocation.image = result.data!!.data!!
                            Picasso.get()
                                   .load(walkiesLocation.image)
                                   .into(binding.walkiesLocationImage)
                            binding.chooseImage.setText(R.string.change_walkiesLocation_image)
                        }
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            location = result.data!!.extras?.getParcelable("location")!!
                            i("Location == $location")
                            walkiesLocation.lat = location.lat
                            walkiesLocation.lng = location.lng
                            walkiesLocation.zoom = location.zoom
                        }
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}