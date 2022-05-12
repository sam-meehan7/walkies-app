package sam.app.walkies.main

import android.app.Application
import sam.app.walkies.models.WalkiesLocationJSONStore
import sam.app.walkies.models.WalkiesLocationStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var walkiesLocations: WalkiesLocationStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        walkiesLocations = WalkiesLocationJSONStore(applicationContext)
        i("Walkies Application has started")
    }

}