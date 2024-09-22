package sh.miles.pineappleenvoys.ui.editor

import com.google.common.primitives.Doubles
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene
import sh.miles.pineappleenvoys.PineappleEnvoysPlugin
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.configuration.LootConfiguration
import sh.miles.pineappleenvoys.loot.LootEntry
import sh.miles.pineappleenvoys.ui.generic.AnvilInput
import sh.miles.pineappleenvoys.ui.generic.chat.ChatInput
import sh.miles.pineappleenvoys.ui.generic.chat.ChatInputManager

class LootEntryEditor(
    player: Player,
    private val parent: PlayerGui<*>?,
    private val configuration: LootConfiguration,
    private val entry: LootEntry<*>
) : PlayerGui<MenuScene>(
    { MenuType.GENERIC_9x3.create(player, PineappleChat.parse("<gold><bold>Loot Entry Editor")) }, player
) {
    private val LOOT_SLOT = 15
    private val ICON_SLOT = 13
    private val WEIGHT_SLOT = 11
    private var expectedClosure = false


    override fun decorate() {
        expectedClosure = false
        for (slot in (0 until size())) {
            slot(slot) { inventory ->
                GuiSlotBuilder().inventory(inventory).index(slot).drag { it.isCancelled = true }
                    .click { it.isCancelled = true }.build()
            }
        }
        slot(LOOT_SLOT) { inventory -> buildLootSlot(LOOT_SLOT, inventory) }
        slot(ICON_SLOT) { inventory -> buildIconSlot(ICON_SLOT, inventory) }
        slot(WEIGHT_SLOT) { inventory -> buildWeightSlot(WEIGHT_SLOT, inventory) }
    }

    private fun buildLootSlot(slot: Int, inventory: Inventory): GuiSlot {
        val icon: ItemSpec

        val register = Registries.LOOT_PROVIDER
        val provider = entry.provider
        val click: (InventoryClickEvent) -> Unit

        if (provider == register.COMMAND_PROVIDER) {
            icon = ItemSpec(Material.WRITABLE_BOOK)
            icon.addLoreLine("<gray><italic:!>Left click to change command")
            icon.addLoreLine(" ")
            icon.addLoreLine("<gray><italic:!>Current command: <gold>${entry.lootAsString()}")
            click = {
                expectedClosure = true
                close()
                val player = viewer()
                ChatInputManager.start(ChatInput(player, PineappleChat.parse(
                    """
                        <gold>Enter the command to be set as the reward <dark_gray>(<gray>type <red>/exit <gray> to exit<dark_gray>)
                        <gray><italic>You can start with a "/" character
                        <gray><italic>%player_name% is the player name placeholder
                    """.trimIndent()
                ), 600L, "/exit", { input ->
                    (entry as LootEntry<String>).loot = input
                    configuration.dirty = true
                    open()
                }, { open() })
                )
            }
        } else if (provider == register.ITEM_PROVIDER) {
            icon = if (entry.loot != null) ItemSpec(entry.loot as ItemSpec) else ItemSpec(Material.BARRIER)
            if (icon.lore.size > 1) {
                icon.addLoreLine(" ")
            }
            icon.addLoreLine("<gray><italic:!>Left click with new item to set loot")
            icon.addLoreLine("<gray><italic:!>Right click to get current item")
            click = {
                val cursor = it.cursor
                if (cursor != null && !cursor.type.isAir) {
                    (entry as LootEntry<ItemSpec>).loot = ItemSpec.fromStack(cursor)
                    configuration.dirty = true
                    it.whoClicked.inventory.addItem(cursor)
                    it.whoClicked.setItemOnCursor(null)
                    decorate()
                }
            }
        } else if (provider == register.ITEM_BASE64_PROVIDER) {
            icon = if (entry.loot != null) ItemSpec.fromStack(entry.loot as ItemStack) else ItemSpec(Material.BARRIER)
            icon.addLoreLine("<gray><italic:!>Left click with new item to change item")
            icon.addLoreLine("<gray><italic:!>Right click to get current item")
            click = {
                val cursor = it.cursor
                if (cursor != null && !cursor.type.isAir) {
                    (entry as LootEntry<ItemStack>).loot = cursor
                    configuration.dirty = true
                    it.whoClicked.inventory.addItem(cursor)
                    it.whoClicked.setItemOnCursor(null)
                    decorate()
                }
            }
        } else {
            throw IllegalStateException("No expected loot provider found! This is a bug!")
        }

        icon.setDefaultTextMutator { PineappleChat.parse(it) }

        return GuiSlotBuilder().inventory(inventory).index(slot).drag { it.isCancelled = true }.click {
            it.isCancelled = true
            click.invoke(it)
        }.item(icon.buildSpec()).build()
    }

    private fun buildIconSlot(slot: Int, inventory: Inventory): GuiSlot {
        val icon = ItemSpec(entry.icon)
        icon.setDefaultTextMutator { PineappleChat.parse(it) }
        icon.addLoreLine("<gray><italic:!>Click with item on cursor to replace this icon")
        return GuiSlotBuilder().inventory(inventory).index(slot).drag { it.isCancelled = true }.click {
            it.isCancelled = true
            val cursor = it.cursor
            if (cursor != null && !cursor.type.isAir) {
                entry.icon = ItemSpec.fromStack(cursor)
                configuration.dirty = true
                it.whoClicked.inventory.addItem(cursor)
                it.whoClicked.setItemOnCursor(null)
                slot(slot) { inv -> buildIconSlot(slot, inv) }
            }
        }.item(icon.buildSpec()).build()
    }

    private fun buildWeightSlot(slot: Int, inventory: Inventory): GuiSlot {
        return GuiSlotBuilder().inventory(inventory).index(slot).drag { it.isCancelled = true }.click { event ->
            event.isCancelled = true
            expectedClosure = true
            AnvilInput(viewer(),
                PineappleChat.parse("<gray>Enter a valid weight"),
                this,
                ItemBuilder.of(Material.PAPER).name(PineappleChat.parse("${entry.weight}")).build(),
                { if (it.isBlank()) null else Doubles.tryParse(it) },
                { weight, _ ->
                    entry.weight = weight
                    configuration.dirty = true
                    expectedClosure = false
                },
                { _, _ ->
                    expectedClosure = false
                    return@AnvilInput false
                }).open()
        }.item(
            ItemBuilder.of(Material.LAPIS_LAZULI).name(PineappleChat.parse("<color:#FF1440><italic:!>Weight")).lore(
                mutableListOf(
                    PineappleChat.parse("<gray><italic:!>The weight of the reward determines its likeliness to appear relative to other rewards"),
                    PineappleChat.parse("<gray><italic:!>Current Weight <gold>${entry.weight}"),
                    TextComponent(""),
                    PineappleChat.parse("<gray><italic:!>Left click to change weight")
                )
            ).build()
        ).build()
    }

    override fun handleClose(event: InventoryCloseEvent) {
        super.handleClose(event)
        if (parent != null && !expectedClosure) {
            Bukkit.getScheduler().runTask(PineappleEnvoysPlugin.plugin, Runnable {
                parent.open()
            })
        }
    }

}
