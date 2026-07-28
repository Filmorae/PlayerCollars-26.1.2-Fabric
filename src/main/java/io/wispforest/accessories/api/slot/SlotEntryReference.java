package io.wispforest.accessories.api.slot;

import net.minecraft.world.item.ItemStack;

public record SlotEntryReference(ItemStack stack, Reference reference) {
    public SlotEntryReference(ItemStack stack) {
        this(stack, new Reference());
    }

    public static final class Reference {
        public void setStack(ItemStack stack) {
            // Legacy no-op shim; live slot mutation is handled by Trinkets Updated.
        }
    }
}
