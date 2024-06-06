package tech.alexnijjar.golemoverhaul.common.entities.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModSoundEvents;
import tech.alexnijjar.golemoverhaul.common.utils.ModUtils;

import java.util.Locale;

public class HayGolem extends BaseGolem implements Shearable {

    private static final EntityDataAccessor<Byte> ID_COLOR = SynchedEntityData.defineId(HayGolem.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_SHEARED = SynchedEntityData.defineId(HayGolem.class, EntityDataSerializers.BOOLEAN);

    private static final BlockPattern HAY_GOLEM_PATTERN = BlockPatternBuilder.start()
        .aisle(
            "~^~",
            "/#/",
            "~/~"
        )
        .where('^', BlockInWorld.hasState(ModUtils.PUMPKINS_PREDICATE))
        .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.HAY_BLOCK)))
        .where('/', BlockInWorld.hasState(state -> state.is(BlockTags.FENCES)))
        .where('~', blockInWorld -> blockInWorld.getState().isAir())
        .build();

    public HayGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 40)
            .add(Attributes.MOVEMENT_SPEED, 0.34)
            .add(Attributes.ATTACK_DAMAGE, 3);
    }

    public static boolean checkMobSpawnRules(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!GolemOverhaulConfig.spawnHayGolems || !GolemOverhaulConfig.allowSpawning) return false;
        return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    public static void trySpawnGolem(Level level, BlockPos pos) {
        BlockPattern.BlockPatternMatch pattern = HAY_GOLEM_PATTERN.find(level, pos);
        if (pattern == null) return;
        HayGolem golem = ModEntityTypes.HAY_GOLEM.get().create(level);
        if (golem == null) return;
        ModUtils.spawnGolemInWorld(level, pattern, golem, pattern.getBlock(1, 2, 0).getPos());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_COLOR, (byte) 0);
        builder.define(ID_SHEARED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("color", this.getColor().name().toLowerCase(Locale.ROOT));
        compound.putBoolean("sheared", this.isSheared());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("color")) {
            this.setColor(Color.valueOf(compound.getString("color").toUpperCase(Locale.ROOT)));
        }
        this.setSheared(compound.getBoolean("sheared"));
    }

    @Override
    public boolean canTarget() {
        return false;
    }

    public Color getColor() {
        return Color.values()[this.entityData.get(ID_COLOR)];
    }

    public void setColor(Color color) {
        this.entityData.set(ID_COLOR, (byte) color.ordinal());
    }

    public boolean isSheared() {
        return this.entityData.get(ID_SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(ID_SHEARED, sheared);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.HAY_GOLEM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.HAY_GOLEM_DEATH.get();
    }

    @Override
    public Item getRepairItem() {
        return Items.WHEAT;
    }

    @Override
    public float getRepairItemHealAmount() {
        return 10;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setColor(level.getRandom().nextBoolean() ? Color.GREEN : Color.RED);
        return super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(0.5f, 0, 0.5f);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!isSheared() && !level().isClientSide()) {
            if (stack.is(Items.SHEARS)) {
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, getSlotForHand(hand));
                }
                shear(SoundSource.PLAYERS);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void shear(SoundSource source) {
        playSound(SoundEvents.SNOW_GOLEM_SHEAR);
        setSheared(true);
        if (!this.level().isClientSide()) {
            this.spawnAtLocation(new ItemStack(Items.CARVED_PUMPKIN), this.getEyeHeight());
        }
    }

    @Override
    public boolean readyForShearing() {
        return !isSheared();
    }

    public enum Color {
        GREEN,
        RED,
    }
}
