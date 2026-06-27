---
title: Alchemical Replication and Transformation
navigation:
  title: 03 · Replication and Transformation
items:
  - "transmutatoria:transmutation_sigil_scroll"
  - "transmutatoria:transmutation_equation_scroll"
---

*Separate the earth from the fire, the subtle from the gross, gently and with great skill;*

*It rises from the earth to heaven, then descends again to earth, receiving the power of both the higher and the lower realms;*

## Scroll System Overview

Transmutation Scrolls are an alchemist's core tools. They enable **Alchemical Replication** (creating a copy of an item) and **Alchemical Transformation** (turning one item into another). Each kind of scroll has five tiers:

| Variant | Expiration | Durability | Description |
|---|---|---|---|
| Transmutation Scroll | Daily at 12:00 | 32 | Entry-level; suited to short-term tasks |
| Terrestrial Scroll | Daily at 12:00 | 64 | Intermediate; more durable |
| Lunar Scroll | Every 8 days (new moon at 0:00) | 128 | Advanced; expires on a lunar cycle |
| Solar Scroll | Never | 256 | Does not mutate; suited to long-term storage |
| Void Scroll | Never | Unbreakable | The ultimate scroll; never breaks |

- The **Transmutation Sigil Scroll** is used for Alchemical Replication.
- The **Transmutation Equation Scroll** is used for Alchemical Transformation.

## Sigil Scroll: Alchemical Replication

### Principle

Alchemical Replication treats an item as a "template" and uses essence reactions in the crucible to **create a new copy**. A corresponding **Alchemical Replication recipe** must exist.

### Activation

1. Hold the Sigil Scroll and right-click to open its interface.
2. Place the **item you want to copy** in the slot on the right.
3. If an Alchemical Replication recipe exists for that item, the scroll reads the recipe and activates.

After activation:

- The left side displays the other end of the recipe—the item required in the crucible's transformation input slot (usually the target item itself or a base material).
- The essence ring at the bottom shows how many essences are required.
- The scroll receives a random set of alchemy slots (the count is chosen randomly from the recipe's level range).

### Using It in the Crucible

- Catalyst slot: the activated Sigil Scroll.
- Transformation input slot: an item of **exactly the same type** as the one used to activate the scroll.
- Essence input slots: supply essences according to the hexagonal slot diagram shown on the HUD.

## Equation Scroll: Alchemical Transformation

### Principle

Alchemical Transformation converts one item into another. A corresponding **Alchemical Transformation recipe** must exist.

### Activation

1. Hold the Equation Scroll and right-click to open its interface.
2. Place the **material you want to transform** in the slot on the left.
3. If an Alchemical Transformation recipe exists for that item, the scroll reads the recipe and activates.

After activation:

- The right side previews the resulting item.
- The level is calculated from the **alchemy output item**.

### Using It in the Crucible

- Catalyst slot: the activated Equation Scroll.
- Transformation input slot: an item **exactly matching** the material used to activate the scroll.
- Essence input slots: supply essences according to the hexagonal slot diagram shown on the HUD.

::: warning Note
The more slots a recipe has, the more essences it requires and the harder complete annihilation becomes. Recipes for high-level items usually require more slots.
:::

## Scroll Expiration and Mutation

### Expiration

An activated scroll **expires** as time passes while it is in a player's inventory. Its expiration time is determined by its `ExpireInfo`:

- **DEFAULT**: expires every 24,000 ticks (one in-game day), with an offset of 6,000 ticks (approximately noon).
- **LUNAR**: expires every 192,000 ticks (eight in-game days), with an offset of 114,000 ticks (the new moon at 0:00).
- **Solar/Void Scrolls**: have no expiration component and never expire.

Expiration is also checked while a scroll sits in an idle crucible.

### Mutation Effects

When a scroll expires, it **mutates**:

1. A slot's target essence may be replaced at random. Mutation probability = `1 - 0.2^expiration count`. Repeated expirations rapidly increase the chance of mutation.
2. Every slot's essence display is hidden again (returning to `?`).
3. The magic number is reset, so arrows on directional slots may point in new directions.

::: tip Tip
After a mutation, every essence becomes unknown again, and you must test reactions to rediscover each slot's target. Use a scroll soon after obtaining it, or choose a Solar or Void Scroll to avoid mutation.
:::

## Entropy and Scroll Durability

### Accumulating Entropy

Each slot reaction adds entropy according to the essence relationship:

| Relationship | Entropy Added |
|---|---|
| Double restraint / doubly restrained | +2 |
| Restraint / restrained / mutual restraint / symbiosis | +1 |
| Neutral | +0 |
| Same (triggers damage) | +0 |

### Durability Cost

When a slot triggers damage, the scroll loses **`1 + current entropy / 4`** durability (using integer division).

The higher the entropy, the more durability each subsequent annihilation costs. A high-entropy reaction therefore demands a more durable scroll to annihilate every slot.

## [Essence Conversion and Reaction](../4_essence_conversion_and_reaction/index.md)

Manually control reactions between essences and manage crucible polarity.

