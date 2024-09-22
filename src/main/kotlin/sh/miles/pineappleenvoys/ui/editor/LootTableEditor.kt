package sh.miles.pineappleenvoys.ui.editor

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.gui.PagedPlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.configuration.LootConfiguration
import sh.miles.pineappleenvoys.loot.CommandLootProvider
import sh.miles.pineappleenvoys.loot.LootEntry
import sh.miles.pineappleenvoys.ui.generic.ItemSelector
import sh.miles.pineappleenvoys.ui.generic.Selection

class LootTableEditor(player: Player, private val configuration: LootConfiguration) : PagedPlayerGui<MenuScene>({
    MenuType.GENERIC_9x2.create(
        player, PineappleChat.parse("<gold><bold>Loot Table Editor")
    )
}, player) {

    override fun decorate() {
        val entries: MutableList<List<LootEntry<*>>> = mutableListOf()
        var currentList: MutableList<LootEntry<*>> = mutableListOf()
        for (entry in configuration.loot.entries) {
            currentList.add(entry.value)

            if (currentList.size == 9) {
                entries.add(currentList)
                currentList = mutableListOf()
            }
        }

        if (currentList.isNotEmpty()) {
            entries.add(currentList)
        }

        var maxPage = findMaxPage()
        if (maxPage > entries.size) {
            maxPage -= 1
        }
        for (index in (0 until maxPage)) {
            decoratePage(index, maxPage, entries[index])
        }

        if (this.currentPage() >= maxPage) {
            previousPage()
        } else {
            deployPage(this.currentPage())
        }
    }

    private fun decoratePage(page: Int, maxPage: Int, entries: List<LootEntry<*>>) {
        for (slot in (0 until 9)) {
            if (slot < entries.size) {
                slot(page, slot) { inventory -> buildLootEntry(entries[slot], inventory, slot) }
            } else {
                slot(page, slot) { inventory ->
                    GuiSlotBuilder().inventory(inventory).index(slot).drag { it.isCancelled = true }
                        .click { it.isCancelled = true }.deployable().build()
                }
            }
        }

        for (slot in (9 until 18)) {
            slot(page, slot) { inventory ->
                GuiSlotBuilder().inventory(inventory).index(slot).drag { it.isCancelled = true }
                    .click { it.isCancelled = true }
                    .item(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).nameLegacy(" ").build()).deployable()
                    .build()
            }
        }

        slot(page, 13) { inventory -> createLootEntryButton(inventory, 13) }

        if (page != 0) {
            slot(page, 9) { inventory ->
                GuiSlotBuilder().inventory(inventory).index(9).drag { it.isCancelled = true }.click {
                    it.isCancelled = true
                    previousPage()
                }.item(
                    ItemBuilder.of(Material.PLAYER_HEAD)
                        .skullTexture("1ebc9b98471b11aa7df49aeb2b17156bef278d9f3f47b1858fd052ebacc53")
                        .name(PineappleChat.parse("<gray><italic:!>Previous Page")).build()
                ).deployable().build()
            }
        }

        if (page < maxPage - 1) {
            slot(page, 17) { inventory ->
                GuiSlotBuilder().inventory(inventory).index(17).drag { it.isCancelled = true }.click {
                    it.isCancelled = true
                    nextPage()
                }.item(
                    ItemBuilder.of(Material.PLAYER_HEAD)
                        .skullTexture("472e48c6c2a8efdaf38dc074902e524f36ee6beb03228444fd6c28e8faff385")
                        .name(PineappleChat.parse("<gray><italic:!>Next Page")).build()
                ).deployable().build()
            }
        }
    }

    override fun findMaxPage(): Int {
        val size = configuration.loot.size()
        val pageAmount = size / 9 + 1
        return if ((size) % 9 == 0) pageAmount - 1 else pageAmount
    }

    private fun buildLootEntry(lootEntry: LootEntry<*>, inventory: Inventory, slot: Int): GuiSlot {
        val icon = ItemSpec(lootEntry.icon)

        if (lootEntry.provider.rewardClass.isAssignableFrom(String::class.java)) {
            icon.addLoreLine("<gray><italic:!>Command: <gold>${lootEntry.lootAsString()}")
        } else {
            icon.addLoreLine("<gray><italic:!>Gives Item: <gold>${lootEntry.lootAsString()}")
        }


        icon.setDefaultTextMutator { PineappleChat.parse(it) }
        icon.addLoreLine("<gray><italic:!>Weight: <gold>${lootEntry.weight}")
        icon.addLoreLine(" ")
        icon.addLoreLine("<gray><italic:!>Left Click To <green><bold>Edit")
        icon.addLoreLine("<gray><italic:!>Shift Right Click To <red><bold>Delete Permanently")
        return GuiSlotBuilder().inventory(inventory).index(slot).item(icon.buildSpec()).click {
            it.isCancelled = true
            if (it.click == ClickType.LEFT) {
                LootEntryEditor(viewer(), this, configuration, lootEntry).open()
            } else if (it.click == ClickType.SHIFT_RIGHT) {
                configuration.removeLootEntry(lootEntry)
                decorate()
            }
        }.drag {
            it.isCancelled = true
        }.deployable().build()
    }

    private fun createLootEntryButton(inventory: Inventory, slot: Int): GuiSlot {
        return GuiSlotBuilder()
            .inventory(inventory)
            .index(slot)
            .item(
                ItemBuilder.of(Material.PLAYER_HEAD)
                    .name(PineappleChat.parse("<gold><italic:!>Add Loot Entry"))
                    .skullTexture("dd1500e5b04c8053d40c7968330887d24b073daf1e273faf4db8b62ebd99da83")
                    .build()
            )
            .drag { it.isCancelled = true }
            .click {
                it.isCancelled = true
                ItemSelector(
                    viewer(), PineappleChat.parse("<gold><italic:!>Loot Type Selector"), this, mapOf(
                        11 to Selection(
                            ItemBuilder.of(Material.PAPER)
                                .name(PineappleChat.parse("<gold><italic:!>Command"))
                                .lore(PineappleChat.parse("<dark_gray><italic:!>Click to create Command loot type"))
                                .build(),
                        ) {
                            val icon = ItemSpec(Material.PAPER)
                            icon.setDefaultTextMutator { PineappleChat.parse(it) }
                            this.configuration.addLootEntry(
                                LootEntry(
                                    Registries.LOOT_PROVIDER.COMMAND_PROVIDER,
                                    null,
                                    0.25,
                                    icon
                                )
                            )
                            this.decorate()
                        },
                        13 to Selection(
                            ItemBuilder.of(Material.DIAMOND_PICKAXE)
                                .name(PineappleChat.parse("<gold><italic:!>Simple Item"))
                                .lore(
                                    PineappleChat.parse("<dark_gray><italic:!>Click to create a Simple Item loot type"),
                                    PineappleChat.parse(""),
                                    PineappleChat.parse("<gray><italic:!>Should be used when you don't need all internal item data to be preserved"),
                                    PineappleChat.parse("<gray><italic:!>e.g. A simple leather enchanted chestplate with some color")
                                )
                                .build()
                        ) {
                            val icon = ItemSpec(Material.DIAMOND_PICKAXE)
                            icon.setDefaultTextMutator { PineappleChat.parse(it) }
                            this.configuration.addLootEntry(
                                LootEntry(
                                    Registries.LOOT_PROVIDER.ITEM_PROVIDER,
                                    null,
                                    0.25,
                                    icon
                                )
                            )
                            this.decorate()
                        },
                        15 to Selection(
                            ItemBuilder.of(Material.WRITTEN_BOOK)
                                .name(PineappleChat.parse("<gold><italic:!>Base64 Item"))
                                .lore(
                                    PineappleChat.parse("<dark_gray><italic:!>Click to create a Base64 Item loot type"),
                                    PineappleChat.parse(""),
                                    PineappleChat.parse("<gray><italic:!>Should be used when you need all item data to preserved"),
                                    PineappleChat.parse("<gray><italic:!>e.g. A item from a plugin like ExecutableItems or Oraxen")
                                ).build()
                        ) {
                            val icon = ItemSpec(Material.WRITTEN_BOOK)
                            icon.setDefaultTextMutator { PineappleChat.parse(it) }
                            this.configuration.addLootEntry(
                                LootEntry(
                                    Registries.LOOT_PROVIDER.ITEM_BASE64_PROVIDER,
                                    null,
                                    0.25,
                                    icon
                                )
                            )
                            this.decorate()
                        }
                    )
                ).open()
            }
            .deployable()
            .build()
    }
}
