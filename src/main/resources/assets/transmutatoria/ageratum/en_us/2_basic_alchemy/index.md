---
title: Basic Alchemy
navigation:
  title: 02 · Basic Alchemy
items:
  - "transmutatoria:transmutation_sigil_scroll"
  - "transmutatoria:transmutation_equation_scroll"
  - "transmutatoria:transmutation_crucible"
---

*The Sun is its father, the Moon its mother, the wind carried it in its womb, and the earth nourished it;*

*Here lies the source of all perfection in the world, its power drawn from the earth;*

## Overview

Once you understand the fundamentals of essences, you must learn to use **Transmutation Scrolls** to replicate and transform items, organize your essence metals with **Alchemist's Storage Boxes**, and operate the **Transmutation Crucible's HUD** to perform alchemy.

## Crafting Scrolls

Scrolls come in two functional types, each with five tiers. The basic Transmutation Scroll recipes are as follows:

### Transmutation Sigil Scroll

Used for **Alchemical Replication**—creating copies of items.

| Material | Purpose |
|---|---|
| Any Essence Metal | Provides alchemical affinity |
| Transmutation Crystal | Core catalyst |
| Paper | Bears the runes |
| Gold Nugget | Conducts alchemical energy |
| Glowstone Dust | Grants the power of the "sigil" |

### Transmutation Equation Scroll

Used for **Alchemical Transformation**—turning one item into another.

| Material | Purpose |
|---|---|
| Any Essence Metal | Provides alchemical affinity |
| Transmutation Crystal | Core catalyst |
| Paper | Bears the runes |
| Gold Nugget | Conducts alchemical energy |
| Redstone Dust | Grants the power of "transformation" |

The only difference between the two scrolls is the choice of Glowstone Dust or Redstone Dust, which determines whether the scroll performs replication or transformation.

### Scroll Tiers

Basic Transmutation Scrolls can be upgraded with additional materials:

| Variant | Durability | Expiration | Characteristic |
|---|---|---|---|
| Transmutation | 32 | Daily at 12:00 | Entry-level |
| Terrestrial | 64 | Daily at 12:00 | Moderate durability |
| Lunar | 128 | Every 8 days (new moon) | Long cycle |
| Solar | 256 | Never | Suited to long-term storage |
| Void | Unbreakable | Never | Ultimate scroll |

::: tip Tip
Higher-tier scrolls require more resources, but offer greater durability and more forgiving expiration rules. See [Alchemical Replication and Transformation](../3_replication_and_transformation/index.md) for details.
:::

## Activating a Scroll

A newly crafted scroll is **blank**. You must insert a target item to activate it.

### Activation Steps

1. **Hold the scroll and right-click** to open its interface.
2. Insert the target item:
   - **Sigil Scroll**: put the **item you want to copy** in the right slot. The scroll automatically searches for an Alchemical Replication recipe.
   - **Equation Scroll**: put the **material you want to transform** in the left slot. The scroll automatically searches for an Alchemical Transformation recipe.
3. If an alchemical rule exists for the item, the scroll **reads and consumes** it, then activates.

### Changes After Activation

- The interface previews the item on the other side of the recipe.
- An **essence ring** at the bottom displays how many essences are required (the number of slots).
- The scroll receives a random set of **alchemy slots**, each with a hidden target essence metal.
- Immediately after activation, every target essence is shown as **`?`** (unknown). Reveal them one by one through reactions in the crucible.

::: warning Note
The item inserted during activation is **consumed**. Make sure it is the correct one: insert the desired copy target into a Sigil Scroll, and the material to be transformed into an Equation Scroll.
:::

## Alchemist's Storage Box

The Alchemist's Storage Box is the central tool for organizing essence metals. It functions as both an **item** and a **placeable block**.

### Features

- Provides **twelve slots**, one bound to each essence metal.
- **Automatic storage**: when you pick up an essence metal, a matching Storage Box in your inventory or offhand automatically absorbs it.
- **Quick insertion**: hold a Storage Box and aim at a crucible to quickly insert the selected essence into its input slot.
- Hold `Alt` on the crucible HUD to expand the essence wheel and display the basic restraining relationships among the essences currently in the box.

### Four State Variants

There are four Storage Box variants, each able to hold essence metals in only its **corresponding state**:

| Storage Box | State | Accepts |
|---|---|---|
| Alchemist's Storage Box (base state) | 0 | Base-state essence metals |
| Nigredo Alchemist's Storage Box | -1 | Nigredo-tainted essence metals |
| Albedo Alchemist's Storage Box | 1 | Albedo-infused essence metals |
| Citrinitas Alchemist's Storage Box | 2 | Citrinitas-infused essence metals |

### Block Form

The Storage Box can be placed on the ground and used as a block:

- **Right-click the block**: opens the Storage Box menu, complete with a lid animation and sound.
- **Shift + right-click the block**: returns it to item form while preserving its contents in the item's NBT.

### Automatic Storage Priority

When you pick up an essence metal, the system searches for a matching Storage Box in this order:

1. Offhand
2. Inventory (in slot order)

The search stops at the first box of the correct state that has room.

## Operating the Transmutation Crucible

The Transmutation Crucible is the mod's central workstation. Looking at it displays a HUD that serves as the information hub for every alchemical operation.

### HUD Layout

| Area | Position | Display |
|---|---|---|
| Vertical bar | Left | Top: catalyst · Middle: transformation input · Bottom: transformation output |
| Hexagonal area | Center | Displays the alchemy slot diagram after scroll activation; otherwise empty |
| Hourglass dial | Upper right | Reaction progress · Current crucible polarity · Polarity range required by the scroll recipe |
| Metal tube | Far left | Current scroll durability (purple portion = estimated cost) |

### Catalysts and Slot Displays

The essence slot diagram in the center of the HUD changes with the catalyst:

| Catalyst | Display |
|---|---|
| Transmutation Crystal | Two essence reaction slots |
| Any Essence Metal | Slots for all metals restrained or doubly restrained by that metal |
| Activated Scroll | A connected hexagonal alchemy slot diagram (including arrows and type markers) |
| Philosopher's Stone / Eye of Ender | No essence slots (uses the transformation input slot) |

### Essence Wheel

When you hold an Alchemist's Storage Box and look at the crucible, a **twelve-slot circular wheel** appears at the top of the screen:

- The essence in the **center** is currently selected.
- **Scroll the mouse wheel** to change the selected essence.
- Hold `Alt` to expand the restraining relationship lines.

### Selecting and Filling Slots

- A **selection ring** on the essence input slots indicates the slot currently being operated.
- **Hold Shift and scroll** to change the selected slot.
- **Right-click** to insert the Storage Box's selected essence into the selected slot.
- You can also **drop essence metal items onto the crucible**, and they will automatically enter matching slots.

### Reaction Requirements

All of the following must be true before a reaction can begin:

1. The crucible contains enough water (at least 20 mB).
2. No reaction is currently in progress (the progress bar is at 0).
3. There are no uncollected products.
4. A catalyst has been inserted.
5. The required transformation input and/or essence inputs for that catalyst are in place.

Once every condition is met, the reaction begins automatically and the hourglass dial on the right displays its progress.

## Performing Alchemy

After activating a scroll, put it in the Transmutation Crucible's catalyst slot. The HUD displays the scroll's essence slot diagram—a connected arrangement of hexagonal slots, each marked with the essence metal it requires (initially unknown, then displayed once revealed).

### Procedure

1. **Insert the scroll**: drop the activated scroll into the crucible so it enters the catalyst slot.
2. **Add the transformation material**: for a Sigil Scroll, insert **the item you want to copy**; for an Equation Scroll, insert **the item you want to transform**. Its type must match the item used to activate the scroll.
3. **Fill the essence slots**: take essence metals from an Alchemist's Storage Box and fill every input slot shown on the HUD. Hold `Shift` and scroll the mouse wheel to change the active slot, then right-click to insert the essence currently selected in the box.
4. **Wait for the reaction**: once the number of inserted essences meets the scroll's requirement, the reaction begins automatically. The hourglass dial shows its progress.
5. **Collect the product**: when the reaction finishes, the product appears in the transformation output slot. Right-click the crucible to retrieve it.

### What Happens During a Reaction

Slots react one by one in the order their essences were inserted:

- Each slot calculates a state change from the relationship between the **inserted essence** and the slot's **target essence**. The basic rule is the same as an Essence Reaction (the restraining metal activates and the restrained metal deteriorates), but each slot's **special type** can alter that rule.
- During the reaction, input essences are consumed and produce essence outputs with the corresponding state changes.
- If an inserted essence **matches the slot's target essence**, the slot **triggers damage** (annihilation) and reveals its type and target essence. Every trigger damages the scroll by `1 + current entropy / 4` durability.
- The final item is produced **only if every slot triggers damage**, the crucible's polarity lies within the range required by the scroll recipe, and the transformation input still matches the scroll's requirement.
- Whether or not the final item is produced, all input essences are cleared after the reaction.

### One-Time and Reusable Scrolls

Scrolls may be **one-time** or **reusable**:

- **One-time scroll** (`one_time = true`): after producing an item successfully, the scroll in the catalyst slot is consumed.
- **Reusable scroll**: remains in the catalyst slot after a successful reaction, allowing another round once new transformation and essence inputs are supplied.

### Managing Polarity

Every alchemical reaction changes the crucible's polarity. If it leaves the range of ±50:

- Polarity > 50: the crucible becomes a **Block of Redstone**, dropping every item.
- Polarity < -50: the crucible becomes an **Alchemical Dross Block**, dropping every item.

Right-click the crucible with a **Philosopher's Stone** to move its polarity 1 point toward 0 for fine adjustment.

### Reading the HUD

- **Vertical bar on the left**: displays the catalyst, transformation input, and transformation output from top to bottom.
- **Hexagonal area in the center**: displays the scroll's essence slot diagram. Activated but unrevealed slots have gold borders; revealed slots show an essence icon and type marker.
- **Hourglass dial in the upper right**: displays current reaction progress. The outer ring indicates polarity.
- **Metal tube on the far left**: displays the scroll's current durability and accumulated entropy. The purple portion is the estimated durability cost of crafting the item.

::: tip Tip
If the scroll's essence slot diagram includes directional arrows (on Activity or Exchange Slots, for example), their actual directions are determined by the scroll's magic number. Directions may change whenever the scroll mutates or is reactivated. See [Advanced Alchemy](../5_advanced/index.md) for details.
:::

## [Alchemical Replication and Transformation](../3_replication_and_transformation/index.md)

Learn more about Sigil and Equation Scroll activation, recipe matching, level functions, and mutation.
