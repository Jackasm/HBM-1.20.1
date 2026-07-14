package com.hbm.items;

import com.hbm.items.weapon.sedna.*;
import com.hbm.items.weapon.sedna.factory.*;
import com.hbm.items.weapon.sedna.impl.GunChargeThrower;
import com.hbm.items.weapon.sedna.impl.ItemGunChemthrower;
import com.hbm.items.weapon.sedna.impl.ItemGunNI4NI;
import com.hbm.items.weapon.sedna.impl.ItemGunStinger;
import com.hbm.items.weapon.sedna.mags.*;
import com.hbm.sound.ModSounds;
import com.hbm.util.RefStrings;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.hbm.items.weapon.sedna.factory.BulletConfigRegistry.*;

import static com.hbm.items.weapon.sedna.factory.XFactory10ga.LAMBDA_DOUBLE_BARREL_ANIMS;
import static com.hbm.items.weapon.sedna.factory.XFactory10ga.LAMBDA_RECOIL_DOUBLE_BARREL;
import static com.hbm.items.weapon.sedna.factory.XFactory12ga.*;
import static com.hbm.items.weapon.sedna.factory.XFactory12ga.LAMBDA_SHREDDER_ANIMS;
import static com.hbm.items.weapon.sedna.factory.XFactory35800.LAMBDA_ABERRATOR;
import static com.hbm.items.weapon.sedna.factory.XFactory35800.LAMBDA_RECOIL_ABERRATOR;
import static com.hbm.items.weapon.sedna.factory.XFactory44.*;
import static com.hbm.items.weapon.sedna.factory.XFactory50.*;
import static com.hbm.items.weapon.sedna.factory.XFactory556mm.*;
import static com.hbm.items.weapon.sedna.factory.XFactory762mm.*;
import static com.hbm.items.weapon.sedna.factory.XFactoryBlackPowder.LAMBDA_PEPPERBOX_ANIMS;
import static com.hbm.items.weapon.sedna.factory.XFactoryBlackPowder.LAMBDA_RECOIL_PEPPERBOX;
import static com.hbm.items.weapon.sedna.factory.XFactoryRocket.*;
import static com.hbm.sound.ModSounds.*;

public class ModGunItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RefStrings.MODID);

    private static final Map<String, RegistryObject<Item>> GUN_REGISTRY = new HashMap<>();

    public static RegistryObject<Item> registerGun(String name, Supplier<Item> supplier) {
        RegistryObject<Item> item = ITEMS.register(name, supplier);
        ItemRegistryHelper.addWeaponToTab(name, item);
        GUN_REGISTRY.put(name, item);
        return item;
    }

    static BulletConfig[] all12 = new BulletConfig[] {
            g12_bp, g12_bp_magnum, g12_bp_slug, g12, g12_slug,
            g12_flechette, g12_magnum, g12_explosive, g12_phosphorus
    };

    static BulletConfig[] all9mm = new BulletConfig[] {
            p9_sp, p9_fmj, p9_jhp, p9_ap
    };

    public static BulletConfig[] rocket_rpzb = new BulletConfig[] {
            rocket_rpzb_he,
            rocket_rpzb_heat,
            rocket_rpzb_demo,
            rocket_rpzb_inc,
            rocket_rpzb_phosphorus
    };

    public static BulletConfig[] rocket_qd = new BulletConfig[] {
            rocket_qd_he,
            rocket_qd_heat,
            rocket_qd_demo,
            rocket_qd_inc,
            rocket_qd_phosphorus
    };

    public static BulletConfig[] rocket_ml = new BulletConfig[] {
            rocket_ml_he,
            rocket_ml_heat,
            rocket_ml_demo,
            rocket_ml_inc,
            rocket_ml_phosphorus
    };

    public static final RegistryObject<Item> GUN_PEPPERBOX = registerGun("weapon/gun_pepperbox",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(300).draw(4).inspect(23).crosshair(Crosshair.CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(5F).delay(27).reload(67).jam(58)
                .sound(ModSounds.FIRE_BLACK_POWDER.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 6)
                .addConfigs(STONE, FLINT, IRON, SHOT))
                .offset(0.75, -0.0625, -0.1875D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_PEPPERBOX))
                .setupStandardConfiguration()
                .anim(LAMBDA_PEPPERBOX_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_PEPPERBOX))
                .setNameMutator(stack -> "item.hbm.weapon.gun_pepperbox")
    );

    public static final RegistryObject<Item> GUN_MARESLEG = registerGun("weapon/gun_maresleg",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(600).draw(10).inspect(39).reloadSequential(true)
                .crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(16F).delay(20).reload(22, 10, 13, 0).jam(24)
                .sound(ModSounds.FIRE_SHOTGUN.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 6).addConfigs(all12))
                .offset(0.75, -0.0625, -0.1875)
                .setupStandardFire().recoil(LAMBDA_RECOIL_MARESLEG))
                .setupStandardConfiguration()
                .anim(LAMBDA_MARESLEG_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_MARESLEG))
                .setNameMutator(LAMBDA_NAME_MARESLEG)
    );

    public static final RegistryObject<Item> GUN_MARESLEG_BROKEN = registerGun("weapon/gun_maresleg_broken",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
                .dura(0).draw(5).inspect(39).reloadSequential(true)
                .crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(48F).spreadAmmo(1.15F).delay(20)
                .reload(22, 10, 13, 0).jam(24)
                .sound(ModSounds.FIRE_SHOTGUN.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 6)
                .addConfigs(g12_bp, g12_bp_magnum, g12_bp_slug, g12,
                        g12_slug, g12_flechette, g12_magnum, g12_explosive, g12_phosphorus))
                .offset(0.75, -0.0625, -0.1875)
                .canFire(Lego.LAMBDA_STANDARD_CAN_FIRE)
                .fire(Lego.LAMBDA_NOWEAR_FIRE)
                .recoil(XFactory12ga.LAMBDA_RECOIL_MARESLEG))
                .setupStandardConfiguration()
                .anim(XFactory12ga.LAMBDA_MARESLEG_SHORT_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_MARESLEG_SHORT))
                .setNameMutator(stack -> "item.hbm.weapon.gun_maresleg_broken")
    );

    public static final RegistryObject<Item> GUN_LIBERATOR = registerGun("weapon/gun_liberator",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(200).draw(20).inspect(21).reloadSequential(true)
                .crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(16F).delay(20).rounds(4).reload(25, 15, 7, 0).jam(45)
                .sound(ModSounds.WEAPON_FIRE_SHOTGUN_ALT.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 4).addConfigs(all12))
                .offset(0.75, -0.0625, -0.1875)
                .setupStandardFire().recoil(LAMBDA_RECOIL_LIBERATOR))
                .setupStandardConfiguration()
                .anim(LAMBDA_LIBERATOR_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_LIBERATOR))
    );

    public static final RegistryObject<Item> GUN_SPAS12 = registerGun("weapon/gun_spas12",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(600).draw(20).inspect(39).reloadSequential(true)
                .reloadChangeType(true).crosshair(Crosshair.L_CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(32F).spreadHipfire(0F).delay(20)
                .reload(5, 10, 10, 10, 0).jam(36)
                .sound(ModSounds.SHOTGUN_SHOOT.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 8).addConfigs(all12))
                .offset(0.75, -0.0625, -0.1875)
                .setupStandardFire().recoil(LAMBDA_RECOIL_MARESLEG))
                .setupStandardConfiguration()
                .ps(LAMBDA_SPAS_SECONDARY).pt(null)
                .anim(LAMBDA_SPAS_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_SPAS))
    );

    public static final RegistryObject<Item> GUN_AUTOSHOTGUN = registerGun("weapon/gun_autoshotgun",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(2_000).draw(10).inspect(33).reloadSequential(true)
                .crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(48F).delay(10).auto(true).autoAfterDry(true)
                .dryfireAfterAuto(true).reload(44).jam(19)
                .sound(ModSounds.WEAPON_FIRE_SHOTGUN_AUTO.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 20).addConfigs(all12))
                .offset(0.75, -0.125, -0.25)
                .setupStandardFire().recoil(LAMBDA_RECOIL_AUTOSHOTGUN))
                .setupStandardConfiguration()
                .anim(LAMBDA_SHREDDER_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_SHREDDER))
    );

    public static final RegistryObject<Item> GUN_AUTOSHOTGUN_SEXY = registerGun("weapon/gun_autoshotgun_sexy",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
                .dura(5_000).draw(20).inspect(65).reloadSequential(true)
                .inspectCancel(false).crosshair(Crosshair.L_CIRCLE)
                .hideCrosshair(false).smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(64F).delay(4).auto(true).dryfireAfterAuto(true)
                .reload(110).jam(19)
                .sound(ModSounds.WEAPON_FIRE_SHOTGUN_AUTO.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 100)
                .addConfigs(g12_bp, g12_bp_magnum, g12_bp_slug, g12, g12_slug, g12_flechette,
                        g12_magnum, g12_explosive, g12_phosphorus))
                .offset(0.75, -0.125, -0.25)
                .setupStandardFire().recoil(LAMBDA_RECOIL_SEXY))
                .setupStandardConfiguration()
                .anim(LAMBDA_SEXY_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_SHREDDER_SEXY))
    );

    public static final RegistryObject<Item> GUN_AUTOSHOTGUN_SHREDDER = registerGun("weapon/gun_autoshotgun_shredder",
            () -> new GunItem(GunItem.WeaponQuality.B_SIDE, new GunConfig()
                .dura(2_000).draw(10).inspect(33).reloadSequential(true)
                .crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(50F).delay(10).auto(true).autoAfterDry(true)
                .dryfireAfterAuto(true).reload(44).jam(19)
                .sound(ModSounds.WEAPON_FIRE_SHOTGUN_AUTO.get(), 1.0F, 1.0F)
                .mag(new MagazineBelt()
                .addConfigs(g12_shredder, g12_shredder_slug, g12_shredder_flechette, g12_shredder_magnum,
                        g12_shredder_explosive, g12_shredder_phosphorus))
                .offset(0.75, -0.125, -0.25)
                .setupStandardFire().recoil(LAMBDA_RECOIL_AUTOSHOTGUN))
                .setupStandardConfiguration()
                .anim(LAMBDA_SHREDDER_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_SHREDDER))
    );

    public static final RegistryObject<Item> GUN_DOUBLE_BARREL = registerGun("weapon/gun_double_barrel",
            () -> new GunItem(GunItem.WeaponQuality.SPECIAL, new GunConfig()
                .dura(1000).draw(10).inspect(39).crosshair(Crosshair.L_CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(30F).rounds(2).delay(10).reload(41).reloadOnEmpty(true)
                .sound(ModSounds.FIRE_SHOTGUN.get(), 1.0F, 0.9F)
                .mag(new MagazineFullReload(0, 2)
                .addConfigs(g10, g10_shrapnel, g10_du, g10_slug, g10_explosive))
                .offset(0.75, -0.0625, -0.1875)
                .setupStandardFire().recoil(LAMBDA_RECOIL_DOUBLE_BARREL))
                .setupStandardConfiguration()
                .anim(LAMBDA_DOUBLE_BARREL_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_DOUBLE_BARREL))
                .setNameMutator(stack -> "item.hbm.weapon.gun_double_barrel")
    );

    public static final RegistryObject<Item> GUN_DOUBLE_BARREL_SACRED_DRAGON = registerGun(
            "weapon/gun_double_barrel_sacred_dragon",
            () -> new GunItem(GunItem.WeaponQuality.B_SIDE, new GunConfig()
                .dura(6000).draw(10).inspect(39).crosshair(Crosshair.L_CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(45F).spreadAmmo(1.35F).rounds(2).delay(10).reload(41).reloadOnEmpty(true)
                .sound(ModSounds.FIRE_SHOTGUN.get(), 1.0F, 0.9F)
                .mag(new MagazineFullReload(0, 2)
                .addConfigs(g10, g10_shrapnel, g10_du, g10_slug, g10_explosive))
                .offset(0.75, -0.0625, -0.1875)
                .setupStandardFire().recoil(LAMBDA_RECOIL_DOUBLE_BARREL))
                .setupStandardConfiguration()
                .anim(LAMBDA_DOUBLE_BARREL_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_DOUBLE_BARREL))
                .setNameMutator(stack -> "item.hbm.weapon.gun_double_barrel_sacred_dragon")
    );

    public static final RegistryObject<Item> GUN_AUTOSHOTGUN_HERETIC = registerGun(
            "weapon/gun_autoshotgun_heretic",
            () -> new GunItem(GunItem.WeaponQuality.DEBUG, new GunConfig()
                .draw(20).inspect(65).reloadSequential(true).inspectCancel(false)
                .crosshair(Crosshair.L_CIRCLE).hideCrosshair(true)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(100F).delay(3).auto(true).dryfireAfterAuto(true)
                .reload(110).jam(19)
                .sound(ModSounds.WEAPON_FIRE_SHOTGUN_AUTO.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 250)
                .addConfigs(g10, g10_shrapnel, g10_du, g10_slug, g10_explosive))
                .offset(0.75, -0.125, -0.25)
                .canFire(Lego.LAMBDA_STANDARD_CAN_FIRE)
                .fire(Lego.LAMBDA_NOWEAR_FIRE)
                .recoil(XFactory12ga.LAMBDA_RECOIL_SEXY))
                .setupStandardConfiguration()
                .anim(XFactory12ga.LAMBDA_SEXY_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_SHREDDER_SEXY))
                .setNameMutator(stack -> "item.hbm.weapon.gun_autoshotgun_heretic")
    );

    public static final RegistryObject<Item> GUN_GREASEGUN = registerGun("weapon/gun_greasegun",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(3_000).draw(20).inspect(31).crosshair(Crosshair.L_CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(3F).delay(4).dry(40).auto(true).spread(0.015F)
                .reload(60).jam(55)
                .sound(ModSounds.FIRE_GREASEGUN.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 30).addConfigs(all9mm))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(XFactory9mm.LAMBDA_RECOIL_GREASEGUN))
                .setupStandardConfiguration()
                .anim(XFactory9mm.LAMBDA_GREASEGUN_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_GREASEGUN))
                .setNameMutator(XFactory9mm.LAMBDA_NAME_GREASEGUN)
    );

    public static final RegistryObject<Item> GUN_LAG = registerGun("weapon/gun_lag",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(1_700).draw(7).inspect(31).crosshair(Crosshair.CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(25F).delay(4).dry(10).spread(0.005F)
                .reload(53).jam(44)
                .sound(ModSounds.FIRE_PISTOL.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 17).addConfigs(all9mm))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().fire(XFactory9mm.LAMBDA_FIRE_LAG)
                .recoil(XFactory9mm.LAMBDA_RECOIL_LAG))
                .setupStandardConfiguration()
                .anim(XFactory9mm.LAMBDA_LAG_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_LAG))
    );

    public static final RegistryObject<Item> GUN_UZI = registerGun("weapon/gun_uzi",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(3_000).draw(15).inspect(31).crosshair(Crosshair.CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(3F).delay(2).dry(25).auto(true).spread(0.005F)
                .reload(55).jam(50)
                .sound(com.hbm.sound.ModSounds.FIRE_UZI.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 30).addConfigs(all9mm))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(XFactory9mm.LAMBDA_RECOIL_UZI))
                .setupStandardConfiguration()
                .anim(XFactory9mm.LAMBDA_UZI_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_UZI))
                .setNameMutator(XFactory9mm.LAMBDA_NAME_UZI)
    );

    public static final RegistryObject<Item> GUN_AM180 = registerGun("weapon/gun_am180",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(177 * 25).draw(15).inspect(38).crosshair(Crosshair.L_CIRCLE)
                .smoke(XFactory22lr.LAMBDA_SMOKE)
                .rec(new Receiver(0)
                .dmg(2F).delay(1).dry(10).auto(true).spread(0.01F).reload(66).jam(30)
                .sound(ModSounds.FIRE_GREASEGUN.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 177)
                .addConfigs(p22_sp, p22_fmj, p22_jhp, p22_ap))
                .offset(1, -0.0625 * 1.5, -0.1875D)
                .setupStandardFire().recoil(XFactory22lr.LAMBDA_RECOIL_AM180))
                .setupStandardConfiguration()
                .anim(XFactory22lr.LAMBDA_AM180_ANIMS)
                .orchestra(Orchestras.ORCHESTRA_AM180))
                .setNameMutator(XFactory22lr.LAMBDA_NAME_AM180)
    );

    public static final RegistryObject<Item> GUN_LIGHT_REVOLVER = registerGun("weapon/gun_light_revolver",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
                .dura(300).draw(4).inspect(23).crosshair(Crosshair.CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(7.5F).delay(11).reload(55).jam(45)
                .sound(ModSounds.FIRE_PISTOL.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 6)
                .addConfigs(m357_bp, m357_sp, m357_fmj, m357_jhp, m357_ap, m357_express))
                .offset(0.75, -0.0625, -0.3125D)
                .setupStandardFire().recoil(XFactory357.LAMBDA_RECOIL_ATLAS))
                .setupStandardConfiguration()
                .anim(XFactory357.LAMBDA_ATLAS_ANIMS).orchestra(Orchestras.ORCHESTRA_ATLAS))
                .setNameMutator(stack -> "item.hbm.weapon.gun_light_revolver")
    );

    public static final RegistryObject<Item> GUN_LIGHT_REVOLVER_ATLAS = registerGun("weapon/gun_light_revolver_atlas",
            () -> new GunItem(GunItem.WeaponQuality.B_SIDE, new GunConfig()
                .dura(300).draw(4).inspect(23).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
                .rec(new Receiver(0)
                .dmg(12.5F).delay(11).reload(55).jam(45)
                .sound(ModSounds.FIRE_PISTOL.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 6)
                .addConfigs(m357_bp, m357_sp, m357_fmj, m357_jhp, m357_ap, m357_express))
                .offset(0.75, -0.0625, -0.3125D)
                .setupStandardFire().recoil(XFactory357.LAMBDA_RECOIL_ATLAS))
                .setupStandardConfiguration()
                .anim(XFactory357.LAMBDA_ATLAS_ANIMS).orchestra(Orchestras.ORCHESTRA_ATLAS))
                .setNameMutator(stack -> "item.hbm.weapon.gun_light_revolver_atlas")
    );

    public static final RegistryObject<Item> GUN_HENRY = registerGun("weapon/gun_henry",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(300).draw(15).inspect(23).reloadSequential(true).crosshair(Crosshair.CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
                .dmg(10F).delay(20).reload(25, 11, 14, 8).jam(45).sound(ModSounds.WEAPON_FIRE_RIFLE.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 14).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
                .offset(0.75, -0.0625, -0.1875D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_HENRY))
                .setupStandardConfiguration()
				.anim(LAMBDA_HENRY_ANIMS).orchestra(Orchestras.ORCHESTRA_HENRY))
				.setNameMutator(stack -> "item.hbm.weapon.gun_henry")
    );

    public static final RegistryObject<Item> GUN_HENRY_LINCOLN = registerGun("weapon/gun_henry_lincoln",
            () -> new GunItem(GunItem.WeaponQuality.B_SIDE, new GunConfig()
				.dura(300).draw(15).inspect(23).reloadSequential(true).crosshair(Crosshair.CIRCLE)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
                .dmg(20F).spreadHipfire(0F).delay(20).reload(25, 11, 14, 8).jam(45)
                .sound(ModSounds.WEAPON_FIRE_RIFLE.get(), 1.0F, 1.25F)
                .mag(new MagazineSingleReload(0, 14).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
                .offset(0.75, -0.0625, -0.1875D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_HENRY))
                .setupStandardConfiguration()
				.anim(LAMBDA_HENRY_ANIMS).orchestra(Orchestras.ORCHESTRA_HENRY))
				.setNameMutator(stack -> "item.hbm.weapon.gun_henry_lincoln")
    );

    public static final RegistryObject<Item> GUN_HEAVY_REVOLVER = registerGun("weapon/gun_heavy_revolver",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(600).draw(10).inspect(23).crosshair(Crosshair.L_CLASSIC).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
                .dmg(15F).delay(14).reload(46).jam(23).sound(ModSounds.WEAPON_FIRE_44SHOOT.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 6).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
                .offset(0.75, -0.0625, -0.3125D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_NOPIP))
                .setupStandardConfiguration()
				.anim(LAMBDA_NOPIP_ANIMS).orchestra(Orchestras.ORCHESTRA_NOPIP))
                .setNameMutator(LAMBDA_NAME_NOPIP)
				.setNameMutator(stack -> "item.hbm.weapon.gun_heavy_revolver")
    );

    public static final RegistryObject<Item> GUN_HEAVY_REVOLVER_LILMAC = registerGun("weapon/gun_heavy_revolver_lilmac",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(31_000).draw(10).inspect(23).crosshair(Crosshair.L_CLASSIC).scopeTexture(scope_lilmac)
                .smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
                .dmg(30F).delay(14).reload(46).jam(23).sound(ModSounds.WEAPON_FIRE_44SHOOT.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 6).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
                .offset(0.75, -0.0625, -0.3125D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_NOPIP))
                .setupStandardConfiguration()
				.anim(LAMBDA_LILMAC_ANIMS).orchestra(Orchestras.ORCHESTRA_NOPIP))
                .setNameMutator(stack -> "item.hbm.weapon.gun_heavy_revolver_lilmac")
    );

    public static final RegistryObject<Item> GUN_HEAVY_REVOLVER_PROTEGE = registerGun("weapon/gun_heavy_revolver_protege",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(31_000).draw(10).inspect(23).crosshair(Crosshair.L_CLASSIC).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
                .dmg(30F).delay(14).reload(46).jam(23).sound(ModSounds.WEAPON_FIRE_44SHOOT.get(), 1.0F, 0.8F)
                .mag(new MagazineFullReload(0, 6).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
                .offset(0.75, -0.0625, -0.3125D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_NOPIP))
                .setupStandardConfiguration()
				.anim(LAMBDA_LILMAC_ANIMS).orchestra(Orchestras.ORCHESTRA_NOPIP))
                .setNameMutator(stack -> "item.hbm.weapon.gun_heavy_revolver_protege")
    );

    public static final RegistryObject<Item> GUN_HANGMAN = registerGun("weapon/gun_hangman",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(600).draw(10).inspect(31).inspectCancel(false).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
                .dmg(25F).delay(10).reload(46).jam(23).sound(ModSounds.WEAPON_FIRE_44SHOOT.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 8).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_HANGMAN))
                .setupStandardConfiguration().ps(SMACK_A_FUCKER)
				.anim(LAMBDA_HANGMAN_ANIMS).orchestra(Orchestras.ORCHESTRA_HANGMAN))
                .setNameMutator(stack -> "item.hbm.weapon.gun_hangman")
    );

    public static final RegistryObject<Item> GUN_AMAT = registerGun("weapon/gun_amat",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(350).draw(20).inspect(50).crosshair(Crosshair.CIRCLE).scopeTexture(XFactory50.scope).smoke(XFactory50.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(30F).delay(25).dry(25).spreadHipfire(0.05F).reload(51).jam(43).sound(WEAPON_FIRE_AMAT.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 7)
                .addConfigs(bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_sm, bmg50_he))
                .offset(1, -0.0625 * 1.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_AMAT))
                .setupStandardConfiguration()
				.anim(LAMBDA_AMAT_ANIMS).orchestra(Orchestras.ORCHESTRA_AMAT))
                .setNameMutator(stack -> "item.hbm.weapon.gun_amat")
    );

    public static final RegistryObject<Item> GUN_AMAT_SUBTLETY = registerGun("weapon/gun_amat_subtlety",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(1_000).draw(20).inspect(50).crosshair(Crosshair.CIRCLE).scopeTexture(XFactory50.scope).smoke(XFactory50.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(50F).delay(25).dry(25).spreadHipfire(0.05F).reload(51).jam(43).sound(WEAPON_FIRE_AMAT.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 7)
                .addConfigs(bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_sm, bmg50_he))
                .offset(1, -0.0625 * 1.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_AMAT))
                .setupStandardConfiguration()
				.anim(LAMBDA_AMAT_ANIMS).orchestra(Orchestras.ORCHESTRA_AMAT))
                .setNameMutator(stack -> "item.hbm.weapon.gun_amat_subtlety")
    );

    public static final RegistryObject<Item> GUN_AMAT_PENANCE = registerGun("weapon/gun_amat_penance",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(5_000).draw(20).inspect(50).crosshair(Crosshair.CIRCLE).scopeTexture(scope_thermal)
                .thermalSights(true).smoke(XFactory50.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(45F).delay(25).dry(25).spreadHipfire(0F).reload(51).jam(43).sound(WEAPON_FIRE_SILENSERSHOT.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 7).addConfigs(bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_sm, bmg50_he))
                .offset(1, -0.0625 * 1.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_AMAT))
                .setupStandardConfiguration()
				.anim(LAMBDA_AMAT_ANIMS).orchestra(Orchestras.ORCHESTRA_AMAT))
                .setNameMutator(stack -> "item.hbm.weapon.gun_amat_penance")
    );

    public static final RegistryObject<Item> GUN_M2 = registerGun("weapon/gun_m2",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(10).inspect(31).crosshair(Crosshair.L_CIRCLE).smoke(XFactory50.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(7.5F).delay(2).dry(10).auto(true).spread(0.005F).sound(TURRET_CHEKHOV_FIRE.get(), 1.0F, 1.0F)
                .mag(new MagazineBelt().addConfigs(bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_he))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_M2))
                .setupStandardConfiguration()
				.anim(LAMBDA_M2_ANIMS).orchestra(Orchestras.ORCHESTRA_M2))
                .setNameMutator(stack -> "item.hbm.weapon.gun_m2")
    );

    public static final RegistryObject<Item> GUN_G3 = registerGun("weapon/gun_g3",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(10).inspect(33).crosshair(Crosshair.CIRCLE).smoke(XFactory556mm.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(5F).delay(2).auto(true).dry(15).reload(50).jam(47).sound(WEAPON_FIRE_ASSAULT.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 30).addConfigs(r556_sp, r556_fmj, r556_jhp, r556_ap))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_G3))
                .setupStandardConfiguration().ps(Lego.LAMBDA_STANDARD_CLICK_SECONDARY)
				.anim(LAMBDA_G3_ANIMS).orchestra(Orchestras.ORCHESTRA_G3))
				.setNameMutator(LAMBDA_NAME_G3)
    );

    public static final RegistryObject<Item> GUN_G3_ZEBRA = registerGun("weapon/gun_g3_zebra",
            () -> new GunItem(GunItem.WeaponQuality.B_SIDE, new GunConfig()
				.dura(6_000).draw(10).inspect(33).crosshair(Crosshair.CIRCLE).smoke(XFactory556mm.LAMBDA_SMOKE).scopeTexture(XFactory556mm.scope)
				.rec(new Receiver(0)
                .dmg(7.5F).delay(2).auto(true).dry(15).spreadHipfire(0.01F).reload(50).jam(47).sound(WEAPON_FIRE_SILENCED.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 30).addConfigs(r556_inc_sp, r556_inc_fmj, r556_inc_jhp, r556_inc_ap))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_ZEBRA))
                .setupStandardConfiguration().ps(Lego.LAMBDA_STANDARD_CLICK_SECONDARY)
				.anim(LAMBDA_G3_ANIMS).orchestra(Orchestras.ORCHESTRA_G3))
				.setNameMutator(LAMBDA_NAME_G3)
    );

    public static final RegistryObject<Item> GUN_STG77 = registerGun("weapon/gun_stg77",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(10).inspect(125).crosshair(Crosshair.CIRCLE).scopeTexture(XFactory556mm.scope).smoke(XFactory556mm.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(10F).delay(2).dry(15).auto(true).reload(46).jam(0).sound(WEAPON_FIRE_ASSAULT.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 30).addConfigs(r556_sp, r556_fmj, r556_jhp, r556_ap))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_STG))
                .pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).ps(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD).pt(Lego.LAMBDA_TOGGLE_AIM)
				.decider(LAMBDA_STG77_DECIDER)
				.anim(LAMBDA_STG77_ANIMS).orchestra(Orchestras.ORCHESTRA_STG77))
				.setNameMutator(stack -> "item.hbm.weapon.gun_stg77")
    );

    public static final RegistryObject<Item> GUN_BOLTER = registerGun("weapon/gun_bolter",
            () -> new GunItem(GunItem.WeaponQuality.SPECIAL, new GunConfig()
				.dura(3_000).draw(20).inspect(31).crosshair(Crosshair.L_CIRCLE).smoke(XFactory75Bolt.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(15F).delay(2).auto(true).spread(0.005F).reload(40).jam(55).sound(ModSounds.FIRE_BLACK_POWDER.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 30).addConfigs(b75, b75_inc, b75_exp))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(XFactory75Bolt.LAMBDA_RECOIL_BOLT))
                .setupStandardConfiguration()
				.anim(XFactory75Bolt.LAMBDA_BOLTER_ANIMS).orchestra(Orchestras.ORCHESTRA_BOLTER))
				.setNameMutator(stack -> "item.hbm.weapon.gun_bolter")
    );

    public static final RegistryObject<Item> GUN_CARBINE = registerGun("weapon/gun_carbine",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(10).inspect(31).reloadSequential(true).crosshair(Crosshair.CIRCLE).smoke(XFactory762mm.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(15F).delay(5).dry(15).spread(0.0F).reload(30, 0, 15, 0).jam(60).sound(ModSounds.FIRE_BLACK_POWDER.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 14)
                .addConfigs(r762_sp, r762_fmj, r762_jhp, r762_ap, r762_du, r762_he))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_CARBINE))
                .setupStandardConfiguration()
				.anim(LAMBDA_CARBINE_ANIMS).orchestra(Orchestras.ORCHESTRA_CARBINE))
                .setNameMutator(stack -> "item.hbm.weapon.gun_carbine")
    );

    public static final RegistryObject<Item> GUN_MINIGUN = registerGun("weapon/gun_minigun",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(50_000).draw(20).inspect(20).crosshair(Crosshair.L_CIRCLE).smoke(XFactory762mm.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(6F).delay(1).auto(true).dry(15).spread(0.01F).sound(WEAPON_FIRE_CALSHOOT.get(), 1.0F, 1.0F)
                .mag(new MagazineBelt().addConfigs(r762_sp, r762_fmj, r762_jhp, r762_ap, r762_du, r762_he))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_MINIGUN))
                .setupStandardConfiguration()
				.anim(LAMBDA_MINIGUN_ANIMS).orchestra(Orchestras.ORCHESTRA_MINIGUN))
                .setNameMutator(stack -> "item.hbm.weapon.gun_minigun")
    );

    public static final RegistryObject<Item> GUN_MINIGUN_LACUNAE = registerGun("weapon/gun_minigun_lacunae",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(50_000).draw(20).inspect(20).crosshair(Crosshair.L_CIRCLE)
				.rec(new Receiver(0)
                .dmg(12F).delay(1).auto(true).dry(15).reload(15).spread(0.01F).sound(WEAPON_FIRE_LASER_GATLING.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 200)
                .addConfigs(energy_lacunae, energy_lacunae_overcharge, energy_lacunae_ir))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_LACUNAE))
                .setupStandardConfiguration()
				.anim(LAMBDA_MINIGUN_ANIMS).orchestra(Orchestras.ORCHESTRA_MINIGUN))
                .setNameMutator(stack -> "item.hbm.weapon.gun_minigun_lacunae")
    );

    public static final RegistryObject<Item> GUN_MAS36 = registerGun("weapon/gun_mas36",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(5_000).draw(20).inspect(31).reloadSequential(true).crosshair(Crosshair.CIRCLE).smoke(XFactory762mm.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(30F).delay(25).dry(25).spread(0.0F).reload(43).jam(43).sound(WEAPON_FIRE_RIFLE_HEAVY.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 7).addConfigs(r762_sp, r762_fmj, r762_jhp, r762_ap, r762_du, r762_he))
                .offset(1, -0.0625 * 1.5, -0.25D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_CARBINE))
                .setupStandardConfiguration()
				.anim(LAMBDA_MAS36_ANIMS).orchestra(Orchestras.ORCHESTRA_MAS36))
                .setNameMutator(stack -> "item.hbm.weapon.gun_mas36")
    );

    public static final RegistryObject<Item> GUN_ABERRATOR = registerGun("weapon/gun_aberrator",
            () -> new GunItem(GunItem.WeaponQuality.SECRET, new GunConfig()
				.dura(2_000).draw(10).inspect(26).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
                .dmg(100F).delay(13).dry(21).reload(51).sound(ModSounds.WEAPON_FIRE_ABERRATOR.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 5).addConfigs(p35800, p35800_bl))
                .offset(0.75, -0.0625 * 1.5, -0.1875)
                .canFire(Lego.LAMBDA_STANDARD_CAN_FIRE).fire(Lego.LAMBDA_NOWEAR_FIRE).recoil(LAMBDA_RECOIL_ABERRATOR))
                .setupStandardConfiguration()
				.anim(LAMBDA_ABERRATOR).orchestra(Orchestras.ORCHESTRA_ABERRATOR))
				.setNameMutator(stack -> "item.hbm.weapon.gun_aberrator")
    );

    public static final RegistryObject<Item> GUN_TESLA_CANNON = registerGun("weapon/gun_tesla_cannon",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(1_000).draw(10).inspect(33).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
                .dmg(35F).delay(20).spreadHipfire(1.5F).reload(44).jam(19).sound(WEAPON_FIRE_TESLA.get(), 1.0F, 1.0F)
                .mag(new MagazineBelt().addConfigs(energy_tesla, energy_tesla_overcharge, energy_tesla_ir))
                .offset(0.75, 0, -0.375).offsetScoped(0.75, 0, -0.25)
                .setupStandardFire().recoil(XFactoryEnergy.LAMBDA_RECOIL_ENERGY))
                .setupStandardConfiguration()
				.anim(XFactoryEnergy.LAMBDA_TESLA_ANIMS).orchestra(Orchestras.ORCHESTRA_TESLA))
				.setNameMutator(stack -> "item.hbm.weapon.gun_tesla_cannon")
    );

    public static final RegistryObject<Item> GUN_LASER_PISTOL = registerGun("weapon/gun_laser_pistol",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(500).draw(10).inspect(26).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
                .dmg(25F).delay(5).spread(1F).spreadHipfire(1F).reload(45).jam(37).sound(WEAPON_FIRE_LASER_PISTOL.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 30).addConfigs(energy_las, energy_las_overcharge, energy_las_ir))
                .offset(0.75, -0.0625 * 1.5, -0.1875)
                .setupStandardFire().recoil(XFactoryEnergy.LAMBDA_RECOIL_ENERGY))
                .setupStandardConfiguration()
				.anim(XFactoryEnergy.LAMBDA_LASER_PISTOL).orchestra(Orchestras.ORCHESTRA_LASER_PISTOL))
				.setNameMutator(stack -> "item.hbm.weapon.gun_laser_pistol")
    );

    public static final RegistryObject<Item> GUN_LASER_PISTOL_PEW_PEW = registerGun("weapon/gun_laser_pistol_pew_pew",
            () -> new GunItem(GunItem.WeaponQuality.B_SIDE, new GunConfig()
				.dura(500).draw(10).inspect(26).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
                .dmg(30F).rounds(5).delay(10).spread(0.25F).spreadHipfire(1F).reload(45).jam(37).sound(WEAPON_FIRE_LASER_PISTOL.get(), 1.0F, 0.8F)
                .mag(new MagazineFullReload(0, 10).addConfigs(energy_las, energy_las_overcharge, energy_las_ir))
                .offset(0.75, -0.0625 * 1.5, -0.1875)
                .setupStandardFire().recoil(XFactoryEnergy.LAMBDA_RECOIL_ENERGY))
                .setupStandardConfiguration()
				.anim(XFactoryEnergy.LAMBDA_LASER_PISTOL).orchestra(Orchestras.ORCHESTRA_LASER_PISTOL))
				.setNameMutator(stack -> "item.hbm.weapon.gun_laser_pistol_pew_pew")
    );

    public static final RegistryObject<Item> GUN_LASER_PISTOL_MORNING_GLORY = registerGun("weapon/gun_laser_pistol_morning_glory",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(1_500).draw(10).inspect(26).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
                .dmg(20F).delay(7).spread(0F).spreadHipfire(0.5F).reload(45).jam(37).sound(WEAPON_FIRE_LASER_PISTOL.get(), 1.0F, 1.1F)
                .mag(new MagazineFullReload(0, 20).addConfigs(energy_emerald, energy_emerald_overcharge, energy_emerald_ir))
                .offset(0.75, -0.0625 * 1.5, -0.1875)
                .setupStandardFire().recoil(XFactoryEnergy.LAMBDA_RECOIL_ENERGY))
                .setupStandardConfiguration()
				.anim(XFactoryEnergy.LAMBDA_LASER_PISTOL).orchestra(Orchestras.ORCHESTRA_LASER_PISTOL))
				.setNameMutator(stack -> "item.hbm.weapon.gun_laser_pistol_morning_glory")
    );

    public static final RegistryObject<Item> GUN_LASRIFLE = registerGun("weapon/gun_lasrifle",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(2_000).draw(10).inspect(26).crosshair(Crosshair.CIRCLE).scopeTexture(XFactoryEnergy.scope_luna)
				.rec(new Receiver(0)
                .dmg(50F).delay(8).spreadHipfire(1F).reload(44).jam(36).sound(WEAPON_FIRE_LASER.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 24).addConfigs(energy_las, energy_las_overcharge, energy_las_ir))
                .offset(0.75, -0.0625 * 1.5, -0.1875)
                .setupStandardFire().recoil(XFactoryEnergy.LAMBDA_RECOIL_ENERGY))
                .setupStandardConfiguration()
				.anim(XFactoryEnergy.LAMBDA_LASRIFLE).orchestra(Orchestras.ORCHESTRA_LASRIFLE))
				.setNameMutator(stack -> "item.hbm.weapon.gun_lasrifle")
    );

    public static final RegistryObject<Item> GUN_FLAREGUN = registerGun("weapon/gun_flaregun",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(100).draw(7).inspect(39).crosshair(Crosshair.L_CIRCUMFLEX).smoke(XFactory40mm.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(15F).delay(20).reload(28).jam(33).sound(WEAPON_FIRE_FLAREGUN.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 1).addConfigs(g26_flare, g26_flare_supply, g26_flare_weapon))
                .offset(0.75, -0.0625, -0.1875D)
                .setupStandardFire().recoil(XFactory40mm.LAMBDA_RECOIL_GL))
                .setupStandardConfiguration()
				.anim(XFactory40mm.LAMBDA_FLAREGUN_ANIMS).orchestra(Orchestras.ORCHESTRA_FLAREGUN))
                .setNameMutator(stack -> "item.hbm.weapon.gun_flaregun")
    );

    public static final RegistryObject<Item> GUN_CONGOLAKE = registerGun("weapon/gun_congolake",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(400).draw(7).inspect(39).reloadSequential(true).reloadChangeType(true)
                .crosshair(Crosshair.L_CIRCUMFLEX).smoke(XFactory40mm.LAMBDA_SMOKE)
				.rec(new Receiver(0)
                .dmg(20F).delay(24).reload(16, 16, 16, 0).jam(0).sound(WEAPON_FIRE_CONGOLAKE.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 4).addConfigs(g40_he, g40_heat, g40_demo, g40_inc, g40_phosphorus))
                .offset(0.75, -0.0625, -0.1875D)
                .setupStandardFire().recoil(XFactory40mm.LAMBDA_RECOIL_GL))
                .setupStandardConfiguration()
				.anim(XFactory40mm.LAMBDA_CONGOLAKE_ANIMS).orchestra(Orchestras.ORCHESTRA_CONGOLAKE))
                .setNameMutator(stack -> "item.hbm.weapon.gun_congolake")
    );

    public static final RegistryObject<Item> GUN_TAU = registerGun("weapon/gun_tau",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(6_400).draw(10).inspect(10).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
                .dmg(25F).spreadHipfire(0F).delay(4).auto(true).spread(0F)
                .mag(new MagazineBelt().addConfigs(tau_uranium, tau_uranium_charge))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(XFactoryAccelerator.LAMBDA_RECOIL_TAU))
                .pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY)
				.rp(XFactoryAccelerator.LAMBDA_TAU_PRIMARY_RELEASE)
				.ps(XFactoryAccelerator.LAMBDA_TAU_SECONDARY_PRESS)
				.rs(XFactoryAccelerator.LAMBDA_TAU_SECONDARY_RELEASE)
				.pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(XFactoryAccelerator.LAMBDA_TAU_ANIMS).orchestra(Orchestras.ORCHESTRA_TAU))
				.setNameMutator(stack -> "item.hbm.weapon.gun_tau")
    );

    public static final RegistryObject<Item> GUN_COILGUN = registerGun("weapon/gun_coilgun",
            () -> new GunItem(GunItem.WeaponQuality.SPECIAL, new GunConfig()
				.dura(400).draw(5).inspect(39).crosshair(Crosshair.L_CIRCUMFLEX)
				.rec(new Receiver(0)
                .dmg(35F).delay(5).reload(20).jam(33).sound(WEAPON_FIRE_COILGUNSHOOT.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 1).addConfigs(coil_tungsten, coil_ferrouranium))
                .offset(0.75, -0.0625, -0.1875D)
                .setupStandardFire().recoil(XFactoryAccelerator.LAMBDA_RECOIL_COILGUN))
                .setupStandardConfiguration()
				.anim(XFactoryAccelerator.LAMBDA_COILGUN_ANIMS).orchestra(Orchestras.ORCHESTRA_COILGUN))
				.setNameMutator(stack -> "item.hbm.weapon.gun_coilgun")
    );

    public static final RegistryObject<Item> GUN_N_I_4_N_I = registerGun("weapon/gun_n_i_4_n_i",
            () -> new ItemGunNI4NI(GunItem.WeaponQuality.SPECIAL, new GunConfig()
				.dura(0).draw(5).inspect(39).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
                .dmg(35F).delay(10).sound(WEAPON_FIRE_COILGUNSHOOT.get(), 1.0F, 1.0F)
                .mag(new MagazineInfinite(ni4ni_arc))
                .offset(0.75, -0.0625, -0.1875D)
                .setupStandardFire().fire(Lego.LAMBDA_NOWEAR_FIRE))
                .setupStandardConfiguration()
				.ps(XFactoryAccelerator.LAMBDA_NI4NI_SECONDARY_PRESS)
				.anim(XFactoryAccelerator.LAMBDA_NI4NI_ANIMS).orchestra(Orchestras.ORCHESTRA_COILGUN))
				.setNameMutator(stack -> "item.hbm.weapon.gun_n_i_4_n_i")
    );

    public static final RegistryObject<Item> GUN_FLAMER = registerGun("weapon/gun_flamer",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(20_000).draw(10).inspect(17).crosshair(Crosshair.L_CIRCLE)
				.rec(new Receiver(0)
                .dmg(1F).spreadHipfire(0F).delay(1).auto(true).reload(90).jam(17)
                .mag(new MagazineFullReload(0, 300).addConfigs(flame_diesel, flame_gas, flame_napalm, flame_balefire))
                .offset(0.75, -0.0625, -0.25D)
                .setupStandardFire())
                .setupStandardConfiguration()
				.anim(XFactoryFlamer.LAMBDA_FLAMER_ANIMS).orchestra(Orchestras.ORCHESTRA_FLAMER))
                .setNameMutator(stack -> "item.hbm.weapon.gun_flamer")
    );

    public static final RegistryObject<Item> GUN_FLAMER_TOPAZ = registerGun("weapon/gun_flamer_topaz",
            () -> new GunItem(GunItem.WeaponQuality.B_SIDE, new GunConfig()
				.dura(20_000).draw(10).inspect(17).crosshair(Crosshair.L_CIRCLE)
				.rec(new Receiver(0)
                .dmg(1.5F).spreadHipfire(0F).delay(1).auto(true).reload(90).jam(17)
                .mag(new MagazineFullReload(0, 500).addConfigs(flame_topaz_diesel, flame_topaz_gas, flame_topaz_napalm, flame_topaz_balefire))
                .offset(0.75, -0.0625, -0.25D)
                .setupStandardFire())
                .setupStandardConfiguration()
				.anim(XFactoryFlamer.LAMBDA_FLAMER_ANIMS).orchestra(Orchestras.ORCHESTRA_FLAMER))
                .setNameMutator(stack -> "item.hbm.weapon.gun_flamer_topaz")
    );

    public static final RegistryObject<Item> GUN_FLAMER_DAYBREAKER = registerGun("weapon/gun_flamer_daybreaker",
            () -> new GunItem(GunItem.WeaponQuality.LEGENDARY, new GunConfig()
				.dura(20_000).draw(10).inspect(17).crosshair(Crosshair.L_CIRCLE)
				.rec(new Receiver(0)
                .dmg(25F).spreadHipfire(0F).delay(10).auto(true).reload(90).jam(17).sound(ModSounds.FIRE_BLACK_POWDER.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 50).addConfigs(flame_daybreaker_diesel, flame_daybreaker_gas, flame_daybreaker_napalm, flame_daybreaker_balefire))
                .offset(0.75, -0.0625, -0.25D)
                .setupStandardFire())
                .setupStandardConfiguration()
				.anim(XFactoryFlamer.LAMBDA_FLAMER_ANIMS).orchestra(Orchestras.ORCHESTRA_FLAMER_DAYBREAKER))
                .setNameMutator(stack -> "item.hbm.weapon.gun_flamer_daybreaker")
    );

    public static final RegistryObject<Item> GUN_CHEMTHROWER = registerGun("weapon/gun_chemthrower",
            () -> new ItemGunChemthrower(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(90_000).draw(10).inspect(17).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
                .delay(1).spreadHipfire(0F).auto(true)
                .mag(new MagazineFluid(0, 3_000))
                .offset(0.75, -0.0625, -0.25D)
                .canFire(ItemGunChemthrower.LAMBDA_CAN_FIRE).fire(ItemGunChemthrower.LAMBDA_FIRE))
                .pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(XFactoryFlamer.LAMBDA_CHEMTHROWER_ANIMS).orchestra(Orchestras.ORCHESTRA_CHEMTHROWER))
                .setNameMutator(stack -> "item.hbm.weapon.gun_chemthrower")
    );

    public static final RegistryObject<Item> GUN_PANZERSCHRECK = registerGun("weapon/gun_panzerschreck",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(300).draw(7).inspect(40).crosshair(Crosshair.L_CIRCUMFLEX)
				.rec(new Receiver(0)
                .dmg(25F).delay(5).reload(50).jam(40).sound(WEAPON_FIRE_RPG.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 1).addConfigs(rocket_rpzb))
                .offset(1, -0.0625 * 1.5, -0.1875D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_ROCKET))
                .setupStandardConfiguration()
				.anim(LAMBDA_PANZERSCHRECK_ANIMS).orchestra(Orchestras.ORCHESTRA_PANERSCHRECK))
                .setNameMutator(stack -> "item.hbm.weapon.gun_panzerschreck")
    );

    public static final RegistryObject<Item> GUN_STINGER = registerGun("weapon/gun_stinger",
            () -> new ItemGunStinger(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(300).draw(7).inspect(40).crosshair(Crosshair.L_BOX_OUTLINE)
				.rec(new Receiver(0)
                .dmg(35F).delay(5).reload(50).jam(40).sound(WEAPON_FIRE_RPG.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 1).addConfigs(rocket_rpzb))
                .offset(1, -0.0625 * 1.5, -0.1875D)
                .setupLockonFire().recoil(LAMBDA_RECOIL_ROCKET))
                .setupStandardConfiguration().ps(LAMBDA_STINGER_SECONDARY_PRESS).rs(LAMBDA_STINGER_SECONDARY_RELEASE)
				.anim(LAMBDA_PANZERSCHRECK_ANIMS).orchestra(Orchestras.ORCHESTRA_STINGER))
				.setNameMutator(stack -> "item.hbm.weapon.gun_stinger")
    );

    public static final RegistryObject<Item> GUN_QUADRO = registerGun("weapon/gun_quadro",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(400).draw(7).inspect(40).crosshair(Crosshair.L_CIRCUMFLEX).hideCrosshair(true)
				.rec(new Receiver(0)
                .dmg(40F).spreadHipfire(0F).delay(10).reload(55).jam(40).sound(WEAPON_FIRE_RPG.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 4).addConfigs(rocket_qd))
                .offset(1, -0.0625 * 1.5, -0.1875D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_ROCKET))
                .setupStandardConfiguration()
				.anim(LAMBDA_QUADRO_ANIMS).orchestra(Orchestras.ORCHESTRA_QUADRO))
				.setNameMutator(stack -> "item.hbm.weapon.gun_quadro")
    );

    public static final RegistryObject<Item> GUN_MISSILE_LAUNCHER = registerGun("weapon/gun_missile_launcher",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(500).draw(20).inspect(40).crosshair(Crosshair.L_CIRCUMFLEX).hideCrosshair(true)
				.rec(new Receiver(0)
                .dmg(50F).spreadHipfire(0F).delay(5).reload(48).jam(33).sound(WEAPON_FIRE_RPG.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 1).addConfigs(rocket_ml))
                .offset(1, -0.0625 * 1.5, -0.1875D)
                .setupStandardFire().recoil(LAMBDA_RECOIL_ROCKET))
                .setupStandardConfiguration().pp(LAMBDA_MISSILE_LAUNCHER_PRIMARY_PRESS)
				.anim(LAMBDA_MISSILE_LAUNCHER_ANIMS).orchestra(Orchestras.ORCHESTRA_MISSILE_LAUNCHER))
				.setNameMutator(stack -> "item.hbm.weapon.gun_missile_launcher")
    );

    public static final RegistryObject<Item> GUN_FATMAN = registerGun("weapon/gun_fatman",
            () -> new GunItem(GunItem.WeaponQuality.A_SIDE, new GunConfig()
				.dura(300).draw(20).inspect(30).reloadChangeType(true).crosshair(Crosshair.L_CIRCUMFLEX).hideCrosshair(false)
				.rec(new Receiver(0)
                .dmg(100F).spreadHipfire(0F).delay(10).reload(57).jam(40).sound(ModSounds.FIRE_FATMAN.get(), 1.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 1).addConfigs(nuke_standard, nuke_demo, nuke_high, nuke_tots, nuke_hive, nuke_balefire))
                .offset(1, -0.0625 * 1.5, -0.1875D).offsetScoped(1, -0.0625 * 1.5, -0.125D)
                .setupStandardFire().recoil(XFactoryCatapult.LAMBDA_RECOIL_FATMAN))
                .setupStandardConfiguration()
				.anim(XFactoryCatapult.LAMBDA_FATMAN_ANIMS).orchestra(Orchestras.ORCHESTRA_FATMAN))
                .setNameMutator(stack -> "item.hbm.weapon.gun_fatman")
    );

    public static final RegistryObject<Item> GUN_FOLLY = registerGun("weapon/gun_folly",
            () -> new GunItem(GunItem.WeaponQuality.SECRET, new GunConfig()
				.dura(0).draw(40).crosshair(Crosshair.NONE)
				.rec(new Receiver(0)
                .dmg(1_000F).delay(26).dryfire(false).reload(160).jam(0).sound(ModSounds.FIRE_FOLLY.get(), 100.0F, 1.0F)
                .mag(new MagazineSingleReload(0, 1).addConfigs(folly_sm, folly_nuke))
                .offset(0.75, -0.0625, -0.1875D).offsetScoped(0.75, -0.0625, -0.125D)
                .canFire(XFactoryFolly.LAMBDA_CAN_FIRE).fire(XFactoryFolly.LAMBDA_FIRE).recoil(XFactoryFolly.LAMBDA_RECOIL_FOLLY))
                .setupStandardConfiguration().pt(XFactoryFolly.LAMBDA_TOGGLE_AIM)
				.anim(XFactoryFolly.LAMBDA_FOLLY_ANIMS).orchestra(Orchestras.ORCHESTRA_FOLLY))
                .setNameMutator(stack -> "item.hbm.weapon.gun_folly")
    );

    public static final RegistryObject<Item> GUN_FIREEXT = registerGun("weapon/fireext",
            () -> new GunItem(GunItem.WeaponQuality.UTILITY, new GunConfig()
				.dura(5_000).draw(10).inspect(55).reloadChangeType(true).hideCrosshair(false).crosshair(Crosshair.L_CIRCLE)
				.rec(new Receiver(0)
                .dmg(0F).delay(1).dry(0).auto(true).spread(0F).spreadHipfire(0F).reload(20).jam(0)
                .sound(ModSounds.EXTINGUISHER.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 300).addConfigs(fext_co2, fext_foam, fext_sand))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire())
                .setupStandardConfiguration()
				.orchestra(Orchestras.ORCHESTRA_FIREEXT))
                .setNameMutator(stack -> "item.hbm.weapon.fireext")
    );

    public static final RegistryObject<Item> GUN_CHARGE_THROWER = registerGun("weapon/gun_charge_thrower",
            () -> new GunChargeThrower(GunItem.WeaponQuality.UTILITY, new GunConfig()
				.dura(3_000).draw(10).inspect(55).reloadChangeType(true).hideCrosshair(false).crosshair(Crosshair.L_CIRCUMFLEX)
				.rec(new Receiver(0)
                .dmg(10F).delay(4).dry(10).auto(true).spread(0F).spreadHipfire(0F).reload(60).jam(0)
                .sound(ModSounds.GRENADE.get(), 1.0F, 1.0F)
                .mag(new MagazineFullReload(0, 1).addConfigs(ct_hook, ct_mortar, ct_mortar_charge))
                .offset(1, -0.0625 * 2.5, -0.25D)
                .setupStandardFire().recoil(XFactoryTool.LAMBDA_RECOIL_CT))
                .setupStandardConfiguration()
				.anim(XFactoryTool.LAMBDA_CT_ANIMS).orchestra(Orchestras.ORCHESTRA_CHARGE_THROWER))
                .setNameMutator(stack -> "item.hbm.weapon.gun_charge_thrower")
    );


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
