package com.hbm.items.weapon.sedna;

import com.hbm.items.weapon.sedna.factory.Lego;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;

/**
 * Receivers are the gun's "moving parts", i.e. they determine things like base damage,
 * spread, the ejector and the magazine. Think of this class like the barrel, receiver
 * and chamber of the gun. An underbarrel grenade launcher for example would be a
 * separate receiver instance compared to the regular gun it is attached to.
 */
public class Receiver {

    // WeaponModManager keys
    public static final String F_BASEDAMAGE = "F_BASEDAMAGE";
    public static final String I_DELAYAFTERFIRE = "I_DELAYAFTERFIRE";
    public static final String I_DELAYAFTERDRYFIRE = "I_DELAYAFTERDRYFIRE";
    public static final String I_ROUNDSPERCYCLE = "I_ROUNDSPERCYCLE";
    public static final String F_SPLITPROJECTILES = "F_SPLITPROJECTILES";
    public static final String F_SPREADINNATE = "F_SPREADINNATE";
    public static final String F_SPREADAMMO = "F_SPREADAMMO";
    public static final String F_SPREADHIPFIRE = "F_SPREADHIPFIRE";
    public static final String F_SPREADDURABILITY = "F_SPREADDURABILITY";
    public static final String B_REFIREONHOLD = "B_REFIREONHOLD";
    public static final String B_REFIREAFTERDRY = "B_REFIREAFTERDRY";
    public static final String B_DOESDRYFIRE = "B_DOESDRYFIRE";
    public static final String B_DOESDRYFIREAFTERAUTO = "B_DOESDRYFIREAFTERAUTO";
    public static final String B_EJECTONFIRE = "B_EJECTONFIRE";
    public static final String B_RELOADONEMPTY = "B_RELOADONEMPTY";
    public static final String I_RELOADBEGINDURATION = "I_RELOADBEGINDURATION";
    public static final String I_RELOADCYCLEDURATION = "I_RELOADCYCLEDURATION";
    public static final String I_RELOADENDDURATION = "I_RELOADENDDURATION";
    public static final String I_RELOADCOCKONEMPTYPRE = "I_RELOADCOCKONEMPTYPRE";
    public static final String I_RELOADCOCKONEMPTYPOST = "I_RELOADCOCKONEMPTYPOST";
    public static final String I_JAMDURATION = "I_JAMDURATION";
    public static final String S_FIRESOUND = "S_FIRESOUND";
    public static final String F_FIREVOLUME = "F_FIREVOLUME";
    public static final String F_FIREPITCH = "F_FIREPITCH";
    public static final String O_MAGAZINE = "O_MAGAZINE";
    public static final String O_PROJECTILEOFFSET = "O_PROJECTILEOFFSET";
    public static final String O_PROJECTILEOFFSETSCOPED = "O_PROJECTILEOFFSETSCOPED";
    public static final String FUN_CANFIRE = "FUN_CANFIRE";
    public static final String CON_ONFIRE = "CON_ONFIRE";
    public static final String CON_ONRECOIL = "CON_ONRECOIL";
    public static final String I_BOLTCYCLEDURATION = "I_BOLTCYCLEDURATION";

    // Fields
    public int index;
    public GunConfig parent;

    // Core stats
    private float baseDamage;
    private int delayAfterFire;
    private int delayAfterDryFire;
    private int roundsPerCycle = 1;
    private float splitProjectiles = 1.0f;
    private float spreadInnate;
    private float spreadMultAmmo = 1.0f;
    private float spreadPenaltyHipfire = 0.025f;
    private float spreadDurability = 0.125f;

    // Behavior flags
    private boolean refireOnHold;
    private boolean refireAfterDry;
    private boolean doesDryFire = true;
    private boolean doesDryFireAfterAuto;
    private boolean ejectOnFire = true;
    private boolean reloadOnEmpty;

    // Reload timings
    private int reloadBeginDuration;
    private int reloadCycleDuration;
    private int boltCycleDuration;
    private int reloadEndDuration;
    private int reloadCockOnEmptyPre;
    private int reloadCockOnEmptyPost;

    // Jam
    private int jamDuration;

    // Sound
    private SoundEvent fireSound;
    private float fireVolume = 1.0f;
    private float firePitch = 1.0f;

    // Magazine
    private IMagazine magazine;

    // Projectile offsets
    private Vec3 projectileOffset = Vec3.ZERO;
    private Vec3 projectileOffsetScoped = Vec3.ZERO;

    // Lambda functions
    @Nullable
    private BiFunction<ItemStack, GunItem.LambdaContext, Boolean> canFire;
    @Nullable
    private BiConsumer<ItemStack, GunItem.LambdaContext> onFire;
    @Nullable
    private BiConsumer<ItemStack, GunItem.LambdaContext> onRecoil;

    // Constructor
    public Receiver(int index) {
        this.index = index;
    }

    public Receiver() {
        this(0);
    }

    /* GETTERS with WeaponModManager evaluation */

    public float getBaseDamage(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.baseDamage, stack, F_BASEDAMAGE, this, parent != null ? parent.index : 0);
    }

    public int getDelayAfterFire(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.delayAfterFire, stack, I_DELAYAFTERFIRE, this, parent != null ? parent.index : 0);
    }

    public int getDelayAfterDryFire(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.delayAfterDryFire, stack, I_DELAYAFTERDRYFIRE, this, parent != null ? parent.index : 0);
    }

    public int getRoundsPerCycle(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.roundsPerCycle, stack, I_ROUNDSPERCYCLE, this, parent != null ? parent.index : 0);
    }

    public float getSplitProjectiles(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.splitProjectiles, stack, F_SPLITPROJECTILES, this, parent != null ? parent.index : 0);
    }

    public float getInnateSpread(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.spreadInnate, stack, F_SPREADINNATE, this, parent != null ? parent.index : 0);
    }

    public float getAmmoSpread(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.spreadMultAmmo, stack, F_SPREADAMMO, this, parent != null ? parent.index : 0);
    }

    public float getHipfireSpread(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.spreadPenaltyHipfire, stack, F_SPREADHIPFIRE, this, parent != null ? parent.index : 0);
    }

    public float getDurabilitySpread(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.spreadDurability, stack, F_SPREADDURABILITY, this, parent != null ? parent.index : 0);
    }

    public boolean getRefireOnHold(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.refireOnHold, stack, B_REFIREONHOLD, this, parent != null ? parent.index : 0);
    }

    public boolean getRefireAfterDry(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.refireAfterDry, stack, B_REFIREAFTERDRY, this, parent != null ? parent.index : 0);
    }

    public boolean getDoesDryFire(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.doesDryFire, stack, B_DOESDRYFIRE, this, parent != null ? parent.index : 0);
    }

    public boolean getDoesDryFireAfterAuto(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.doesDryFireAfterAuto, stack, B_DOESDRYFIREAFTERAUTO, this, parent != null ? parent.index : 0);
    }

    public boolean getEjectOnFire(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.ejectOnFire, stack, B_EJECTONFIRE, this, parent != null ? parent.index : 0);
    }

    public boolean getReloadOnEmpty(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.reloadOnEmpty, stack, B_RELOADONEMPTY, this, parent != null ? parent.index : 0);
    }

    public int getReloadBeginDuration(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.reloadBeginDuration, stack, I_RELOADBEGINDURATION, this, parent != null ? parent.index : 0);
    }

    public int getReloadCycleDuration(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.reloadCycleDuration, stack, I_RELOADCYCLEDURATION, this, parent != null ? parent.index : 0);
    }

    public int getBoltCycleDuration(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.boltCycleDuration, stack, I_BOLTCYCLEDURATION, this, parent != null ? parent.index : 0);
    }

    public int getReloadEndDuration(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.reloadEndDuration, stack, I_RELOADENDDURATION, this, parent != null ? parent.index : 0);
    }

    public int getReloadCockOnEmptyPre(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.reloadCockOnEmptyPre, stack, I_RELOADCOCKONEMPTYPRE, this, parent != null ? parent.index : 0);
    }

    public int getReloadCockOnEmptyPost(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.reloadCockOnEmptyPost, stack, I_RELOADCOCKONEMPTYPOST, this, parent != null ? parent.index : 0);
    }

    public int getJamDuration(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.jamDuration, stack, I_JAMDURATION, this, parent != null ? parent.index : 0);
    }

    @Nullable
    public SoundEvent getFireSound(@NotNull ItemStack stack) {
        return WeaponModManager.evalSound(this.fireSound, stack, this,
                parent != null ? parent.index : 0);
    }

    public float getFireVolume(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.fireVolume, stack, F_FIREVOLUME, this, parent != null ? parent.index : 0);
    }

    public float getFirePitch(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.firePitch, stack, F_FIREPITCH, this, parent != null ? parent.index : 0);
    }

    @Nullable
    public IMagazine getMagazine(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.magazine, stack, O_MAGAZINE, this, parent != null ? parent.index : 0);
    }

    @NotNull
    public Vec3 getProjectileOffset(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.projectileOffset, stack, O_PROJECTILEOFFSET, this, parent != null ? parent.index : 0);
    }

    @NotNull
    public Vec3 getProjectileOffsetScoped(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.projectileOffsetScoped, stack, O_PROJECTILEOFFSETSCOPED, this, parent != null ? parent.index : 0);
    }

    @Nullable
    public BiFunction<ItemStack, GunItem.LambdaContext, Boolean> getCanFire(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.canFire, stack, FUN_CANFIRE, this, parent != null ? parent.index : 0);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getOnFire(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onFire, stack, CON_ONFIRE, this, parent != null ? parent.index : 0);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getRecoil(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onRecoil, stack, CON_ONRECOIL, this, parent != null ? parent.index : 0);
    }

    /* FLUID API SETTERS */

    public Receiver dmg(float dmg) {
        this.baseDamage = dmg;
        return this;
    }

    public Receiver delay(int delay) {
        this.delayAfterFire = delay;
        this.delayAfterDryFire = delay;
        return this;
    }

    public Receiver dry(int delay) {
        this.delayAfterDryFire = delay;
        return this;
    }

    public Receiver rounds(int rounds) {
        this.roundsPerCycle = rounds;
        return this;
    }

    public Receiver split(float rounds) {
        this.splitProjectiles = rounds;
        return this;
    }

    public Receiver spread(float spread) {
        this.spreadInnate = spread;
        return this;
    }

    public Receiver spreadAmmo(float spread) {
        this.spreadMultAmmo = spread;
        return this;
    }

    public Receiver spreadHipfire(float spread) {
        this.spreadPenaltyHipfire = spread;
        return this;
    }

    public Receiver spreadDurability(float spread) {
        this.spreadDurability = spread;
        return this;
    }

    public Receiver auto(boolean auto) {
        this.refireOnHold = auto;
        return this;
    }

    public Receiver autoAfterDry(boolean auto) {
        this.refireAfterDry = auto;
        return this;
    }

    public Receiver dryfire(boolean dryfire) {
        this.doesDryFire = dryfire;
        return this;
    }

    public Receiver dryfireAfterAuto(boolean dryfire) {
        this.doesDryFireAfterAuto = dryfire;
        return this;
    }

    public Receiver ejectOnFire(boolean eject) {
        this.ejectOnFire = eject;
        return this;
    }

    public Receiver reloadOnEmpty(boolean reload) {
        this.reloadOnEmpty = reload;
        return this;
    }

    public Receiver mag(@Nullable IMagazine magazine) {
        this.magazine = magazine;
        return this;
    }

    public Receiver offset(double forward, double up, double side) {
        this.projectileOffset = new Vec3(side, up, forward);
        this.projectileOffsetScoped = new Vec3(0, up, forward);
        return this;
    }

    public Receiver offsetScoped(double forward, double up, double side) {
        this.projectileOffsetScoped = new Vec3(side, up, forward);
        return this;
    }

    public Receiver jam(int jam) {
        this.jamDuration = jam;
        return this;
    }

    public Receiver reload(int delay) {
        return reload(0, delay, delay, 0, 0);
    }

    public Receiver reload(int begin, int cycle, int end, int cock) {
        return reload(0, begin, cycle, end, cock);
    }

    public Receiver reload(int pre, int begin, int cycle, int end, int post) {
        this.reloadBeginDuration = begin;
        this.reloadCycleDuration = cycle;
        this.reloadEndDuration = end;
        this.reloadCockOnEmptyPre = pre;
        this.reloadCockOnEmptyPost = post;
        return this;
    }

    public Receiver canFire(@Nullable BiFunction<ItemStack, GunItem.LambdaContext, Boolean> lambda) {
        this.canFire = lambda;
        return this;
    }

    public Receiver fire(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onFire = lambda;
        return this;
    }

    public Receiver recoil(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onRecoil = lambda;
        return this;
    }

    public Receiver sound(@Nullable SoundEvent sound, float volume, float pitch) {
        this.fireSound = sound;
        this.fireVolume = volume;
        this.firePitch = pitch;
        return this;
    }

    public Receiver sound(@Nullable SoundEvent sound) {
        return sound(sound, 1.0f, 1.0f);
    }


    public Receiver setupStandardFire() {
         return this.canFire(Lego.LAMBDA_STANDARD_CAN_FIRE).fire(Lego.LAMBDA_STANDARD_FIRE);
    }

    public Receiver setupLockonFire() {
        return this.canFire(Lego.LAMBDA_LOCKON_CAN_FIRE).fire(Lego.LAMBDA_STANDARD_FIRE);
    }

    /* UTILITY METHODS */

    public float calculateTotalSpread(@NotNull ItemStack stack, boolean isAiming, float wearPercent) {
        float spread = getInnateSpread(stack);
        spread *= getAmmoSpread(stack);

        if (!isAiming) {
            spread += getHipfireSpread(stack);
        }

        if (wearPercent > 0) {
            spread += getDurabilitySpread(stack) * wearPercent;
        }

        return spread;
    }

    public boolean hasMagazine() {
        return magazine != null;
    }

    public boolean isAutomatic(@NotNull ItemStack stack) {
        return getRefireOnHold(stack);
    }

    public int getTotalReloadDuration(@NotNull ItemStack stack, int roundsToLoad) {
        int begin = getReloadBeginDuration(stack);
        int cycle = getReloadCycleDuration(stack);
        int end = getReloadEndDuration(stack);

        return begin + (cycle * roundsToLoad) + end;
    }

    @NotNull
    public Vec3 getEffectiveOffset(@NotNull ItemStack stack, boolean isScoped) {
        return isScoped ? getProjectileOffsetScoped(stack) : getProjectileOffset(stack);
    }
}