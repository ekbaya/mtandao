package com.example.mtandao.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mtandao.R;
import com.example.mtandao.controllers.PostAPI;
import com.example.mtandao.services.PostsListener;
import com.example.mtandao.utils.Loader;
import com.example.mtandao.utils.Permission;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener , PostsListener.UploadPostListener {
    //views
    private EditText editTitle, editDescription;
    private ImageView imageView;
    private Button btnPost;

    private Permission permission;
    private String[] storagePermissions;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int PICK_IMAGE_REQUEST_CODE = 300;
    private Uri image_url = null;

    private PostAPI postAPI;
    private Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        //init
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        imageView = findViewById(R.id.imageView);
        btnPost = findViewById(R.id.btnPost);

        imageView.setOnClickListener(this);
        btnPost.setOnClickListener(this);

        permission = new Permission(this);
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        postAPI = new PostAPI(this);
        postAPI.setUploadPostListener(this);
        loader = new Loader(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view.equals(imageView)){
            if (!permission.checkStoragePermissions()){
                requestStoragePermission();
            }
            else {
                pickImageFromGallery();
            }

        }
        if (view.equals(btnPost)){
            if (validated()){
                //start uploading
                //get image from the imageView
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //compress image
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                //get byte[] from the outputStream
                byte[] data = baos.toByteArray();

                Map<Object, String> post = new HashMap<>();
                post.put("title", editTitle.getText().toString());
                post.put("description", editDescription.getText().toString());

                //post data to our database
                postAPI.uploadPost(post, data);
                loader.showDialogue();

            }
        }
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        //request runtime permissions
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean validated() {
        if (TextUtils.isEmpty(editTitle.getText().toString())){
            Toast.makeText(this, "Post tile is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (imageView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.add_image).getConstantState()){
            Toast.makeText(this, "Please select an image for the post", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(editDescription.getText().toString())){
            Toast.makeText(this, "Post description is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if (requestCode == STORAGE_REQUEST_CODE){
           if (grantResults.length > 0){
               boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
               if (writeStorageAccepted){
                   //permissions has been accepted
                   pickImageFromGallery();
               }
               else {
                   //permission has been denied
                   Toast.makeText(this, "Please enable storage permissions", Toast.LENGTH_SHORT).show();
               }
           }
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == PICK_IMAGE_REQUEST_CODE){
                //image is picked from gallery so we get the image url
                image_url = data.getData();
                imageView.setImageURI(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPostUploaded() {
        loader.hideDialogue();
        Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddPostActivity.this, MainActivity.class));
        finish();

    }

    @Override
    public void onFailureResponse(Exception e) {
        loader.hideDialogue();
        Toast.makeText(this, "Failed, "+e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}