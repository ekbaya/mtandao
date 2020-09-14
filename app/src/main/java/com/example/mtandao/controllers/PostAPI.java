package com.example.mtandao.controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.mtandao.services.PostsListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;
import java.util.Objects;

public class PostAPI {
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private PostsListener.UploadPostListener uploadPostListener;

    public PostAPI(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Posts");
        auth = FirebaseAuth.getInstance();
    }

    public void uploadPost(final Map<Object, String> post, byte[] data){
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;// ensures user is not equal to null
        final String uid = user.getUid();


        final String timestamp = String.valueOf(System.currentTimeMillis());
        String filePath = "Posts/" +"post_"+timestamp;  //e.g Posts/post_2324243535522252627

        storageReference = FirebaseStorage.getInstance().getReference().child(filePath);
        storageReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image has been uploaded, get its url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String downloadUrl = Objects.requireNonNull(uriTask.getResult()).toString();

                        post.put("likes", "0");
                        post.put("comments", "0");
                        post.put("image", downloadUrl);
                        post.put("id", timestamp);
                        post.put("uid", uid);
                        post.put("pTime", timestamp);

                        reference.setValue(post)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            uploadPostListener.onPostUploaded();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        uploadPostListener.onFailureResponse(e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadPostListener.onFailureResponse(e);
                    }
                });
    }

    public PostsListener.UploadPostListener getUploadPostListener() {
        return uploadPostListener;
    }

    public void setUploadPostListener(PostsListener.UploadPostListener uploadPostListener) {
        this.uploadPostListener = uploadPostListener;
    }
}
