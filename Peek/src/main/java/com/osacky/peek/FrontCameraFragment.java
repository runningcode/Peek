package com.osacky.peek;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FrontCameraFragment extends Fragment implements View.OnClickListener, Callback {

    public static final String TAG = "CameraFragment";

    private Camera camera;
    private SurfaceView surfaceView;
    private ParseFile photoFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.camera_fragment, parent, false);

        if (camera == null) {
            try {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            } catch (Exception e) {
                Log.e(TAG, "No camera with exception: " + e.getMessage());
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }

        surfaceView = (SurfaceView) v.findViewById(R.id.surface_view);
        surfaceView.setOnClickListener(this);

        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
        return v;
    }

    /*
     * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
     * they are saved. Since we never need a full-size image in our app, we'll
     * save a scaled one right away.
     */

    private void saveScaledPhoto(byte[] data) {
        camera.stopPreview();
        camera.release();
        surfaceView.setOnClickListener(null);
        surfaceView.getHolder().removeCallback(this);
        camera = null;
        // Resize photo from camera byte array
        Bitmap mealImage = BitmapFactory.decodeByteArray(data, 0, data.length);

        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(mealImage, 0,
                0, mealImage.getWidth(), mealImage.getHeight(),
                matrix, true);

        Bitmap mealImageScaled = Bitmap.createScaledBitmap(rotatedScaledMealImage, 600, 300
                , false);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mealImageScaled.compress(Bitmap.CompressFormat.JPEG, 90, bos);

        byte[] scaledData = bos.toByteArray();
        mealImage.recycle();

        // Save the scaled image to Parse
        String fileName = "bottom.jpg";

        photoFile = new ParseFile(fileName, scaledData);
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    ((CreatePeekActivity) getActivity()).getCurrentPhoto().setBottom(
                            photoFile);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(FrontCameraFragment.this).commit();
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (camera == null) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                Log.i(TAG, "No camera: " + e.getMessage());
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (camera == null)
            return;
        camera.takePicture(new Camera.ShutterCallback() {

                       @Override
                       public void onShutter() {
                           // nothing to do
                       }

                   }, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        saveScaledPhoto(data);
                    }

                }
        );

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error setting up preview", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

