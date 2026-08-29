---
title: Advanced Alchemy - Part II
navigation:
  title: Advanced Alchemy - Part II
items:
  - "transmutatoria:transmutation_sigil_scroll"
  - "transmutatoria:transmutation_equation_scroll"
---

# Advanced Alchemy - Part II

<color=#941400>Thus I am called Hermes Trismegistus, having the three parts of the philosophy of the whole world;</color>

<color=#941400>What I have said of the operation of the Sun is complete;</color>

## Overview

In the previous chapter, you completed your first alchemical synthesis. This chapter goes deeper into the details of scroll alchemy - why does annihilation sometimes consume especially large amounts of durability? Why is there sometimes no output even when everything clearly annihilated? What happens after a scroll expires? And what special slots will you encounter when the recipe level is higher?

## Entropy

Each time a slot reacts, if the relationship between the inserted essence and the target essence is neither "same" nor "neutral", the scroll's **entropy** accumulates:

| Relationship | Entropy Increase |
|------|---------|
| Double restrain / doubly restrained | +2 |
| Normal restraint / restrained / mutual restraint / symbiosis | +1 |
| Neutral / same (annihilation) | +0 |

<row>
<item id="transmutatoria:nigredo_venotite"/>
<item id="transmutatoria:venotite"/>
<item id="transmutatoria:albedo_venotite"/>
<item id="transmutatoria:citrinitas_venotite"/>
</row>

The higher the scroll's entropy, the greater the durability cost of later annihilation. This means brute-force guessing - trying every kind of essence - comes at a heavy price. A clever alchemist uses the restraint and symbiosis rules to infer the identity of the target essence from reaction results.

The scroll interface's lower left displays the current stability. The higher the entropy, the more the description tends toward unstable.

## One-Time Recipes

Some alchemy recipes are one-time - after successfully producing an item, no matter how much durability the scroll still has, the scroll in the catalyst slot vanishes.

## Polarity Window

Chapter 3 introduced polarity. In scroll alchemy, polarity gains a new meaning: every alchemy recipe has a required polarity range (such as -10 to 10). **Even if all slots fully annihilate, if the crucible's current polarity is not within the recipe's required range, there will be no output**.

After putting in an activated scroll, the dial in the upper right of the HUD displays the polarity window: **the two inner pointers** mark the recipe's required polarity range, and **the outer pointer** indicates the crucible's current polarity. The polarity condition is satisfied only when the outer pointer falls between the two inner pointers.

In scroll reactions, unmatched slot reactions also change polarity (the "virtual" target essence's change is reflected as a polarity change). Therefore, before starting synthesis, first check whether the outer pointer is inside the window. If the deviation is large, you can use Chapter 3's Essence Reaction (symbiosis / mutual restraint) to adjust polarity in advance.

## Expiration and Mutation

Transmutation, Terrestrial, and Lunar scrolls **expire** over time (Solar and Void never expire). See the previous chapter's tier table for expiration rules. After putting in a scroll, the **hourglass** on the dial in the upper right of the HUD displays the expiration countdown.

After a scroll expires, **mutation** triggers:

- The target essences of all slots are hidden again (returning to `?`).
- The target essences of some slots may be randomly replaced.

Mutation means you need to explore the correct essences again. Therefore, after activating a scroll, use it as soon as possible. For complex synthesis that takes a long time, Lunar or higher-tier scrolls are recommended.

## Special Slots

In the previous chapters, all slots followed the basic restraint and symbiosis rules. But when the recipe level is **above 8**, slots have a chance to be **special types** - they change how reactions behave. The higher the level, the higher the chance and number of special slots.

There are currently 12 kinds of special slots:
<row>
![劣化槽位](../textures/slots/deterioration.png)
![活化槽位](../textures/slots/activation.png)
![反转槽位](../textures/slots/inversion.png)
![扩散槽位](../textures/slots/diffusion.png)
![抑制槽位](../textures/slots/inhibition.png)
![清理槽位](../textures/slots/purge.png)
![还原槽位](../textures/slots/restoration.png)
![共振槽位](../textures/slots/resonance.png)
![活动槽位](../textures/slots/activity.png)
![交换槽位](../textures/slots/exchange.png)
![自旋槽位](../textures/slots/spin.png)
![不稳槽位](../textures/slots/unstable.png)
</row>

| Slot | Behavior |
|------|------|
| Deterioration Slot | After the base reaction, deteriorates the product by 1 stage and increases crucible polarity by 1. |
| Activation Slot | After the base reaction, activates the product by 1 stage and decreases crucible polarity by 1. |
| Inversion Slot | Reverses the base reaction's state change and polarity change. What originally activated becomes deterioration; what originally increased polarity becomes decreased polarity. |
| Diffusion Slot | After this slot reacts, passes this slot's activation or deterioration effect to every adjacent produced, uninhibited product. Its polarity change is applied once for this slot and once for each successful diffusion. |
| Inhibition Slot | If this slot does not annihilate, it inhibits adjacent empty slots. The essence state of an inhibited slot remains unchanged, and it no longer changes crucible polarity or entropy; but it can still annihilate, be cleared, or be swapped. |
| Purge Slot | If this slot's essence deteriorates, it clears every adjacent produced item. This clearing does not count as annihilation. |
| Restoration Slot | When this slot annihilates, it retains the inserted essence instead of clearing it. |
| Resonance Slot | After this slot reacts, adds the sum of all adjacent produced essence states to itself. Nigredo, base, Albedo, and Citrinitas are counted as -1, 0, 1, and 2 respectively. |
| Activity Slot | If this slot does not annihilate, it reacts again with the adjacent produced item indicated by the arrow. Matching essences annihilate each other; otherwise, both sides change state according to their relationship. This extra reaction does not affect crucible polarity. |
| Exchange Slot | After this slot reacts, if the adjacent slot indicated by the arrow already has a product, it swaps products with it. |
| Spin Slot | After all slots react, rotates all adjacent non-empty products clockwise. |
| Unstable Slot | If this slot's essence activates or deteriorates, it swaps its type with a random other slot after all slots react. |

::: tip Tip
**This slot** means the slot currently resolving, **adjacent slots** means up to six slots around the hexagon, and **produced essences** means essences that have already resolved and remain on the output side during this reaction.
:::

When special slots exist, **slot filling order is crucial** - reactions resolve slot by slot, and the resolution order is the order in which the slots were filled with essences. Filling slot 1 before slot 2 may produce a completely different result from filling 2 before 1.

For example, an Inhibition Slot: if the Inhibition Slot is filled first, it reacts first, adjacent empty slots are locked, and later filled essences have ineffective non-annihilation reactions; if adjacent slots are filled first, they have already finished resolving, and the later Inhibition Slot cannot interfere with them.

The HUD displays every special slot's type, letting you plan the insertion order in advance.

---

After mastering entropy, the polarity window, expiration mechanics, and the existence of special slots, you already understand the core logic of scroll alchemy. The final chapter guides you toward alchemy's ultimate goal - the Great Work.
