package zettasword.zetta_spells.particle;

import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleStoneSpike extends ParticleWizardry {
    private final SpriteSet sprites;

    public ParticleStoneSpike(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.sprites = spriteProvider;
        this.setParticleSpeed(0, 0, 0);
        this.setLifetime(40 + random.nextInt(5));
        this.scale(3f);
        this.gravity = 0;
        this.hasPhysics = true;
        this.setColor(1.0F, 1.0F, 1.0F);
    }


    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class StoneSpikeProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public StoneSpikeProvider(SpriteSet spriteProvider) {
            StoneSpikeProvider.spriteProvider = spriteProvider;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleStoneSpike(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleStoneSpike(world, x, y, z, spriteProvider);
        }
    }
}
