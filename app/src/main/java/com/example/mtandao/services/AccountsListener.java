package com.example.mtandao.services;

public interface AccountsListener {
    interface RegistrationListener{
        void onAccountCreated();
        void onFailureResponse(Exception e);
    }

    interface  LoginListener{
        void onSuccessLogin();
        void onLoginFailure();
        void onFailureResponse(Exception e);
    }
}
