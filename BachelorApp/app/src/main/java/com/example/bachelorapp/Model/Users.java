package com.example.bachelorapp.Model;

public class Users {
    private String email, password, username, image, genres;

    public Users() {}

    public Users(String email, String password, String username, String image, String genres) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.image = image;
        this.genres = genres;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
