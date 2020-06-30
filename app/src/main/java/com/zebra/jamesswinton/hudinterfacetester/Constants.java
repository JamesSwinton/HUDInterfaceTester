package com.zebra.jamesswinton.hudinterfacetester;

import java.util.LinkedHashMap;

public class Constants {

    public static final LinkedHashMap<String, String> colours = new LinkedHashMap<String, String>()
    {{
        put("Black", "BLACK");
        put("White", "WHITE");
        put("Gray", "GRAY");
        put("Light Gray", "LTGRAY");
        put("Dark Gray", "DKGRAY");
        put("Red", "RED");
        put("Blue", "BLUE");
        put("Green", "GREEN");
        put("Cyan", "CYAN");
        put("Magenta", "MAGENTA");
        put("Yellow", "YELLOW");
    }};

    public static final LinkedHashMap<String, String> gravity = new LinkedHashMap<String, String>()
    {{
        put("Start", "START");
        put("Center", "CENTER");
        put("End", "END");
    }};

    public static final LinkedHashMap<String, String> scale = new LinkedHashMap<String, String>()
    {{
        put("Center", "CENTER");
        put("Center Crop", "CENTER_CROP");
        put("Center Inside", "CENTER_INSIDE");
        put("Fit Center", "FIT_CENTER");
        put("Fit End", "FIT_END");
        put("Fit Start", "FIT_START");
        put("Fit XY", "FIT_XY");
        put("Matrix", "MATRIX");
    }};
}
