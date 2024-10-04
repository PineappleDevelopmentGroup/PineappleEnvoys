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
        val temporaryHold = mutableListOf<UnrealizedLocation>()
        val snapshot = snapshot
        val spawnBlockSet = envoy.spawnBlockSet
        for (x in (0 until 16)) {
            for (y in (envoy.region.minY.toInt() until envoy.region.maxY.toInt())) {
                for (z in 0 until 16) {
                    val acX = snapshot.x.shr(4) + x
                    val acZ = snapshot.z.shr(4) + z

                    if (!envoy.region.contains(acX.toDouble(), y.toDouble(), acZ.toDouble())) {
                        continue
                    }

                    if (!spawnBlockSet.contains(snapshot.getBlockType(x, y, z))) {
                        continue
                    }

                    val yPlusOne = y + 1
                    if (yPlusOne > MAX_Y) {
                        continue
                    }

                    if (!snapshot.getBlockType(x, yPlusOne, z).isAir) {
                        continue
                    }

                    temporaryHold.add(UnrealizedLocation(acX.toDouble(), y.toDouble(), acZ.toDouble()))
                }
            }
        }

        val random = ThreadLocalRandom.current()
        while (location.size <= envoy.envoyAmount) {
            this.location.add(temporaryHold.removeAt(random.nextInt(0, temporaryHold.size)))
        }
    }

    fun collect(): List<UnrealizedLocation> {
        return ArrayList(this.location)
    }


}
