package com.example.caloriesapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BarcodeScannerActivity extends AppCompatActivity {

    private static final String TAG = "BarcodeScannerActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 10;

    private PreviewView viewFinder;
    private ExecutorService cameraExecutor;
    private TextView barcodeValueText;
    private TextView instructionText;
    private volatile boolean hasCapturedBarcode = false;
    private String capturedBarcodeValue = null;
    private BarcodeAnalyzer barcodeAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        viewFinder = findViewById(R.id.viewFinder);
        barcodeAnalyzer = new BarcodeAnalyzer();

        ImageButton closeButton = findViewById(R.id.btn_close_scanner);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> finish());
        }

        FrameLayout captureButton = findViewById(R.id.capture_button);
        if (captureButton != null) {
            captureButton.setOnClickListener(v -> resetCaptureState());
        }

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[] { Manifest.permission.CAMERA }, PERMISSION_REQUEST_CAMERA);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, barcodeAnalyzer);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                try {
                    cameraProvider.unbindAll();

                    cameraProvider.bindToLifecycle(
                            this, cameraSelector, preview, imageAnalysis);

                } catch (Exception exc) {
                    Log.e(TAG, "Use case binding failed", exc);
                }

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera initialization failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void resetCaptureState() {
        hasCapturedBarcode = false;
        capturedBarcodeValue = null;
        if (barcodeValueText != null) {
            barcodeValueText.setText("Capture Barcode");
        }
        if (instructionText != null) {
            instructionText.setText("Point your camera at a bar code");
        }
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
        private final BarcodeScanner scanner;

        BarcodeAnalyzer() {
            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();
            scanner = BarcodeScanning.getClient(options);
        }

        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            if (hasCapturedBarcode) {
                imageProxy.close();
                return;
            }
            @androidx.camera.core.ExperimentalGetImage
            android.media.Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage,
                        imageProxy.getImageInfo().getRotationDegrees());

                scanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            if (!barcodes.isEmpty() && !hasCapturedBarcode) {
                                Barcode barcode = barcodes.get(0);
                                capturedBarcodeValue = barcode.getRawValue();
                                hasCapturedBarcode = true;
                                Log.d(TAG, "Barcode detected: " + capturedBarcodeValue);
                                runOnUiThread(() -> {
                                    if (barcodeValueText != null) {
                                        barcodeValueText.setText(
                                                capturedBarcodeValue != null ? capturedBarcodeValue : "Unknown barcode");
                                    }
                                    if (instructionText != null) {
                                        instructionText.setText("Tap capture to scan another barcode");
                                    }
                                    Toast.makeText(BarcodeScannerActivity.this,
                                            capturedBarcodeValue != null ? "Barcode: " + capturedBarcodeValue
                                                    : "Barcode captured",
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Barcode detection failed", e))
                        .addOnCompleteListener(task -> imageProxy.close());
            } else {
                imageProxy.close();
            }
        }
    }
}
