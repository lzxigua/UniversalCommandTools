package com.lzxigua.uct.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import com.lzxigua.uct.UCTConfig;
import com.lzxigua.uct.UniversalCommandTools;

/**
 * Converts an arbitrary item into a command weapon when it is combined with the
 * Enchanted Command Block Book in an anvil.
 *
 * <p>Runs at {@link EventPriority#LOWEST} on purpose: Cracker's Wither Storm Mod
 * ships its own anvil recipes (e.g. diamond sword + book -> command block sword)
 * that may already have produced a result. By running last and bailing out when
 * {@link AnvilUpdateEvent#getOutput()} is non-empty, those recipes keep their
 * behaviour and only leftover combinations fall through to us.</p>
 */
@Mod.EventBusSubscriber(modid = UniversalCommandTools.MOD_ID)
public class AnvilHandler {
    public static final String NBT_KEY = "IsCommandItem";

    /**
     * The prefix reuses vanilla's command block name, so it is localised for every
     * language automatically. The separator keys only decide whether a space follows
     * it: English always keeps one, Chinese follows the config.
     */
    private static final String COMMAND_BLOCK_NAME = "block.minecraft.command_block";
    private static final String SEPARATOR_SPACED = "item.universal_command_tools.separator.spaced";
    private static final String SEPARATOR_TIGHT = "item.universal_command_tools.separator.tight";

    private static final ResourceLocation COMMAND_BOOK_ID = new ResourceLocation("witherstormmod", "command_block_book");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty()) {
            return;
        }

        // Never steal a result that CWSM's own anvil recipes already produced.
        if (!event.getOutput().isEmpty()) {
            return;
        }

        Item commandBook = ForgeRegistries.ITEMS.getValue(COMMAND_BOOK_ID);
        if (commandBook == null || !right.is(commandBook)) {
            return;
        }

        ResourceLocation leftId = ForgeRegistries.ITEMS.getKey(left.getItem());
        if (leftId == null) {
            return;
        }

        if (UCTConfig.INSTANCE.isBlacklisted(leftId.toString())) {
            return;
        }

        // Already converted once; don't stack the prefix or re-apply Unbreakable.
        if (left.hasTag() && left.getTag().getBoolean(NBT_KEY)) {
            return;
        }

        ItemStack output = left.copy();
        output.setCount(1);
        output.getOrCreateTag().putBoolean(NBT_KEY, true);
        output.getOrCreateTag().putBoolean("Unbreakable", true);
        // Hide the "Unbreakable" tooltip line the same way the real command tools do
        // (HideFlags bit 2). OR the bit in case the item already hides something else.
        output.getOrCreateTag().putInt("HideFlags", output.getOrCreateTag().getInt("HideFlags") | 4);

        String separatorKey = UCTConfig.INSTANCE.spaceAfterPrefix() ? SEPARATOR_SPACED : SEPARATOR_TIGHT;
        // Explicit italic=false: vanilla adds ITALIC to named items in getTooltipLines,
        // but a component's own style takes precedence over the inherited style.
        Style nameStyle = Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE).withItalic(false);
        MutableComponent name = Component.translatable(COMMAND_BLOCK_NAME)
                .withStyle(nameStyle)
                .append(Component.translatable(separatorKey))
                .append(left.getHoverName().copy().withStyle(Style.EMPTY.withItalic(false)));
        output.setHoverName(name);

        event.setOutput(output);
        event.setCost(1);
        event.setMaterialCost(1);
    }
}
