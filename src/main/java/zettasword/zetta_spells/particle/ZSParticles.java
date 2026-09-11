package zettasword.zetta_spells.particle;

import com.binaris.wizardry.api.content.DeferredObject;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import zettasword.zetta_spells.ZettaSpells;

public class ZSParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ZettaSpells.MODID);

    public static final DeferredObject<SimpleParticleType> EARTH_SPIKE = new DeferredObject<>(PARTICLES.register("earth_spike",
            () -> new SimpleParticleType(false)));
}
