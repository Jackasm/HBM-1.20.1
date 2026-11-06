package com.hbm.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.generic.BlockDoorGeneric;
import com.hbm.render.anim.HbmAnimations.Animation;
import com.hbm.sound.AudioWrapper;

import com.hbm.sound.SoundHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TileEntityDoorGeneric extends TileEntityLockableBase {

    public static final byte STATE_CLOSED  = 0;
    public static final byte STATE_OPEN    = 1;
    public static final byte STATE_CLOSING = 2;
    public static final byte STATE_OPENING = 3;

    public byte state = STATE_CLOSED;
    protected DoorDecl doorType;
    public int openTicks = 0;
    public long animStartTime = 0;
    public int redstonePower;
    public boolean shouldUseBB = false;
    private byte skinIndex = 0;

    public final Set<BlockPos> activatedBlocks = new HashSet<>(4);

    private AudioWrapper audio;
    private AudioWrapper audio2;

    public Animation currentAnimation;

    public TileEntityDoorGeneric(BlockPos pos, BlockState state) {
        super(com.hbm.tileentity.ModTileEntity.DOOR_GENERIC.get(), pos, state);
    }

    // ==================== TICK ====================

    public void tick() {
        if (level == null) return;

        if (getDoorType() == null) {
            this.setRemoved();
            return;
        }

        if (getDoorType().onDoorUpdate() != null) {
            Objects.requireNonNull(getDoorType().onDoorUpdate()).accept(this);
        }

        if (state == STATE_OPENING) {
            openTicks++;
            if (openTicks >= getDoorType().timeToOpen()) openTicks = getDoorType().timeToOpen();
        } else if (state == STATE_CLOSING) {
            openTicks--;
            if (openTicks <= 0) openTicks = 0;
        }

        if (!level.isClientSide) {
            int[][] ranges = getDoorType().getDoorOpenRanges();
            Direction dir = getFacing();

            if (state == STATE_OPENING) {
                processRanges(ranges, dir, true);
            } else if (state == STATE_CLOSING) {
                processRanges(ranges, dir, false);
            }

            if (state == STATE_OPENING && openTicks == getDoorType().timeToOpen()) state = STATE_OPEN;
            if (state == STATE_CLOSING && openTicks == 0) state = STATE_CLOSED;

            networkPackNT(100);

            if (redstonePower == -1 && state == STATE_OPEN) {
                tryToggle(-1);
            } else if (redstonePower > 0 && state == STATE_CLOSED) {
                tryToggle(-1);
            }
            if (redstonePower == -1) redstonePower = 0;
        }
    }

    /** Порт цикла по ranges из 1.7.10 (makeExtra/removeExtra). */
    private void processRanges(int[][] ranges, Direction dir, boolean opening) {
        BlockPos corePos = worldPosition;

        for (int i = 0; i < ranges.length; i++) {
            int[] range = ranges[i];
            BlockPos startPos = new BlockPos(range[0], range[1], range[2]);
            float time = getDoorType().getDoorRangeOpenTime(openTicks, i);

            int maxJ = Math.abs(range[3]);
            int startJ = opening ? 0 : maxJ - 1;
            int endJ = opening ? maxJ : -1;
            int stepJ = opening ? 1 : -1;

            for (int j = startJ; j != endJ; j += stepJ) {
                if (maxJ > 1) {
                    if (opening && (float) j / (maxJ - 1) > time) break;
                    if (!opening && (float) j / (maxJ - 1) < time) break;
                }

                for (int k = 0; k < range[4]; k++) {
                    BlockPos add;
                    switch (range[5]) {
                        case 0 -> add = new BlockPos(0, k, (int) Math.signum(range[3]) * j);
                        case 1 -> add = new BlockPos(k, (int) Math.signum(range[3]) * j, 0);
                        case 2 -> add = new BlockPos((int) Math.signum(range[3]) * j, k, 0);
                        default -> add = BlockPos.ZERO;
                    }

                    BlockPos finalPos = rotateAdd(startPos, add, dir, corePos);

                    if (finalPos.equals(corePos)) {
                        this.shouldUseBB = false;
                    } else {
                        if (Objects.requireNonNull(level).getBlockState(worldPosition).getBlock() instanceof BlockDummyable dummy) {
                            if (opening) {
                                dummy.makeExtra(level, finalPos);
                            } else {
                                dummy.removeExtra(level, finalPos);
                            }
                        }
                    }
                }
            }
        }
    }

    /** Аналог `startPos.add(add).rotate(rotation).add(pos)`. */
    private BlockPos rotateAdd(BlockPos startPos, BlockPos add, Direction dir, BlockPos corePos) {
        BlockPos local = startPos.offset(add);
        BlockPos rotated = rotatePos(local, dir);
        return corePos.offset(rotated);
    }

    /** Вращение локальной координаты в зависимости от FACING двери. */
    private BlockPos rotatePos(BlockPos pos, Direction dir) {
        // В 1.7.10 использовался `Rotation.getBlockRotation(dir)`
        // и `add(Rotation.CLOCKWISE_180)` для POS_X/NEG_X.
        // Здесь — упрощённая версия: NORTH = без вращения, остальные по осям.
        return switch (dir) {
            case NORTH -> pos;
            case SOUTH -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
            case EAST  -> new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
            case WEST  -> new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
            default -> pos;
        };
    }

    private Direction getFacing() {
        BlockState state = getBlockState();
        if (state.hasProperty(BlockDummyable.FACING)) {
            return state.getValue(BlockDummyable.FACING);
        }
        return Direction.NORTH;
    }

    // ==================== SERIALIZE ====================

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeByte(state);
        buf.writeByte(skinIndex);
        buf.writeBoolean(shouldUseBB);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        handleNewState(buf.readByte());
        skinIndex = buf.readByte();
        shouldUseBB = buf.readBoolean();
    }

    // ==================== DOOR TYPE ====================

    public DoorDecl getDoorType() {
        if (this.doorType == null && getBlockState().getBlock() instanceof BlockDoorGeneric door) {
            this.doorType = door.type;
        }
        return this.doorType;
    }

    // ==================== TOGGLE ====================

    public boolean tryToggle(@Nullable Player player) {
        if (this.isLocked() && player == null) return false;
        if (state == STATE_CLOSED && redstonePower > 0) return false;

        if (this.state == STATE_CLOSED) {
            if (level != null && !level.isClientSide && canAccess(player)) this.state = STATE_OPENING;
            return true;
        } else if (this.state == STATE_OPEN) {
            if (level != null && !level.isClientSide && canAccess(player)) this.state = STATE_CLOSING;
            return true;
        }
        return false;
    }

    public boolean tryToggle(int passcode) {
        if (this.isLocked() && passcode != this.getPins()) return false;

        if (this.state == STATE_CLOSED) {
            if (level != null && !level.isClientSide) this.state = STATE_OPENING;
            return true;
        } else if (this.state == STATE_OPEN) {
            if (level != null && !level.isClientSide) this.state = STATE_CLOSING;
            return true;
        }
        return false;
    }

    // ==================== STATE / SOUNDS (CLIENT) ====================

    @OnlyIn(Dist.CLIENT)
    public void handleNewState(byte newState) {
        if (this.state == newState) return;

        DoorDecl doorType = getDoorType();
        if (doorType == null) { this.state = newState; return; }

        if (this.state == STATE_CLOSED && newState == STATE_OPENING) {
            stopAudio(audio, true);
            if (doorType.getOpenSoundLoop() != null) {
                audio = createLoopedSound(doorType.getOpenSoundLoop());
                if (audio != null) audio.startSound();
            }
            if (doorType.getOpenSoundStart() != null) {
                playSoundAt(doorType.getOpenSoundStart());
            }
            if (doorType.getSoundLoop2() != null) {
                stopAudio(audio2, false);
                audio2 = createLoopedSound(doorType.getSoundLoop2());
                if (audio2 != null) audio2.startSound();
            }
        }

        if (this.state == STATE_OPEN && newState == STATE_CLOSING) {
            stopAudio(audio, false);
            if (doorType.getCloseSoundLoop() != null) {
                audio = createLoopedSound(doorType.getCloseSoundLoop());
                if (audio != null) audio.startSound();
            }
            if (doorType.getCloseSoundStart() != null) {
                playSoundAt(doorType.getCloseSoundStart());
            }
            if (doorType.getSoundLoop2() != null) {
                stopAudio(audio2, false);
                audio2 = createLoopedSound(doorType.getSoundLoop2());
                if (audio2 != null) audio2.startSound();
            }
        }

        if (newState == STATE_OPEN || newState == STATE_CLOSED) {
            stopAudio(audio, false); audio = null;
            stopAudio(audio2, false); audio2 = null;
        }

        if (this.state == STATE_OPENING && newState == STATE_OPEN) {
            if (doorType.getOpenSoundEnd() != null) playSoundAt(doorType.getOpenSoundEnd());
        }
        if (this.state == STATE_CLOSING && newState == STATE_CLOSED) {
            if (doorType.getCloseSoundEnd() != null) playSoundAt(doorType.getCloseSoundEnd());
        }

        this.state = newState;

        if (newState == STATE_OPENING || newState == STATE_CLOSING) {
            animStartTime = System.currentTimeMillis();
            currentAnimation = doorType.getSEDNAAnim(newState, this.skinIndex);
        }
    }

    private void stopAudio(@Nullable AudioWrapper wrapper, boolean keepAlive) {
        if (wrapper != null) {
            wrapper.stopSound();
            if (keepAlive) wrapper.setKeepAlive(0);
        }
    }

    @Nullable
    private AudioWrapper createLoopedSound(SoundEvent sound) {
        if (level == null || sound == null) return null;
        return SoundHelper.createLoopedSound(
                sound,
                worldPosition.getX() + 0.5F,
                worldPosition.getY() + 0.5F,
                worldPosition.getZ() + 0.5F,
                getVolume(),
                10F,
                1F,
                10
        );
    }

    private void playSoundAt(SoundEvent sound) {
        if (level == null || sound == null) return;
        SoundHelper.playSound(level, worldPosition, sound, SoundSource.BLOCKS, getVolume(), 1F);
    }

    public float getVolume() {
        return doorType != null ? getVolume(doorType.getSoundVolume()) : 1F;
    }

    // ==================== SKIN ====================

    public int getSkinIndex() {
        return skinIndex;
    }

    public boolean cycleSkinIndex() {
        if (getDoorType() == null || !getDoorType().hasSkins()) return false;
        this.skinIndex = (byte) ((this.skinIndex + 1) % getDoorType().getSkinCount());
        setChanged();
        return true;
    }

    public void open() {
        if (state == STATE_CLOSED) state = STATE_OPENING;
    }

    public void close() {
        if (state == STATE_OPEN) state = STATE_CLOSING;
    }

    // ==================== RENDER ====================

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 16, worldPosition.getY() - 16, worldPosition.getZ() - 16,
                worldPosition.getX() + 16, worldPosition.getY() + 16, worldPosition.getZ() + 16
        );
    }

    public void setSkinIndex(int index) {
        this.skinIndex = (byte) (index & 0xFF);
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte("state", state);
        tag.putInt("openTicks", openTicks);
        tag.putLong("animStartTime", animStartTime);
        tag.putInt("redstoned", redstonePower);
        tag.putBoolean("shouldUseBB", shouldUseBB);
        if (getDoorType() != null && getDoorType().hasSkins()) {
            tag.putByte("skin", skinIndex);
        }
        ListTag list = new ListTag();
        for (BlockPos p : activatedBlocks) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", p.getX());
            entry.putInt("y", p.getY());
            entry.putInt("z", p.getZ());
            list.add(entry);
        }
        tag.put("activatedBlocks", list);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.state = tag.getByte("state");
        this.openTicks = tag.getInt("openTicks");
        this.animStartTime = tag.getLong("animStartTime");
        this.redstonePower = tag.getInt("redstoned");
        this.shouldUseBB = tag.getBoolean("shouldUseBB");
        this.skinIndex = tag.getByte("skin");

        this.activatedBlocks.clear();
        ListTag list = tag.getList("activatedBlocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            this.activatedBlocks.add(new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z")));
        }
    }

    // ==================== REDSTONE ====================

    public void updateRedstonePower(BlockPos pos) {
        if (level == null) return;
        boolean powered = level.hasNeighborSignal(pos);
        boolean contained = activatedBlocks.contains(pos);

        if (!contained && powered) {
            activatedBlocks.add(pos);
            if (redstonePower == -1) redstonePower = 0;
            redstonePower++;
        } else if (contained && !powered) {
            activatedBlocks.remove(pos);
            redstonePower--;
            if (redstonePower == 0) redstonePower = -1;
        }
    }

    // ==================== CAPS ====================

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (audio != null) {
            audio.stopSound();
            audio = null;
        }
        if (audio2 != null) {
            audio2.stopSound();
            audio2 = null;
        }
    }
}