package sh.miles.pineappleenvoys.configuration

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.BoundingBox
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.collection.WeightedRandom
import sh.miles.pineapple.collection.set.PolicySet
import sh.miles.pineappleenvoys.GlobalConfig
import sh.miles.pineappleenvoys.PineappleEnvoysPlugin
import sh.miles.pineappleenvoys.envoy.EnvoyDrop
import sh.miles.pineappleenvoys.util.ChunkScanner
import sh.miles.pineappleenvoys.util.ChunkSnapshotRetriever
import sh.miles.pineappleenvoys.util.MarkedKey
import sh.miles.pineappleenvoys.util.UnrealizedLocation
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

data class EnvoyConfiguration(
    val id: String,
    val drops: WeightedRandom<EnvoyDrop>,
    val world: World,
    val region: BoundingBox,
    val envoyAmount: Int,
    val spawnBlockSet: PolicySet<Material>
) : MarkedKey<String> {

    override var dirty: Boolean
        get() = false
        set(value) {}

    override fun getKey(): String {
        return id
    }

    fun generateValidSpawnLocations(): CompletableFuture<List<UnrealizedLocation>> {
        return CompletableFuture.supplyAsync {
            val chunks = mutableListOf<Pair<Int, Int>>()

            for (x in (region.minX.toInt() until region.maxX.toInt() step 16)) {
                for (z in (region.minZ.toInt() until region.maxZ.toInt() step 16)) {
                    chunks.add(Pair(x.shr(4), z.shr(4)))
                }
            }

            // we want to ensure randomness rather than uniformity between spawns.
            // By dispatching the snapshot retrievers and
            chunks.shuffle()

            if (chunks.isEmpty()) {
                PineappleLib.getLogger()
                    .warning("During the generation of envoy $id the region is smaller than 1 chunk. This is not recommended and could lead to further issues. Please consider expanding the area you allot to envoys")
                chunks.add(Pair(region.minX.toInt().shr(4), region.maxX.toInt().shr(4)))
            }

            val lock = ReentrantLock()
            val expectedSize = chunks.size
            val sizeCounter = AtomicInteger(0)
            val locationCollector = ConcurrentLinkedQueue<UnrealizedLocation>()

            for (chunk in chunks) {
                PineappleEnvoysPlugin.ticker.queueSupplier(
                    ChunkSnapshotRetriever(
                        this.world,
                        chunk.first,
                        chunk.second,
                        envoyAmount
                    )
                ) { snapshot ->
                    if (snapshot == null) return@queueSupplier
                    CompletableFuture.runAsync {
                        val scanner = ChunkScanner(snapshot, this)
                        scanner.scan()
                        locationCollector.addAll(scanner.collect())
                        sizeCounter.incrementAndGet()
                    }.exceptionally {
                        throw it
                    }
                }
            }

            var lastCheck = System.currentTimeMillis()
            var expireCounter = 0
            while (expectedSize != sizeCounter.get()) {
                if (expireCounter > GlobalConfig.EXPIRE_SEARCH) {
                    PineappleLib.getLogger()
                        .severe("Envoys has suspended search after inability to find suitable spawns after 2 minutes")
                    return@supplyAsync locationCollector.stream().toList()
                }

                val now = System.currentTimeMillis()
                if (now - lastCheck >= 1000) {
                    ++expireCounter
                    lastCheck = now
                }

                lock.lock()
            }

            if (locationCollector.size < envoyAmount) {
                PineappleLib.getLogger()
                    .warning("Envoys finished its search successfully, but was only able to acquire ${locationCollector.size} spawn locations when the specified amount is $envoyAmount")
            }

            val randomizedResult = locationCollector.stream().toList().shuffled()
            val drop = if (randomizedResult.size > envoyAmount) randomizedResult.size - envoyAmount else 0
            return@supplyAsync randomizedResult.dropLast(drop)
        }
    }

}
