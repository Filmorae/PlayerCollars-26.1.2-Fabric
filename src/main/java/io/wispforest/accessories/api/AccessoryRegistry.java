package io.wispforest.accessories.api;

import net.minecraft.world.item.Item;

public final class AccessoryRegistry {
    private AccessoryRegistry() {
    }

    public static void register(Item item, AccessoryItem accessory) {
        // Legacy no-op shim; accessories are registered through Trinkets Updated.
    }
}
