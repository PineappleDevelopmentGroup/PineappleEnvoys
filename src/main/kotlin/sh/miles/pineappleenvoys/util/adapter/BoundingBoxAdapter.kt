package sh.miles.pineappleenvoys.util.adapter

import org.bukkit.util.BoundingBox
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter

object BoundingBoxAdapter : SerializedAdapter<BoundingBox> {

    private const val MIN_X = "minX"
    private const val MIN_Y = "minY"
    private const val MIN_Z = "minZ"
    private const val MAX_X = "maxX"
    private const val MAX_Y = "maxY"
    private const val MAX_Z = "maxZ"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): BoundingBox {
        val parent = element.asObject
        val minX = parent.getPrimitive(MIN_X).map { it.asDouble }.orThrow("Missing required field $MIN_X")
        val minY = parent.getPrimitive(MIN_Y).map { it.asDouble }.orThrow("Missing required field $MIN_Y")
        val minZ = parent.getPrimitive(MIN_Z).map { it.asDouble }.orThrow("Missing required field $MIN_Z")
        val maxX = parent.getPrimitive(MAX_X).map { it.asDouble }.orThrow("Missing required field $MAX_X")
        val maxY = parent.getPrimitive(MAX_Y).map { it.asDouble }.orThrow("Missing required field $MAX_Y")
        val maxZ = parent.getPrimitive(MAX_Z).map { it.asDouble }.orThrow("Missing required field $MAX_Z")

        return BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
    }

    override fun serialize(box: BoundingBox, context: SerializedSerializeContext): SerializedElement {
        val parent = SerializedElement.`object`()
        parent.add(MIN_X, box.minX)
        parent.add(MIN_Y, box.minY)
        parent.add(MIN_Z, box.minZ)
        parent.add(MAX_X, box.maxX)
        parent.add(MAX_Y, box.maxY)
        parent.add(MAX_Z, box.maxZ)
        return parent
    }

    override fun getKey(): Class<*> {
        return BoundingBox::class.java
    }
}
