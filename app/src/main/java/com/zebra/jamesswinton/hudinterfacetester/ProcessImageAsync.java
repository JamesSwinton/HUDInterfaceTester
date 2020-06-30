package com.zebra.jamesswinton.hudinterfacetester;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class ProcessImageAsync extends AsyncTask<Void, Void, Void> {

    // Debugging
    private static final String TAG = "ProcessPDFAsync";

    // Constants
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    // Private Variables
    private Uri mPdfUri = null;
    private WeakReference<Context> mContext = null;
    private OnImageProcessedCallback mOnImageProcessed = null;

    // Public Variables


    public ProcessImageAsync(WeakReference<Context> context, Uri pdfUri, OnImageProcessedCallback onImageProcessedCallback) {
        this.mPdfUri = pdfUri;
        this.mContext = context;
        this.mOnImageProcessed = onImageProcessedCallback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // Get PDF File from URI
            File imageFile = FileHelper.getFileFromUri(mContext.get(), mPdfUri);

            // Get Bitmaps from PDF Pages
            Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            // Return
            mHandler.post(() -> mOnImageProcessed.onProcessed(imageFile, image));

        } catch (Exception e) {
            // Log Results
            e.printStackTrace();
            Log.e(TAG, "Exception: " + e.getMessage());

            // Return Error
            mHandler.post(() -> mOnImageProcessed.onError(e.getMessage()));
        }

        // Empty Return
        return null;
    }
}
