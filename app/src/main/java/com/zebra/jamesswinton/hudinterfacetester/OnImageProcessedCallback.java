package com.zebra.jamesswinton.hudinterfacetester;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;

public interface OnImageProcessedCallback {
    void onProcessed(File imageFile, Bitmap image);
    void onError(String error);
}
