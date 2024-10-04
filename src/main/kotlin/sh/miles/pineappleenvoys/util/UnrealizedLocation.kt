package sh.miles.pineappleenvoys.util

import org.bukkit.Location
import org.bukkit.World

data class UnrealizedLocation(val x: Double, val y: Double, val z: Double) {

    fun realize(world: World): Location {
        return Location(world, x, y, z)
    }
}
