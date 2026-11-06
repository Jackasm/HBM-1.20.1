package com.hbm.tileentity;

import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.tileentity.door.IRenderDoors;
import com.hbm.sound.ModSounds;
import com.hbm.util.BobMathUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class DoorDecl {

    /** Format: x, y, z, tangent amount 1, tangent amount 2, axis (0-x, 1-y, 2-z) */
    public abstract int[][] getDoorOpenRanges();
    public abstract int[] getDimensions();

    public int[][] getExtraDimensions() { return null; }

    public int getBlockOffset() { return 0; }
    public boolean remoteControllable() { return false; }

    public float getDoorRangeOpenTime(int ticks, int idx) {
        return getNormTime(ticks);
    }

    public int timeToOpen() { return 20; }

    public float getNormTime(float time) {
        return getNormTime(time, 0, timeToOpen());
    }

    public float getNormTime(float time, float min, float max) {
        return BobMathUtil.remap01_clamp(time, min, max);
    }

    public @Nullable ResourceLocation getTextureForPart(String partName) {
        return getTextureForPart(0, partName);
    }

    public @Nullable ResourceLocation getTextureForPart(int skinIndex, String partName) { return null; }
    public @Nullable HFRWavefrontObject getModel() { return null; }

    public void getTranslation(String partName, float openTicks, boolean child, float[] trans) { set(trans, 0, 0, 0); }
    public void getRotation(String partName, float openTicks, float[] rot) { set(rot, 0, 0, 0); }
    public void getOrigin(String partName, float[] orig) { set(orig, 0, 0, 0); }
    public boolean doesRender(String partName, boolean child) { return true; }

    private static final String[] NOTHING = new String[] {};
    public String[] getChildren(String partName) { return NOTHING; }
    public double[][] getClippingPlanes() { return new double[][] {}; }
    public void doOffsetTransform() {}

    public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
        return open ? new AABB(0, 0, 0, 0, 0, 0) : new AABB(0, 0, 0, 1, 1, 1);
    }

    public boolean neverDemandRedstone() { return false; }
    public boolean isLadder(boolean open) { return false; }

    @Nullable public SoundEvent getOpenSoundLoop() { return null; }
    @Nullable public SoundEvent getSoundLoop2() { return null; }
    @Nullable public SoundEvent getCloseSoundLoop() { return getOpenSoundLoop(); }
    @Nullable public SoundEvent getOpenSoundStart() { return null; }
    @Nullable public SoundEvent getCloseSoundStart() { return getOpenSoundStart(); }
    @Nullable public SoundEvent getOpenSoundEnd() { return null; }
    @Nullable public SoundEvent getCloseSoundEnd() { return getOpenSoundEnd(); }

    public float getSoundVolume() { return 1; }

    public float[] set(float[] f, float x, float y, float z) {
        f[0] = x;
        f[1] = y;
        f[2] = z;
        return f;
    }

    public @Nullable ResourceLocation[] getSEDNASkins() { return null; }
    public boolean hasSkins() { return getSkinCount() > 0; }
    public int getSkinCount() { return 0; }

    public ResourceLocation getCyclingSkins() {
        ResourceLocation[] skins = this.getSEDNASkins();
        int index = (int) ((System.currentTimeMillis() % (skins.length * 1000)) / 1000);
        return skins[index];
    }

    public ResourceLocation getSkinFromIndex(int index) {
        ResourceLocation[] skins = this.getSEDNASkins();
        return skins[Math.abs(index) % skins.length];
    }

    // keyframe animation system
    public @Nullable IRenderDoors getSEDNARenderer() { return null; }
    public @Nullable BusAnimation getBusAnimation(byte state, byte skinIndex) { return null; }

    public @Nullable HbmAnimations.Animation getSEDNAAnim(byte state, byte skinIndex) {
        BusAnimation anim = this.getBusAnimation(state, skinIndex);
        if (anim != null) return new HbmAnimations.Animation("DOOR_ANIM", System.currentTimeMillis(), anim);
        return null;
    }

    public @Nullable Consumer<TileEntityDoorGeneric> onDoorUpdate() { return null; }

    // ==================== Двери ====================

    public static final DoorDecl SLIDE_DOOR = new DoorDecl() {
        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_SLIDING_DOOR_OPENED.get(); }
        @Override public SoundEvent getCloseSoundEnd() { return ModSounds.DOOR_SLIDING_DOOR_SHUT.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_SLIDING_DOOR_OPENING.get(); }
        @Override public SoundEvent getSoundLoop2() { return ModSounds.DOOR_SLIDING_DOOR_OPENING.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderSlidingBlastDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("LOCK", new BusAnimationSequence().setPos(0, 0, 0).addPos(1, 0, 0, 200))
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 0, 0, 350).addPos(0, 0.05, 0, 200).addPos(0, 1, 0, 650, IType.SIN_UP));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("LOCK", new BusAnimationSequence().setPos(1, 0, 0).addPos(1, 0, 0, 1000).addPos(0, 0, 0, 200))
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0.05, 0, 650, IType.SIN_UP).addPos(0, 0, 0, 200));
            return null;
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_blast_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_blast_door_industrial_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 2; }

        @Override public int timeToOpen() { return 24; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { -2, 0, 0, 4, 5, 1 } }; }
        @Override public int[] getDimensions() { return new int[] { 3, 0, 0, 0, 3, 3 }; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (open) {
                if (y == 3) return new AABB(0, 0.5, 0, 1, 1, 1);
                else if (y == 0) return new AABB(0, 0, 0, 1, forCollision ? 0 : 0.08, 1);
            }
            return super.getBlockBound(x, y, z, open, forCollision);
        }
    };

    public static final DoorDecl FIRE_DOOR = new DoorDecl() {
        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_WGH_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_WGH_START.get(); }
        @Override public SoundEvent getSoundLoop2() { return ModSounds.DOOR_ALARM6.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderFireDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 1, 0, this.timeToOpen() * 50));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, this.timeToOpen() * 50));
            return null;
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_fire_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_fire_door_black_tex,
                    com.hbm.main.HBMResourceManager.pheo_fire_door_orange_tex,
                    com.hbm.main.HBMResourceManager.pheo_fire_door_yellow_tex,
                    com.hbm.main.HBMResourceManager.pheo_fire_door_trefoil_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 5; }

        @Override public int timeToOpen() { return 160; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { -1, 0, 0, 3, 4, 1 } }; }
        @Override public int[] getDimensions() { return new int[] { 2, 0, 0, 0, 2, 1 }; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (!open) return new AABB(0, 0, 0, 1, 1, 1);
            if (z == 1) return new AABB(0.5, 0, 0, 1, 1, 1);
            else if (z == -2) return new AABB(0, 0, 0, 0.5, 1, 1);
            else if (y > 1) return new AABB(0, 0.75, 0, 1, 1, 1);
            else if (y == 0) return new AABB(0, 0, 0, 1, forCollision ? 0 : 0.1, 1);
            return super.getBlockBound(x, y, z, open, forCollision);
        }
    };

    public static final DoorDecl VAULT_DOOR = new DoorDecl() {

        @Override public int getSkinCount() { return 7; }
        @Override public int timeToOpen() { return 120; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { -1, 1, 0, 3, 3, 2 } }; }
        @Override public int[] getDimensions() { return new int[] { 4, 0, 0, 0, 2, 2 }; }
        @Override public int[][] getExtraDimensions() { return new int[][] { { 0, 0, 1, -1, 2, 2 } }; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderVaultDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("PULL", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 0, 1, 2000, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 2000).addPos(1, 0, 0, 4000));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("PULL", new BusAnimationSequence().setPos(0, 0, 1).addPos(0, 0, 1, 4000).addPos(0, 0, 0, 2000, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().setPos(1, 0, 0).addPos(0, 0, 0, 4000));
            return null;
        }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (!open || y == 0) return new AABB(0, 0, 0, 1, 1, 1);
            return super.getBlockBound(x, y, z, open, forCollision);
        }

        private static void playDoorSound(TileEntityDoorGeneric door, SoundEvent sound, float volume, float pitch) {
            if (door.getLevel() == null || sound == null) return;
            door.getLevel().playSound(null, door.getBlockPos(), sound,
                    net.minecraft.sounds.SoundSource.BLOCKS, volume, pitch);
        }

        private final Consumer<TileEntityDoorGeneric> onUpdate = (door) -> {
            if (door.getLevel() == null || door.getLevel().isClientSide) return;

            if (door.state == TileEntityDoorGeneric.STATE_OPENING) {
                if (door.openTicks == 0) {
                    playDoorSound(door, ModSounds.BLOCK_VAULT_SCRAPE_NEW.get(), 1.0F, 1.0F);
                }
                for (int i = 45; i <= 115; i += 10) {
                    if (door.openTicks == i) {
                        playDoorSound(door, ModSounds.BLOCK_VAULT_THUD_NEW.get(), 1.0F, 1.0F);
                    }
                }
            } else if (door.state == TileEntityDoorGeneric.STATE_CLOSING) {
                if (door.openTicks == 30) {
                    playDoorSound(door, ModSounds.BLOCK_VAULT_SCRAPE_NEW.get(), 1.0F, 1.0F);
                }
                for (int i = 45; i <= 115; i += 10) {
                    if (door.openTicks == i) {
                        playDoorSound(door, ModSounds.BLOCK_VAULT_THUD_NEW.get(), 1.0F, 1.0F);
                    }
                }
            }
        };

        @Override
        public Consumer<TileEntityDoorGeneric> onDoorUpdate() { return onUpdate; }
    };

    public static final DoorDecl SLIDING_SEAL_DOOR = new DoorDecl() {

        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_SLIDING_SEAL_STOP.get(); }
        @Override public SoundEvent getOpenSoundStart() { return ModSounds.DOOR_SLIDING_SEAL_OPEN.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderSealDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 1, 0, this.timeToOpen() * 50));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, this.timeToOpen() * 50));
            return null;
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_seal_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_seal_door_clean_tex,
                    com.hbm.main.HBMResourceManager.pheo_seal_door_trefoil_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 3; }

        @Override public int timeToOpen() { return 20; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (forCollision && open) return new AABB(0, 0, 0, 0, 0, 0);
            return new AABB(0, 0, 1 - 0.25, 1, 1, 1);
        }

        @Override public int[][] getDoorOpenRanges() { return new int[][] { { 0, 0, 0, 1, 2, 2 } }; }
        @Override public int[] getDimensions() { return new int[] { 1, 0, 0, 0, 0, 0 }; }
    };

    public static final DoorDecl SECURE_ACCESS_DOOR = new DoorDecl() {

        @Override public SoundEvent getCloseSoundLoop() { return ModSounds.DOOR_GARAGE_MOVE.get(); }
        @Override public SoundEvent getCloseSoundEnd() { return ModSounds.DOOR_GARAGE_STOP.get(); }
        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_GARAGE_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_GARAGE_MOVE.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderSecureDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 1, 0, this.timeToOpen() * 50));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, this.timeToOpen() * 50));
            return null;
        }

        @Override public int timeToOpen() { return 120; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { -2, 1, 0, 4, 5, 1 } }; }
        @Override public int[] getDimensions() { return new int[] { 4, 0, 0, 0, 2, 2 }; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (!open) {
                if (y > 0) return new AABB(0, 0, 0.375, 1, 1, 0.625);
                return new AABB(0, 0, 0, 1, 1, 1);
            }
            if (y == 1) return new AABB(0, 0, 0, 1, forCollision ? 0 : 0.0625, 1);
            else if (y == 4) return new AABB(0, 0.5, 0.15, 1, 1, 0.85);
            else if (y == 0) return new AABB(0, 0, 0, 1, 1, 1);
            return super.getBlockBound(x, y, z, open, forCollision);
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_secure_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_secure_door_grey_tex,
                    com.hbm.main.HBMResourceManager.pheo_secure_door_black_tex,
                    com.hbm.main.HBMResourceManager.pheo_secure_door_yellow_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 4; }
    };

    public static final DoorDecl ROUND_AIRLOCK_DOOR = new DoorDecl() {

        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_GARAGE_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_GARAGE_MOVE.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderAirlockDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 1, 0, this.timeToOpen() * 50));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, this.timeToOpen() * 50));
            return null;
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_airlock_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_airlock_door_clean_tex,
                    com.hbm.main.HBMResourceManager.pheo_airlock_door_green_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 3; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (!open) return super.getBlockBound(x, y, z, open, forCollision);
            if (z == 1) return new AABB(0.4, 0, 0, 1, 1, 1);
            else if (z == -2) return new AABB(0, 0, 0, 0.6, 1, 1);
            else if (y == 3) return new AABB(0, 0.5, 0, 1, 1, 1);
            else if (y == 0) return new AABB(0, 0, 0, 1, forCollision ? 0 : 0.0625, 1);
            return super.getBlockBound(x, y, z, open, forCollision);
        }

        @Override public int timeToOpen() { return 60; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { 0, 0, 0, -2, 4, 2 }, { 0, 0, 0, 3, 4, 2 } }; }
        @Override public int[] getDimensions() { return new int[] { 3, 0, 0, 0, 2, 1 }; }
    };

    public static final DoorDecl QE_SLIDING = new DoorDecl() {

        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_QE_SLIDING_OPENED.get(); }
        @Override public SoundEvent getCloseSoundEnd() { return ModSounds.DOOR_QE_SLIDING_SHUT.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_QE_SLIDING_OPENING.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public int timeToOpen() { return 10; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderSlidingDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 1, 0, this.timeToOpen() * 50));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, this.timeToOpen() * 50));
            return null;
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_sliding_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_sliding_door_black_tex,
                    com.hbm.main.HBMResourceManager.pheo_sliding_door_grid_tex,
                    com.hbm.main.HBMResourceManager.pheo_sliding_door_plate_tex,
                    com.hbm.main.HBMResourceManager.pheo_sliding_door_white_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 5; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (forCollision && open) {
                // Открытая — створки «уехали», проём свободен
                return new AABB(0, 0, 0, 0, 0, 0);
            }
            // Закрытая (или запрос визуальной формы) — тонкая полоска
            return new AABB(0, 0, 1 - 0.1875, 1, 1, 1);
        }

        @Override public int[][] getDoorOpenRanges() { return new int[][] { { 0, 0, 0, 2, 2, 2 } }; }
        @Override public int[] getDimensions() { return new int[] { 1, 0, 0, 0, 1, 0 }; }
    };

    public static final DoorDecl QE_CONTAINMENT = new DoorDecl() {

        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_WGH_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_WGH_START.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderContainmentDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 1, 0, this.timeToOpen() * 50));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, this.timeToOpen() * 50));
            return null;
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_containment_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_containment_door_trefoil_tex,
                    com.hbm.main.HBMResourceManager.pheo_containment_door_trefoil_yellow_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 3; }

        @Override public int timeToOpen() { return 160; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { -1, 0, 0, 3, 3, 1 } }; }
        @Override public int[] getDimensions() { return new int[] { 2, 0, 0, 0, 1, 1 }; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (!open) return new AABB(0, 0, 0.5, 1, 1, 1);
            if (y > 1) return new AABB(0, 0.25, 0.5, 1, 1, 1);
            else if (y == 0) return new AABB(0, 0, 0.5, 1, forCollision ? 0 : 0.125, 1);
            return super.getBlockBound(x, y, z, open, forCollision);
        }
    };

    public static final DoorDecl WATER_DOOR = new DoorDecl() {

        @Override public boolean neverDemandRedstone() { return true; }
        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_WGH_BIG_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_WGH_BIG_START.get(); }
        @Override public SoundEvent getOpenSoundStart() { return ModSounds.DOOR_LEVER.get(); }
        @Override public SoundEvent getCloseSoundStart() { return null; }
        @Override public SoundEvent getCloseSoundEnd() { return ModSounds.DOOR_LEVER.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderWaterDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 0, 0, 1500).addPos(0, 1, 0, 1500, IType.SIN_FULL))
                    .addBus("BOLT", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 0, 1, 1500, IType.SIN_FULL));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, 1500, IType.SIN_FULL))
                    .addBus("BOLT", new BusAnimationSequence().setPos(0, 0, 1).addPos(0, 0, 1, 1200).addPos(0, 0, 0, 1500, IType.SIN_FULL));
            return null;
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_water_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_water_door_clean_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 2; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (!open) return new AABB(0, 0, 0.75, 1, 1, 1);
            else if (y > 1) return new AABB(0, 0.85, 0.75, 1, 1, 1);
            else if (y == 0) return new AABB(0, 0, 0.75, 1, forCollision ? 0 : 0.15, 1);
            return super.getBlockBound(x, y, z, open, forCollision);
        }

        @Override public int timeToOpen() { return 60; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { 1, 0, 0, -3, 3, 2 } }; }
        @Override public float getDoorRangeOpenTime(int ticks, int idx) { return getNormTime(ticks, 35, 40); }
        @Override public int[] getDimensions() { return new int[] { 2, 0, 0, 0, 1, 1 }; }
    };

    public static final DoorDecl SILO_HATCH = new DoorDecl() {

        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_WGH_BIG_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_WGH_BIG_START.get(); }
        @Override public SoundEvent getOpenSoundStart() { return null; }
        @Override public SoundEvent getCloseSoundStart() { return null; }
        @Override public SoundEvent getCloseSoundEnd() { return ModSounds.DOOR_WGH_BIG_STOP.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public boolean remoteControllable() { return true; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderSiloHatch.INSTANCE;
        }
        @Override
        public void getTranslation(String partName, float openTicks, boolean child, float[] trans) {
            if ("Hatch".equals(partName)) {
                set(trans, 0, 0.25F * (float) com.hbm.util.Library.smoothstep(getNormTime(openTicks, 0, 10), 0, 1), 0);
            } else {
                set(trans, 0, 0, 0);
            }
        }

        @Override
        public void getOrigin(String partName, float[] orig) {
            if ("Hatch".equals(partName)) {
                set(orig, 0F, 0.875F, -1.875F);
                return;
            }
            set(orig, 0, 0, 0);
            super.getOrigin(partName, orig);
        }

        @Override
        public void getRotation(String partName, float openTicks, float[] rot) {
            if ("Hatch".equals(partName)) {
                set(rot, (float) com.hbm.util.Library.smoothstep(getNormTime(openTicks, 20, 100), 0, 1) * -240, 0, 0);
                return;
            }
            super.getRotation(partName, openTicks, rot);
        }

        @Override public int timeToOpen() { return 60; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { 1, 0, 1, -3, 3, 0 }, { 0, 0, 1, -3, 3, 0 }, { -1, 0, 1, -3, 3, 0 } }; }
        @Override public float getDoorRangeOpenTime(int ticks, int idx) { return getNormTime(ticks, 20, 20); }

        @Override public int getBlockOffset() { return 2; }
        @Override public int[] getDimensions() { return new int[] { 0, 0, 2, 2, 2, 2 }; }

        // Текстура и модель сило-люка — добавляются через HBMResourceManager, если есть.
        // Скажешь — раскомментируем/заменим.
    };

    public static final DoorDecl SILO_HATCH_LARGE = new DoorDecl() {

        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_WGH_BIG_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_WGH_BIG_START.get(); }
        @Override public SoundEvent getOpenSoundStart() { return null; }
        @Override public SoundEvent getCloseSoundStart() { return null; }
        @Override public SoundEvent getCloseSoundEnd() { return ModSounds.DOOR_WGH_BIG_STOP.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public boolean remoteControllable() { return true; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderSiloHatchLarge.INSTANCE;
        }
        @Override
        public void getTranslation(String partName, float openTicks, boolean child, float[] trans) {
            if ("Hatch".equals(partName)) {
                set(trans, 0, 0.25F * (float) com.hbm.util.Library.smoothstep(getNormTime(openTicks, 0, 10), 0, 1), 0);
            } else {
                set(trans, 0, 0, 0);
            }
        }

        @Override
        public void getOrigin(String partName, float[] orig) {
            if ("Hatch".equals(partName)) {
                set(orig, 0F, 0.875F, -2.875F);
                return;
            }
            set(orig, 0, 0, 0);
            super.getOrigin(partName, orig);
        }

        @Override
        public void getRotation(String partName, float openTicks, float[] rot) {
            if ("Hatch".equals(partName)) {
                set(rot, (float) com.hbm.util.Library.smoothstep(getNormTime(openTicks, 20, 100), 0, 1) * -240, 0, 0);
                return;
            }
            super.getRotation(partName, openTicks, rot);
        }

        @Override public int timeToOpen() { return 60; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] {
                { 2, 0, 1, -3, 3, 0 },
                { 1, 0, 2, -5, 3, 0 },
                { 0, 0, 2, -5, 3, 0 },
                { -1, 0, 2, -5, 3, 0 },
                { -2, 0, 1, -3, 3, 0 }
        }; }
        @Override public float getDoorRangeOpenTime(int ticks, int idx) { return getNormTime(ticks, 20, 20); }

        @Override public int getBlockOffset() { return 3; }
        @Override public int[] getDimensions() { return new int[] { 0, 0, 3, 3, 3, 3 }; }
    };

    public static final DoorDecl LARGE_VEHICLE_DOOR = new DoorDecl() {

        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_GARAGE_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_GARAGE_MOVE.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderVehicleDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 1, 0, this.timeToOpen() * 50));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("DOOR", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, this.timeToOpen() * 50));
            return null;
        }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (!open) return super.getBlockBound(x, y, z, open, forCollision);
            if (z == 3) return new AABB(0.4, 0, 0, 1, 1, 1);
            else if (z == -3) return new AABB(0, 0, 0, 0.6, 1, 1);
            else if (y == 0) return new AABB(0, 0, 0, 1, forCollision ? 0 : 0.0625, 1);
            return super.getBlockBound(x, y, z, open, forCollision);
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_vehicle_door_tex,
                    com.hbm.main.HBMResourceManager.pheo_vehicle_door_black_tex,
                    com.hbm.main.HBMResourceManager.pheo_vehicle_door_blue_tex,
                    com.hbm.main.HBMResourceManager.pheo_vehicle_door_red_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 4; }

        @Override public int timeToOpen() { return 60; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { 0, 0, 0, -4, 6, 2 }, { 0, 0, 0, 4, 6, 2 } }; }
        @Override public int[] getDimensions() { return new int[] { 5, 0, 0, 0, 3, 3 }; }
    };

    public static final DoorDecl CARGO_DOOR = new DoorDecl() {

        @Override public SoundEvent getCloseSoundLoop() { return ModSounds.DOOR_GARAGE_MOVE.get(); }
        @Override public SoundEvent getCloseSoundEnd() { return ModSounds.DOOR_GARAGE_STOP.get(); }
        @Override public SoundEvent getOpenSoundEnd() { return ModSounds.DOOR_GARAGE_STOP.get(); }
        @Override public SoundEvent getOpenSoundLoop() { return ModSounds.DOOR_GARAGE_MOVE.get(); }
        @Override public float getSoundVolume() { return 2; }
        @Override public IRenderDoors getSEDNARenderer() {
            return com.hbm.render.tileentity.door.RenderCargoDoor.INSTANCE;
        }
        @Override
        public BusAnimation getBusAnimation(byte state, byte skinIndex) {
            int half = this.timeToOpen() * 25;
            if (state == TileEntityDoorGeneric.STATE_OPENING) return new BusAnimation()
                    .addBus("BOT", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 1, 0, half * 2))
                    .addBus("TOP", new BusAnimationSequence().setPos(0, 0, 0).addPos(0, 0, 0, half).addPos(0, 1, 0, half));
            if (state == TileEntityDoorGeneric.STATE_CLOSING) return new BusAnimation()
                    .addBus("BOT", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 0, 0, half * 2))
                    .addBus("TOP", new BusAnimationSequence().setPos(0, 1, 0).addPos(0, 1, 0, half).addPos(0, 0, 0, half));
            return null;
        }

        @Override public int timeToOpen() { return 60; }
        @Override public int[][] getDoorOpenRanges() { return new int[][] { { -1, -1, 0, 3, 3, 1 } }; }
        @Override public int[] getDimensions() { return new int[] { 2, 0, 0, 0, 1, 1 }; }

        @Override
        public AABB getBlockBound(int x, int y, int z, boolean open, boolean forCollision) {
            if (!open) return new AABB(0, 0, 0.375, 1, 1, 0.625);
            if (y > 1) return new AABB(0, 0.25, 0.375, 1, 1, 0.625);
            else if (y == 0) return new AABB(0, 0, 0.375, 1, forCollision ? 0 : 0.125, 0.625);
            return super.getBlockBound(x, y, z, open, forCollision);
        }

        private ResourceLocation[] skins;

        @Override public ResourceLocation[] getSEDNASkins() {
            if (skins == null) skins = new ResourceLocation[] {
                    com.hbm.main.HBMResourceManager.pheo_cargo_door_tex
            };
            return skins;
        }

        @Override public int getSkinCount() { return 1; }
    };
}