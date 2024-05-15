package tech.alexnijjar.golemoverhaul.common.entities;

import java.util.UUID;

public interface AdditionalBeeData {

    UUID golemoverhaul$getOwner();

    void golemoverhaul$setOwner(UUID owner);

    boolean hasGolemHive();
}
