package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.client.gui.util.GuiSprite;
import com.linngdu664.transmutatoria.client.gui.util.GuiSubSprite;
import com.linngdu664.transmutatoria.client.gui.util.GuiTexture;

public class Textures {
    private static final GuiSprite SLOTS_FULL_SPRITE = new GuiSprite("hud/slots", 189, 61);
    public static final GuiSubSprite NORMAL_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 0, 0, 27, 27);
    public static final GuiSubSprite DETERIORATION_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 1, 0, 27, 27);
    public static final GuiSubSprite ACTIVATION_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 2, 0, 27, 27);
    public static final GuiSubSprite INVERSION_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 3, 0, 27, 27);
    public static final GuiSubSprite DIFFUSION_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 4, 0, 27, 27);
    public static final GuiSubSprite INHIBITION_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 5, 0, 27, 27);
    public static final GuiSubSprite PURGE_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 6, 0, 27, 27);
    public static final GuiSubSprite RESTORATION_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 0, 26, 27, 27);
    public static final GuiSubSprite RESONANCE_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 1, 26, 27, 27);
    public static final GuiSubSprite ACTIVITY_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 2, 26, 27, 27);
    public static final GuiSubSprite EXCHANGE_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 3, 26, 27, 27);
    public static final GuiSubSprite SPIN_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 4, 26, 27, 27);
    public static final GuiSubSprite UNSTABLE_SLOT = new GuiSubSprite(SLOTS_FULL_SPRITE, 27 * 5, 26, 27, 27);

    public static final GuiSubSprite UP_ARROW = new GuiSubSprite(SLOTS_FULL_SPRITE, 6 * 0, 53, 5, 5);
    public static final GuiSubSprite UPRIGHT_ARROW = new GuiSubSprite(SLOTS_FULL_SPRITE, 6 * 1, 53, 5, 5);
    public static final GuiSubSprite DOWNRIGHT_ARROW = new GuiSubSprite(SLOTS_FULL_SPRITE, 6 * 2, 53, 5, 5);
    public static final GuiSubSprite DOWN_ARROW = new GuiSubSprite(SLOTS_FULL_SPRITE, 6 * 3, 53, 5, 5);
    public static final GuiSubSprite DOWNLEFT_ARROW = new GuiSubSprite(SLOTS_FULL_SPRITE, 6 * 4, 53, 5, 5);
    public static final GuiSubSprite UPLEFT_ARROW = new GuiSubSprite(SLOTS_FULL_SPRITE, 6 * 5, 53, 5, 5);


    public static final GuiSprite SLOT_SELECTED = new GuiSprite("hud/slot_selected", 29, 29);
    public static final GuiSprite SIMPLE_FRAME = new GuiSprite("hud/simple_frame", 22, 22);

    public static final GuiTexture ABYSSION = new GuiTexture("textures/item/abyssion.png", 16, 16);
    public static final GuiTexture ANIMERCURY = new GuiTexture("textures/item/animercury.png", 16, 16);
    public static final GuiTexture ASTROTITE = new GuiTexture("textures/item/astrotite.png", 16, 16);
    public static final GuiTexture CHRONOPLATINUM = new GuiTexture("textures/item/chronoplatinum.png", 16, 16);
    public static final GuiTexture ECLIPSIUM = new GuiTexture("textures/item/eclipsium.png", 16, 16);
    public static final GuiTexture FULGURZINC = new GuiTexture("textures/item/fulgurzinc.png", 16, 16);
    public static final GuiTexture LUNARGENT = new GuiTexture("textures/item/lunargent.png", 16, 16);
    public static final GuiTexture NECROPLUMB = new GuiTexture("textures/item/necroplumb.png", 16, 16);
    public static final GuiTexture OSSANTIMONY = new GuiTexture("textures/item/ossantimony.png", 16, 16);
    public static final GuiTexture PANDEMONIUM = new GuiTexture("textures/item/pandemonium.png", 16, 16);
    public static final GuiTexture SANGUIBRONZE = new GuiTexture("textures/item/sanguibronze.png", 16, 16);
    public static final GuiTexture VENOTITE = new GuiTexture("textures/item/venotite.png", 16, 16);

    public static final GuiTexture ALCHEMY_ARRAY_1 = new GuiTexture("textures/gui/alchemy_array1.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_2 = new GuiTexture("textures/gui/alchemy_array2.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_3 = new GuiTexture("textures/gui/alchemy_array3.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_4 = new GuiTexture("textures/gui/alchemy_array4.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_5 = new GuiTexture("textures/gui/alchemy_array5.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_6 = new GuiTexture("textures/gui/alchemy_array6.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_7 = new GuiTexture("textures/gui/alchemy_array7.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_8 = new GuiTexture("textures/gui/alchemy_array8.png", 256, 256);

    public static final GuiTexture[] ALCHEMY_ARRAYS = {
        ALCHEMY_ARRAY_1, ALCHEMY_ARRAY_2, ALCHEMY_ARRAY_3, ALCHEMY_ARRAY_4,
        ALCHEMY_ARRAY_5, ALCHEMY_ARRAY_6, ALCHEMY_ARRAY_7, ALCHEMY_ARRAY_8
    };

    public static final GuiSubSprite[] ROMAN_NUMBERS = new GuiSubSprite[24];

    static {
        GuiSprite roman124 = new GuiSprite("hud/roman1-24", 13, 192);
        for (int i = 0; i < ROMAN_NUMBERS.length; i++) {
            ROMAN_NUMBERS[i] = new GuiSubSprite(roman124, 0, i * 8, 13, 8);
        }
    }
}
