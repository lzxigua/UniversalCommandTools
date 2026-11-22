**English** | [简体中文](./README.zh-CN.md)

# Universal Command Tools

An addon for [Cracker's Wither Storm Mod](https://www.curseforge.com/minecraft/mc-mods/crackers-wither-storm-mod) (CWSM) that turns **any item** into a command weapon.

Combine an item with the **Enchanted Command Block Book** in an anvil, and the result can damage the Wither Storm's command block exactly like the mod's built-in command tools.

## Features

- **Any item works** — sticks, apples, modded items, anything.
- **Same look and feel as the real command tools** — light-purple prefix, no italics, `Unbreakable` hidden with `HideFlags`.
- **Localised prefix** — the prefix is a translation key: English always renders `Command Block ` with a space, while Chinese renders `命令方块` and follows the `spaceAfterPrefix` setting.
- **Costs 1 XP level and 1 book** per conversion.
- **CWSM recipes stay untouched** — the mod's own anvil recipes (e.g. diamond sword → command block sword) are processed first and win.
- **Configurable blacklist** — CWSM's command weapons are listed by default so nothing is converted twice.
- **Hot reload** — `/uct reload` re-reads the config without restarting the game.
- **Soft dependency** — without CWSM the game still starts and this addon simply does nothing.

## Requirements

| Component | Version |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.x (built against 47.4.10) |
| Cracker's Wither Storm Mod | 4.x (required for the feature to do anything) |

## Usage

1. Put an item in the left anvil slot and the Enchanted Command Block Book (`witherstormmod:command_block_book`) in the right slot.
2. Take the result: the item becomes a command weapon, gains a hidden `Unbreakable` tag and a light-purple prefix (`Command Block ` in English, `命令方块`/`命令方块 ` in Chinese).
3. The operation costs 1 XP level and consumes 1 book.

Items that already have the tag, CWSM command weapons, and the command book itself are never converted again. Converted items work whenever the command block is vulnerable.

## Configuration

`config/universal_command_tools.json` is created on first launch:

```json
{
  "spaceAfterPrefix": false,
  "blacklist": [
    "witherstormmod:command_block_book",
    "witherstormmod:command_block_sword",
    "witherstormmod:wooden_command_block_sword",
    "..."
  ]
}
```

- `spaceAfterPrefix` — only matters for prefixes whose tight form has no space (currently Chinese): `false` gives `命令方块木棍`, `true` gives `命令方块 木棍`. English always keeps its space.
- `blacklist` — registry names the anvil refuses to convert. Add any conflicting modded item here.

Apply changes in game with `/uct reload` (or restart). Items converted before a reload keep the name they were given.

## How it works

- `AnvilUpdateEvent` at `LOWEST` priority: CWSM's own anvil recipes run first, and the handler bails out whenever a result is already set. Only unmatched combinations fall through. The handler writes `IsCommandItem:1b`, `Unbreakable:1b`, `HideFlags |= 4` and a styled display name built from a translation key.
- CWSM decides whether a held item may damage its command block through its command-tool item tag. A Mixin on vanilla `ItemStack.is(TagKey)` makes marked stacks pass that tag check, so the item behaves like a real command weapon everywhere the mod checks for one.

No CWSM class or asset is referenced or redistributed; the tag name is matched as a string.

## Building

```bash
./gradlew build
```

The jar is written to `build/libs/universal_command_tools-<version>.jar`.

## License

MIT — see [LICENSE](LICENSE).

## Disclaimer

Fan-made addon, not affiliated with nonamecrackers2 or the Cracker's Wither Storm Mod team. It contains no assets or code from the original mod, and requires the original mod to be installed.
