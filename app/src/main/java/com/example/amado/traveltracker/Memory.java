package com.example.amado.traveltracker;

import java.io.Serializable;

/**
 * Created by Amado on 30/04/2015.
 */
public class Memory implements Serializable{
    long id;
    double latitude;
    double longitude;
    String city;
    String country;
    String notes;
}
