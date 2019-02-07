package com.kocsistem.chatbot.model;



public class UserInfo {

    private int Id;
    private String UserName;
    private String FirstName;
    private String LastName;
    private String ProfilePicturePath;
    private String Title;
    private int MontlyPoint;
    private int TotalPoint;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getProfilePicturePath() {
        return ProfilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        ProfilePicturePath = profilePicturePath;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getMontlyPoint() {
        return MontlyPoint;
    }

    public void setMontlyPoint(int montlyPoint) {
        MontlyPoint = montlyPoint;
    }

    public int getTotalPoint() {
        return TotalPoint;
    }

    public void setTotalPoint(int totalPoint) {
        TotalPoint = totalPoint;
    }
}
