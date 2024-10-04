package sh.miles.pineappleenvoys.util

import org.bukkit.ChunkSnapshot
import org.bukkit.World
import sh.miles.pineapple.task.work.ServerThreadSupplier
import java.util.concurrent.atomic.AtomicInteger

class ChunkSnapshotRetriever(
    private val world: World,
    private val chunkX: Int,
    private val chunkZ: Int,
    private val expected: Int
) :
    ServerThreadSupplier<ChunkSnapshot?> {

    private var result: ChunkSnapshot? = null

    override fun compute() {
        result = world.getChunkAt(chunkX, chunkZ).chunkSnapshot
    }

    override fun getResult(): ChunkSnapshot? {
        return this.result
    }
}
