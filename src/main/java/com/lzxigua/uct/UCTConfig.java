package com.lzxigua.uct;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Single-file JSON configuration: {@code config/universal_command_tools.json}.
 *
 * <p>Written with pretty printing on first launch (one entry per line) and
 * re-readable at runtime through {@code /uct reload}. A plain ForgeConfigSpec
 * could not satisfy either requirement: NightConfig always serialises lists on a
 * single line and offers no public runtime reload, so the addon manages this
 * small file itself.</p>
 */
public final class UCTConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Tagged items the anvil refuses to convert; kept out of the way of CWSM's own recipes. */
    private static final List<String> DEFAULT_BLACKLIST = List.of(
            "witherstormmod:command_block_book",
            // Diamond-tier command tools
            "witherstormmod:command_block_sword",
            "witherstormmod:command_block_pickaxe",
            "witherstormmod:command_block_axe",
            "witherstormmod:command_block_shovel",
            "witherstormmod:command_block_hoe",
            // Wooden command tools
            "witherstormmod:wooden_command_block_sword",
            "witherstormmod:wooden_command_block_pickaxe",
            "witherstormmod:wooden_command_block_axe",
            "witherstormmod:wooden_command_block_shovel",
            "witherstormmod:wooden_command_block_hoe",
            // Stone command tools
            "witherstormmod:stone_command_block_sword",
            "witherstormmod:stone_command_block_pickaxe",
            "witherstormmod:stone_command_block_axe",
            "witherstormmod:stone_command_block_shovel",
            "witherstormmod:stone_command_block_hoe",
            // Iron command tools
            "witherstormmod:iron_command_block_sword",
            "witherstormmod:iron_command_block_pickaxe",
            "witherstormmod:iron_command_block_axe",
            "witherstormmod:iron_command_block_shovel",
            "witherstormmod:iron_command_block_hoe",
            // Gold command tools
            "witherstormmod:gold_command_block_sword",
            "witherstormmod:gold_command_block_pickaxe",
            "witherstormmod:gold_command_block_axe",
            "witherstormmod:gold_command_block_shovel",
            "witherstormmod:gold_command_block_hoe");

    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("universal_command_tools.json");

    public static final UCTConfig INSTANCE = new UCTConfig();

    private boolean spaceAfterPrefix = false;
    private Set<String> blacklist = new LinkedHashSet<>(DEFAULT_BLACKLIST);

    private UCTConfig() {
    }

    /**
     * Reads the config file. If it does not exist yet, writes the defaults first.
     * Errors are logged and the current in-memory values are kept, so a broken
     * edit never takes the game down.
     */
    public synchronized void load() {
        try {
            if (!Files.exists(FILE)) {
                writeDefaults();
                return;
            }
            try (Reader reader = Files.newBufferedReader(FILE)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (json.has("spaceAfterPrefix")) {
                    this.spaceAfterPrefix = json.get("spaceAfterPrefix").getAsBoolean();
                }
                if (json.has("blacklist") && json.get("blacklist").isJsonArray()) {
                    Set<String> loaded = new LinkedHashSet<>();
                    JsonArray array = json.getAsJsonArray("blacklist");
                    for (var element : array) {
                        loaded.add(element.getAsString());
                    }
                    this.blacklist = loaded;
                }
            }
        } catch (Exception e) {
            UniversalCommandTools.LOGGER.warn("Failed to read {}, keeping current values", FILE, e);
        }
    }

    private void writeDefaults() throws Exception {
        Files.createDirectories(FILE.getParent());
        JsonObject json = new JsonObject();
        json.addProperty("spaceAfterPrefix", false);
        JsonArray array = new JsonArray();
        DEFAULT_BLACKLIST.forEach(array::add);
        json.add("blacklist", array);
        try (Writer writer = Files.newBufferedWriter(FILE)) {
            GSON.toJson(json, writer);
        }
        UniversalCommandTools.LOGGER.info("Wrote default config to {}", FILE);
    }

    public boolean spaceAfterPrefix() {
        return this.spaceAfterPrefix;
    }

    public boolean isBlacklisted(String itemId) {
        return this.blacklist.contains(itemId);
    }
}
