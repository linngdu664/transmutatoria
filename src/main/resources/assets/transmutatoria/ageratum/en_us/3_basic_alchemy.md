---
title: Basic Alchemy
navigation:
  title: Basic Alchemy
items:
  - "transmutatoria:transmutation_crucible"
---

# Basic Alchemy

<color=#941400>Separate earth from fire, the subtle from the gross, gently and with great care;</color>

<color=#941400>It ascends from the earth to the heavens, and again descends to the earth, receiving the power of things above and things below;</color>

## Overview

In the first two chapters, you accumulated essence metals and understood restraint and symbiosis. This chapter puts the crucible into actual use, changing and synthesizing essence metals through two reactions, and introduces the attribute of **polarity**.

## Basic Crucible Operation

You already used the crucible in the first chapter, gaining an initial impression of its HUD and basic operations - adding water, dropping in the catalyst and materials, waiting for the reaction, and taking out the product.

The two reactions in this chapter operate in the same way as Transmutation Decomposition. The difference is that after you put in a Transmutation Crystal or essence metal as the catalyst, an **essence slot diagram** appears in the middle of the HUD.

<row>
<item id="transmutatoria:transmutation_crucible"/>
</row>

Here are two additional useful operations:

- Holding a storage box and right-clicking the crucible can put the currently selected essence directly into an essence slot. Use the **mouse wheel** to switch the storage box's currently selected essence.
- Before a reaction starts or while it is running, **Shift + empty-hand right-click** the crucible to take back materials and the catalyst.

## Essence Reaction

**Catalyst: Transmutation Crystal**

<row>
<item id="transmutatoria:transmutation_crystal"/>
</row>

This is the most basic reaction type - two essence metals directly react and change state according to their relationship.

1. Drop a **Transmutation Crystal** into the catalyst slot.
2. Drop two **essence metals** into the essence slots.
3. The reaction starts automatically and completes quickly.

The two metals change simultaneously according to their restraint and symbiosis relationship:

| Relationship | Change |
|---------|------|
| A restrains B | A +1, B -1 |
| A doubly restrains B | A +2, B -2 |
| Symbiosis | Both sides +1 |
| Mutual restraint | Both sides -1 |
| Neutral | Both sides unchanged |

After the reaction ends, right-click the crucible to take out the two changed metals.

**Example**: Animercury restrains Eclipsium. Citrinitas-infused Animercury reacting with Nigredo-tainted Eclipsium results in: Citrinitas-infused Animercury **-1** -> Albedo-infused Animercury; Nigredo-tainted Eclipsium **+1** -> Eclipsium.

<row>
<item id="transmutatoria:citrinitas_animercury"/>
<item id="transmutatoria:nigredo_eclipsium"/>
</row>

<row>
<item id="transmutatoria:albedo_animercury"/>
<item id="transmutatoria:eclipsium"/>
</row>

## Essence Fusion

**Catalyst: any essence metal**

Use the catalyst metal's restraint relationship to gather all of its restrained targets and synthesize the catalyst metal itself.

Any essence metal can be used as a fusion catalyst, for example:

<row>
<item id="transmutatoria:eclipsium"/>
<item id="transmutatoria:abyssion"/>
<item id="transmutatoria:fulgurzinc"/>
<item id="transmutatoria:pandemonium"/>
</row>

1. Drop one **essence metal** into the catalyst slot.
2. Drop all metals **restrained and doubly restrained** by the catalyst metal into the essence slots (all must be covered).
3. The reaction starts automatically and completes quickly. The output slot produces the catalyst metal, and all input essences are consumed.

Different metals require different amounts of fusion materials - the wider a metal's restraint coverage, the more types it requires. Pandemonium restrains all 11 metals, so synthesizing it requires putting them all in.

## Polarity

Polarity is a core attribute of the crucible, ranging from **-50 to 50**, initially 0. The **outer pointer** of the dial in the upper right of the HUD indicates the current polarity of the crucible.

Essence Reaction changes crucible polarity:

| Relationship | Polarity Change |
|------|---------|
| Symbiosis | +2 |
| Mutual restraint | -2 |
| Other relationships | 0 |

Essence Fusion does not change polarity.

::: warning Note
When polarity exceeds the range, the crucible is destroyed: **polarity > 50 becomes a redstone block**, and **polarity < -50 becomes an Alchemical Dross Block**. All items in the crucible drop.
:::

---

After mastering Essence Reaction and Essence Fusion, you can freely change and synthesize essence metals. The next chapter introduces transmutation scrolls - using the power of essences to replicate and transform all things.
