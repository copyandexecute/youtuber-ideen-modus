package de.hglabor.youtuberideen.game.mechanic

import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType

// ja das ist scheiße könnte man geiler machen aber der spielmodus wird eh direkt verworfen
object LootTables {
    //hmm lecker
    val cakeIngridients = listOf(
        ItemStack(Material.MILK_BUCKET),
        ItemStack(Material.MILK_BUCKET),
        ItemStack(Material.MILK_BUCKET),
        ItemStack(Material.SUGAR),
        ItemStack(Material.EGG),
        ItemStack(Material.SUGAR),
        ItemStack(Material.WHEAT),
        ItemStack(Material.WHEAT),
        ItemStack(Material.WHEAT),
    )

    val normal = setOf(
        LootItem(ItemStack(Material.CAKE), probability = 1),
        LootItem(ItemStack(Material.DIAMOND_HELMET), probability = 15),
        LootItem(ItemStack(Material.DIAMOND_CHESTPLATE), probability = 15),
        LootItem(ItemStack(Material.DIAMOND_LEGGINGS), probability = 15),
        LootItem(ItemStack(Material.DIAMOND_BOOTS), probability = 15),
        LootItem(ItemStack(Material.WATER_BUCKET), probability = 15),
        LootItem(ItemStack(Material.LAVA_BUCKET), probability = 15),
        LootItem(ItemStack(Material.MILK_BUCKET), probability = 45),
        LootItem(ItemStack(Material.NETHERITE_HELMET), probability = 5),
        LootItem(ItemStack(Material.NETHERITE_CHESTPLATE), probability = 5),
        LootItem(ItemStack(Material.NETHERITE_LEGGINGS), probability = 5),
        LootItem(ItemStack(Material.NETHERITE_BOOTS), probability = 5),
        LootItem(ItemStack(Material.NETHERITE_SWORD), probability = 5),
        LootItem(ItemStack(Material.DIAMOND_SWORD), probability = 10),
        LootItem(ItemStack(Material.IRON_PICKAXE), probability = 20),
        LootItem(ItemStack(Material.CROSSBOW), probability = 10),
        LootItem(ItemStack(Material.EGG), maxAmount = 15, probability = 30),
        LootItem(ItemStack(Material.SUGAR), maxAmount = 4, probability = 30),
        LootItem(ItemStack(Material.HAY_BLOCK), maxAmount = 1, probability = 20),
        LootItem(ItemStack(Material.SHIELD), probability = 25),
        LootItem(ItemStack(Material.OAK_PLANKS), maxAmount = 64, probability = 50),
        LootItem(ItemStack(Material.COBBLESTONE), maxAmount = 64, probability = 50),
        LootItem(ItemStack(Material.STONE), maxAmount = 64, probability = 50),
        LootItem(ItemStack(Material.LILY_PAD), maxAmount = 64, probability = 35),
        LootItem(ItemStack(Material.PUMPKIN_PIE), maxAmount = 16, probability = 50),
        LootItem(ItemStack(Material.COOKED_BEEF), maxAmount = 16, probability = 50),
        LootItem(ItemStack(Material.APPLE), maxAmount = 5, probability = 30),
        LootItem(ItemStack(Material.GOLDEN_APPLE), maxAmount = 1, probability = 30),
        LootItem(ItemStack(Material.SCAFFOLDING), maxAmount = 16, probability = 45),
        LootItem(ItemStack(Material.TNT), maxAmount = 16, probability = 20),
        LootItem(ItemStack(Material.ARROW), maxAmount = 16, probability = 25),
        LootItem(ItemStack(Material.COBWEB), maxAmount = 3, probability = 13),
        LootItem(ItemStack(Material.IRON_INGOT), maxAmount = 7, probability = 20),
        LootItem(ItemStack(Material.DIAMOND), maxAmount = 7, probability = 10),
        LootItem(ItemStack(Material.ENDER_PEARL), maxAmount = 2, probability = 15),
        LootItem(ItemStack(Material.COBWEB), maxAmount = 3, probability = 13),
        LootItem(ItemStack(Material.FLINT_AND_STEEL), probability = 10),
        LootItem(itemStack(Material.POTION) {
            meta<PotionMeta> {
                this.basePotionData = PotionData(PotionType.FIRE_RESISTANCE)
                this.color = Color.ORANGE
            }
        }, probability = 15),
        LootItem(itemStack(Material.SPLASH_POTION) {
            meta<PotionMeta> {
                this.basePotionData = PotionData(PotionType.INSTANT_HEAL)
                this.color = Color.RED
            }
        }, maxAmount = 5, probability = 15),
        LootItem(itemStack(Material.POTION) {
            meta<PotionMeta> {
                this.basePotionData = PotionData(PotionType.REGEN)
                this.color = Color.PURPLE
            }
        }, maxAmount = 5, probability = 10),
    )

    data class LootItem(val itemStack: ItemStack, val minAmount: Int = 1, val maxAmount: Int = 1, val probability: Int)
}