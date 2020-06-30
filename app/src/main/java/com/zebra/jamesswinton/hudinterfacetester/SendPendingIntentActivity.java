package com.zebra.jamesswinton.hudinterfacetester;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.zebra.jamesswinton.hudinterfacetester.databinding.ActivitySendPendingIntentBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SendPendingIntentActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "SendPendingIntentActiv";

    // Constants
    private static final int SELECT_IMAGE_INTENT = 100;
    private static final int PERMISSIONS_REQUEST = 0;
    private static final String[] PERMISSIONS = {
            WRITE_EXTERNAL_STORAGE
    };

    // Private Variables


    // Public Variables
    private ActivitySendPendingIntentBinding mDataBinding;
    private String[] mColourArray = null;
    private String[] mPositonArray = null;
    private String[] mScaleArray = null;

    private String mTextColour;
    private String mBackgroundColour;
    private String mPosition;

    private String mImagePath;
    private String mScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_send_pending_intent);

        // Request Permissions
        if (!checkStandardPermissions()) {
            // Request Permissions
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);
        }

        // Get Values
        initSpinners();

        // Send Broadcast
        mDataBinding.sendIntentButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction("com.zebra.hudinterface.DISPLAY_TEXT");
            if (mDataBinding.clearBeforeDisplayCheckbox.isChecked()) {
                intent.putExtra("text.text", "^" + (mDataBinding.data.getText() != null
                        ? mDataBinding.data.getText().toString() : null));
            } else {
                intent.putExtra("text.text", mDataBinding.data.getText() != null
                        ? mDataBinding.data.getText().toString() : null);
            }
            intent.putExtra("text.size", mDataBinding.textSize.getText() != null
                    ? Integer.valueOf(mDataBinding.textSize.getText().toString()) : null);
            intent.putExtra("text.justification", mPosition);
            intent.putExtra("text.background_colour", mBackgroundColour);
            intent.putExtra("text.colour", mTextColour);
            sendBroadcast(intent);
        });

        mDataBinding.displayImageButton.setOnClickListener(view -> {
            if (mImagePath == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent();
            intent.setAction("com.zebra.hudinterface.DISPLAY_IMAGE");
            if (mDataBinding.clearBeforeDisplayCheckbox.isChecked()) {
                intent.putExtra("image.path", "^" + mImagePath);
            } else {
                intent.putExtra("image.path", mImagePath);
            }
            intent.putExtra("image.width", mDataBinding.imageWidth.getText() != null
                    ? Integer.valueOf(mDataBinding.imageWidth.getText().toString()) : null);
            intent.putExtra("image.height", mDataBinding.imageHeight.getText() != null
                    ? Integer.valueOf(mDataBinding.imageHeight.getText().toString()) : null);
            intent.putExtra("image.scale", mScale);
            sendBroadcast(intent);
        });

        mDataBinding.sendJimIntentButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mDataBinding.jimData.getText())
                    || TextUtils.isEmpty(mDataBinding.jimImagesDirectory.getText())) {
                Toast.makeText(SendPendingIntentActivity.this,
                        "Please Enter JIM data & Image Directory", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject jim = new JSONObject(mDataBinding.jimData.getText().toString());
                Intent intent = new Intent();
                intent.setAction("com.zebra.hudinterface.DISPLAY_JIM");
                intent.putExtra("jim.jim", jim.toString());
                intent.putExtra("jim.image_directory", mDataBinding.jimImagesDirectory.getText().toString());
                sendBroadcast(intent);
            } catch (JSONException e) {
                Toast.makeText(SendPendingIntentActivity.this, "Invalid JSON: "
                        + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        mDataBinding.sendCaptureImageIntent.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mDataBinding.captureImageFileName.getText())
                    || TextUtils.isEmpty(mDataBinding.captureImagePath.getText())) {
                Toast.makeText(SendPendingIntentActivity.this,
                        "Please Enter Image File Name & Path", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent();
            intent.setAction("com.zebra.hudinterface.CAPTURE_IMAGE");
            intent.putExtra("capture.file_name", mDataBinding.captureImageFileName.getText().toString());
            intent.putExtra("capture.file_path", mDataBinding.captureImagePath.getText().toString());
            sendBroadcast(intent);
        });

        // send Clear Display
        mDataBinding.clearDisplayButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction("com.zebra.hudinterface.CLEAR");
            sendBroadcast(intent);
        });

        // Send Toggle Display
        mDataBinding.toggleOverlayButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction("com.zebra.hudinterface.TOGGLE_OVERLAY");
            sendBroadcast(intent);
        });

        // Pick Image Button
        mDataBinding.pickImage.setOnClickListener(view -> {
            selectFilesFromStorage();
        });
    }

    private void initSpinners() {
        // Convert Constants to Array
        mColourArray = Constants.colours.keySet().toArray(new String[Constants.colours.keySet().size()]);
        mPositonArray = Constants.gravity.keySet().toArray(new String[Constants.gravity.keySet().size()]);
        mScaleArray = Constants.scale.keySet().toArray(new String[Constants.scale.keySet().size()]);

        // Init Adapters
        ArrayAdapter<String> colourAdapter = new ArrayAdapter<>(
                this,
                R.layout.adapter_dropdown_menu_item,
                mColourArray);

        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(
                this,
                R.layout.adapter_dropdown_menu_item,
                mPositonArray);

        ArrayAdapter<String> scaleAdapter = new ArrayAdapter<>(
                this,
                R.layout.adapter_dropdown_menu_item,
                mScaleArray);

        // Set Adapter
        mDataBinding.textColor.setAdapter(colourAdapter);
        mDataBinding.backgroundColor.setAdapter(colourAdapter);
        mDataBinding.position.setAdapter(positionAdapter);
        mDataBinding.imageScale.setAdapter(scaleAdapter);

        // Set Listeners
        mDataBinding.textColor.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItemKey = (String) adapterView.getItemAtPosition(i);
            mTextColour = Constants.colours.get(selectedItemKey);
        });

        mDataBinding.backgroundColor.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItemKey = (String) adapterView.getItemAtPosition(i);
            mBackgroundColour = Constants.colours.get(selectedItemKey);
        });

        mDataBinding.position.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItemKey = (String) adapterView.getItemAtPosition(i);
            mPosition = Constants.gravity.get(selectedItemKey);
        });

        mDataBinding.imageScale.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItemKey = (String) adapterView.getItemAtPosition(i);
            mScale = Constants.scale.get(selectedItemKey);
        });

        // Set defaults
        mDataBinding.textColor.setText(colourAdapter.getItem(1), false);
        mDataBinding.backgroundColor.setText(colourAdapter.getItem(0), false);
        mDataBinding.position.setText(positionAdapter.getItem(0), false);
        mDataBinding.imageScale.setText(scaleAdapter.getItem(0), false);
        mTextColour = colourAdapter.getItem(1);
        mBackgroundColour = colourAdapter.getItem(0);
        mPosition = positionAdapter.getItem(0);
        mScale = scaleAdapter.getItem(0);
    }

    @SuppressLint("InflateParams")
    private void selectFilesFromStorage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE_INTENT) {
                if(resultData != null && resultData.getData() != null) { // checking empty selection
                    new ProcessImageAsync(new WeakReference<>(this), resultData.getData(), new OnImageProcessedCallback() {
                        @Override
                        public void onProcessed(File imageFile, Bitmap image) {
                            mImagePath = imageFile.getAbsolutePath();

                            // Get Bitmaps from PDF Pages
                            mDataBinding.pickImage.setImageBitmap(image);
                        }

                        @Override
                        public void onError(String error) {
                            // Log Results
                            Log.e(TAG, "IOException: " + error);
                            Toast.makeText(SendPendingIntentActivity.this, "Could not process Image", Toast.LENGTH_LONG).show();
                        }
                    }).execute();
                }
            }
        }
    }

    public boolean checkStandardPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                permissionsGranted = false;
                break;
            }
        }

        return permissionsGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);

        // Handle Permissions Request
        if (requestCode == PERMISSIONS_REQUEST) {
            Log.i(TAG, "Permissions Request Complete - checking permissions granted...");

            // Validate Permissions State
            boolean permissionsGranted = true;
            if (results.length > 0) {
                for (int result : results) {
                    if (result != PERMISSION_GRANTED) {
                        permissionsGranted = false;
                    }
                }
            } else {
                permissionsGranted = false;
            }

            // Check Permissions were granted & Load slide images or exit
            if (permissionsGranted) {
                Log.i(TAG, "Permissions Granted");
            } else {
                Log.e(TAG, "Permissions Denied - Exiting App");

                // Explain reason
                Toast.makeText(this, "Please enable all permissions to run this app",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
