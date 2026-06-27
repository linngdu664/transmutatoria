---
title: Essence Fundamentals
navigation:
  title: 01 · Essence Fundamentals
items:
  - "transmutatoria:transmutation_crystal"
---

*True, without falsehood, certain and most true;*

*That which is below is like that which is above, and that which is above is like that which is below, to accomplish the miracle of the One;*

*All things were born from the One, brought forth from the One through division;*

## Overview

The first step in alchemy is obtaining a **Transmutation Crystal**, the starting material for the entire mod. Use it to craft a Transmutation Crucible, then decompose more crystals with Eyes of Ender to enter the world of **essence metals**.

## Creating a Transmutation Crystal

### Materials

| Material | Quantity |
|---|---|
| Redstone Dust | 1 |
| Gunpowder | 1 |
| Glowstone Dust | 1 |
| Block of Emerald | 1 (not consumed) |
| Cauldron | 1 (filled with water) |

### Steps

1. Place a **Block of Emerald** on the ground.
2. Place a **Cauldron** on top of it and fill the cauldron with a Water Bucket.
3. **Drop** Redstone Dust, Gunpowder, and Glowstone Dust into the cauldron (press Q to drop the items into it).
4. When all three touch the water, they transform into a **Transmutation Crystal**.

### Crafting the Transmutation Crucible

Craft a **Transmutation Crucible** at a crafting table with a Transmutation Crystal, two Gold Ingots, and a Cauldron:

| Material | Position |
|---|---|
| Gold Ingot | Left and right |
| Transmutation Crystal | Center |
| Cauldron | Below |

::: tip Tip
Creating Transmutation Crystals does not consume the Block of Emerald or the Cauldron. Reuse the same apparatus for batch production.
:::

## Transmutation Decomposition

### Catalyst: Eye of Ender

This is the only initial way to obtain essence metals: the Eye of Ender "observes" a Transmutation Crystal and reveals the essences within.

**Procedure:**

1. Place the Transmutation Crucible on the ground and fill it with a Water Bucket (up to 1,000 mB).
2. Put an **Eye of Ender** in the catalyst slot.
3. Put a **Transmutation Crystal** in the transformation input slot.
4. The reaction starts automatically and takes **10 ticks** (0.5 seconds).

**Products:**

| Chance | Product |
|---|---|
| 99% | One random base-state essence metal from the first eleven |
| 1% | **Pandemonium** |

Each reaction consumes **20 mB of water**. Prepare plenty of Transmutation Crystals and Eyes of Ender and decompose them in batches to obtain a varied collection of essence metals.

::: warning Note
The reaction will not start if the crucible contains less than 20 mB of water. Make sure it has enough.
:::

## Essence Metals

There are **twelve essence metals**, divided among four elemental pillars, with three metals in each.

### The Twelve Metals of the Four Pillars

| Pillar | Codes | Essence Metals |
|---|---|---|
| Astral Fire | A/B/C | Eclipsium, Lunargent, Astrotite |
| Abyssal Water | D/E/F | Abyssion, Animercury, Necroplumb |
| Decaying Earth | G/H/I | Sanguibronze, Venotite, Ossantimony |
| Tempest Air | J/K/L | Fulgurzinc, Chronoplatinum, Pandemonium |

### Restraining Relationships

Essences have two layers of restraining relationships: **the basic relationships between pillars** and **elemental relationships between individual metals**. Together they form the final reaction table.

#### Basic Relationships Between Pillars

| Restraining Pillar | Restrained Pillar |
|---|---|
| Astral Fire | Tempest Air |
| Tempest Air | Decaying Earth |
| Decaying Earth | Abyssal Water |
| Abyssal Water | Astral Fire |

These restraints form a cycle: Astral Fire → Tempest Air → Decaying Earth → Abyssal Water → Astral Fire.

#### Elemental Restraining Relationships

In addition to the relationships between pillars, certain metals have extra restraining relationships:

| Restraining Metal | Restrained Metal(s) |
|---|---|
| Necroplumb (F) | Eclipsium (A), Ossantimony (I) |
| Sanguibronze (G) | Lunargent (B) |
| Abyssion (D) | Animercury (E) |
| Venotite (H) | Sanguibronze (G) |
| Ossantimony (I) | Venotite (H) |
| Chronoplatinum (K) | Astrotite (C) |

#### Elemental Symbiosis

Some pairs of metals are symbiotic: both metals increase in state when they react.

| Metal A | Metal B |
|---|---|
| Lunargent (B) | Venotite (H) |
| Abyssion (D) | Fulgurzinc (J) |
| Sanguibronze (G) | Ossantimony (I) |

#### Pandemonium

Pandemonium (L) is an extraordinary exception:

- It has only a **1%** chance to appear during Transmutation Decomposition.
- It is **unaffected by the relationships between pillars** and cannot be restrained by any essence.
- It **restrains all eleven other essences**.
- It is a key material in many advanced recipes and in ultimate alchemy.

## Emerald Tablet

Crafted at a crafting table from one Transmutation Crystal and four Emeralds.

The Emerald Tablet is an interactive reference tool. Right-click it to view the complete relationship between any two essence metals. It is invaluable when planning reaction sequences and managing polarity.

::: tip Tip
Hold the Emerald Tablet while standing before the crucible to consult the relationship table without memorizing all 66 pairs.
:::

## Essence States and Reactions

### Four States

| State | Name | Prefix | Description |
|---|---|---|---|
| < -1 | Alchemical Dross | — | Excessive deterioration; cannot be used again |
| -1 | Nigredo | `nigredo_*` | Deteriorated by one stage |
| 0 | Base state | No prefix | Fundamental form |
| 1 | Albedo | `albedo_*` | Activated by one stage |
| 2 | Citrinitas | `citrinitas_*` | Activated by two stages |
| > 2 | Redstone Dust | — | Excessive activation; reverts to vanilla redstone |

### Reaction Rules

When two essence metals react, each changes state according to their relationship:

| Relationship | self (slot 0) | other (slot 1) | Polarity Change |
|---|---|---|---|
| Symbiosis | +1 | +1 | +2 |
| Restraint | +1 | -1 | 0 |
| Double restraint | +2 | -2 | 0 |
| Restrained | -1 | +1 | 0 |
| Doubly restrained | -2 | +2 | 0 |
| Mutual restraint | -1 | -1 | -2 |
| Neutral | 0 | 0 | 0 |
| Same | 0 | 0 | 0 |

- The **restraining metal activates**: its state rises toward Albedo and Citrinitas.
- The **restrained metal deteriorates**: its state falls toward Nigredo and Alchemical Dross.
- A state below -1 becomes Alchemical Dross; a state above +2 becomes Redstone Dust.

### Example

Consider a reaction between base-state **Astrotite (C)** and base-state **Abyssion (D)**. Astrotite belongs to Astral Fire, while Abyssion belongs to Abyssal Water. Abyssal Water restrains Astral Fire, so Abyssion restrains Astrotite.

- Put Astrotite in slot 0 (`self`) and Abyssion in slot 1 (`other`).
- The relationship is **restrained**: self = -1, other = +1.
- Result: Astrotite **-1** → Nigredo-tainted Astrotite; Abyssion **+1** → Albedo-infused Abyssion.
- Polarity change: 0.

::: warning Note
**Insertion order determines which metal is `self` and which is `other`.** Reversing their positions—Abyssion in slot 0 and Astrotite in slot 1—changes the relationship to **restraint** rather than restrained, completely reversing the result.
:::

## [Basic Alchemy](../2_basic_alchemy/index.md)

The essential knowledge every apprentice alchemist needs.

