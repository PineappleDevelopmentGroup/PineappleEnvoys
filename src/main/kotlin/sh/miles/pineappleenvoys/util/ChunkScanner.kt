package sh.miles.pineappleenvoys.util

import org.bukkit.ChunkSnapshot
import sh.miles.pineappleenvoys.configuration.EnvoyConfiguration
import java.util.concurrent.ThreadLocalRandom

class ChunkScanner(private val snapshot: ChunkSnapshot, private val envoy: EnvoyConfiguration) {

    companion object {
        private const val MIN_Y = -64
        private const val MAX_Y = 320
    }


    private val location = mutableListOf<UnrealizedLocation>()

    fun scan() {
        println("SNAPSHOT ${snapshot.x},${snapshot.z}")
        val temporaryHold = mutableListOf<UnrealizedLocation>()
        val snapshot = snapshot
        val spawnBlockSet = envoy.spawnBlockSet
        for (x in (0 until 16)) {
            val acX = snapshot.x.shr(4) + x
            for (y in (envoy.region.minY.toInt() until envoy.region.maxY.toInt())) {
                val yPlusOne = y + 1
                for (z in 0 until 16) {
                    val acZ = snapshot.z.shr(4) + z

                    if (!envoy.region.contains(acX.toDouble(), y.toDouble(), acZ.toDouble())) {
                        continue
                    }

                    if (!spawnBlockSet.contains(snapshot.getBlockType(x, y, z))) {
                        continue
                    }

                    if (yPlusOne > MAX_Y) {
                        continue
                    }

                    if (!snapshot.getBlockType(x, yPlusOne, z).isAir) {
                        continue
                    }

//                    println("===")
//                    println(snapshot.getBlockType(x, y, z))
//                    println("$x,$y,$z | $acX,$y,$acZ > ${acX.toDouble()},${y},${acZ}")
//                    println("===")

                    temporaryHold.add(UnrealizedLocation(acX, yPlusOne, acZ))
                }
            }
        }

        val random = ThreadLocalRandom.current()
        for (ignored in temporaryHold.indices) {
            this.location.add(temporaryHold.removeAt(random.nextInt(0, temporaryHold.size)))
        }
    }

    fun collect(): List<UnrealizedLocation> {
        return ArrayList(this.location)
    }


}
