package com.hbm.main;

import com.hbm.explosion.vanillant.standard.ExplosionEffectTiny;
import com.hbm.particle.*;
import com.hbm.particle.helper.ParticleCreators;
import com.hbm.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;


@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    private static HashMap<Integer, Long> vanished = new HashMap<>();
    // Нужно для создания частиц:
    private static final ParticleCreators PARTICLE_CREATORS_LOADER = new ParticleCreators();



    @Override
    public void effectNT(CompoundTag nbt) {
        if(nbt == null) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel world = mc.level;
        LocalPlayer player = mc.player;
        TextureManager textureManager = mc.getTextureManager();
        RandomSource rand = Objects.requireNonNull(world).random;
        ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
        int particleSetting = particleStatus.getId();

        String type = nbt.getString("type");
        double posX = nbt.getDouble("posX");
        double posY = nbt.getDouble("posY");
        double posZ = nbt.getDouble("posZ");

        if(ParticleCreators.particleCreators.containsKey(type)) {
            ParticleCreators.particleCreators.get(type).makeParticle(world, player, textureManager, rand, posX, posY, posZ, nbt);
            return;
        }

        // Обработка различных типов эффектов
        switch(type) {
            case "plasmablast":
                spawnPlasmaBlast(nbt, posX, posY, posZ);
                break;
            case "explosion":
                spawnExplosion(nbt, posX, posY, posZ);
                break;
            case "explosion_tiny":
                ExplosionEffectTiny.spawnExplosionTinyEffect(nbt, posX, posY, posZ);
                break;
            case "vanillaExt":
                handleVanillaExt(nbt, world, posX, posY, posZ, rand, mc);
                break;
            case "giblets":
                handleGibletsEffect(nbt, world, posX, posY, posZ, rand);
                break;
            case "tower":
                handleTowerEffect(nbt, world, posX, posY, posZ, rand, particleSetting, mc);
                break;
            case "smoke":
                handleSmokeEffect(nbt, world, posX, posY, posZ, rand, mc);
                break;
            case "tinytot":
                handleTinyTotEffect(nbt, world, posX, posY, posZ, rand, mc);
                break;
            case "vomit":
                handleVomitEffect(nbt, world, posX, posY, posZ, rand, particleSetting, mc);
                break;
            case "muke":
                handleMukeEffect(nbt, world, posX, posY, posZ, mc);
                break;
            case "radFog":
                handleRadFogEffect(nbt, world, posX, posY, posZ, mc);
                break;
            case "foundry":
                handleFoundryEffect(nbt, world, posX, posY, posZ, mc);
                break;
            case "sweat":
                handleSweatEffect(nbt, world, posX, posY, posZ, rand, mc);
                break;
            case "vanillaburst":
                handleVanillaBurst(nbt, world, posX, posY, posZ, rand, mc);
                break;
            case "rubble_burst":
                handleRubbleBurst(nbt, world, posX, posY, posZ, rand, mc);
                break;
            case "bf":
                handleBF(nbt, world, posX, posY, posZ, mc);
            case "ufo":
                handleUfoEffect(nbt, world, posX, posY, posZ, rand, mc);
                break;
            case "diesel_suit":
                handleDieselSuitEffect(nbt, world, posX, posY, posZ, rand, mc, player);
                break;
            case "jetpack":
                handleJetpackEffect(nbt, world, posX, posY, posZ, rand, mc, player);
                break;
            case "launchSmoke":
                spawnLaunchSmoke(world, nbt, posX, posY, posZ);
                break;
            case "missileContrail":
                spawnMissileContrail(world, nbt, posX, posY, posZ);
                break;
            case "exhaust":
                handleExhaustEffect(world, nbt, posX, posY, posZ);
                break;

            default:
                // Если тип не распознан, можно создать базовые частицы
                needsToRealise(nbt, posX, posY, posZ);
        }
    }

    private void handleExhaustEffect(ClientLevel world, CompoundTag nbt, double x, double y, double z) {
        if (world == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Проверяем расстояние до игрока
        double dist = mc.player.distanceToSqr(x, y, z);
        if (dist > 350 * 350) return;

        String mode = nbt.getString("mode");
        int count = Math.max(1, nbt.getInt("count"));
        double width = nbt.getDouble("width");

        if ("soyuz".equals(mode)) {
            for (int i = 0; i < count; i++) {
                ParticleRocketFlame fx = new ParticleRocketFlame(
                        world,
                        x + world.random.nextGaussian() * width,
                        y,
                        z + world.random.nextGaussian() * width,
                        0, -0.75 + world.random.nextDouble() * 0.5, 0
                );
                mc.particleEngine.add(fx);
            }
        }

        if ("meteor".equals(mode)) {
            for (int i = 0; i < count; i++) {
                ParticleRocketFlame fx = new ParticleRocketFlame(
                        world,
                        x + world.random.nextGaussian() * width,
                        y + world.random.nextGaussian() * width,
                        z + world.random.nextGaussian() * width,
                        0, 0, 0
                );
                mc.particleEngine.add(fx);
            }
        }
    }

    private void spawnMissileContrail(ClientLevel world, CompoundTag nbt, double x, double y, double z) {
        if (world == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Проверяем расстояние до игрока (как в оригинале)
        double dist = mc.player.distanceToSqr(x, y, z);
        if (dist > 350 * 350) return;

        float scale = nbt.contains("scale") ? nbt.getFloat("scale") : 1.0F;
        double mX = nbt.getDouble("moX");
        double mY = nbt.getDouble("moY");
        double mZ = nbt.getDouble("moZ");
        int maxAge = nbt.contains("maxAge") ? nbt.getInt("maxAge") : 300;

        // Создаём частицу через систему частиц
        Particle particle = new ParticleRocketFlame(world, x, y, z, mX, mY, mZ)
                .setScale(scale)
                .setMaxAge(maxAge);
        mc.particleEngine.add(particle);
    }

    private void spawnLaunchSmoke(ClientLevel world, CompoundTag nbt, double x, double y, double z) {
        double moX = nbt.getDouble("moX");
        double moY = nbt.getDouble("moY");
        double moZ = nbt.getDouble("moZ");

        world.addParticle(
                ModParticles.SMOKE_PLUME.get(),
                x, y, z,
                moX, moY, moZ
        );
    }

    private void handleJetpackEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                     RandomSource rand, Minecraft mc, LocalPlayer player) {
        int particleSetting = Minecraft.getInstance().options.particles().get().getId();
        if (particleSetting == 2) return;

        int entityId = nbt.getInt("player");
        Entity ent = world.getEntity(entityId);

        if (ent instanceof Player p) {
            Vec3 vec = new Vec3(0, 0, -0.25);
            Vec3 offset = new Vec3(0.125, 0, 0);
            float angle = (float) -Math.toRadians(p.getYRot() - (p.getYRot() - p.yBodyRot));

            vec = vec.yRot(angle);
            offset = offset.yRot(angle);

            double ix = p.getX() + vec.x;
            double iy = p.getY() + p.getEyeHeight() - 1;
            double iz = p.getZ() + vec.z;
            double ox = offset.x;
            double oz = offset.z;

            double moX = 0;
            double moY = 0;
            double moZ = 0;

            int mode = nbt.getInt("mode");

            if (mode == 0) {
                moY -= 0.2;
            }

            if (mode == 1) {
                Vec3 look = p.getLookAngle();
                moX -= look.x * 0.1D;
                moY -= look.y * 0.1D;
                moZ -= look.z * 0.1D;
            }

            if (particleSetting == 0) {

                Vec3 pos = new Vec3(ix, iy, iz);
                Vec3 thrust = new Vec3(moX, moY, moZ).normalize();
                Vec3 target = pos.add(thrust.scale(10));

                var clipContext = new net.minecraft.world.level.ClipContext(
                        pos,
                        target,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE,
                        null
                );
                var hitResult = world.clip(clipContext);
                if (hitResult != null && hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                    var blockPos = hitResult.getBlockPos();
                    var blockState = world.getBlockState(blockPos);
                    Block b = blockState.getBlock();

                    Vec3 delta = new Vec3(ix - hitResult.getLocation().x,
                            iy - hitResult.getLocation().y,
                            iz - hitResult.getLocation().z);
                    double len = delta.length();
                    Vec3 vel = new Vec3(0.75 - len * 0.075, 0, 0);

                    for (int i = 0; i < (int)(10 - len); i++) {
                        vel = vel.yRot(rand.nextFloat() * (float) Math.PI * 2F);

                        var stack = new ItemStack(b);
                        var particle = new net.minecraft.core.particles.ItemParticleOption(
                                net.minecraft.core.particles.ParticleTypes.ITEM, stack
                        );
                        mc.particleEngine.createParticle(
                                particle,
                                hitResult.getLocation().x,
                                hitResult.getLocation().y + 0.1,
                                hitResult.getLocation().z,
                                vel.x, 0.1, vel.z
                        );
                    }
                }
            }

            double mX2 = clamp(p.getDeltaMovement().x + moX * 2, -5, 5);
            double mY2 = clamp(p.getDeltaMovement().y + moY * 2, -5, 5);
            double mZ2 = clamp(p.getDeltaMovement().z + moZ * 2, -5, 5);
            double mX3 = clamp(p.getDeltaMovement().x + moX * 2, -10, 10);
            double mY3 = clamp(p.getDeltaMovement().y + moY * 2, -10, 10);
            double mZ3 = clamp(p.getDeltaMovement().z + moZ * 2, -10, 10);

            // Добавляем частицы пламени
            mc.particleEngine.createParticle(ParticleTypes.FLAME, ix + ox, iy, iz + oz, mX2, mY2, mZ2);
            mc.particleEngine.createParticle(ParticleTypes.FLAME, ix - ox, iy, iz - oz, mX2, mY2, mZ2);

            if (particleSetting == 0) {
                // Добавляем дым
                ParticleExSmoke fx3 = new ParticleExSmoke((ClientLevel) world, ix + ox, iy, iz + oz, mX3, mY3, mZ3);
                ParticleExSmoke fx4 = new ParticleExSmoke((ClientLevel) world, ix - ox, iy, iz - oz, mX3, mY3, mZ3);
                mc.particleEngine.add(fx3);
                mc.particleEngine.add(fx4);
            }
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    private void handleDieselSuitEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                        RandomSource rand, Minecraft mc, LocalPlayer player) {
        int particleSetting = Minecraft.getInstance().options.particles().get().getId();
        if (particleSetting == 2) return;

        int entityId = nbt.getInt("player");
        Entity ent = world.getEntity(entityId);

        if (ent instanceof Player p) {
            Vec3 vec = new Vec3(0, 0, -0.6);
            Vec3 offset = new Vec3(0.275, 0, 0);
            float angle = (float) -Math.toRadians(p.getYRot() - (p.getYRot() - p.yBodyRot));

            vec = vec.yRot(angle);
            offset = offset.yRot(angle);

            double ix = p.getX() + vec.x;
            double iy = p.getY() + p.getEyeHeight() - 1 + 0.4;
            double iz = p.getZ() + vec.z;
            double ox = offset.x;
            double oz = offset.z;

            if (player.isCrouching()) {
                iy += 0.25;
            }

            vec = vec.normalize();
            double mult = 0.025D;
            double mX = vec.x * mult;
            double mZ = vec.z * mult;

            for (int i = 0; i < 2; i++) {
                double sign = (i == 0) ? -1 : 1;

                SmokeParticle fx = (SmokeParticle) mc.particleEngine.createParticle(
                        ParticleTypes.SMOKE,
                        ix + ox * sign,
                        iy,
                        iz + oz * sign,
                        mX, 0, mZ
                );

                if (fx != null) {
                    float scale = 0.5F;
                    fx.scale(scale);
                    mc.particleEngine.add(fx);
                }
            }
        }
    }

    private void handleUfoEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                 RandomSource rand, Minecraft mc) {
        double motion = nbt.getDouble("motion");
        if (motion <= 0) motion = 0.1D; // Значение по умолчанию

        // Создаем облака частиц
        ParticleMukeCloud cloud = new ParticleMukeCloud(
                (ClientLevel) world,
                posX, posY, posZ,
                rand.nextGaussian() * motion,
                0,
                rand.nextGaussian() * motion
        );
        mc.particleEngine.add(cloud);

        // Дополнительные эффекты для UFO
        // Можно добавить дополнительные частицы для большей эффектности
        if (nbt.getBoolean("extra")) {
            for (int i = 0; i < 3; i++) {
                ParticleMukeCloud extraCloud = new ParticleMukeCloud(
                        (ClientLevel) world,
                        posX + (rand.nextDouble() - 0.5) * 0.5,
                        posY + (rand.nextDouble() - 0.5) * 0.5,
                        posZ + (rand.nextDouble() - 0.5) * 0.5,
                        rand.nextGaussian() * motion * 0.5,
                        rand.nextGaussian() * motion * 0.5,
                        rand.nextGaussian() * motion * 0.5
                );
                mc.particleEngine.add(extraCloud);
            }
        }
    }

    private void handleBF(CompoundTag nbt, Level world, double posX, double posY, double posZ, Minecraft mc)
    {
        ParticleMukeCloudBF cloud = new ParticleMukeCloudBF((ClientLevel) world, posX, posY, posZ, 0, 0, 0);
        mc.particleEngine.add(cloud);
    }

    private void handleRubbleBurst(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                   RandomSource rand, Minecraft mc) {
        int blockId = nbt.getInt("blockId");
        int meta = nbt.getInt("meta");
        int count = nbt.getInt("count");

        Block block = BuiltInRegistries.BLOCK.byId(blockId);
        if (block == null || block == Blocks.AIR) return;

        BlockState state = block.defaultBlockState();
        // Если нужна конкретная метаданные, можно использовать state.getBlock().getStateForPlacement()

        // Создаём эффект разрушения блока
        for (int i = 0; i < count; i++) {
            double x = posX + (rand.nextDouble() - 0.5) * 1.5;
            double y = posY + rand.nextDouble() * 1.5;
            double z = posZ + (rand.nextDouble() - 0.5) * 1.5;

            double mX = (rand.nextDouble() - 0.5) * 0.5;
            double mY = rand.nextDouble() * 0.5;
            double mZ = (rand.nextDouble() - 0.5) * 0.5;

            // Используем ItemParticleOption для частиц блоков
            ItemStack stack = new ItemStack(block);
            var particle = new ItemParticleOption(ParticleTypes.ITEM, stack);
            mc.particleEngine.createParticle(particle, x, y, z, mX, mY, mZ);
        }
    }

    private void handleVanillaBurst(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                    RandomSource rand, Minecraft mc) {
        String mode = nbt.getString("mode");
        int count = nbt.getInt("count");
        double motion = nbt.getDouble("motion");

        for (int i = 0; i < count; i++) {
            double mX = rand.nextGaussian() * motion;
            double mY = rand.nextGaussian() * motion;
            double mZ = rand.nextGaussian() * motion;

            if ("flame".equals(mode)) {
                mc.particleEngine.createParticle(
                        ParticleTypes.FLAME,
                        posX, posY, posZ,
                        mX, mY, mZ
                );
            }

            if ("cloud".equals(mode)) {
                mc.particleEngine.createParticle(
                        ParticleTypes.CLOUD,
                        posX, posY, posZ,
                        mX, mY, mZ
                );
            }

            if ("reddust".equals(mode)) {
                // Для частиц с цветом в 1.20.1 используем DustParticleOptions
                DustParticleOptions dust = new DustParticleOptions(
                        new Vector3f(1.0F, 0.0F, 0.0F),  // красный
                        1.0F  // размер
                );
                mc.particleEngine.createParticle(
                        dust,
                        posX, posY, posZ,
                        mX, mY, mZ
                );
            }

            if ("bluedust".equals(mode)) {
                DustParticleOptions dust = new DustParticleOptions(
                        new Vector3f(0.0F, 0.0F, 1.0F),  // синий
                        1.0F
                );
                mc.particleEngine.createParticle(
                        dust,
                        posX, posY, posZ,
                        mX, mY, mZ
                );
            }

            if ("greendust".equals(mode)) {
                DustParticleOptions dust = new DustParticleOptions(
                        new Vector3f(0.0F, 1.0F, 0.0F),  // зелёный
                        1.0F
                );
                mc.particleEngine.createParticle(
                        dust,
                        posX, posY, posZ,
                        mX, mY, mZ
                );
            }

            if ("blockdust".equals(mode)) {
                int blockId = nbt.getInt("block");
                Block b = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(String.valueOf(blockId)));

                if (b != null && b != Blocks.AIR) {
                    ItemStack stack = new ItemStack(b);
                    var particle = new ItemParticleOption(ParticleTypes.ITEM, stack);
                    mc.particleEngine.createParticle(
                            particle,
                            posX, posY, posZ,
                            mX, mY + 0.2, mZ
                    );
                }
            }
        }
    }

    private void handleSweatEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                   RandomSource rand, Minecraft mc) {
        int entityId = nbt.getInt("entity");
        Entity e = world.getEntity(entityId);

        int blockId = nbt.getInt("block");
        Block b = BuiltInRegistries.BLOCK.byId(blockId);

        if (b == null || b == Blocks.AIR) return;

        if (e instanceof LivingEntity living) {
            int count = nbt.getInt("count");

            for (int i = 0; i < count; i++) {
                double minX = living.getBoundingBox().minX;
                double maxX = living.getBoundingBox().maxX;
                double minY = living.getBoundingBox().minY;
                double maxY = living.getBoundingBox().maxY;
                double minZ = living.getBoundingBox().minZ;
                double maxZ = living.getBoundingBox().maxZ;

                double ix = minX - 0.2 + (maxX - minX + 0.4) * rand.nextDouble();
                double iy = minY + (maxY - minY + 0.2) * rand.nextDouble();
                double iz = minZ - 0.2 + (maxZ - minZ + 0.4) * rand.nextDouble();

                ItemStack stack = new ItemStack(b);
                mc.particleEngine.createParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, stack),
                        ix, iy, iz, 0, 0, 0
                );
            }
        }
    }

    private void handleFoundryEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ, Minecraft mc) {
        int color = nbt.getInt("color");
        byte dir = nbt.getByte("dir");
        float length = nbt.getFloat("len");
        float base = nbt.getFloat("base");
        float offset = nbt.getFloat("off");

        ParticleFoundry sploosh = new ParticleFoundry(
                (ClientLevel) world, posX, posY, posZ,
                color, Direction.from3DDataValue(dir),
                length, base, offset
        );
        mc.particleEngine.add(sploosh);
    }

    private void handleRadFogEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ, Minecraft mc) {
        ParticleRadiationFog fog = new ParticleRadiationFog((ClientLevel) world, posX, posY, posZ);
        mc.particleEngine.add(fog);
    }

    private void handleMukeEffect(CompoundTag nbt, Level world, double x, double y, double z, Minecraft mc) {
        boolean balefire = nbt.getBoolean("balefire");

        // Создаём волну
        ParticleMukeWave wave = new ParticleMukeWave((ClientLevel) world, x, y, z);
        mc.particleEngine.add(wave);

        // Создаём вспышку
        ParticleMukeFlash flash = new ParticleMukeFlash((ClientLevel) world, x, y, z, balefire);
        mc.particleEngine.add(flash);

        // Эффект урона игроку (встряска)
        LocalPlayer player = mc.player;
        if (player != null) {
            player.hurtTime = 15;
            player.hurtDuration = 15;
        }
    }

    private void handleVomitEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                   RandomSource rand, int particleSetting, Minecraft mc) {
        // Если частицы отключены, ничего не делаем
        if (particleSetting == 2) return;

        int entityId = nbt.getInt("entity");
        Entity e = world.getEntity(entityId);
        if (!(e instanceof LivingEntity living)) return;

        int baseCount = nbt.getInt("count");
        int count = baseCount / (particleSetting + 1);
        if (count <= 0) return;

        // Позиция, откуда вылетают частицы (рот существа)
        double ix = living.getX();
        double iy = living.getY() + living.getEyeHeight() - 0.2;
        double iz = living.getZ();

        // Направление взгляда
        Vec3 lookVec = living.getLookAngle();

        String mode = nbt.getString("mode");

        for (int i = 0; i < count; i++) {
            if ("normal".equals(mode)) {
                // Кусочки (как кусочки терракоты)
                double vx = (lookVec.x + rand.nextGaussian() * 0.2) * 0.2;
                double vy = (lookVec.y + rand.nextGaussian() * 0.2) * 0.2;
                double vz = (lookVec.z + rand.nextGaussian() * 0.2) * 0.2;

                // Используем ItemParticleOption для предмета
                ItemStack stack = new ItemStack(Blocks.TERRACOTTA);
                mc.particleEngine.createParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                        ix, iy, iz, vx, vy, vz);
            }

            if ("blood".equals(mode)) {
                // Красные частицы (как кусочки редстоун блока)
                double vx = (lookVec.x + rand.nextGaussian() * 0.2) * 0.2;
                double vy = (lookVec.y + rand.nextGaussian() * 0.2) * 0.2;
                double vz = (lookVec.z + rand.nextGaussian() * 0.2) * 0.2;

                ItemStack stack = new ItemStack(Blocks.REDSTONE_BLOCK);
                mc.particleEngine.createParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                        ix, iy, iz, vx, vy, vz);
            }

            if ("smoke".equals(mode)) {
                // Дым
                double vx = (lookVec.x + rand.nextGaussian() * 0.1) * 0.05;
                double vy = (lookVec.y + rand.nextGaussian() * 0.1) * 0.05;
                double vz = (lookVec.z + rand.nextGaussian() * 0.1) * 0.05;
                mc.particleEngine.createParticle(ParticleTypes.SMOKE, ix, iy, iz, vx, vy, vz);
            }
        }
    }

    private void handleVanillaExt(CompoundTag nbt, Level level, double posX, double posY, double posZ, RandomSource rand, Minecraft mc) {
        String mode = nbt.getString("mode");
        double mX = nbt.getDouble("mX");
        double mY = nbt.getDouble("mY");
        double mZ = nbt.getDouble("mZ");

        if (mode.equals("fireworks")) {
            mc.particleEngine.createParticle(
                    ParticleTypes.FIREWORK,
                    posX,
                    posY,
                    posZ,
                    0, 0, 0
            );
        }
        if (mode.equals("largeexplode")) {
            float size = nbt.getFloat("size");
            byte count = nbt.getByte("count");

            // Главная частица - большой взрыв
            mc.particleEngine.createParticle(ParticleTypes.EXPLOSION_EMITTER, posX, posY, posZ, size, 0, 0);

            // Дополнительные маленькие частицы взрыва
            for (int i = 0; i < count; i++) {
                mc.particleEngine.createParticle(ParticleTypes.EXPLOSION, posX, posY, posZ, 0, 0, 0);
            }
        }
        if(mode.equals("smoke")) {
            mc.particleEngine.createParticle(
                    ParticleTypes.SMOKE,
                    posX,
                    posY,
                    posZ,
                    mX, mY, mZ
            );
        }
        if(mode.equals("cloud")) {
            if (nbt.contains("r")) {
                float rng = rand.nextFloat() * 0.1F;
                float r = nbt.getFloat("r") + rng;
                float g = nbt.getFloat("g") + rng;
                float b = nbt.getFloat("b") + rng;

                // Создаём кастомную частицу с нашими параметрами
                mc.particleEngine.createParticle(
                        ModParticles.PARTICLE_CLOUD.get(),
                        posX, posY, posZ,
                        r, g, b  // xSpeed/ySpeed/zSpeed теперь несут цвет!
                );
            } else {
                // Обычное облако без настроек
                mc.particleEngine.createParticle(
                        ParticleTypes.CLOUD,
                        posX, posY, posZ,
                        0, 0, 0
                );
            }

        }
        if (mode.equals("blockdust")) {
            String blockName = nbt.getString("block");
            Block b = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(blockName));

            if (b != null && b != Blocks.AIR) {
                Item item = b.asItem();
                if (item != Items.AIR) {
                    ItemStack stack = new ItemStack(item, 1);
                    var particle = new ItemParticleOption(ParticleTypes.ITEM, stack);
                    mc.particleEngine.createParticle(
                            particle,
                            posX, posY, posZ,
                            mX, mY + 0.2, mZ
                    );
                }
            }
        }
    }

    private void handleTinyTotEffect(CompoundTag nbt, ClientLevel level, double posX, double posY, double posZ,
                                     RandomSource rand, Minecraft mc) {

        // Создаём волну
        ParticleMukeWave wave = new ParticleMukeWave(level, posX, posY, posZ);
        mc.particleEngine.add(wave);

        // Первый набор облаков (вертикальная линия)
        for (double d = 0.0D; d <= 1.6D; d += 0.1) {
            ParticleMukeCloud cloud = new ParticleMukeCloud(level, posX, posY, posZ,
                    rand.nextGaussian() * 0.05,
                    d + rand.nextGaussian() * 0.02,
                    rand.nextGaussian() * 0.05
            );
            mc.particleEngine.add(cloud);
        }

        // Второй набор облаков (разлёт в стороны)
        for (int i = 0; i < 50; i++) {
            ParticleMukeCloud cloud = new ParticleMukeCloud(level, posX, posY + 0.5, posZ,
                    rand.nextGaussian() * 0.5,
                    rand.nextInt(5) == 0 ? 0.02 : 0,
                    rand.nextGaussian() * 0.5
            );
            mc.particleEngine.add(cloud);
        }

        // Третий набор облаков (гриб)
        for (int i = 0; i < 15; i++) {
            double ix = rand.nextGaussian() * 0.2;
            double iz = rand.nextGaussian() * 0.2;

            if (ix * ix + iz * iz > 0.75) {
                ix *= 0.5;
                iz *= 0.5;
            }

            double iy = 1.6 + (rand.nextDouble() * 2 - 1) * (0.75 - (ix * ix + iz * iz)) * 0.5;

            ParticleMukeCloud cloud = new ParticleMukeCloud(level, posX, posY, posZ,
                    ix,
                    iy + rand.nextGaussian() * 0.02,
                    iz
            );
            mc.particleEngine.add(cloud);
        }
    }

    private void handleSmokeEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                   RandomSource rand, Minecraft mc) {
        String mode = nbt.getString("mode");
        int count = Math.max(1, nbt.getInt("count"));

        if ("foamSplash".equals(mode)) {
            double strength = nbt.getDouble("range");
            Vec3 vec = new Vec3(strength, 0, 0);

            for (int i = 0; i < count; i++) {
                vec = rotateVec3(vec, (float) Math.toRadians(rand.nextFloat() * 360F));

                ParticleFoam fx = new ParticleFoam((ClientLevel) world, posX + vec.x, posY, posZ + vec.z);
                fx.maxAge = 50;
                fx.setMotion(0, 0, 0);
                mc.particleEngine.add(fx);

                vec = rotateVec3(vec, (float) (Math.PI * 2F / count));
            }
        }

        if ("cloud".equals(mode)) {
            for (int i = 0; i < count; i++) {
                double motionY = rand.nextGaussian() * (1 + (count / 100.0));
                double motionX = rand.nextGaussian() * (1 + (count / 150.0));
                double motionZ = rand.nextGaussian() * (1 + (count / 150.0));
                if (rand.nextBoolean()) motionY = Math.abs(motionY);

                ParticleExSmoke fx = new ParticleExSmoke((ClientLevel) world, posX, posY, posZ,
                        motionX, motionY, motionZ);
                mc.particleEngine.add(fx);
            }
        }

        if ("radial".equals(mode)) {
            for (int i = 0; i < count; i++) {
                double motionX = rand.nextGaussian() * (1 + (count / 50.0));
                double motionY = rand.nextGaussian() * (1 + (count / 50.0));
                double motionZ = rand.nextGaussian() * (1 + (count / 50.0));

                ParticleExSmoke fx = new ParticleExSmoke((ClientLevel) world, posX, posY, posZ,
                        motionX, motionY, motionZ);
                mc.particleEngine.add(fx);
            }
        }

        if ("radialDigamma".equals(mode)) {
            Vec3 vec = new Vec3(2, 0, 0);
            vec = rotateVec3(vec, rand.nextFloat() * (float) Math.PI * 2F);

            for (int i = 0; i < count; i++) {
                ParticleDigammaSmoke fx = new ParticleDigammaSmoke((ClientLevel) world, posX, posY, posZ,
                        vec.x, 0, vec.z);
                mc.particleEngine.add(fx);

                vec = rotateVec3(vec, (float) (Math.PI * 2F / count));
            }
        }

        if ("shock".equals(mode)) {
            double strength = nbt.getDouble("strength");

            Vec3 vec = new Vec3(strength, 0, 0);
            vec = rotateVec3(vec, (float) Math.toRadians(rand.nextInt(360)));

            for (int i = 0; i < count; i++) {
                ParticleExSmoke fx = new ParticleExSmoke((ClientLevel) world, posX, posY, posZ,
                        vec.x, 0, vec.z);
                mc.particleEngine.add(fx);

                vec = rotateVec3(vec, (float) (Math.PI * 2F / count));
            }
        }

        if ("shockRand".equals(mode)) {
            double strength = nbt.getDouble("strength");

            Vec3 vec = new Vec3(strength, 0, 0);
            vec = rotateVec3(vec, (float) Math.toRadians(rand.nextInt(360)));

            for (int i = 0; i < count; i++) {
                double r = rand.nextDouble();
                ParticleExSmoke fx = new ParticleExSmoke((ClientLevel) world, posX, posY, posZ,
                        vec.x * r, 0, vec.z * r);
                mc.particleEngine.add(fx);

                vec = rotateVec3(vec, (float) (Math.PI * 2F / count));
            }
        }

        if ("wave".equals(mode)) {
            double range = nbt.getDouble("range");

            Vec3 vec = new Vec3(range, 0, 0);

            for (int i = 0; i < count; i++) {
                vec = rotateVec3(vec, (float) Math.toRadians(rand.nextFloat() * 360F));

                ParticleExSmoke fx = new ParticleExSmoke((ClientLevel) world, posX + vec.x, posY, posZ + vec.z,
                        0, 0, 0);
                fx.maxAge = 50;
                mc.particleEngine.add(fx);
            }
        }
    }

    private Vec3 rotateVec3(Vec3 vec, float angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = vec.x * cos - vec.z * sin;
        double z = vec.x * sin + vec.z * cos;
        return new Vec3(x, vec.y, z);
    }

    private void handleTowerEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ,
                                   RandomSource rand, int particleSetting, Minecraft mc) {
        // Проверяем настройки частиц
        if (particleSetting == 0 || (particleSetting == 1 && rand.nextBoolean())) {

            // Создаём частицу башни охлаждения
            ParticleCoolingTower fx = new ParticleCoolingTower((ClientLevel) world, posX, posY, posZ);

            // Устанавливаем параметры из NBT
            fx.setLift(nbt.getFloat("lift"));
            fx.setBaseScale(nbt.getFloat("base"));
            fx.setMaxScale(nbt.getFloat("max"));
            fx.setLife(nbt.getInt("life") / (particleSetting + 1));

            // Опциональные параметры
            if (nbt.contains("noWind")) fx.noWind();
            if (nbt.contains("strafe")) fx.setStrafe(nbt.getFloat("strafe"));
            if (nbt.contains("alpha")) fx.alphaMod(nbt.getFloat("alpha"));

            // Цвет
            if (nbt.contains("color")) {
                int colorValue = nbt.getInt("color");
                Color color = new Color(colorValue);
                fx.setColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
            }

            // Добавляем частицу в систему
            mc.particleEngine.add(fx);
        }
    }

    private void handleGibletsEffect(CompoundTag nbt, Level world, double posX, double posY, double posZ, RandomSource rand) {
        int ent = nbt.getInt("ent");

        Entity e = world.getEntity(ent);

        if(e == null) return;

        float width = e.getBbWidth();
        float height = e.getBbHeight();
        int gW = (int)(width / 0.25F);
        int gH = (int)(height / 0.25F);

        int count = (int) (gW * 1.5 * gH);

        if(nbt.contains("cDiv", CompoundTag.TAG_INT)) {
            count = (int) Math.ceil(count / (double)nbt.getInt("cDiv"));
        }

        boolean blowMeIntoTheGodDamnStratosphere = rand.nextInt(15) == 0;
        double mult = 1D;

        if(blowMeIntoTheGodDamnStratosphere) {
            mult *= 10;
        }

        for(int i = 0; i < count; i++) {
            world.addParticle(
                    ModParticles.GIBLET.get(),
                    posX, posY, posZ,
                    rand.nextGaussian() * 0.25 * mult,
                    rand.nextDouble() * mult,
                    rand.nextGaussian() * 0.25 * mult
            );
        }
    }



    private void spawnPlasmaBlast(CompoundTag nbt, double x, double y, double z) {
        float r = nbt.getFloat("r");
        float g = nbt.getFloat("g");
        float b = nbt.getFloat("b");
        float pitch = nbt.getFloat("pitch");
        float yaw = nbt.getFloat("yaw");
        float scale = nbt.getFloat("scale");

        ParticlePlasmaBlast particle = new ParticlePlasmaBlast(
                Minecraft.getInstance().level,
                x, y, z, r, g, b, pitch, yaw, scale
        );

        Minecraft.getInstance().particleEngine.add(particle);
    }

    private void spawnExplosion(CompoundTag nbt, double x, double y, double z) {
        // Реализация взрыва
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if(mc.level != null) {
            mc.particleEngine.createParticle(
                    net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                    x, y, z, 0, 0, 0
            );
        }
    }



    private void needsToRealise(CompoundTag nbt, double x, double y, double z) {
        String type = nbt.getString("type");
        if (type.isEmpty()) {
            System.out.println("Unknown effect type: empty or missing 'type' field");
            return;
        }

        // Получаем игрока и отправляем сообщение в чат
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player != null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c[DEBUG] Unhandled particle effect: §e" + type));
        }

        // Также выводим в консоль для удобства
        System.out.println("Missing particle effect handler for type: " + type);

        // Опционально: вывести дополнительную информацию о пакете
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS != null && !type.isEmpty()) {
            System.out.println("  Position: " + x + ", " + y + ", " + z);
            System.out.println("  NBT data: " + nbt);
        }
    }


    public static void vanish(int ent) {
        vanished.put(ent, System.currentTimeMillis() + 2000);
    }

    public static void vanish(int ent, int duration) {
        vanished.put(ent, System.currentTimeMillis() + duration);
    }

    public static boolean isVanished(Entity e) {
        if(e == null) return false;
        if(!vanished.containsKey(e.getId())) return false;
        return vanished.get(e.getId()) > System.currentTimeMillis();
    }

    public Player me() {
        return Minecraft.getInstance().player;
    }

    @Override
    public int getStackColor(ItemStack stack, boolean amplify) {
        if (stack == null || stack.isEmpty()) return 0x000000;

        int color;

        // Используем стандартную систему цветов Minecraft
        color = Minecraft.getInstance().getItemColors().getColor(stack, 0);

        if (color == -1) {
            // Если цвета нет, пробуем альтернативные методы
            if (stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                // Для блоков используем цвет материала
                MapColor mapColor = block.defaultBlockState().getMapColor(null, null);
                color = mapColor.col; // поле col существует в 1.20.1
            } else {
                color = ColorUtil.getAverageColorFromStack(stack);
            }
        }

        if (amplify) {
            color = ColorUtil.amplifyColor(color);
        }

        return color;
    }

}