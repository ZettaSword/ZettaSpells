package zettasword.zetta_spells.system.particles;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.DeferredObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Alteria - is known mage of the past that loved alchemy and magic effects.
 * This module is representation of some her capabilities.
 * **/
public class Alteria {
    // Scale factor to make math units visible in Minecraft blocks
    private static final double SCALE = 2.5;
    private static final int PARTICLES_PER_EQUATION = 100;

    /**
     * Spawns particles outlining the edges of a block.
     *
     * @param level        The world (must be Client Side to render)
     * @param pos          The block position to outline
     * @param data         The particle type
     * @param particlesPerEdge Number of particles per edge (12 edges total)
     */
    public static void spawnBlockOutlineParticles(Level level, BlockPos pos, ParticleOptions data, int particlesPerEdge) {
        if (!level.isClientSide()) {
            return;
        }

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        double step = 1.0 / particlesPerEdge;

        // Spawn particles along all 12 edges of the block
        for (int i = 0; i < particlesPerEdge; i++) {
            double offset = i * step;

            // === BOTTOM FACE (y) ===
            level.addParticle(data, x + offset, y, z, 0, 0, 0);           // Edge 1
            level.addParticle(data, x + 1, y, z + offset, 0, 0, 0);       // Edge 2
            level.addParticle(data, x + 1 - offset, y, z + 1, 0, 0, 0);   // Edge 3
            level.addParticle(data, x, y, z + 1 - offset, 0, 0, 0);       // Edge 4

            // === TOP FACE (y+1) ===
            level.addParticle(data, x + offset, y + 1, z, 0, 0, 0);       // Edge 5
            level.addParticle(data, x + 1, y + 1, z + offset, 0, 0, 0);   // Edge 6
            level.addParticle(data, x + 1 - offset, y + 1, z + 1, 0, 0, 0); // Edge 7
            level.addParticle(data, x, y + 1, z + 1 - offset, 0, 0, 0);   // Edge 8

            // === VERTICAL EDGES ===
            level.addParticle(data, x, y + offset, z, 0, 0, 0);           // Edge 9
            level.addParticle(data, x + 1, y + offset, z, 0, 0, 0);       // Edge 10
            level.addParticle(data, x + 1, y + offset, z + 1, 0, 0, 0);   // Edge 11
            level.addParticle(data, x, y + offset, z + 1, 0, 0, 0);       // Edge 12
        }
    }

    public static void spawnBlockOutlineParticles(Level level, BlockPos pos, DeferredObject<SimpleParticleType> type, int color, int particlesPerEdge) {
        if (!level.isClientSide()) {
            return;
        }

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        double step = 1.0 / particlesPerEdge;

        // Spawn particles along all 12 edges of the block
        for (int i = 0; i < particlesPerEdge; i++) {
            double offset = i * step;

            // === BOTTOM FACE (y) ===

            ParticleBuilder.create(type).pos(x + offset, y, z).color(color).spawn(level);    // Edge 1
            ParticleBuilder.create(type).pos(x + 1, y, z + offset).color(color).spawn(level);    // Edge 2
            ParticleBuilder.create(type).pos(x + 1 - offset, y, z + 1).color(color).spawn(level);    // Edge 3
            ParticleBuilder.create(type).pos(x, y, z + 1 - offset).color(color).spawn(level);    // Edge 4

            // === TOP FACE (y+1) ===
            ParticleBuilder.create(type).pos(x + offset, y + 1, z).color(color).spawn(level);    // Edge 1
            ParticleBuilder.create(type).pos(x + 1, y + 1, z + offset).color(color).spawn(level);    // Edge 2
            ParticleBuilder.create(type).pos(x + 1 - offset, y + 1, z + 1).color(color).spawn(level);    // Edge 3
            ParticleBuilder.create(type).pos(x, y + 1, z + 1 - offset).color(color).spawn(level);    // Edge 4

            // === VERTICAL EDGES ===
            ParticleBuilder.create(type).pos(x + offset, y + offset, z).color(color).spawn(level);    // Edge 1
            ParticleBuilder.create(type).pos(x + 1, y + offset, z + offset).color(color).spawn(level);    // Edge 2
            ParticleBuilder.create(type).pos(x + 1 - offset, y + offset, z + 1).color(color).spawn(level);    // Edge 3
            ParticleBuilder.create(type).pos(x, y + offset, z + 1 - offset).color(color).spawn(level);    // Edge 4
        }
    }

    public static void spawnParticles(Level level, Vec3 origin, String type) {
        double ox = origin.x;
        double oy = origin.y;
        double oz = origin.z;

        // We iterate 't' from 0 to 2*PI (approx 6.28)
        double step = (2 * Math.PI) / PARTICLES_PER_EQUATION;

        for (int i = 0; i < PARTICLES_PER_EQUATION; i++) {
            double t = i * step;
            double x = 0, y = 0, z = 0;

            switch (type) {
                case "circle":
                    // Equation: x = r*cos(t), z = r*sin(t)
                    x = Math.cos(t) * SCALE;
                    z = Math.sin(t) * SCALE;
                    y = 0;
                    break;

                case "helix":
                    // Equation: x = r*cos(t), z = r*sin(t), y = t
                    x = Math.cos(t) * SCALE;
                    z = Math.sin(t) * SCALE;
                    // Map t (0 to 6.28) to height (0 to 10 blocks)
                    y = (t / (2 * Math.PI)) * 10.0;
                    break;

                case "heart":
                    // Parametric Heart:
                    // x = 16sin^3(t)
                    // y = 13cos(t) - 5cos(2t) - 2cos(3t) - cos(4t)
                    // Scaled down by 0.15 to fit in view
                    double hx = 16 * Math.pow(Math.sin(t), 3);
                    double hy = 13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t);
                    x = hx * 0.15 * SCALE;
                    y = hy * 0.15 * SCALE; // Y is Up in Minecraft
                    z = 0;
                    break;

                case "lissajous":
                    // Equation: x = A*sin(a*t + d), z = B*sin(b*t)
                    // Creates a knot pattern
                    x = Math.sin(3 * t + Math.PI / 2) * SCALE;
                    z = Math.sin(4 * t) * SCALE;
                    y = Math.sin(5 * t) * (SCALE / 2); // Add slight 3D depth
                    break;

                case "rose":
                    // Polar Rose: r = cos(k * t)
                    // x = r * cos(t), z = r * sin(t)
                    double k = 4.0; // Number of petals (even k = 2k petals)
                    double r = Math.cos(k * t) * SCALE;
                    x = r * Math.cos(t);
                    z = r * Math.sin(t);
                    y = 0;
                    break;

                default:
                    return;
            }

            // Spawn the particle at calculated position + origin
            // Particle Type: END_ROD (visible, white/purple)
            level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    ox + x, oy + y, oz + z,
                    0, 0, 0
            );
        }
    }

    public static void spawnEnchantmentParticles(Player player, int count, double radius, double heightRange) {
        if (player.level().isClientSide()) {
            Level level = player.level();
            double px = player.getX();
            double py = player.getEyeY(); // Start from eye level for nicer effect
            double pz = player.getZ();

            for (int i = 0; i < count; i++) {
                // Spawn position: random point on a sphere/cylinder around player
                double angle = player.getRandom().nextDouble() * Math.PI * 2;
                double distance = radius * (0.5 + player.getRandom().nextDouble() * 0.5); // 50-100% of radius
                double spawnX = px + Mth.cos((float) angle) * distance;
                double spawnZ = pz + Mth.sin((float) angle) * distance;
                double spawnY = py + player.getRandom().nextDouble() * heightRange - (heightRange / 2);

                // Calculate motion vector: FROM spawn position TO player
                double motionX = px - spawnX;
                double motionY = py - spawnY;
                double motionZ = pz - spawnZ;

                // Normalize and scale motion for smooth flow
                double length = Mth.sqrt((float) (motionX * motionX + motionY * motionY + motionZ * motionZ));
                if (length > 0) {
                    double speed = 0.08 + player.getRandom().nextDouble() * 0.04; // Slight speed variation
                    motionX = (motionX / length) * speed;
                    motionY = (motionY / length) * speed;
                    motionZ = (motionZ / length) * speed;
                }

                // Spawn the enchantment particle with motion
                level.addParticle(
                        ParticleTypes.ENCHANT,  // ✨ Enchantment glyph particle
                        spawnX, spawnY, spawnZ, // Spawn position
                        motionX, motionY, motionZ // Velocity towards player
                );
            }
        }
    }

}
