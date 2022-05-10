package de.hglabor.youtuberideen.seltix

import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.GoalSelector
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ItemCooldowns
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentHelper
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld

class PvPBot(world: World, name: String) : Zombie(EntityType.ZOMBIE, (world as CraftWorld).handle) {
    val cooldowns: ItemCooldowns = ItemCooldowns()
    var isSprintJumping = false

    init {
        goalSelector = GoalSelector(level.profilerSupplier)
        targetSelector = GoalSelector(level.profilerSupplier)
        registerGoals()

        if (true) {
            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack(Items.SHIELD))
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(Items.STONE_SWORD))
        }

        DisguiseAPI.disguiseToAll(bukkitLivingEntity, PlayerDisguise(name, name))
    }

    override fun registerGoals() {
        goalSelector.addGoal(2, StrafeAttackGoal(this, 1.0, 20, 3.0f))
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
    }

    override fun tick() {
        super.tick()
        cooldowns.tick()
    }

    override fun jumpFromGround() {
        super.jumpFromGround()
    }

    override fun setSprinting(sprinting: Boolean) {
        super.setSprinting(sprinting)
        getAttribute(Attributes.MOVEMENT_SPEED)?.baseValue = if (sprinting) 0.4 else 0.2
    }

    public override fun blockUsingShield(attacker: LivingEntity) {
        if (attacker.mainHandItem.item is AxeItem) {
            this.disableShield()
        }
    }

    private fun disableShield(sprinting: Boolean = true) {
        var f = 0.25f + EnchantmentHelper.getBlockEfficiency(this).toFloat() * 0.05f
        if (sprinting) {
            f += 0.75f
        }
        if (random.nextFloat() < f) {
            this.cooldowns.addCooldown(Items.SHIELD, 100)
            stopUsingItem()
            level.broadcastEntityEvent(this, 30.toByte())
        }
    }

    override fun dropExperience() {}
    override fun shouldDropLoot(): Boolean = false
    override fun isSunSensitive(): Boolean = false
    override fun getSwimSound(): SoundEvent = SoundEvents.PLAYER_SWIM
    override fun getSwimSplashSound(): SoundEvent = SoundEvents.PLAYER_SPLASH
    override fun getSwimHighSpeedSplashSound(): SoundEvent = SoundEvents.PLAYER_SPLASH_HIGH_SPEED
    override fun getSoundSource(): SoundSource = SoundSource.PLAYERS
    override fun getDeathSound(): SoundEvent = SoundEvents.PLAYER_DEATH
    override fun getFallSounds(): Fallsounds = Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL)
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return if (source === DamageSource.ON_FIRE) SoundEvents.PLAYER_HURT_ON_FIRE else if (source === DamageSource.DROWN) SoundEvents.PLAYER_HURT_DROWN else if (source === DamageSource.SWEET_BERRY_BUSH) SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH else if (source === DamageSource.FREEZE) SoundEvents.PLAYER_HURT_FREEZE else SoundEvents.PLAYER_HURT
    }

    fun isReadyToStrafe(): Boolean {
        return true
    }

    fun spawnAt(location: Location): PvPBot {
        level.addFreshEntity(this)
        bukkitEntity.teleport(location)
        return this
    }
}
