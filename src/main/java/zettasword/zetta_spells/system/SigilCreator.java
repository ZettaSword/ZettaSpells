package zettasword.zetta_spells.system;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.entity.construct.sigils.*;

public class SigilCreator {
    public static ZSSigil create(Level level, Vec3 pos, int lifetime, String name){
        ZSSigil sigil = switch (name) {
            case "fire" -> new ZSSigilFire(level);
            case "ice" -> new ZSSigilIce(level);
            case "earth" -> new ZSSigilEarth(level);
            case "lightning" -> new ZSSigilLightning(level);
            case "necromancy" -> new ZSSigilNecromancy(level);
            case "healing" -> new ZSSigilHealing(level);
            case "sorcery" -> new ZSSigilSorcery(level);
            default -> new ZSSigilMagic(level);
        };
        sigil.lifetime = lifetime;
        sigil.setPos(pos.add(new Vec3(0, 0.6,0)));
        return sigil;
    }
}
