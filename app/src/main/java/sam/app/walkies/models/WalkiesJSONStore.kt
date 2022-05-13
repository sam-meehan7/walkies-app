package sam.app.walkies.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import sam.app.walkies.helpers.exists
import sam.app.walkies.helpers.read
import sam.app.walkies.helpers.write
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "walkiesLocations.json"

val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()

val listType: Type = object : TypeToken<ArrayList<WalkiesLocationModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class WalkiesLocationJSONStore(private val context: Context) : WalkiesLocationStore {

    var walkiesLocations = mutableListOf<WalkiesLocationModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<WalkiesLocationModel> {
        return walkiesLocations
    }

    override fun create(walkiesLocation: WalkiesLocationModel) {
        walkiesLocation.id = generateRandomId()
        walkiesLocations.add(walkiesLocation)
        serialize()
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
        serialize()
    }

    override fun delete(walkiesLocation: WalkiesLocationModel) {
        walkiesLocations.remove(walkiesLocation)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(walkiesLocations, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        walkiesLocations = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        walkiesLocations.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}
