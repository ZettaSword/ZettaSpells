package zettasword.zetta_spells.system.spellcreation.actions.action;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import zettasword.zetta_spells.ZettaSpells;
import zettasword.zetta_spells.entity.construct.CosmeticSigil;
import zettasword.zetta_spells.system.SpellTarget;
import zettasword.zetta_spells.system.spellcreation.SVar;
import zettasword.zetta_spells.system.spellcreation.SpellCreateContext;
import zettasword.zetta_spells.system.spellcreation.actions.bases.TargetSpellWord;

import java.util.List;

public class TeleportWord extends TargetSpellWord {
    public TeleportWord() {
        super("teleport");
    }

    /**
     * Replaces default cast method with this one instead, which now returns True if any of this method cast will success();
     *
     * @param ctx    Context of the spell.
     * @param target SpellTarget.
     * @param words  Words used in the spell.
     * @param i      Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean cast(SpellCreateContext ctx, SpellTarget target, List<String> words, int i) {
        SVar vec = ctx.getMod("target");
        Entity entity = target.getTargetEntity();
        if (vec != null && entity != null){
            boolean manaCost = consumeMana(ctx, (int) (Math.abs(vec.getVec().x) + Math.abs(vec.getVec().y) + Math.abs(vec.getVec().z)));
            if (manaCost && !ctx.world().isClientSide){
                if (ctx.canCreateFx()){
                    CosmeticSigil sigil = new CosmeticSigil(ctx.world());
                    sigil.setLocation(ZettaSpells.location("textures/sigils/old/circle_sorcery.png"));
                    sigil.setLifetime(40);
                    sigil.setCaster(ctx.getCaster());
                    sigil.setPos(entity.getPosition(1.0F));
                    sigil.addDeltaMovement(new Vec3(0, 0.5,0));
                    ctx.world().addFreshEntity(sigil);
                }

                Vec3 pos = vec.getVec();
                entity.teleportRelative(pos.x, pos.y, pos.z);

                if (ctx.canCreateFx()){
                    CosmeticSigil sigil = new CosmeticSigil(ctx.world());
                    sigil.setLocation(ZettaSpells.location("textures/sigils/old/circle_sorcery.png"));
                    sigil.setLifetime(40);
                    sigil.setCaster(ctx.getCaster());
                    sigil.setPos(entity.getPosition(1.0F));
                    sigil.addDeltaMovement(new Vec3(0, 0.5,0));
                    ctx.world().addFreshEntity(sigil);
                }
            }
            return manaCost;
        }
        return false;
    }

    /**
     * Allows us to get if key things are considered. Like if there is a word, and is a current target is a spider, and more, obviously.
     *
     * @param ctx   Context of the spell.
     * @param words Words used in the spell.
     * @param i     Current index in [words], so you can understand where we are at in the spell.
     **/
    @Override
    public boolean shouldCast(SpellCreateContext ctx, List<String> words, int i) {
        return words.get(i).equals("teleport");
    }
}
