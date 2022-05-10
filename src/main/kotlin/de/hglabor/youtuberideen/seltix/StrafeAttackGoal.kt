package de.hglabor.youtuberideen.seltix

import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.item.Items
import java.util.*
import kotlin.random.Random

class StrafeAttackGoal(
    private val bot: PvPBot,
    private val speedModifier: Double,
    private var attackIntervalMin: Int,
    private val range: Float,
) : Goal() {
    private var attackRadiusSqr: Float = range * range
    private var attackTime = -1
    private var seeTime = 0
    private var strafingClockwise = false
    private var strafingBackwards = false
    private var strafingTime = -1
    private var ticksUntilNextAttack = 0
    fun setMinAttackInterval(attackInterval: Int) {
        attackIntervalMin = attackInterval
    }

    init {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP))
    }

    override fun canUse(): Boolean {
        return bot.target != null && isReadyToStrafe
    }

    private val isReadyToStrafe: Boolean
        get() = bot.isReadyToStrafe()

    override fun canContinueToUse(): Boolean {
        return (canUse() || !bot.navigation.isDone) && isReadyToStrafe
    }

    override fun start() {
        super.start()
        bot.isAggressive = true
        ticksUntilNextAttack = 0
    }

    override fun stop() {
        super.stop()
        bot.isAggressive = false
        seeTime = 0
        attackTime = -1
        bot.stopUsingItem()
    }

    override fun requiresUpdateEveryTick(): Boolean {
        return true
    }

    override fun tick() {
        val livingEntity = bot.target
        if (livingEntity != null) {
            ticksUntilNextAttack = (ticksUntilNextAttack - 1).coerceAtLeast(0)

            if (bot.isSprintJumping) {
                bot.jumpControl.jump()
            }

            if (this.isTimeToAttack()) {
                this.attackRadiusSqr = range * range
            }


            val distance = bot.distanceToSqr(livingEntity.x, livingEntity.y, livingEntity.z)
            val canSeeEntity = bot.sensing.hasLineOfSight(livingEntity)
            val seeTimeBiggerZero = seeTime > 0
            if (canSeeEntity != seeTimeBiggerZero) {
                seeTime = 0
            }
            if (canSeeEntity) {
                ++seeTime
            } else {
                --seeTime
            }
            if (distance <= attackRadiusSqr.toDouble() && seeTime >= 20) {
                bot.navigation.stop()
                ++strafingTime
            } else {
                bot.navigation.moveTo(livingEntity, speedModifier)
                strafingTime = -1
            }
            if (strafingTime >= 20) {
                if (bot.random.nextFloat().toDouble() < 0.3) {
                    strafingClockwise = !strafingClockwise
                }
                if (bot.random.nextFloat().toDouble() < 0.3) {
                    strafingBackwards = !strafingBackwards
                }
                strafingTime = 0
            }
            if (strafingTime > -1) {
                if (distance > (attackRadiusSqr * 0.75f).toDouble()) {
                    strafingBackwards = false
                } else if (distance < (attackRadiusSqr * 0.25f).toDouble()) {
                    strafingBackwards = true
                }
                bot.isSprinting = !strafingBackwards
                bot.isSprintJumping = bot.isSprinting && Random.nextBoolean()
                bot.moveControl.strafe(if (strafingBackwards) -0.5f else 0.5f, if (strafingClockwise) 0.5f else -0.5f)
                bot.lookAt(livingEntity, 180f, 180f)
            } else {
                bot.lookAt(livingEntity, 180f, 180f)
            }

            if (distance <= attackRadiusSqr.toDouble() && seeTime >= 20) {
                checkAndPerformAttack(livingEntity)
            }

            if (bot.isUsingItem) {
                if (!canSeeEntity && seeTime < -60) {
                    bot.stopUsingItem()
                } else if (canSeeEntity) {
                    val i = bot.ticksUsingItem
                    if (i >= 20) {
                        bot.stopUsingItem()
                        //this.bot.performRangedAttack(livingEntity, BowItem.getPowerForTime(i));
                        attackTime = attackIntervalMin
                    }
                }
            } else if (--attackTime <= 0 && seeTime >= -60) {
                if (!bot.cooldowns.isOnCooldown(Items.SHIELD)) {
                    bot.startUsingItem(InteractionHand.OFF_HAND)
                }
                //bot.startUsingItem(ProjectileUtil.getWeaponHoldingHand(bot, Items.BOW))
            }
        }
    }

    private fun checkAndPerformAttack(target: LivingEntity) {
        if (this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown(Random.nextInt(3, 5) * 20)
            this.bot.swing(InteractionHand.MAIN_HAND)
            this.bot.doHurtTarget(target)

            this.attackRadiusSqr = (range * range) * 10f
        }
    }

    private fun resetAttackCooldown(ticks: Int) {
        this.ticksUntilNextAttack = adjustedTickDelay(ticks)
    }

    private fun isTimeToAttack(): Boolean = this.ticksUntilNextAttack <= 0
}
