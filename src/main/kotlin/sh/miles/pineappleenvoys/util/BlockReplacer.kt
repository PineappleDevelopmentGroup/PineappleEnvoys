package sh.miles.pineappleenvoys.util

import org.bukkit.Location
import org.bukkit.block.data.BlockData
import sh.miles.pineapple.task.work.ServerThreadWorker

class BlockReplacer(private val location: Location, private val placeAt: BlockData) : ServerThreadWorker {
    override fun compute() {
        location.world!!.setBlockData(location, placeAt)
    }
}
