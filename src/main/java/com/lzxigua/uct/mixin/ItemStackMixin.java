package com.lzxigua.uct.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Makes any item carrying the {@code IsCommandItem} NBT flag pass the command-tool
 * item tag check used by Cracker's Wither Storm Mod, so the item is treated as a
 * real command weapon everywhere the mod performs that check.
 *
 * <p>Only vanilla {@link ItemStack} is touched; the tag is matched by name, so no
 * CWSM class is referenced at compile time. Marked items also report {@link Rarity#EPIC}
 * so the name keeps its light-purple colour even after the player renames it.</p>
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void uct$commandItemRarity(CallbackInfoReturnable<Rarity> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.hasTag() && self.getTag().getBoolean("IsCommandItem")) {
            cir.setReturnValue(Rarity.EPIC);
        }
    }

    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void uct$allowCommandBlockDamage(TagKey<Item> tag, CallbackInfoReturnable<Boolean> cir) {
        if (tag.location().getNamespace().equals("witherstormmod")
                && tag.location().getPath().equals("command_block_tools")) {
            ItemStack self = (ItemStack) (Object) this;
            if (self.hasTag() && self.getTag().getBoolean("IsCommandItem")) {
                cir.setReturnValue(true);
            }
        }
    }
}
