package sh.miles.pineappleenvoys.ui.generic

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.AnvilScene
import sh.miles.pineappleenvoys.PineappleEnvoysPlugin

class AnvilInput<O>(
    player: Player,
    title: BaseComponent,
    private val lastOpened: PlayerGui<*>?,
    private val input: ItemStack,
    private val map: (String) -> O?,
    private val onComplete: (O, Player) -> Unit,
    private val onFail: (String, Player) -> Boolean
) : PlayerGui<AnvilScene>({ MenuType.ANVIL.create(player, title) }, player) {

    private var closedByClick = false
    private var levels = 0

    override fun decorate() {
        levels = viewer().level
        slot(0) { inventory ->
            GuiSlotBuilder().inventory(inventory).index(0).item(input).click { it.isCancelled = true }
                .drag { it.isCancelled = true }.build()
        }

        slot(1) { inventory ->
            GuiSlotBuilder().inventory(inventory).index(1).click { it.isCancelled = true }
                .drag { it.isCancelled = true }.build()
        }

        slot(2) { inventory ->
            GuiSlotBuilder().inventory(inventory).index(2)
                .click {
                    it.isCancelled = true
                    closedByClick = true
                    handleCloseClean(viewer(), it.inventory as AnvilInventory)
                }
                .drag { it.isCancelled = true }.build()
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        super.handleClose(event)
        viewer().level = levels
        if (!closedByClick) {
            handleCloseClean(viewer(), event.inventory as AnvilInventory)
        }
    }

    private fun handleCloseClean(viewer: Player, anvil: AnvilInventory) {
        if (lastOpened != null) {
            Bukkit.getScheduler().runTask(PineappleEnvoysPlugin.plugin, Runnable { lastOpened.open() })
        }

        val mapped = this.map.invoke(anvil.renameText ?: "")
        if (mapped == null) {

            if (!onFail.invoke(anvil.renameText ?: "", viewer)) {
                anvil.clear()
                if (closedByClick) close()
            }
            return
        }

        onComplete.invoke(mapped, viewer)
        anvil.clear()
        if (closedByClick) close()
    }
}
