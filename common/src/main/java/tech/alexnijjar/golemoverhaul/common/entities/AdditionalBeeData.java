package tech.alexnijjar.golemoverhaul.common.entities;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface AdditionalBeeData {

    @Nullable
    UUID golemoverhaul$getOwner();

    void golemoverhaul$setOwner(UUID owner);

    boolean golemoverhaul$hasGolemHive();
}
