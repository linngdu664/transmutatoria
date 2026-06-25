---
title: Essence Conversion and Reaction
navigation:
  title: 04 · Essence Conversion and Reaction
items:
  - "transmutatoria:transmutation_crystal"
---

*By this means you shall possess the glory of the whole world, and all obscurity shall flee from you;*

*This is the mighty power of all powers;*

## Overview

Besides using scrolls for Alchemical Replication and Transformation, the Transmutation Crucible supports three **essence-level reactions**. These reactions require no scroll; different catalysts act directly upon the essences to convert or fuse them.

## Transmutation Decomposition

**Catalyst: Eye of Ender**

This is the first way to obtain essence metals: use an Eye of Ender to "observe" a Transmutation Crystal and reveal its essence nature.

1. Put an Eye of Ender in the catalyst slot and a Transmutation Crystal in the transformation input slot.
2. The reaction takes 10 ticks. There is a 99% chance of producing one random base-state essence metal from the first eleven, and a 1% chance of producing Pandemonium (an exceptionally precious metal that restrains every other essence and cannot itself be restrained).

::: tip Tip
Prepare plenty of Transmutation Crystals and Eyes of Ender, then decompose them in batches to build a varied supply of essence metals.
:::

## Essence Reaction

**Catalyst: Transmutation Crystal**

This directly reacts two essence metals without consuming a transformation input.

1. Put a Transmutation Crystal in the catalyst slot and one essence metal in each of the two essence input slots.
2. The reaction takes 20 ticks. Each metal changes state according to their relationship: the restraining metal activates (its state rises), while the restrained metal deteriorates (its state falls). Symbiosis raises both states; mutual restraint lowers both. The products are placed in the output slots.

::: warning Note
Insertion order determines which metal restrains and which is restrained. Reversing the same pair reverses the result.
:::

## Essence Fusion

**Catalyst: Any Essence Metal**

Use the catalyst metal's restraining relationships to gather all of its restrained metals into a single reaction.

1. Put an essence metal in the catalyst slot.
2. Fill the essence input slots with **every metal it restrains or doubly restrains** (all required types must be present).
3. The reaction takes 20 ticks. The catalyst is copied into the transformation output slot, and every input essence is cleared.

Different metals require different numbers of fusion materials: the broader a metal's reach, the more types it needs. Pandemonium restrains all eleven other metals, so fusing it requires every one of them.

## Managing States and Polarity

### Essence State Limits

| State | Name | Description |
|---|---|---|
| < -1 | Alchemical Dross | Excessive deterioration; unusable |
| -1 | Nigredo | Deteriorated by one stage |
| 0 | Base state | Fundamental form |
| 1 | Albedo | Activated by one stage |
| 2 | Citrinitas | Activated by two stages |
| > 2 | Redstone Dust | Excessively activated; becomes vanilla redstone |

### Polarity Limits

- **Polarity > 50**: the crucible becomes a Block of Redstone and drops its items.
- **Polarity < -50**: the crucible becomes an Alchemical Dross Block and drops its items.

### Controlling Polarity

- Right-click the crucible with the Philosopher's Stone to move its polarity 1 point toward 0.
- Symbiotic reactions shift polarity upward; mutually restraining reactions shift it downward.
- During scroll reactions, both slot type and essence relationship determine the polarity change.

## [Advanced Alchemy](../5_advanced/index.md)

Master all thirteen alchemy slot types and advanced strategies.

