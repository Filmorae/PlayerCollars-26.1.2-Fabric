package org.jlortiz.playercollars.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Items.class)
public abstract class BoneItemMixin {
    @Shadow public static Item registerItem(ResourceKey<Item> key, Item.Properties settings) { return null; }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;", ordinal = 21))
    private static Item injectEquippableBone(ResourceKey<Item> key) {
        return registerItem(key, new Item.Properties().equippable(EquipmentSlot.HEAD));
    }
}