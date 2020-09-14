package com.example.mtandao.services;

public interface PostsListener {
    interface UploadPostListener{
        void onPostUploaded();
        void onFailureResponse(Exception e);
    }
}
