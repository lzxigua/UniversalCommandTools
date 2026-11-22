package com.lzxigua.uct;

import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(UniversalCommandTools.MOD_ID)
public class UniversalCommandTools {
    public static final String MOD_ID = "universal_command_tools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public UniversalCommandTools() {
        UCTConfig.INSTANCE.load();
    }
}
