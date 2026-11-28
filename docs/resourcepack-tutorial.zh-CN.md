[English](./resourcepack-tutorial.md) | **简体中文**

# 资源包教程：自定义命令武器外观

Universal Command Tools 会给每个加工后的物品写入 `CustomModelData: 21320`。模组本身不附带任何贴图，所以默认外观不变；资源包只要给对应物品的模型加上值为 21320 的 `custom_model_data` 覆盖，就能替换成自己的外观。

推荐约定：`assets/minecraft/`（模组物品则是 `assets/<namespace>/`）只放**被覆盖的模型 JSON**；自己的模型和贴图统一放在自己的命名空间下。示例包用的是本模组的ModID `universal_command_tools`，这是一个方便且稳定的命名空间。

## 1. 资源包结构

```text
MyCommandPack/
├── pack.mcmeta
└── assets/
    ├── minecraft/
    │   └── models/item/<原版物品>.json          # 要覆盖的原版模型
    ├── <namespace>/
    │   └── models/item/<模组物品>.json          # 要覆盖的模组模型
    └── universal_command_tools/
        ├── models/item/<你的模型>.json          # 自定义模型
        └── textures/item/<你的贴图>.png         # 自定义贴图
```

1.20.1 的 `pack.mcmeta`：

```json
{
  "pack": {
    "pack_format": 15,
    "description": "我的命令武器模型"
  }
}
```

## 2. 原版物品

1. 用任意解压工具（如7-Zip）打开启动器的原版客户端 jar（官方启动器为 `.minecraft/versions/1.20.1/1.20.1.jar`）。
2. 提取物品模型，例如 `assets/minecraft/models/item/netherite_sword.json`。
3. 按**相同路径**放进资源包：`assets/minecraft/models/item/netherite_sword.json`。
4. 保持 `parent` 和 `textures` 等原内容不动，追加 `overrides`，指向 `universal_command_tools` 下的自定义模型：

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

如果提取出来的模型本来就有 `overrides` 数组（弓、弩等），保留原有条目，在末尾追加你自己的条目，不要整个替换。

## 3. 模组物品

唯一的区别是**要覆盖的模型**的命名空间：模组物品用ModID，而不是 `minecraft`。

以 `coolmod:laser_gun` 为例：

- 模组 jar 中的模型：`assets/coolmod/models/item/laser_gun.json`
- 资源包中的目标路径：`assets/coolmod/models/item/laser_gun.json`——**不是** `assets/minecraft/models/item/laser_gun.json`

其余部分（保留 `parent`/`textures`、追加 `overrides`）完全一样；自定义模型仍然放在你自己的命名空间下：

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

ModID 就是物品注册名的命名空间（如 `coolmod:laser_gun` 的 `coolmod`）。如果某个物品的模型是运行时生成的、jar 里没有对应 JSON，就需要按同样的结构自己创建模型文件。

`uct-example-twilightforest-animated` 就是一个真实的模组物品示例（`twilightforest:fiery_sword`）。模组模型可能带有 Forge 的 `loader`、`forge_data`、`render_types` 等额外字段，这些要原样保留，只追加 `overrides`。

## 4. 自定义模型与贴图

新建 `assets/universal_command_tools/models/item/my_command_sword.json`：

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "universal_command_tools:item/my_command_sword"
  }
}
```

工具类物品用 `minecraft:item/handheld`，扁平物品用 `minecraft:item/generated`。贴图放在 `assets/universal_command_tools/textures/item/my_command_sword.png`。

要做动画贴图，在旁边加 `<贴图>.png.mcmeta`：

```json
{
  "animation": {
    "interpolate": true,
    "frametime": 10
  }
}
```

命名空间不一定要用 `universal_command_tools`，换成别的也可以；只要 `model` 引用、模型文件路径和贴图路径保持一致即可。

## 5. 实操（以暮色森林的炽铁剑为例）

打开暮色森林的模组jar，打开 `assets` 文件夹，确认命名空间为 `twilightforest` ，继续打开 `twilightforest/models/item/` ，找到模型文件 `fiery_sword.json` 。

回到 `twilightforest` 文件夹，打开 `textures/item/` 文件夹，找到材质文件 `fiery_sword.png` 与动画纹理文件 `fiery_sword.png.mcmeta` 。

已知命名空间为 `twilightforest` ，据此创建资源包目录结构
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
将模型文件 `fiery_sword.json` 从模组jar中复制到 `uct-example-twilightforest-animated/assets/twilightforest/models/item/` ，保持前面原有内容不变，只追加 `overrides` 部分，指向 `universal_command_tools` 下的自定义模型：
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
创建 `uct-example-twilightforest-animated/assets/universal_command_tools/models/item/command_block_fiery_sword.json` ，先原样复制原模型文件 `fiery_sword.json` 的内容，然后从动画纹理文件 `fiery_sword.png.mcmeta` 的存在可以看出炽铁剑本身已经有自己的动画纹理了，所以我们保留它自己的动画纹理，只追加一层自己的动画纹理（也可以是静态纹理）：
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
以材质文件 `fiery_sword.png` 为基础绘制命令动画纹理，并去掉未修改的剑身、剑柄部分，只保留修改过的护手部分，以防覆盖住炽铁剑自己的动画纹理，随后重命名为 `command_block_fiery_sword.png` 放入 `uct-example-twilightforest-animated/assets/universal_command_tools/textures/item/` ，并创建 `command_block_fiery_sword.png.mcmeta` 文件：
```json
{
  "animation": {
    "interpolate": true,
    "frametime": 10
  }
}
```
最后编写 `pack.mcmeta` ：
```json
{
  "pack": {
    "pack_format": 15,
    "description": "自定义命令方块炽铁剑材质示例"
  }
}
```
制作完成，随后放入游戏的 `resourcepacks` 文件夹加载资源包并测试即可。


## 6. 示例资源包

仓库的 `ExampleResourcepacks/` 里有三个最小可用示例，可直接复制修改：

- `uct-example-vanilla`——静态贴图，覆盖 `minecraft:item/netherite_sword`。
- `uct-example-vanilla-animated`——同上，另带一个 `.png.mcmeta` 动画贴图。
- `uct-example-twilightforest-animated`——上文的暮色森林炽铁剑示例资源包，覆盖 `twilightforest:item/fiery_sword`，并把自定义贴图作为 `layer1` 叠加，保留原物品的明灭光效。

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

`uct-example-twilightforest-animated` 使用模组物品的布局，被覆盖的模型放在模组命名空间下：

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

注意 `minecraft`/`twilightforest` 文件夹里只有被覆盖的模型 JSON，自定义模型和贴图都放在 `universal_command_tools` 下。

## 7. 测试与排查

1. 把文件夹（或 `.zip`）放进 `.minecraft/resourcepacks/`。
2. 在「选项 → 资源包」里启用。
3. 用命令方块附魔书在铁砧里加工物品，应该就会显示你的模型。

如果没生效：

- 确认json语法正确。
- 1.20.1 的 `pack_format` 必须是 `15`。
- 模型路径的命名空间要和物品一致：原版用 `minecraft`，模组物品用ModID。
- 谓词键是 `custom_model_data`，值是 `21320`。
- 物品必须真的经过铁砧加工（模组是在加工时写入标签的）。
- 如果多个资源包覆盖同一个模型，列表里靠上的包优先。

另见：[README](../README.zh-CN.md)、[资源包 - 中文 Minecraft Wiki](https://zh.minecraft.wiki/w/%E8%B5%84%E6%BA%90%E5%8C%85)。
