package sh.miles.pineappleenvoys.ui.generic

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene
import sh.miles.pineappleenvoys.PineappleEnvoysPlugin

class ItemSelector(
    player: Player,
    title: BaseComponent,
    private val lastOpen: PlayerGui<*>?,
    private val selections: Map<Int, Selection>
) : PlayerGui<MenuScene>({ MenuType.GENERIC_9x3.create(player, title) }, player) {

    private var clickClosed = false

    override fun decorate() {
        clickClosed = false
        for (i in (0 until 27)) {
            slot(i) {
                GuiSlotBuilder().inventory(it).index(i).drag { it.isCancelled = true }.click { it.isCancelled = true }
                    .build()
            }
        }

        for ((slot, selection) in selections.entries) {
            slot(slot) { inventory ->
                GuiSlotBuilder().inventory(inventory).index(slot).drag { it.isCancelled = true }.click {
                    it.isCancelled = true
                    selection.onSelect.invoke()
                    clickClosed = true
                    close()
                }.item(selection.icon).build()
            }
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        if (!clickClosed) {
            Bukkit.getScheduler().runTask(PineappleEnvoysPlugin.plugin, Runnable { open() })
            return
        }

        if (lastOpen != null) {
            Bukkit.getScheduler().runTask(PineappleEnvoysPlugin.plugin, Runnable { lastOpen.open() })
        }
    }

}

data class Selection(val icon: ItemStack, val onSelect: () -> Unit)
