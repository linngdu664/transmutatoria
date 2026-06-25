---
title: The Great Work
navigation:
  title: 06 · The Great Work
items:
  - "transmutatoria:philosophers_stone"
  - "transmutatoria:prima_materia"
  - "transmutatoria:rubedo_matter"
---

*It is finished, as I bear witness, by the judgment of this world;*

*It is finished, through the miracle of almighty power;*

## The Great Work · Magnum Opus

The Great Work is the ultimate goal of alchemy: the creation of the **Philosopher's Stone**. It passes through four stages—Nigredo, Albedo, Citrinitas, and Rubedo—each requiring a specific polarity and number of essence slots. The entire process is the final test of every skill an alchemist possesses.

## Alchemical Cosmology

Before beginning the Great Work, you must understand the three primes and the four stages.

### The Three Primes

Ancient alchemy held that all matter was composed of three primes:

| Prime | Symbolism | Corresponding Item |
|---|---|---|
| Salt · Sal | Body, fixity, form | Salic Matrix (`salic_matrix`) |
| Mercury | Soul, fluidity, connection | Mercurial Matrix (`mercurial_matrix`) |
| Sulfur | Spirit, activation, transformation | Sulfuric Matrix (`sulfuric_matrix`) |

### Creating the Three Primes

The three matrices are created in sequence through Alchemical Transformation, with increasingly demanding polarity requirements:

| Matrix | Material | Slots | Polarity Requirement |
|---|---|---|---|
| Salic Matrix | Sand (`#minecraft:sand`) | 4 | ≤ 0 |
| Mercurial Matrix | Salic Matrix | 8 | ≥ 20 |
| Sulfuric Matrix | Mercurial Matrix | 13 | -1 to 1 |

::: tip Tip
The Sulfuric Matrix requires polarity between -1 and 1, an extremely narrow window. After creating the Mercurial Matrix, adjust the crucible's polarity with precision before attempting it.
:::

### Prima Materia

**Prima Materia** is the primordial foundation of all matter. Craft it at a crafting table from one Transmutation Crystal and four Cobblestone:

```
      Cobblestone
Cobblestone Crystal Cobblestone
      Cobblestone
```

Prima Materia is the **starting material** of the Great Work.

## The Four Stages

### Stage One: Nigredo

> *"That which is below is like that which is above, and that which is above is like that which is below"*

Nigredo begins the Great Work: primordial matter is broken down in darkness, corrupted, and returned to chaos.

| Condition | Value |
|---|---|
| Transformation material | Prima Materia (`prima_materia`) |
| Product | Nigredo Matter (`nigredo_matter`) |
| Slots | 3 |
| Required polarity | **-50 to -10** (must be negative) |

This is the simplest stage. It has few slots, but requires negative crucible polarity. Before the reaction, lower polarity with mutually restraining essence pairs or reactions that favor deterioration.

### Stage Two: Albedo

> *"Nourished by the earth and carried by the wind"*

Albedo is the stage of purification: darkness is washed away and matter is refined into silvery-white light.

| Condition | Value |
|---|---|
| Transformation material | Nigredo Matter (`nigredo_matter`) |
| Product | Albedo Matter (`albedo_matter`) |
| Slots | 6 |
| Required polarity | **20 to 50** (positive) |

The polarity is reversed: after Nigredo, you must raise it substantially into positive values. Use symbiotic essence pairs or reactions that favor activation.

### Stage Three: Citrinitas

> *"The Sun is its father, the Moon its mother"*

Citrinitas is the stage of awakening: the Sun dyes the silvery-white light gold, and matter acquires the power of transformation.

| Condition | Value |
|---|---|
| Transformation material | Albedo Matter (`albedo_matter`) |
| Product | Citrinitas Matter (`citrinitas_matter`) |
| Slots | 12 |
| Required polarity | **40 to 50** (high positive polarity) |

The slot count doubles to twelve, and special slots begin to appear (approximately a 20% chance). The polarity window is high and narrow, so make sure you have enough symbiotic essence pairs to maintain it.

### Stage Four: Rubedo

> *"This is the mighty power of all powers"*

Rubedo is the stage of completion: gold ignites into crimson, and the nascent Philosopher's Stone is born. This is a **one-time** transformation.

| Condition | Value |
|---|---|
| Transformation material | Citrinitas Matter (`citrinitas_matter`) |
| Product | Rubedo Matter (`rubedo_matter`) |
| Slots | **24** (maximum) |
| Required polarity | **49 to 50** (extreme positive polarity) |
| One-time | **Yes** |

This is the most difficult step in the entire Great Work:

- Twenty-four slots is the system maximum, with approximately a 50% chance of special slots.
- Polarity must fall precisely within the extreme window of 49–50, allowing only 2 points of tolerance.
- The scroll is one-time; failure means starting over.

::: warning Preparation
Before beginning Rubedo:

1. Use a **Lunar Scroll or Solar Scroll**—twenty-four slots consume a great deal of durability.
2. Make sure crucible polarity is stable at 49–50.
3. Prepare a sufficient reserve of essence metals: twenty-four slots require twenty-four matching essences.
4. Study the slot diagram and plan the insertion order carefully, prioritizing special slots such as Inhibition and Purge Slots that can affect other slots.
:::

### Final Step: Calcination

Once you obtain Rubedo Matter, smelt it in a **Furnace** for 200 ticks (10 seconds) to create the Philosopher's Stone.

## Powers of the Philosopher's Stone

The Philosopher's Stone is alchemy's supreme achievement. Carrying it grants the following powers:

### Preservation of Life

- **Continuous regeneration**: holding it in either hand continuously grants Regeneration.
- **Sustaining nourishment**: grants a brief Saturation effect every second, removing the need to eat.
- **Protection from death**: if an off-cooldown Philosopher's Stone is in the hotbar, any fatal damage is negated. Health is set to 1 and that stone receives a **60-second cooldown**. It temporarily loses this protection while on cooldown.

::: tip Tip
The Philosopher's Stone checks only the hotbar (slots 1–9); a stone elsewhere in the inventory has no effect. Multiple stones can take turns protecting you.
:::

### Curing Corruption

Right-click a **Zombie Villager** with the Philosopher's Stone to complete its conversion immediately, bypassing the usual Weakness potion, Golden Apple, and waiting period.

### Polarity Adjustment

Right-click the **Transmutation Crucible** with the Philosopher's Stone to move its polarity **1 point** toward 0.

### Chaos Decomposition

The Philosopher's Stone can serve as a crucible catalyst, breaking an item eligible for Alchemical Replication back down into essence metals.

1. Put the Philosopher's Stone in the catalyst slot.
2. Insert an item matched as an **output** by any Alchemical Replication recipe (that is, an item with a replication recipe that a Sigil Scroll can reproduce).
3. The reaction takes `5 × (minimum recipe level + maximum recipe level)` ticks. Wider and higher level ranges take longer.
4. It produces a random number of base-state essence metals within the recipe's level range (up to 24) and consumes the transformation input.

This is an efficient way to obtain **large quantities of essence metals**: decompose unwanted items that have replication recipes back into essences.

## Reference: Complete Great Work Table

| Step | Material | Product | Slots | Polarity | Notes |
|---|---|---|---|---|---|
| 1 | Sand | Salic Matrix | 4 | ≤ 0 | First prime |
| 2 | Salic Matrix | Mercurial Matrix | 8 | ≥ 20 | Second prime |
| 3 | Mercurial Matrix | Sulfuric Matrix | 13 | -1 to 1 | Third prime |
| 4 | Prima Materia | Nigredo Matter | 3 | -50 to -10 | Nigredo |
| 5 | Nigredo Matter | Albedo Matter | 6 | 20 to 50 | Albedo |
| 6 | Albedo Matter | Citrinitas Matter | 12 | 40 to 50 | Citrinitas |
| 7 | Citrinitas Matter | Rubedo Matter | 24 | 49 to 50 | Rubedo · One-time |
| 8 | Rubedo Matter | Philosopher's Stone | — | — | Furnace calcination |

---

*It is finished, as I bear witness, by the judgment of this world.*

