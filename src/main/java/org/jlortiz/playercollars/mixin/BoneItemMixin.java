package org.jlortiz.playercollars.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Items.class)
public abstract class BoneItemMixin {
    @Shadow
    private static Item registerItem(ResourceKey<Item> id) {
        throw new AssertionError();
    }

    @Shadow
    private static Item registerItem(ResourceKey<Item> id, Item.Properties properties) {
        throw new AssertionError();
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;"
            )
    )
    private static Item injectEquippableBone(ResourceKey<Item> id) {
        if (id.equals(ItemIds.BONE)) {
            return registerItem(id, new Item.Properties().equippable(EquipmentSlot.HEAD));
        }

        return registerItem(id);
    }
}
