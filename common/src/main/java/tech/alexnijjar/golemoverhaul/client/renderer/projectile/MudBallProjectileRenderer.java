package tech.alexnijjar.golemoverhaul.client.renderer.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.MudBallProjectile;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class MudBallProjectileRenderer extends GeoEntityRenderer<MudBallProjectile> {

    public MudBallProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntityTypes.MUD_BALL.get())));
    }
}
