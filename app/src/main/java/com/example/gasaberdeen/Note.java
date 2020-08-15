package com.example.gasaberdeen;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String documentId;
    private String name;
    private float diesel;
    private float petrol;
    private double longitude;
    private double latitude;

    public Note() {
        //public no-arg constructor needed
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    //documentId used for debugging purposes only
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


    public Note(String Name, float Diesel, float Petrol, double longitude, double latitude) {
        this.name = Name;
        this.diesel = Diesel;
        this.petrol = Petrol;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public float getDiesel() {
        return diesel;
    }

    public float getPetrol() {
        return petrol;
    }

    public double getLongitude() { return longitude; }

    public double getLatitude() { return latitude; }
}