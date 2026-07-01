---
title: 源质金属
navigation:
  title: 源质金属
items:
  - "transmutatoria:emerald_tablet"
  - "transmutatoria:alchemist_storage_box"
---

# 源质金属

<color=#941400>太阳为父，月亮为母，从风孕育，从地养护；</color>

<color=#941400>世间一切完美之源就在此处，其能力从地上获取；</color>

## 概述

上一章你获得了第一批源质金属。本章深入理解它们之间的关系——相生相克是混沌炼金术的核心逻辑，贯穿后续所有反应。

## 相生相克

### 四柱克制

回顾上一章的十二金与四柱归属：

| 四柱 | 源质金属 |
|------|---------|
| 星火 | 蚀日金、泣月银、灾星钛 |
| 渊水 | 渊海钢、幻魂汞、冥沼铅 |
| 腐土 | 棘血铜、鸩林锡、骸骨锑 |
| 劫风 | 怒雷锌、凝时铂、无相源金 |

<row>
<item id="transmutatoria:eclipsium"/>
<item id="transmutatoria:lunargent"/>
<item id="transmutatoria:astrotite"/>
</row>

<row>
<item id="transmutatoria:abyssion"/>
<item id="transmutatoria:animercury"/>
<item id="transmutatoria:necroplumb"/>
</row>

<row>
<item id="transmutatoria:sanguibronze"/>
<item id="transmutatoria:venotite"/>
<item id="transmutatoria:ossantimony"/>
</row>

<row>
<item id="transmutatoria:fulgurzinc"/>
<item id="transmutatoria:chronoplatinum"/>
<item id="transmutatoria:pandemonium"/>
</row>

柱与柱之间存在固定的克制循环：

**星火 → 劫风 → 腐土 → 渊水 → 星火**

当两种金属发生反应时：克制方活化（状态提升），被克制方劣化（状态降低）。例如渊水柱的幻魂汞遇到星火柱的蚀日金，幻魂汞活化、蚀日金劣化。

### 元素克制

在柱间克制之上，部分金属之间还存在额外的克制关系。两层叠加后可能使原本不克制的关系变为克制，也可能出现**双倍克制**，甚至形成**互相克制**。

### 共生

部分金属之间存在**共生**关系，反应时双方活化。例如泣月银与鸩林锡共生。

### 无相源金

无相源金是特殊例外：**克制所有其他 11 种源质，且不被任何源质克制**。它不受四柱关系约束，是高级炼金的关键材料。

以上为相生相克的核心规则。完整的克制与共生关系表见[附录·源质关系]。

## 翠绿石板

由 1 个嬗变结晶和 4 个绿宝石在工作台合成。

<row>
<item id="transmutatoria:emerald_tablet"/>
</row>

手持翠绿石板右键使用，可以查阅任意两种源质金属之间的完整克制关系。当你需要确认某种反应的结果时，它是最可靠的参考工具。

## 源质状态

每种源质金属都有四种状态：

| 名称 | 状态值 | 说明 |
|---|---|------|
| 黑化 | -1 | 劣化一阶 |
| 常态 | 0 | 基础形态，刚分解出的源质都是常态 |
| 白化 | 1 | 活化一阶 |
| 黄化 | 2 | 活化二阶 |

<row>
<item id="transmutatoria:nigredo_eclipsium"/>
<item id="transmutatoria:eclipsium"/>
<item id="transmutatoria:albedo_eclipsium"/>
<item id="transmutatoria:citrinitas_eclipsium"/>
</row>

状态超出范围后，金属被破坏：**低于 -1 变为残渣**，**高于 +2 变为红石粉**，两者都失去炼金用途。

<row>
<item id="transmutatoria:alchemical_dross"/>
<item id="minecraft:redstone"/>
</row>

源质的状态不影响其本质——黑化蚀日金和黄化蚀日金的克制关系和效力完全相同。

## 炼金术士储物盒

<row>
<item id="transmutatoria:nigredo_alchemist_storage_box"/>
<item id="transmutatoria:alchemist_storage_box"/>
<item id="transmutatoria:albedo_alchemist_storage_box"/>
<item id="transmutatoria:citrinitas_alchemist_storage_box"/>
</row>

储物盒是管理源质库存的核心工具，同时是物品和可放置的方块。

**合成与变体**：储物盒有四种变体——普通、黑化、白化、黄化——分别只收纳对应状态的源质金属。每个储物盒提供 12 个槽位，每种金属一个。

**自动收纳**：捡起源质金属时，物品栏中匹配状态的储物盒会自动吸入。

**手持使用**：手持储物盒右键打开，可以查看和整理存储的源质。手持储物盒对着炼金锅时，HUD 上方会出现源质轮盘，用于便捷投料。

::: tip 提示
储物盒放在地上是方块形态，Shift + 右键收回。详见[附录·炼金术士储物盒]。
:::

掌握了源质的关系、状态和存储方式，下一章将学习如何使用炼金锅进行实际的源质反应。
