[English](./README.md) | **简体中文**

# 通用命令工具（Universal Command Tools）

《Cracker's Wither Storm Mod》（下称 CWSM）的附属模组：在铁砧里把**任意物品**与**命令方块附魔书**合成，就能把该物品变成可以对凋灵风暴命令方块造成伤害的命令武器。

## 特性

- **任意物品可用**——木棍、苹果、其他模组的物品都可以。
- **外观与原版命令武器一致**——淡紫色 EPIC 名称，改名后仍保持紫色；不斜体，并用 `HideFlags` 隐藏「无法破坏」提示。想要独特外观需要自己写资源包，见[资源包教程](docs/resourcepack-tutorial.zh-CN.md)。
- **前缀走译名**——直接复用原版命令方块的名称，自动跟随客户端语言；英文固定带空格，中文的空格由 `spaceAfterPrefix` 控制。
- **消耗 1 级经验 + 1 本附魔书**。
- **不影响原模组配方**——CWSM 自带的铁砧配方（如钻石剑 → 命令方块剑）优先处理。
- **可配置黑名单**——默认包含 CWSM 的全部命令武器，避免重复加工。
- **热重载**——游戏内 `/uct reload`，无需重启。
- **软依赖**——未安装 CWSM 时游戏正常启动，本模组不生效。

## 依赖

| 组件 | 版本 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.x（基于 47.4.10 构建） |
| Cracker's Wither Storm Mod | 4.x（功能生效的前置） |

## 使用方法

1. 把任意物品放在铁砧左侧，把命令方块附魔书（`witherstormmod:command_block_book`）放在右侧。
2. 取出结果：物品获得命令武器能力、隐藏的 `Unbreakable` 标签和淡紫色前缀。名称会使用物品的默认名，加工前改过的名字会被丢弃（与原模组命令武器一致）。
3. 每次消耗 1 级经验与 1 本附魔书。

已带标签的物品、CWSM 命令武器和附魔书本身不会被再次加工。加工后的物品在命令方块可受伤时正常生效。

## 配置

首次启动会生成 `config/universal_command_tools.json`：

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

- `spaceAfterPrefix`——仅影响本身不带空格的译名（目前是中文）：`false` 为 `命令方块木棍`，`true` 为 `命令方块 木棍`。英文始终保留空格。
- `blacklist`——铁砧拒绝加工的注册名列表。遇到冲突的模组物品可以加到这里。

修改后在游戏内执行 `/uct reload`（或重启）。重载前已加工的物品会保留当时的名字。

## 实现原理

- 以 `LOWEST` 优先级监听 `AnvilUpdateEvent`：CWSM 自己的铁砧配方先处理，已有结果时本模组直接跳过，只有未被匹配的组合才由本模组接管。处理器写入 `IsCommandItem:1b`、`Unbreakable:1b`、`HideFlags |= 4`，并用翻译键构造带样式的显示名。
- CWSM 通过其命令工具物品标签判断手持物品能否伤害命令方块。本模组用 Mixin 注入原版 `ItemStack.is(TagKey)`，让带 `IsCommandItem` 的物品通过该标签检查，从而在模组的各处命令武器检查中表现一致。

本模组不引用、不分发 CWSM 的任何类或资源；标签名以字符串匹配。

## 构建

```bash
./gradlew build
```

产物位于 `build/libs/universal_command_tools-<version>.jar`。

## 许可证

MIT——详见 [LICENSE](LICENSE)。

## 免责声明

爱好者制作的附属模组，与 nonamecrackers2 及 CWSM 团队无关。不包含原模组的任何资源或代码，且必须安装原模组才能生效。
