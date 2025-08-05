package com.example.scan;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.camera2.interop.Camera2Interop;
import androidx.camera.camera2.interop.ExperimentalCamera2Interop;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraX;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.core.Preview;

import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.LifecycleOwner;
import com.example.scan.ml.Model;
import com.google.common.util.concurrent.ListenableFuture;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class camera extends AppCompatActivity {


    private Uri photoUri;
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private Camera camera;
    private ImageView marker;
    private CameraSelector cameraSelector;
    private CameraControl cameraControl;
    private CameraInfo cameraInfo;
    private ImageButton cameraButton, galleryButton,aboutbtn , ButtonCameraBack ,backButton;
    private Button ButtonBack, ButtonMedicalHistory, ButtonSave;
    private CardView about;
    int image_size = 64;
    String datares;
    box box;

    private TextView result;
    private ImageView view_result;
    private CardView cardresult,card_button;
    private LinearLayout layout,resul;

    private List<Patient> patientList;
    private CardView cardButton;
    Uri imageUri;


    private  static  final  String TAG = "fg_camera";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        hideSystemUI();



        ButtonCameraBack = findViewById(R.id.btn_to_camera);
        ButtonSave= findViewById(R.id.btn_save);
        ButtonMedicalHistory = findViewById(R.id.medical_history_button);
        view_result = findViewById(R.id.view_result);
        cardresult = findViewById(R.id.cardrs);
        card_button = findViewById(R.id.cardbtn);
        result = findViewById(R.id.result);
        layout = findViewById(R.id.layout);

        about = findViewById(R.id.abouttxt);
        ButtonBack = findViewById(R.id.button_back);
        backButton = findViewById(R.id.backButton);
        aboutbtn = findViewById(R.id.aboutbtn);
        cameraButton = findViewById(R.id.camerabtn);
        galleryButton = findViewById(R.id.gallerybtn);
        viewFinder = findViewById(R.id.view);
        resul = findViewById(R.id.resul);
        box = findViewById(R.id.box_overlay);

        box.setBoxCoordinates(20, 400, 700, 1000);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(camera.this,home.class);
                startActivity(intent);
                finish();
            }
        });

        // Executor for camera operations
        cameraExecutor = Executors.newSingleThreadExecutor();


        // Check for necessary permissions
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                    this, getRequiredPermissions(), REQUEST_CODE_PERMISSIONS);
        }else {
            // Start camera if permissions are granted
           startCamera();
        }

        // Back button in about section to hide about information
        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { about.setVisibility(View.GONE); }});

        // About button to show about information
        aboutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about.setVisibility(View.VISIBLE);
            }});

        ButtonMedicalHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(camera.this, MedicalHistory.class);
                startActivity(intent);
                finish();
            }
        });

        ButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveImageToGallery();

            }
        });
        ButtonCameraBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInvisibility();
            }
        });

        // Camera button to take a photo
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            } });



        // Gallery button to select an image from the gallery
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { selectImageFromGallery();} });

    }
    // Check if all required permissions are granted
    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Get required permissions depending on the Android version
    private String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            return REQUIRED_PERMISSIONS;
        }
    }

    // Handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // Start camera if permissions are granted
                startCamera();

            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    // Handle results for image capture or gallery selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Get URI of the selected image
                imageUri = data.getData();
            try {
                // Convert URI to Bitmap and display in ImageView
                Bitmap pic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                int dimension = Math.min(pic.getWidth(),pic.getHeight());

                // Crop image to a square
                pic= ThumbnailUtils.extractThumbnail(pic,dimension,dimension);

                // Show the selected image
                view_result.setImageBitmap(pic);

                // Resize image to fit model input
                pic = Bitmap.createScaledBitmap(pic, image_size,image_size,false);

                // Run classification on the image
                classifyImage(pic);

            } catch (IOException e) {
                e.printStackTrace();
            }
            // Update UI visibility
            setvisibility();
        }
    }

    // Classify image using the TensorFlow Lite model
    public void classifyImage(Bitmap pic) {
        try {
            // Load the TensorFlow Lite model
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * image_size * image_size * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[image_size * image_size];
            // Extract pixel data
            pic.getPixels(intValues, 0 ,pic.getWidth(),0,0,pic.getWidth(),pic.getHeight());
            int pixel = 0;


            // Normalize pixel values and add to the input buffer
            for(int i = 0; i < image_size; i++ ){
                for(int j = 0; j < image_size; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f /1));  // Red channel
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f /1)); // Green channel
                    byteBuffer.putFloat((val  & 0xFF) * (1.f /1)); // Blue channel
                }
            }

            // Load the buffer into the input tensor
            inputFeature0.loadBuffer(byteBuffer);

            // Run inference and get classification results
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] logits = outputFeature0.getFloatArray();
            float[] confidences =  applySoftmax(logits);
            int maxPos = 0;
            float maxconfidences = 0;

            for(int i = 0; i < confidences.length;i++){
                if(confidences[i] > maxconfidences){
                    maxconfidences = confidences[i];
                    maxPos = i;
                }
            }

            float threshold = 0.9f;

            // If the highest confidence is below the threshold, return null
            if (maxconfidences <= threshold) {
                result.setText("No clear prediction");
            } else {
                String[] classes = {"Chloronychia","Healthy Nail", "Median Nail", "Melanonychia","Subungual Hematoma",
                        "Subungual Melanoma"};

                result.setText(classes[maxPos]);
            }
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }


    private float[] applySoftmax(float[] logits) {
        float maxLogit = Float.NEGATIVE_INFINITY;
        for (float logit : logits) {
            maxLogit = Math.max(maxLogit, logit);
        }

        float sum = 0.0f;
        for (int i = 0; i < logits.length; i++) {
            logits[i] = (float) Math.exp(logits[i] - maxLogit); // Prevent overflow
            sum += logits[i];
        }

        // Normalize to get probabilities
        for (int i = 0; i < logits.length; i++) {
            logits[i] /= sum;
        }

        return logits;
    }


    // Start the camera preview using CameraX

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Get camera provider
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                // Set up camera preview
                Preview.Builder previewBuilder = new Preview.Builder();

                Preview preview = new Preview.Builder().build();

                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                // Set up ImageCapture use case

                imageCapture = new ImageCapture.Builder().build();

                // Unbind previous use cases
                cameraProvider.unbindAll();
                // Bind camera to lifecycle
                Camera camera = cameraProvider.bindToLifecycle(
                        (LifecycleOwner) this, cameraSelector, preview, imageCapture);
               /* CameraControl cameraControl = camera.getCameraControl();
                float defaultZoomRatio = 2.5f;  // Example of setting a default zoom
                cameraControl.setZoomRatio(defaultZoomRatio);*/


            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors (including cancellation) here.
                Log.e("CameraX", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }



    // Capture a photo
    private void takePhoto() {

        // Create a file to save the photo

        File photoFile = new File(getExternalFilesDir(null),
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(new Date()) + ".jpg");
        // Capture image and process it
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {

            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Load the image from file
                Bitmap pic = BitmapFactory.decodeFile(photoFile.getPath());
                imageUri = outputFileResults.getSavedUri();
                // Crop to a square
                int dimension = Math.min(pic.getWidth(),pic.getHeight());
                pic= ThumbnailUtils.extractThumbnail(pic,dimension,dimension);
                // Display the captured image
                view_result.setImageBitmap(pic);
                // Resize for model input
                pic = Bitmap.createScaledBitmap(pic, image_size, image_size, false);
                // Classify the captured image
                classifyImage(pic);

                // Update UI to show result
                setvisibility();
                

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("NailCheck", "Photo capture failed: " + exception.getMessage(), exception);
            }
        });

    }

    // Set visibility of views after image is processed
    public void setvisibility(){
        resul.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
    }
    // Reset visibility to show camera view again
    public void setInvisibility(){
        layout.setVisibility(View.VISIBLE);
        resul.setVisibility(View.GONE);
    }
    // Select an image from the gallery
    private void selectImageFromGallery() {

        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);

    }

    private void saveImageToGallery() {
        BitmapDrawable drawable = (BitmapDrawable) view_result.getDrawable(); // Assuming imageView holds the image
        Bitmap bitmap = drawable.getBitmap();

        // Create a file in the Pictures directory
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "savedImage_" + System.currentTimeMillis() + ".jpg");

        try {
            // Write the bitmap to the file
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream); // Save as JPEG with 100% quality
            outStream.flush();
            outStream.close();

            // Notify the media scanner to add the file to the gallery
            MediaScannerConnection.scanFile(this,
                    new String[]{file.getAbsolutePath()}, null, (path, uri) -> {
                        Log.d("SaveImage", "Image saved and added to gallery: " + uri);
                    });

            Toast.makeText(this, "Image saved to gallery!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}