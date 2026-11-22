package com.lzxigua.uct.command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.lzxigua.uct.UCTConfig;
import com.lzxigua.uct.UniversalCommandTools;

@Mod.EventBusSubscriber(modid = UniversalCommandTools.MOD_ID)
public class UCTCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("uct")
                        .then(Commands.literal("reload")
                                .executes(context -> {
                                    UCTConfig.INSTANCE.load();
                                    context.getSource().sendSuccess(
                                            () -> Component.literal("Universal Command Tools: config reloaded"),
                                            true);
                                    return 1;
                                })));
    }
}
