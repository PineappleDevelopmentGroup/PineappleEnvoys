package sh.miles.pineappleenvoys.util

import org.bukkit.Location
import org.bukkit.World

data class UnrealizedLocation(val x: Int, val y: Int, val z: Int) {

    fun realize(world: World): Location {
        return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
    }
}
