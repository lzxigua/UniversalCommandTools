**English** | [简体中文](./resourcepack-tutorial.zh-CN.md)

# Resource pack tutorial: custom command weapon models

Universal Command Tools writes `CustomModelData: 21320` to every converted item. The mod ships no textures, so the default appearance stays unchanged; a resource pack can replace the look of any converted item by adding a `custom_model_data` override with that value to the item's model.

Recommended convention: keep `assets/minecraft/` (or `assets/<namespace>/` for modded items) for the **model JSON being overridden** only, and put your own models and textures under your own namespace. The example packs use the mod's ID, `universal_command_tools`, which is a convenient and stable namespace.

## 1. Pack skeleton

```text
MyCommandPack/
├── pack.mcmeta
└── assets/
    ├── minecraft/
    │   └── models/item/<vanilla_item>.json      # model to override
    ├── <namespace>/
    │   └── models/item/<modded_item>.json       # model to override
    └── universal_command_tools/
        ├── models/item/<your_model>.json        # custom model
        └── textures/item/<your_texture>.png     # custom texture
```

`pack.mcmeta` for Minecraft 1.20.1:

```json
{
  "pack": {
    "pack_format": 15,
    "description": "My command weapon models"
  }
}
```

## 2. Vanilla items

1. Open the vanilla client jar of your launcher with any zip tool (e.g. 7-Zip); for the official launcher it is `.minecraft/versions/1.20.1/1.20.1.jar`.
2. Extract the item model, e.g. `assets/minecraft/models/item/netherite_sword.json`.
3. Put it at the **same path** inside your pack: `assets/minecraft/models/item/netherite_sword.json`.
4. Keep the original content such as `parent` and `textures` unchanged, and append `overrides` pointing to your custom model under `universal_command_tools`:

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "minecraft:item/netherite_sword"
  },
  "overrides": [
    {
      "predicate": { "custom_model_data": 21320 },
      "model": "universal_command_tools:item/netherite_command_block_sword"
    }
  ]
}
```

If the extracted model already has an `overrides` array (bows, crossbows, etc.), keep the existing entries and append yours instead of replacing the array.

## 3. Modded items

The only difference is the namespace of the **model being overridden**: modded items use the mod ID instead of `minecraft`.

For `coolmod:laser_gun`:

- model in the mod jar: `assets/coolmod/models/item/laser_gun.json`
- destination in your pack: `assets/coolmod/models/item/laser_gun.json` — **not** `assets/minecraft/models/item/laser_gun.json`

Everything else (keep `parent`/`textures`, append `overrides`) is identical; your custom model still lives under your own namespace:

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "coolmod:item/laser_gun"
  },
  "overrides": [
    {
      "predicate": { "custom_model_data": 21320 },
      "model": "universal_command_tools:item/my_command_gun"
    }
  ]
}
```

The mod ID is the namespace of the item's registry name (e.g. `coolmod:laser_gun` → `coolmod`). If the item model is generated at runtime and not stored in the jar, create the model file yourself with the same structure.

`uct-example-twilightforest-animated` is a real modded example (`twilightforest:fiery_sword`). Modded models may carry extra Forge fields such as `loader`, `forge_data` and `render_types`; keep them exactly as extracted and only append `overrides`.

## 4. Custom model and texture

Create `assets/universal_command_tools/models/item/my_command_sword.json`:

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "universal_command_tools:item/my_command_sword"
  }
}
```

Use `minecraft:item/handheld` for tools and `minecraft:item/generated` for flat items. Put the texture at `assets/universal_command_tools/textures/item/my_command_sword.png`.

For an animated texture, add `<texture>.png.mcmeta` next to it:

```json
{
  "animation": {
    "interpolate": true,
    "frametime": 10
  }
}
```

You can use any namespace instead of `universal_command_tools`; just keep the `model` reference, the model file path and the texture path consistent.

## 5. Walkthrough: Twilight Forest's Fiery Sword

Open the Twilight Forest mod jar, open the `assets` folder and confirm the namespace is `twilightforest`; then open `twilightforest/models/item/` and find the model file `fiery_sword.json`.

Go back to the `twilightforest` folder, open `textures/item/`, and find the texture `fiery_sword.png` and its animation file `fiery_sword.png.mcmeta`.

Now that the namespace is known, create the pack structure:

```text
uct-example-twilightforest-animated/
├── pack.mcmeta
└── assets/
    ├── twilightforest/
    │   └── models/item/
    └── universal_command_tools/
        ├── models/item/
        └── textures/item/
```

Copy `fiery_sword.json` from the mod jar to `uct-example-twilightforest-animated/assets/twilightforest/models/item/`. Keep the existing content unchanged and only append `overrides`, pointing to the custom model under `universal_command_tools`:

```json
{
  "parent": "minecraft:item/handheld",
  "forge_data": {
    "layers": {
      "0": {
        "block_light": 15,
        "sky_light": 15
      }
    }
  },
  "loader": "forge:item_layers",
  "render_types": {
    "minecraft:translucent": [
      0
    ]
  },
  "textures": {
    "layer0": "twilightforest:item/fiery_sword"
  },
  "overrides": [
    {
      "predicate": {
        "custom_model_data": 21320
      },
      "model": "universal_command_tools:item/command_block_fiery_sword"
    }
  ]
}
```

Create `uct-example-twilightforest-animated/assets/universal_command_tools/models/item/command_block_fiery_sword.json`. Start by copying the content of the original `fiery_sword.json`. The Fiery Sword already has its own animated texture (the `fiery_sword.png.mcmeta` file proves it), so keep it and add one extra layer of your own animated (or static) texture:

```json
{
  "parent": "minecraft:item/handheld",
  "forge_data": {
    "layers": {
      "0": {
        "block_light": 15,
        "sky_light": 15
      }
    }
  },
  "loader": "forge:item_layers",
  "render_types": {
    "minecraft:translucent": [
      0
    ]
  },
  "textures": {
    "layer0": "twilightforest:item/fiery_sword",
    "layer1": "universal_command_tools:item/command_block_fiery_sword"
  }
}
```

Draw the command animation texture based on `fiery_sword.png`. Remove the unmodified blade and handle and keep only the modified guard, so it does not cover the Fiery Sword's own animation. Rename it to `command_block_fiery_sword.png`, put it in `uct-example-twilightforest-animated/assets/universal_command_tools/textures/item/`, and create `command_block_fiery_sword.png.mcmeta`:

```json
{
  "animation": {
    "interpolate": true,
    "frametime": 10
  }
}
```

Finally write `pack.mcmeta`:

```json
{
  "pack": {
    "pack_format": 15,
    "description": "Custom command block fiery sword texture example"
  }
}
```

Done. Put the pack into the game's `resourcepacks` folder, load it and test.

## 6. Example packs

Three minimal, ready-to-copy examples live under `ExampleResourcepacks/`:

- `uct-example-vanilla` — static texture; overrides `minecraft:item/netherite_sword`.
- `uct-example-vanilla-animated` — same, plus a `.png.mcmeta` for the animated texture.
- `uct-example-twilightforest-animated` — the Twilight Forest walkthrough above; overrides `twilightforest:item/fiery_sword` and adds the custom texture as `layer1`, keeping the original flickering glow.

```text
ExampleResourcepacks/uct-example-vanilla/
├── pack.mcmeta
└── assets/
    ├── minecraft/
    │   └── models/item/netherite_sword.json
    └── universal_command_tools/
        ├── models/item/netherite_command_block_sword.json
        └── textures/item/netherite_command_block_sword.png
```

`uct-example-twilightforest-animated` follows the modded-item layout, with the overridden model under the mod's namespace:

```text
ExampleResourcepacks/uct-example-twilightforest-animated/
├── pack.mcmeta
└── assets/
    ├── twilightforest/
    │   └── models/item/fiery_sword.json
    └── universal_command_tools/
        ├── models/item/command_block_fiery_sword.json
        └── textures/item/
            ├── command_block_fiery_sword.png
            └── command_block_fiery_sword.png.mcmeta
```

Note how the `minecraft`/`twilightforest` folders only contain the model JSON being overridden; the custom models and textures live under `universal_command_tools`.

## 7. Test and troubleshoot

1. Put the folder (or `.zip`) into `.minecraft/resourcepacks/`.
2. Enable it in Options → Resource Packs.
3. Convert an item in the anvil with the Enchanted Command Block Book. It should now use your model.

If nothing changes:

- Make sure the JSON syntax is valid.
- `pack_format` must be `15` for 1.20.1.
- The model path must match the item's namespace: `minecraft` for vanilla, the mod ID for modded items.
- The predicate key is `custom_model_data` and the value is `21320`.
- The item must have been converted by the anvil (the tag is what the mod adds).
- If another pack also overrides the same model, the pack higher in the list wins.

See also: [README](../README.md), [Resource pack – Minecraft Wiki](https://minecraft.wiki/w/Resource_pack).
