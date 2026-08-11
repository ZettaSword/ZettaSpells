package zettasword.zetta_spells.entity.custom;

import com.binaris.wizardry.api.content.util.EntityUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import zettasword.zetta_spells.entity.ZSEntities;

import java.util.List;

public class ExplodeItemEntity extends ItemEntity {
    public ExplodeItemEntity(Level p_31992_) {
        super(ZSEntities.EXPLODE_ITEM_ENTITY.get(), p_31992_);
    }

    public ExplodeItemEntity(EntityType<ExplodeItemEntity> explodeItemEntityEntityType, Level level) {
        super(explodeItemEntityEntityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (getItem().onEntityItemUpdate(this)) return;
        if (!this.getItem().isEmpty()) {
            List<LivingEntity> entities = EntityUtil.getEntitiesInRange(level(), getX(), getY(), getZ(), 1.0F, LivingEntity.class);
            if (entities != null && !entities.isEmpty()) {
                LivingEntity mob = entities.get(0);
                if (!level().isClientSide)
                    level().explode(this, mob.getX(), mob.getY(), mob.getZ(), 0.5F, Level.ExplosionInteraction.NONE);
                this.discard();
            }
        }
    }

    @Override
    public void playerTouch(Player player) {
        // Do nothing
    }
}
