---
title: Advanced Alchemy
navigation:
  title: 05 · Advanced Alchemy
items:
  - "transmutatoria:transmutation_sigil_scroll"
  - "transmutatoria:transmutation_equation_scroll"
---

*Thus I am called Hermes Trismegistus, for I possess the three parts of the wisdom of the whole world;*

*What I have said concerning the operation of the Sun is complete;*

## The Alchemy Slot System

When a scroll activates, it generates a connected set of hexagonal alchemy slots according to the recipe level. Every slot has both a **target essence metal** and a **slot type** that determines its special behavior.

### Slot Generation Rules

- Slot count = a random value within the recipe's level range (2–24).
- Slots begin at (0, 0) and form connected hexagonal coordinates using a frontier expansion algorithm.
- Normal Slots always display their type.
- If the slot count is 8 or fewer, every slot is a Normal Slot.
- Above 8 slots, special slots may generate. At level 24, the chance is approximately 50%.
- A generated special slot may **hide its type** (appearing to be a Normal Slot) until a reaction triggers damage and reveals it.

## The Thirteen Slot Types

### 1. Normal Slot · NORMAL

The most basic slot. It follows the standard essence relationship rules without modification.

- Useful as an anchor for stabilizing the behavior of nearby special slots.

### 2. Deterioration Slot · DETERIORATION

After the base reaction: **state change -1, polarity change +1**.

- Always pushes the result toward Nigredo.
- Shifts polarity upward, helping offset accumulated negative polarity.
- Suited to positions where you expect the metal to be restrained repeatedly.

### 3. Activation Slot · ACTIVATION

After the base reaction: **state change +1, polarity change -1**.

- Always pushes the result toward Albedo or Citrinitas.
- Shifts polarity downward, helping offset accumulated positive polarity.
- The opposite of a Deterioration Slot; useful when an essence state must be raised.

### 4. Inversion Slot · INVERSION

Reverses both the **state change and polarity change** of the base reaction.

- Restraint becomes being restrained, and being restrained becomes restraint.
- Symbiosis produces the effect of mutual restraint, while mutual restraint produces the effect of symbiosis.
- Neutral relationships are unaffected (the inverse of 0 is still 0).

### 5. Diffusion Slot · DIFFUSION

Counts adjacent slots that already contain an uninhibited output, **passes this slot's state change** to those neighbors (giving each the same state change), and multiplies this slot's **polarity change by `1 + neighbor count`**.

- More neighbors produce a stronger amplification.
- Extremely powerful in dense slot diagrams, though the inhibited state of neighboring slots must be considered.

### 6. Inhibition Slot · INHIBITION

If this slot **does not trigger damage**, it marks adjacent slots whose outputs are still empty as **inhibited**.

- For an inhibited slot, state, polarity, and entropy changes are all **forced to zero**.
- Inhibition is one-way: an Inhibition Slot does not inhibit itself.
- Strategic use can lock down slots you do not want affected.

::: warning Note
If the Inhibition Slot triggers damage (annihilation) during this reaction, it applies no inhibition. You must ensure that its inserted essence does not match its target.
:::

### 7. Purge Slot · PURGE

If this slot's **state change is negative** (its inserted essence deteriorates), it **clears every adjacent output slot**.

- This deletes essence metals already produced by neighboring slots and can be highly destructive.
- It is best placed where a metal is expected to be strongly restrained.
- Combining it with a Deterioration Slot can create a "purification" effect.

::: danger Danger
A Purge Slot clears its neighbors midway through the reaction. Later slots may lose resonance references or exchange targets, so plan the reaction order carefully.
:::

### 8. Restoration Slot · RESTORATION

Forces `isClearItemStack = false`, meaning that **the item is retained even when the base reaction would clear it after triggering damage**.

- When the inserted and target essences match, the base reaction asks to clear the item and trigger damage. A Restoration Slot prevents the clearing, **but still triggers damage**.
- This lets you reveal the slot and advance the annihilation count while keeping the essence metal available for later reactions.
- This is an exceptionally valuable slot type; prioritize it when discovered.

### 9. Resonance Slot · RESONANCE

Adds **the state values of all essence metals already produced in adjacent slots** to this slot's state change.

- Nearby high-state metals (Citrinitas at +2) give a substantial state bonus.
- Nearby low-state metals (Nigredo at -1) weaken the slot instead.

### 10. Activity Slot · ACTIVITY

Has a **direction** (determined by the magic number and shown by an arrow once revealed). If this slot does not clear its item, it performs **another relationship reaction** with the adjacent output in the indicated direction:

- If the two essences are **the same**, both are cleared (annihilated).
- Otherwise, their states are adjusted according to their relationship.

An Activity Slot acts as a secondary reaction trigger capable of creating chain reactions.

### 11. Exchange Slot · EXCHANGE

Has a **direction**. Swaps its item with the indicated **non-empty adjacent output slot**.

- This rearranges output positions and has strategic value for later slots that depend on neighboring positions, such as Diffusion, Resonance, and Purge Slots.

### 12. Spin Slot · SPIN

Rotates the items in all non-empty neighboring output slots **one position clockwise**.

- This operation is added to `deferredTasks` (the deferred task list) and executes after every slot's base reaction is complete.
- It can adjust the output layout in bulk.

### 13. Unstable Slot · UNSTABLE

If this slot's **state change is nonzero**, a deferred task swaps it with **one random other slot** (preserving types while exchanging all other properties: coordinates, target essence, and reveal state).

- The entire slot diagram can therefore change dynamically during a reaction.
- The magic number determines the swap target, making it unpredictable.

## Slot Interaction Strategies

### Order Is Critical

Reactions execute in the **insertion order** of their essence metals (the `inputOrder`). This means:

1. **Fill Inhibition Slots first**: if an Inhibition Slot does not trigger damage, it can lock nearby empty slots and shield them from later side effects.
2. **Fill Purge Slots first**: if the slot's state change is negative, it clears its neighbors, which can then be filled again by later slots.
3. **Fill Resonance Slots later**: let neighboring slots produce their results first, then allow the Resonance Slot to add their state values.
4. **Fill Exchange and Spin Slots later**: let other slots produce outputs before rearranging the layout.

### Managing Annihilation

- Transformation can succeed only when **every slot triggers damage**.
- A Restoration Slot is the exception: it triggers damage without consuming its item, making it one of the most valuable slot types.
- If a slot targets an essence that is difficult to obtain, such as Pandemonium, prioritize securing a matching essence for it.
- Reactions that produce more entropy cause the scroll to lose more durability. Let double-restraint slots react early so later durability costs are not inflated by their entropy—or save them until last to reduce their effect on earlier slots.

### Polarity Window

Even if every slot is annihilated, the crucible's polarity must fall within the scroll recipe's `[minPolarity, maxPolarity]` range before an item can be produced. The default range is -50 to 50.

- If polarity would stray too far during the reaction, adjust the crucible beforehand with Essence Reactions.
- Activation and Deterioration Slots change the direction of polarity shifts; use them to fine-tune the final polarity.
- Use the Philosopher's Stone between reactions for precise adjustments.

### Special Slot Chances

| Total Slots | Chance of a Special Slot | Chance Its Type Is Shown |
|---|---|---|
| ≤ 8 | 0% | — |
| 12 | ~20% | ~57% |
| 16 | ~33% | ~50% |
| 20 | ~43% | ~43% |
| 24 | 50% | ~50% |

High-level recipes contain more slots and more special slots. Their reactions are correspondingly more complex and demand more careful planning.

## [The Great Work](../6_great_work/index.md)

The creation of the Philosopher's Stone and the art of ultimate alchemy.

