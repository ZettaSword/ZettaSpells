package zettasword.zetta_spells.entity.living;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import zettasword.zetta_spells.entity.ZSEntities;

public class WoodGolem extends IronGolem implements NeutralMob {

    public WoodGolem(EntityType<? extends WoodGolem> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
    }

    public WoodGolem(Level level) {
        this(ZSEntities.WOOD_GOLEM.get(), level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class,
                10, false, false, entity -> entity instanceof Enemy));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class,
                10, true, false, null));
    }

    @Override
    public int getOfferFlowerTick() {
        return 0;
    }

    @Override
    public void offerFlower(boolean offeringFlower) {

    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return InteractionResult.PASS;
    }
}