package com.hbm.create.blockentity;

import net.minecraft.nbt.CompoundTag;

public class SequencedGearshiftBlockEntity {

    public record SequenceContext(SequencerInstructions instruction, double relativeValue) {

        public static SequenceContext fromGearshift(SequencerInstructions instruction, double kineticSpeed,
                                                    int absoluteValue) {
            return instruction.needsPropagation()
                    ? new SequenceContext(instruction, kineticSpeed == 0 ? 0 : absoluteValue / kineticSpeed)
                    : null;
        }

        public double getEffectiveValue(double speedAtTarget) {
            return Math.abs(relativeValue * speedAtTarget);
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            NBTHelper.writeEnum(nbt, "Mode", instruction);
            nbt.putDouble("Value", relativeValue);
            return nbt;
        }

        public static SequenceContext fromNBT(CompoundTag nbt) {
            if (nbt.isEmpty())
                return null;
            return new SequenceContext(NBTHelper.readEnum(nbt, "Mode", SequencerInstructions.class),
                    nbt.getDouble("Value"));
        }

    }
}
