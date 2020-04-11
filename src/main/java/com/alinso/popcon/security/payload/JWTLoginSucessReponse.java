package com.alinso.popcon.security.payload;

public class JWTLoginSucessReponse {
    private boolean success;
    private String token;
    private String profilePicName;

    public JWTLoginSucessReponse(boolean success, String token, String profilePicName) {
        this.success = success;
        this.token = token;
        this.profilePicName = profilePicName;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JWTLoginSucessReponse{" +
                "success=" + success +
                ", token='" + token + '\'' +
                ", profilePicName='" + profilePicName + '\'' +
                '}';
    }

    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
    }
}
