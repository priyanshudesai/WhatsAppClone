package com.pd.chatapp.model.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "contacts",indices = @Index(value = {"userID"},unique = true))
public class Users {
    @PrimaryKey(autoGenerate = true)
    private int contactId;

    @SerializedName("userID")
    @ColumnInfo(name = "userID")
    private String userID;

    @SerializedName("userName")
    @ColumnInfo(name = "userName")
    private String userName;

    @SerializedName("userPhone")
    @ColumnInfo(name = "userPhone")
    private String userPhone;

    @SerializedName("imageProfile")
    @ColumnInfo(name = "imageProfile")
    private String imageProfile;

    @SerializedName("imageCover")
    @ColumnInfo(name = "imageCover")
    private String imageCover;

    @SerializedName("email")
    @ColumnInfo(name = "email")
    private String email;

    @SerializedName("dateOfBirth")
    @ColumnInfo(name = "dateOfBirth")
    private String dateOfBirth;

    @SerializedName("gender")
    @ColumnInfo(name = "gender")
    private String gender;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("bio")
    @ColumnInfo(name = "bio")
    private String bio;

    public Users() {
    }

    public Users(String userID, String userName, String userPhone, String imageProfile, String imageCover, String email, String dateOfBirth, String gender, String status, String bio) {
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.imageProfile = imageProfile;
        this.imageCover = imageCover;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.status = status;
        this.bio = bio;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getImageCover() {
        return imageCover;
    }

    public void setImageCover(String imageCover) {
        this.imageCover = imageCover;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    @Override
    public String toString() {
        return "Users{" +
                "contactId=" + contactId +
                ", userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", imageProfile='" + imageProfile + '\'' +
                ", imageCover='" + imageCover + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", status='" + status + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }
}
