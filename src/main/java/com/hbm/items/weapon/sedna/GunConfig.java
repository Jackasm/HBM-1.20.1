package com.hbm.items.weapon.sedna;

import com.hbm.items.weapon.sedna.factory.GunStateDecider;
import com.hbm.items.weapon.sedna.factory.Lego;
import com.hbm.items.weapon.sedna.hud.IHUDComponent;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
import com.hbm.render.anim.AnimationEnums.GunAnimation;
import com.hbm.render.anim.BusAnimation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Despite how complicated the GunConfig looks, it actually only exists to hold together a bunch of fields.
 * Everything else is infrastructure for getting and setting.
 * The gun config determines general gun specific stats like durability, crosshair, animations,
 * receivers, click handling and the decider.
 *
 * @author hbm
 */
public class GunConfig {

    public List<GunItem.SmokeNode> smokeNodes = new ArrayList<>();

    public static final String O_RECEIVERS = "O_RECEIVERS";
    public static final String F_DURABILITY = "F_DURABILITY";
    public static final String I_DRAWDURATION = "I_DRAWDURATION";
    public static final String I_INSPECTDURATION = "I_INSPECTDURATION";
    public static final String I_INSPECTCANCEL = "I_INSPECTCANCEL";
    public static final String O_CROSSHAIR = "O_CROSSHAIR";
    public static final String B_HIDECROSSHAIR = "B_HIDECROSSHAIR";
    public static final String B_THERMALSIGHTS = "B_THERMALSIGHTS";
    public static final String B_RELOADREQUIRESTYPECHANGE = "B_RELOADREQUIRESTYPECHANGE";
    public static final String B_RELOADANIMATIONSEQUENTIAL = "B_RELOADANIMATIONSEQUENTIAL";
    public static final String O_SCOPETEXTURE = "O_SCOPETEXTURE";
    public static final String CON_SMOKE = "CON_SMOKE";
    public static final String CON_ORCHESTRA = "CON_ORCHESTRA";
    public static final String CON_ONPRESSPRIMARY = "CON_ONPRESSPRIMARY";
    public static final String CON_ONPRESSSECONDARY = "CON_ONPRESSSECONDARY";
    public static final String CON_ONPRESSTERTIARY = "CON_ONPRESSTERTIARY";
    public static final String CON_ONPRESSRELOAD = "CON_ONPRESSRELOAD";
    public static final String CON_ONRELEASEPRIMARY = "CON_ONRELEASEPRIMARY";
    public static final String CON_ONRELEASESECONDARY = "CON_ONRELEASESECONDARY";
    public static final String CON_ONRELEASETERTIARY = "CON_ONRELEASETERTIARY";
    public static final String CON_ONRELEASERELOAD = "CON_ONRELEASERELOAD";
    public static final String CON_DECIDER = "CON_DECIDER";
    public static final String FUN_ANIMNATIONS = "FUN_ANIMNATIONS";
    public static final String O_HUDCOMPONENTS = "O_HUDCOMPONENTS";

    public int index;

    /** List of receivers used by the gun, primary and secondary are usually indices 0 and 1 respectively, if applicable */
    protected Receiver[] receivers_DNA;
    protected float durability_DNA;
    protected int drawDuration_DNA = 0;
    protected int inspectDuration_DNA = 0;
    protected boolean inspectCancel_DNA = true;
    protected Crosshair crosshair_DNA;
    protected boolean hideCrosshair_DNA = true;
    protected boolean thermalSights_DNA = false;
    protected boolean reloadRequiresTypeChange_DNA = false;
    protected boolean reloadAnimationsSequential_DNA;
    protected ResourceLocation scopeTexture_DNA;

    /** Handles smoke clientside */
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> smokeHandler_DNA;

    /** This piece only triggers during reloads, playing sounds depending on the reload's progress making reload sounds easier and synced to animations */
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> orchestra_DNA;

    /** Lambda functions for clicking shit */
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> onPressPrimary_DNA;
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> onPressSecondary_DNA;
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> onPressTertiary_DNA;
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> onPressReload_DNA;

    /** Lambda functions for releasing the aforementioned shit */
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> onReleasePrimary_DNA;
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> onReleaseSecondary_DNA;
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> onReleaseTertiary_DNA;
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> onReleaseReload_DNA;

    /** The engine for the state machine that determines the gun's overall behavior */
    @Nullable
    protected BiConsumer<ItemStack, GunItem.LambdaContext> decider_DNA;

    /** Lambda that returns the relevant animation for the given params */
    @Nullable
    protected BiFunction<ItemStack, GunAnimation, BusAnimation> animations_DNA;

    @Nullable
    protected IHUDComponent[] hudComponents_DNA;

    @Nullable
    public Receiver[] getReceivers(@NotNull ItemStack stack) {
        return WeaponModManager.eval(receivers_DNA, stack, O_RECEIVERS, this, this.index);
    }

    public float getDurability(@NotNull ItemStack stack) {
        return WeaponModManager.eval(durability_DNA, stack, F_DURABILITY, this, this.index);
    }

    public int getDrawDuration(@NotNull ItemStack stack) {
        return WeaponModManager.eval(drawDuration_DNA, stack, I_DRAWDURATION, this, this.index);
    }

    public int getInspectDuration(@NotNull ItemStack stack) {
        return WeaponModManager.eval(inspectDuration_DNA, stack, I_INSPECTDURATION, this, this.index);
    }

    public boolean getInspectCancel(@NotNull ItemStack stack) {
        return WeaponModManager.eval(inspectCancel_DNA, stack, I_INSPECTCANCEL, this, this.index);
    }

    @Nullable
    public Crosshair getCrosshair(@NotNull ItemStack stack) {
        return WeaponModManager.eval(crosshair_DNA, stack, O_CROSSHAIR, this, this.index);
    }

    public boolean getHideCrosshair(@NotNull ItemStack stack) {
        return WeaponModManager.eval(hideCrosshair_DNA, stack, B_HIDECROSSHAIR, this, this.index);
    }

    public boolean hasThermalSights(@NotNull ItemStack stack) {
        return WeaponModManager.eval(thermalSights_DNA, stack, B_THERMALSIGHTS, this, this.index);
    }

    public boolean getReloadChangesType(@NotNull ItemStack stack) {
        return WeaponModManager.eval(reloadRequiresTypeChange_DNA, stack, B_RELOADREQUIRESTYPECHANGE, this, this.index);
    }

    public boolean getReloadAnimSequential(@NotNull ItemStack stack) {
        return WeaponModManager.eval(reloadAnimationsSequential_DNA, stack, B_RELOADANIMATIONSEQUENTIAL, this, this.index);
    }

    @Nullable
    public ResourceLocation getScopeTexture(@NotNull ItemStack stack) {
        return WeaponModManager.eval(scopeTexture_DNA, stack, O_SCOPETEXTURE, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getSmokeHandler(@NotNull ItemStack stack) {
        return WeaponModManager.eval(smokeHandler_DNA, stack, CON_SMOKE, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getOrchestra(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.orchestra_DNA, stack, CON_ORCHESTRA, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getPressPrimary(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onPressPrimary_DNA, stack, CON_ONPRESSPRIMARY, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getPressSecondary(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onPressSecondary_DNA, stack, CON_ONPRESSSECONDARY, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getPressTertiary(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onPressTertiary_DNA, stack, CON_ONPRESSTERTIARY, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getPressReload(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onPressReload_DNA, stack, CON_ONPRESSRELOAD, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getReleasePrimary(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onReleasePrimary_DNA, stack, CON_ONRELEASEPRIMARY, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getReleaseSecondary(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onReleaseSecondary_DNA, stack, CON_ONRELEASESECONDARY, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getReleaseTertiary(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onReleaseTertiary_DNA, stack, CON_ONRELEASETERTIARY, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getReleaseReload(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.onReleaseReload_DNA, stack, CON_ONRELEASERELOAD, this, this.index);
    }

    @Nullable
    public BiConsumer<ItemStack, GunItem.LambdaContext> getDecider(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.decider_DNA, stack, CON_DECIDER, this, this.index);
    }

    @Nullable
    public BiFunction<ItemStack, GunAnimation, BusAnimation> getAnims(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.animations_DNA, stack, FUN_ANIMNATIONS, this, this.index);
    }

    @Nullable
    public IHUDComponent[] getHUDComponents(@NotNull ItemStack stack) {
        return WeaponModManager.eval(this.hudComponents_DNA, stack, O_HUDCOMPONENTS, this, this.index);
    }

    public GunConfig rec(Receiver... receivers) {
        this.receivers_DNA = receivers;
        for (Receiver r : receivers_DNA) {
            r.parent = this;
        }
        return this;
    }

    public GunConfig dura(float dura) {
        this.durability_DNA = dura;
        return this;
    }

    public GunConfig draw(int draw) {
        this.drawDuration_DNA = draw;
        return this;
    }

    public GunConfig inspect(int inspect) {
        this.inspectDuration_DNA = inspect;
        return this;
    }

    public GunConfig inspectCancel(boolean flag) {
        this.inspectCancel_DNA = flag;
        return this;
    }

    public GunConfig crosshair(@Nullable Crosshair crosshair) {
        this.crosshair_DNA = crosshair;
        return this;
    }

    public GunConfig hideCrosshair(boolean flag) {
        this.hideCrosshair_DNA = flag;
        return this;
    }

    public GunConfig thermalSights(boolean flag) {
        this.thermalSights_DNA = flag;
        return this;
    }

    public GunConfig reloadChangeType(boolean flag) {
        this.reloadRequiresTypeChange_DNA = flag;
        return this;
    }

    public GunConfig reloadSequential(boolean flag) {
        this.reloadAnimationsSequential_DNA = flag;
        return this;
    }

    public GunConfig scopeTexture(@Nullable ResourceLocation tex) {
        this.scopeTexture_DNA = tex;
        return this;
    }

    public GunConfig smoke(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> smoke) {
        this.smokeHandler_DNA = smoke;
        return this;
    }

    public GunConfig orchestra(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> orchestra) {
        this.orchestra_DNA = orchestra;
        return this;
    }

    public GunConfig pp(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onPressPrimary_DNA = lambda;
        return this;
    }

    public GunConfig ps(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onPressSecondary_DNA = lambda;
        return this;
    }

    public GunConfig pt(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onPressTertiary_DNA = lambda;
        return this;
    }

    public GunConfig pr(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onPressReload_DNA = lambda;
        return this;
    }

    public GunConfig rp(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onReleasePrimary_DNA = lambda;
        return this;
    }

    public GunConfig rs(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onReleaseSecondary_DNA = lambda;
        return this;
    }

    public GunConfig rt(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onReleaseTertiary_DNA = lambda;
        return this;
    }

    public GunConfig rr(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.onReleaseReload_DNA = lambda;
        return this;
    }

    public GunConfig decider(@Nullable BiConsumer<ItemStack, GunItem.LambdaContext> lambda) {
        this.decider_DNA = lambda;
        return this;
    }

    public GunConfig anim(@Nullable BiFunction<ItemStack, GunAnimation, BusAnimation> lambda) {
        this.animations_DNA = lambda;
        return this;
    }

    public GunConfig hud(@Nullable IHUDComponent... components) {
        this.hudComponents_DNA = components;
        return this;
    }

    /**
     * Standard package for keybind handling and decider using LEGO prefabs:
     * Primary fire on LMB, reload on R, aiming on MMB and the standard decider
     * which includes jamming and auto fire handling
     */
    public GunConfig setupStandardConfiguration() {
         this.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY);
         this.pr(Lego.LAMBDA_STANDARD_RELOAD);
         this.pt(Lego.LAMBDA_TOGGLE_AIM);
         this.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER);

        return this;
    }

    // Helper method to check if this config has receivers
    public boolean hasReceivers() {
        return receivers_DNA != null && receivers_DNA.length > 0;
    }

    // Helper method to get primary receiver
    @Nullable
    public Receiver getPrimaryReceiver(@NotNull ItemStack stack) {
        Receiver[] receivers = getReceivers(stack);
        return receivers != null && receivers.length > 0 ? receivers[0] : null;
    }

    // Helper method to get secondary receiver (if exists)
    @Nullable
    public Receiver getSecondaryReceiver(@NotNull ItemStack stack) {
        Receiver[] receivers = getReceivers(stack);
        return receivers != null && receivers.length > 1 ? receivers[1] : null;
    }

    // Builder-style method for creating complex configs
    public static GunConfig builder() {
        return new GunConfig();
    }

    // Getter for index
    public int getIndex() {
        return index;
    }

    // Setter for index (used by GunItem)
    void setIndex(int index) {
        this.index = index;
    }
}