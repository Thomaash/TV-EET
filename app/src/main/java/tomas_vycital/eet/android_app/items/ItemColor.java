package tomas_vycital.eet.android_app.items;

import android.graphics.Color;

/**
 * Defines colors for items
 */
enum ItemColor {
    color0(0, Color.HSVToColor(new float[]{0, 1, ItemColor.defaultValue})),
    color1(1, Color.HSVToColor(new float[]{15, 1, ItemColor.defaultValue})),
    color2(2, Color.HSVToColor(new float[]{30, 1, ItemColor.defaultValue})),
    color3(3, Color.HSVToColor(new float[]{45, 1, ItemColor.defaultValue})),
    color4(4, Color.HSVToColor(new float[]{60, 1, ItemColor.defaultValue})),
    color5(5, Color.HSVToColor(new float[]{75, 1, ItemColor.defaultValue})),
    color6(6, Color.HSVToColor(new float[]{90, 1, ItemColor.defaultValue})),
    color7(7, Color.HSVToColor(new float[]{105, 1, ItemColor.defaultValue})),
    color8(8, Color.HSVToColor(new float[]{120, 1, ItemColor.defaultValue})),
    color9(9, Color.HSVToColor(new float[]{135, 1, ItemColor.defaultValue})),
    color10(10, Color.HSVToColor(new float[]{150, 1, ItemColor.defaultValue})),
    color11(11, Color.HSVToColor(new float[]{165, 1, ItemColor.defaultValue})),
    color12(12, Color.HSVToColor(new float[]{180, 1, ItemColor.defaultValue})),
    color13(13, Color.HSVToColor(new float[]{195, 1, ItemColor.defaultValue})),
    color14(14, Color.HSVToColor(new float[]{210, 1, ItemColor.defaultValue})),
    color15(15, Color.HSVToColor(new float[]{225, 1, ItemColor.defaultValue})),
    color16(16, Color.HSVToColor(new float[]{240, 1, ItemColor.defaultValue})),
    color17(17, Color.HSVToColor(new float[]{255, 1, ItemColor.defaultValue})),
    color18(18, Color.HSVToColor(new float[]{270, 1, ItemColor.defaultValue})),
    color19(19, Color.HSVToColor(new float[]{285, 1, ItemColor.defaultValue})),
    color20(20, Color.HSVToColor(new float[]{300, 1, ItemColor.defaultValue})),
    color21(21, Color.HSVToColor(new float[]{315, 1, ItemColor.defaultValue})),
    color22(22, Color.HSVToColor(new float[]{330, 1, ItemColor.defaultValue})),
    color23(23, Color.HSVToColor(new float[]{345, 1, ItemColor.defaultValue})),
    color24(24, Color.BLACK),
    color25(25, Color.HSVToColor(new float[]{0, 0, .1f})),
    color26(26, Color.HSVToColor(new float[]{0, 0, .2f})),
    color27(27, Color.HSVToColor(new float[]{0, 0, .3f})),
    color28(28, Color.HSVToColor(new float[]{0, 0, .4f})),
    color29(29, Color.HSVToColor(new float[]{0, 0, .5f})),
    color30(30, Color.HSVToColor(new float[]{0, 0, .6f})),
    color31(31, Color.HSVToColor(new float[]{0, 0, .7f}));

    private static final float defaultValue = 0.8f;

    private final int id;
    private final int foreground;

    ItemColor(int id, int foreground) {
        this.id = id;
        this.foreground = foreground;
    }

    public static ItemColor fromID(int id) {
        for (ItemColor color : ItemColor.values()) {
            if (color.id == id) {
                return color;
            }
        }
        return ItemColor.color24;
    }

    public int getID() {
        return this.id;
    }

    public int getInt() {
        return this.foreground;
    }
}
