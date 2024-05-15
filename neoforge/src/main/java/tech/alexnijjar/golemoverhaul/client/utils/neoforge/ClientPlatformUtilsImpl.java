package tech.alexnijjar.golemoverhaul.client.utils.neoforge;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public class ClientPlatformUtilsImpl {

    public static <T extends Entity> void registerRenderer(Supplier<EntityType<T>> entity, EntityRendererProvider<T> provider) {
        EntityRenderers.register(entity.get(), provider);
    }
}
