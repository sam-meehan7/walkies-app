package sam.app.walkies.models

interface WalkiesLocationStore {
    fun findAll(): List<WalkiesLocationModel>
    fun create(walkiesLocation: WalkiesLocationModel)
    fun update(walkiesLocation: WalkiesLocationModel)
    fun delete(walkiesLocation: WalkiesLocationModel)
}