package com.unallapps.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.unallapps.chatapp.databinding.ActivityProfileBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    Button profileUpdate;
    EditText ageEdittext;
    ImageView profilImage;
    Uri selectedUri;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        profileUpdate = binding.profileUpload;
        ageEdittext = binding.profileAge;
        profilImage = binding.selectImage;

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        getData();
        profilImage.setOnClickListener(view1 -> selectImageGallery());
        profileUpdate.setOnClickListener(view12 -> profileUpdateBtn());
    }

    private void profileUpdateBtn() {
        UUID uuidImage = UUID.randomUUID();
        String imageName = "images/" + uuidImage + "jpg";
        StorageReference newReference = storageReference.child(imageName);
        newReference.putFile(selectedUri).addOnSuccessListener(taskSnapshot -> {
            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("images/" + uuidImage + "jpg");
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String dowloandURL = uri.toString();
                UUID uuid = UUID.randomUUID();
                String uuidString = uuid.toString();
                String userAge = ageEdittext.getText().toString();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String userEmail = user.getEmail().toString();
                databaseReference.child("Profiles").child(uuidString).child("userimageurl").setValue(dowloandURL);
                databaseReference.child("Profiles").child(uuidString).child("userage").setValue(userAge);
                databaseReference.child("Profiles").child(uuidString).child("useremail").setValue(userEmail);
                Toast.makeText(this, "Uploaded!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            });
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show());
    }

    private void selectImageGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getData() {
        DatabaseReference databaseReference2 = firebaseDatabase.getReference("Profiles");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) snapshot1.getValue();
                    String userName = hashMap.get("useremail");
                    if (userName.matches(firebaseAuth.getCurrentUser().getEmail())) {
                        String age = hashMap.get("userage");
                        String image = hashMap.get("userimageurl");
                        System.out.println(image);
                        if (age != null && image != null) {
                            Picasso.get().load(image).into(profilImage);
                            ageEdittext.setText(age);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selectedUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedUri);
                profilImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}