package com.example.agrimart;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agrimart.model.User;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView locationTextView;
    private ImageView getLocationButton;
    private EditText signUpName, signUpEmail, signUpPhone, signUpPassword, signUpConfirmPassword;
    private String address = "";
    private AppCompatButton signUpBtn;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize the FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get references to the button, text views, and edit texts
        signUpName = findViewById(R.id.signUpName);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPhone = findViewById(R.id.signUpPhone);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpConfirmPassword = findViewById(R.id.signUpConfirmPassword);
        getLocationButton = findViewById(R.id.signUpLocationImg);
        locationTextView = findViewById(R.id.signUpLocationTxt);

        signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(v -> {
            if (validateAndSubmit()) {
                registerUser();
            }
        });

        // Set a click listener on the Get Location button
        getLocationButton.setOnClickListener(v -> {
            // Check if location services are enabled
            if (!isLocationEnabled()) {
                Toast.makeText(this, "Location services are not enabled. Please enable them in the settings.", Toast.LENGTH_LONG).show();
            } else {
                // Check for location permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission if not granted
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    // If permission is granted, get the location
                    getLastLocation();
                }
            }
        });
    }

    // Method to check if location services are enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // Method to get the last known location
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            // Get latitude and longitude
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Use Geocoder to get the detailed location
                            address = getAddressFromLocation(latitude, longitude);

                            // Update the TextView with the location and address
                            locationTextView.setText(address);
                        } else {
                            locationTextView.setText("Location not available");
                            Toast.makeText(SignUpActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to fetch the detailed address
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String locationDetails = "Not Available";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String locality = address.getLocality(); // Current location (City or equivalent)
                //  String taluka = address.getSubLocality(); // Taluka or equivalent
                String district = address.getSubAdminArea(); // District
                String state = address.getAdminArea(); // State
                String country = address.getCountryName(); // Country

                // Construct the full location details
                locationDetails = String.format(Locale.getDefault(),
                        "Location: %s, Division: %s, State: %s, Nation: %s",
                        locality != null ? locality : "Not Available",
                        district != null ? district : "Not Available",
                        state != null ? state : "Not Available",
                        country != null ? country : "Not Available");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to get address", Toast.LENGTH_SHORT).show();
        }

        return locationDetails;
    }

    // User sign-up in Firebase
    private void registerUser() {
        String name = signUpName.getText().toString().trim();
        String email = signUpEmail.getText().toString().trim();
        String phone = signUpPhone.getText().toString().trim();
        String password = signUpPassword.getText().toString().trim();
        String confirmPassword = signUpConfirmPassword.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        saveUserToDatabase(name, email, phone, address, password);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String name, String email, String phone, String address, String password) {
        String sanitizedEmail = email.replace(".", "_"); // Email sanitization
        User user = new User(name, email, phone, address, password);

        databaseReference.child(sanitizedEmail).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Validate the inputs before submitting
    private boolean validateAndSubmit() {
        String name = signUpName.getText().toString().trim();
        String email = signUpEmail.getText().toString().trim();
        String phone = signUpPhone.getText().toString().trim();
        String password = signUpPassword.getText().toString().trim();
        String confirmPassword = signUpConfirmPassword.getText().toString().trim();
        String location = locationTextView.getText().toString();

        if (TextUtils.isEmpty(name)) {
            showToast("Name cannot be empty");
            return false;
        }

        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            showToast("Please enter a valid email");
            return false;
        }

        if (TextUtils.isEmpty(phone) || !isValidPhone(phone)) {
            showToast("Please enter a valid phone number");
            return false;
        }

        if (TextUtils.isEmpty(location) || location.equals("Location not available")) {
            showToast("Please get a valid location");
            return false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            showToast("Password must be at least 6 characters long");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return false;
        }

        return true; // Return true if all validations pass
    }

    // Helper method to check if email is valid
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Helper method to check if phone is valid
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}"); // Assuming 10-digit phone number
    }

    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void signin(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();
    }
}