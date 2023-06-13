package com.example.androidproject.Fragments;

import static android.app.Activity.RESULT_OK;

import static com.google.common.io.Files.getFileExtension;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Activity.AllTransactionActivity;
import com.example.androidproject.Activity.CustomerHome;
import com.example.androidproject.Activity.Login;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment {

    private Button btnLogout, btnTransactionHist, uploadImage, openURLbtn, queueBtn, allTransactionHistBtn;
    private TextView editProfile, fullname, useremail;
    Context context = getContext();
    private SharedPreferences sharedPreferences;
    private ImageView userImage;
    private FloatingActionButton fab;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();

        //image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        imageUri = data.getData();

                        Picasso.get().load(imageUri).into(userImage);
                        userImage.setVisibility(View.VISIBLE);
                        //Toast.makeText(context, "Image added", Toast.LENGTH_SHORT).show();
                    }
                    else {

                    }

                    if(imageUri != null){
                        showAlertDialogForImage(imageUri);
                    }

                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(user.getUid());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        btnLogout = view.findViewById(R.id.btn_logout);
        userImage = view.findViewById(R.id.user_profile_image);
        editProfile = view.findViewById(R.id.edit_profile_btn);
        fullname = view.findViewById(R.id.user_fullname);
        useremail = view.findViewById(R.id.user_email);

        displayUserData();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                displayUserData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Button uploadImageBtn = view.findViewById(R.id.upload_image);
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear the relevant shared preferences
                /*SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_id_prefs");
                editor.remove("isLoggedIn");
                editor.apply();*/
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_id_prefs", user.getUid());
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                // Redirect to the login activity
                Intent intent = new Intent(requireActivity(), Login.class);
                startActivity(intent);
                requireActivity().finish();

            }
        });

        btnTransactionHist = view.findViewById(R.id.btn_transaction);
        btnTransactionHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllTransactionActivity.class);
                startActivity(intent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        openURLbtn = view.findViewById(R.id.openURLbutton);
        openURLbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl();
            }
        });

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference userRef = usersRef.child(SignInSingleton.getInstance().getAuthUserId());

        return view;
    }

    private void openUrl() {
        String url = "https://www.jollibee.com.ph/"; // Replace with your desired URL scheme

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveImageToFirebase(Uri imageUri) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
        StorageReference imageRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(String.valueOf(imageUri)));
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // The image was uploaded successfully
                // Get the download URL of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Handle the success case, you can use the uri to retrieve the uploaded image URL
                        String imageUrl = uri.toString();

                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("userimage", imageUrl);

                        // Update the form data in Firebase Realtime Database
                        databaseReference.updateChildren(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data update successful
                                    //Toast.makeText(context, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle update failure
                                    //Toast.makeText(context, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors that occurred while uploading the image
            }
        });
    }

    private AlertDialog dialog;
    private void updateUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view2 = LayoutInflater.from(context).inflate(R.layout.user_edit, null);
        builder.setView(view2);

        TextInputEditText fname = view2.findViewById(R.id.txtFirstName);
        TextInputEditText lname = view2.findViewById(R.id.txtLastName);
        TextInputEditText email = view2.findViewById(R.id.txtEmail);
        TextInputEditText pwd = view2.findViewById(R.id.txtPassword);

        //get signed in authentication
        String uid = user.getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String f_name = snapshot.child("userfirstname").getValue(String.class);
                String l_name = snapshot.child("userlastname").getValue(String.class);
                String u_email = user.getEmail();

                fname.setText(f_name);
                lname.setText(l_name);
                email.setText(u_email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //update button
        Button btnUpdate = view2.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fname.getText().toString().equals("") || lname.getText().toString().equals("")){
                    Toast.makeText(context, "Please fill your information correctly.", Toast.LENGTH_SHORT).show();
                }else{

                    Map<String, Object> updateData = new HashMap<>();
                    //updateData.put("useremail", email.getText().toString());
                    updateData.put("userfirstname", fname.getText().toString());
                    updateData.put("userlastname", lname.getText().toString());

                    // Update the form data in Firebase Realtime Database
                    databaseReference.updateChildren(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data update successful
                                    Toast.makeText(context, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle update failure
                                    Toast.makeText(context, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                                }
                            });

                }


                // Update authentication email
                /*String newEmail = email.getText().toString();
                if (!TextUtils.isEmpty(newEmail) && !newEmail.equals(user.getEmail())) {
                    user.updateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Email updated successfully
                                        Toast.makeText(context, "Email updated successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed to update email
                                        Toast.makeText(context, "Failed to update email.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }*/

                // Update authentication password
                /*String newPassword = pwd.getText().toString();
                if (!TextUtils.isEmpty(newPassword)) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Password updated successfully
                                        Toast.makeText(context, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed to update password
                                        Toast.makeText(context, "Failed to update password.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }*/

            }
        });

        builder.setNegativeButton("Cancel", null);
        dialog = builder.create();
        dialog.show();
    }

    private void displayUserData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String f_name = snapshot.child("userfirstname").getValue(String.class);
                String l_name = snapshot.child("userlastname").getValue(String.class);
                String imageURL = snapshot.child("userimage").getValue(String.class);
                String u_email = user.getEmail();

                fullname.setText(f_name+ " " +l_name);
                useremail.setText(u_email.toString());

                if(imageURL.equals("")){
                    String link = "https://i.pinimg.com/564x/d0/06/af/d006af4e980707901a55c3c0f29f0dd9.jpg";
                    Picasso.get().load(link).into(userImage);
                    userImage.setVisibility(View.VISIBLE);
                }else{
                    Picasso.get().load(imageURL).into(userImage);
                    userImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void showAlertDialogForImage(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Continue Update Your Photo?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImageToFirebase(imageUri);
                Toast.makeText(context, "Photo changed successfully.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                displayUserData();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String dbimage = snapshot.child("userimage").getValue(String.class);

                        if(dbimage.equals("")){
                            String link = "https://i.pinimg.com/564x/d0/06/af/d006af4e980707901a55c3c0f29f0dd9.jpg";
                            Picasso.get().load(link).into(userImage);
                            userImage.setVisibility(View.VISIBLE);
                        }else{
                            Picasso.get().load(dbimage).into(userImage);
                            userImage.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}