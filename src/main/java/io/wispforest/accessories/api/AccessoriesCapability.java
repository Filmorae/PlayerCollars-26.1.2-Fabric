package io.wispforest.accessories.api;

import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public final class AccessoriesCapability {
    private AccessoriesCapability() {
    }

    // Legacy compatibility shim; Player Collars uses Trinkets Updated at runtime.
    public static AccessoriesCapability get(LivingEntity entity) {
        return null;
    }

    public List<SlotEntryReference> getEquipped(Predicate<ItemStack> predicate) {
        return List.of();
    }

    public List<SlotEntryReference> getAllEquipped() {
        return List.of();
    }
}
