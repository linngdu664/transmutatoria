package com.linngdu664.transmutatoria.client.gui.texture;

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

    public static final GuiSubSprite ROMAN_I = new GuiSubSprite(SLOTS_FULL_SPRITE, 36, 53, 1, 8);
    public static final GuiSubSprite ROMAN_V = new GuiSubSprite(SLOTS_FULL_SPRITE, 38, 53, 3, 8);
    public static final GuiSubSprite ROMAN_X = new GuiSubSprite(SLOTS_FULL_SPRITE, 42, 53, 3, 8);

    public static final GuiSprite SLOT_SELECTED = new GuiSprite("hud/slot_selected", 29, 29);
    public static final GuiSprite SIMPLE_FRAME = new GuiSprite("hud/simple_frame", 24, 24);
    public static final GuiSprite SIMPLE_FRAME_MASK = new GuiSprite("hud/simple_frame_mask", 24, 24);
    public static final GuiSprite STORAGE_BOX_INSERT_ARROW = new GuiSprite("hud/storage_box_insert_arrow", 16, 10);
    public static final GuiSprite STORAGE_BOX_RESTRAINED_BORDER = new GuiSprite("hud/storage_box_restrained_border", 26, 26);
    public static final GuiSprite STORAGE_BOX_RESTRAINING_BORDER = new GuiSprite("hud/storage_box_restraining_border", 26, 26);
    public static final GuiSprite STORAGE_BOX_SYMBIOSIS_BORDER = new GuiSprite("hud/storage_box_symbiosis_border", 26, 26);
    public static final GuiSprite STORAGE_BOX_MUTUAL_RESTRAINED_BORDER = new GuiSprite("hud/storage_box_mutual_restrained_border", 26, 26);
    public static final GuiSprite STORAGE_BOX_DOUBLE_RESTRAINED_BORDER = new GuiSprite("hud/storage_box_double_restrained_border", 26, 26);
    public static final GuiSprite STORAGE_BOX_DOUBLE_RESTRAINING_BORDER = new GuiSprite("hud/storage_box_double_restraining_border", 26, 26);
    public static final GuiSprite STORAGE_BOX_SAME_BORDER = new GuiSprite("hud/storage_box_same_border", 26, 26);

    public static final GuiSprite DASHBOARD_BG = new GuiSprite("hud/dashboard_bg", 80, 80);
    public static final GuiSprite DASHBOARD_BG_POINTER = new GuiSprite("hud/dashboard_bg_pointer", 83, 5);
    public static final GuiSprite DASHBOARD_BG_POINTER_FLAG = new GuiSprite("hud/dashboard_bg_pointer_flag", 83, 5);
    public static final GuiSprite DASHBOARD_HOURGLASS_DOWN = new GuiSprite("hud/dashboard_hourglass_down", 14, 19);
    public static final GuiSprite DASHBOARD_HOURGLASS_UP = new GuiSprite("hud/dashboard_hourglass_up", 14, 19);
    public static final GuiSprite DURABILITY_STRIP = new GuiSprite("hud/durability_strip", 23, 181);
    public static final GuiSprite DURABILITY_STRIP_DAMAGE = new GuiSprite("hud/durability_strip_damage", 11, 131);
    public static final GuiSprite DURABILITY_STRIP_DURABILITY = new GuiSprite("hud/durability_strip_durability", 11, 131);
    public static final GuiSprite PROGRESS_BAR = new GuiSprite("hud/progress_bar", 42, 203);
    public static final GuiSprite PROGRESS_BAR_CONTENT = new GuiSprite("hud/progress_bar_content", 10, 98);



    public static final GuiSprite ABYSSION = new GuiSprite("essence/abyssion", 16, 16);
    public static final GuiSprite ANIMERCURY = new GuiSprite("essence/animercury", 16, 16);
    public static final GuiSprite ASTROTITE = new GuiSprite("essence/astrotite", 16, 16);
    public static final GuiSprite CHRONOPLATINUM = new GuiSprite("essence/chronoplatinum", 16, 16);
    public static final GuiSprite ECLIPSIUM = new GuiSprite("essence/eclipsium", 16, 16);
    public static final GuiSprite FULGURZINC = new GuiSprite("essence/fulgurzinc", 16, 16);
    public static final GuiSprite LUNARGENT = new GuiSprite("essence/lunargent", 16, 16);
    public static final GuiSprite NECROPLUMB = new GuiSprite("essence/necroplumb", 16, 16);
    public static final GuiSprite OSSANTIMONY = new GuiSprite("essence/ossantimony", 16, 16);
    public static final GuiSprite PANDEMONIUM = new GuiSprite("essence/pandemonium", 16, 16);
    public static final GuiSprite SANGUIBRONZE = new GuiSprite("essence/sanguibronze", 16, 16);
    public static final GuiSprite VENOTITE = new GuiSprite("essence/venotite", 16, 16);
    public static final GuiSprite UNKNOWN_ESSENCE = new GuiSprite("essence/unknown_essence", 16, 16);

    public static final GuiSprite SCROLL_CONTAINER = new GuiSprite("scroll/scroll_container", 176, 90);
    public static final GuiSprite SCROLL_GRIP_LEFT = new GuiSprite("scroll/scroll_grip_left", 16, 121);
    public static final GuiSprite SCROLL_GRIP_RIGHT = new GuiSprite("scroll/scroll_grip_right", 16, 121);
    public static final GuiSprite SCROLL_PAGE = new GuiSprite("scroll/scroll_page", 162, 121);
    public static final GuiSprite SCROLL_TRANSMUTATION = new GuiSprite("scroll/scroll_transmutation", 10, 15);
    public static final GuiSprite SCROLL_TERRESTRIAL = new GuiSprite("scroll/scroll_terrestrial", 10, 15);
    public static final GuiSprite SCROLL_LUNAR = new GuiSprite("scroll/scroll_lunar", 10, 15);
    public static final GuiSprite SCROLL_SOLAR = new GuiSprite("scroll/scroll_solar", 10, 15);
    public static final GuiSprite SCROLL_VOID = new GuiSprite("scroll/scroll_void", 10, 15);

    public static final GuiSprite SCROLL_ARR_EQ_BASE = new GuiSprite("scroll/scroll_arr_eq_base", 128, 128);
    public static final GuiSprite SCROLL_ARR_EQ_LIGHT = new GuiSprite("scroll/scroll_arr_eq_light", 128, 128);
    public static final GuiSprite SCROLL_ARR_EQ_SHINE = new GuiSprite("scroll/scroll_arr_eq_shine", 128, 128);
    public static final GuiSprite SCROLL_ARR_SG_BASE = new GuiSprite("scroll/scroll_arr_sg_base", 128, 128);
    public static final GuiSprite SCROLL_ARR_SG_LIGHT = new GuiSprite("scroll/scroll_arr_sg_light", 128, 128);
    public static final GuiSprite SCROLL_ARR_SG_SHINE = new GuiSprite("scroll/scroll_arr_sg_shine", 128, 128);


    public static final GuiTexture ALCHEMY_ARRAY_1 = new GuiTexture("textures/gui/alchemy_array1.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_2 = new GuiTexture("textures/gui/alchemy_array2.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_3 = new GuiTexture("textures/gui/alchemy_array3.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_4 = new GuiTexture("textures/gui/alchemy_array4.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_5 = new GuiTexture("textures/gui/alchemy_array5.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_6 = new GuiTexture("textures/gui/alchemy_array6.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_7 = new GuiTexture("textures/gui/alchemy_array7.png", 256, 256);
    public static final GuiTexture ALCHEMY_ARRAY_8 = new GuiTexture("textures/gui/alchemy_array8.png", 256, 256);


    public static final GuiSprite EMERALD_TABLET_ESSENCE_NODE = new GuiSprite("tablet/essence_node", 28, 28);
}
