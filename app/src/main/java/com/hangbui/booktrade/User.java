package com.hangbui.booktrade;

public class User {

    private String id;
    private String email;
    private String name;
    private String photoUrl;
    private String university;

    public User(String id, String email, String name, String photoUrl, String universityId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.university = university;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }
}


