package tech.alexnijjar.golemoverhaul.common.entities.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.common.entities.IShearable;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.MudBallProjectile;
import tech.alexnijjar.golemoverhaul.common.tags.ModItemTags;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class TerracottaGolem extends BaseGolem implements IShearable, RangedAttackMob {

    public static final int RANGED_ATTACK_DELAY_TICKS = 2;

    private static final EntityDataAccessor<Byte> ID_TYPE = SynchedEntityData.defineId(TerracottaGolem.class,
            EntityDataSerializers.BYTE);

    private final RangedAttackGoal rangedAttackGoal = new RangedAttackGoal(this, 1, 20, 15);

    private int attackAnimationDelay = -1;

    private ItemStack equippedStack = ItemStack.EMPTY;

    public TerracottaGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.xpReward = 6;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, Type.CACTUS.attackDamage);
    }

    public static boolean checkMobSpawnRules(EntityType<? extends Mob> type, LevelAccessor level,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!GolemOverhaulConfig.spawnTerracottaGolems || !GolemOverhaulConfig.allowSpawning)
            return false;
        return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_TYPE, (byte) 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("type", this.getTerracottaType().name().toLowerCase(Locale.ROOT));
        if (!this.equippedStack.isEmpty()) {
            compound.put("item", this.equippedStack.save(this.registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTerracottaType(Type.valueOf(compound.getString("type").toUpperCase(Locale.ROOT)));
        if (compound.contains("item")) {
            this.equippedStack = ItemStack.parse(this.registryAccess(), compound.getCompound("item"))
                    .orElse(ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canMeleeAttack() {
        return !getTerracottaType().ranged;
    }

    public Type getTerracottaType() {
        return Type.values()[this.entityData.get(ID_TYPE)];
    }

    public void setTerracottaType(Type type) {
        this.entityData.set(ID_TYPE, (byte) type.ordinal());

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(type.attackDamage);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(type.knockbackResistance);

        this.goalSelector.removeGoal(this.rangedAttackGoal);
        if (type.ranged) {
            this.goalSelector.addGoal(2, this.rangedAttackGoal);
        }
        this.updateAttackGoals();
    }

    @Override
    public int getAttackTicks() {
        return 12;
    }

    @Override
    public Item getRepairItem() {
        return Items.CLAY_BALL;
    }

    @Override
    public float getRepairItemHealAmount() {
        return 4;
    }

    @Override
    public SoundEvent getRepairSound() {
        return SoundEvents.STONE_PLACE;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance,
            MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setTerracottaType(Type.values()[level.getRandom().nextInt(Type.values().length)]);
        return super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (getTerracottaType() == Type.NORMAL && !level().isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            Type type = Type.ofStack(stack);
            if (type != null) {
                this.equippedStack = stack.copyWithCount(1);
                stack.shrink(1);
                playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value());
                setTerracottaType(type);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public @NotNull List<ItemStack> onSheared() {
        playSound(SoundEvents.SNOW_GOLEM_SHEAR);
        setTerracottaType(Type.NORMAL);
        if (!this.equippedStack.isEmpty()) {
            return List.of(this.equippedStack);
        }
        return List.of(getTerracottaType().equipItem.getDefaultInstance());
    }

    @Override
    public boolean isShearable() {
        return this.getTerracottaType() != Type.NORMAL;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide() && getTerracottaType() == Type.CACTUS && !source.is(DamageTypes.THORNS)) {
            if (source.getDirectEntity() instanceof LivingEntity entity) {
                entity.hurt(damageSources().thorns(this), 6);
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (attackAnimationDelay == -1) {
            sendAttackEvent();
            attackAnimationDelay = RANGED_ATTACK_DELAY_TICKS;
        }
    }

    public void actuallyShoot(LivingEntity target) {
        if (target == null)
            return;
        Projectile projectile = new MudBallProjectile(level(), this);
        projectile.setPos(getX(), getY(), getZ());

        double x = target.getX() - getX();
        double y = target.getY() - projectile.getY();
        double z = target.getZ() - getZ();
        double distance = Math.sqrt(x * x + z * z) * 0.2;
        projectile.shoot(x, y + distance, z, 1, 5);

        level().addFreshEntity(projectile);
        playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1, 0.4f / (getRandom().nextFloat() * 0.4f + 0.8f));
    }

    @Override
    protected void customServerAiStep() {
        attackAnimationDelay = Math.max(-1, attackAnimationDelay - 1);
        if (attackAnimationDelay == 0) {
            actuallyShoot(getTarget());
            attackAnimationDelay = -1;
        }
    }

    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(0.5f, 0, 0.5f);
    }

    public enum Type {
        NORMAL(2, 0, Items.AIR, false, stack -> false),
        CACTUS(6, 1, Items.CACTUS, false, stack -> stack.is(ModItemTags.CACTUS)),
        DEAD_BUSH(4, 0, Items.DEAD_BUSH, true, stack -> stack.is(Items.DEAD_BUSH)),
        ;

        private final float attackDamage;
        private final float knockbackResistance;
        private final Item equipItem;
        private final boolean ranged;
        private final Predicate<ItemStack> isValidStack;

        Type(float attackDamage, float knockbackResistance, Item equipItem, boolean ranged,
                Predicate<ItemStack> isValidStack) {
            this.attackDamage = attackDamage;
            this.knockbackResistance = knockbackResistance;
            this.equipItem = equipItem;
            this.ranged = ranged;
            this.isValidStack = isValidStack;
        }

        @Nullable
        private static Type ofStack(ItemStack stack) {
            for (Type type : values()) {
                if (type.isValidStack.test(stack)) {
                    return type;
                }
            }
            return null;
        }
    }
}
