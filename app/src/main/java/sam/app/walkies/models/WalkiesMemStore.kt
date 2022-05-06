package sam.app.walkies.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class walkiesLocationMemStore : WalkiesLocationStore {

    val walkiesLocations = ArrayList<WalkiesLocationModel>()

    override fun findAll(): List<WalkiesLocationModel> {
        return walkiesLocations
    }

    override fun create(walkiesLocation: WalkiesLocationModel) {
        walkiesLocation.id = getId()
        walkiesLocations.add(walkiesLocation)
        logAll()
    }

    override fun update(walkiesLocation: WalkiesLocationModel) {
        var foundWalkiesLocation: WalkiesLocationModel? = walkiesLocations.find { p -> p.id == walkiesLocation.id }
        if (foundWalkiesLocation != null) {
            foundWalkiesLocation.title = walkiesLocation.title
            foundWalkiesLocation.description = walkiesLocation.description
            foundWalkiesLocation.image = walkiesLocation.image
            foundWalkiesLocation.lat = walkiesLocation.lat
            foundWalkiesLocation.lng = walkiesLocation.lng
            foundWalkiesLocation.zoom = walkiesLocation.zoom
            logAll()
        }
    }

    override fun delete(walkiesLocation: WalkiesLocationModel) {
        walkiesLocations.remove(walkiesLocation)
    }

    private fun logAll() {
        walkiesLocations.forEach { i("$it") }
    }
}