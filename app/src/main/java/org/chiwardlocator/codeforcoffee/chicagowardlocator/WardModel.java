package org.chiwardlocator.codeforcoffee.chicagowardlocator;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by codeforcoffee on 4/9/16.
 */
public class WardModel {

    private String name;
    private String alderman;
    private String phoneNumber;

    public WardModel(String name, String alderman, String phoneNumber) {
        this.name = name;
        this.alderman = alderman;
        this.phoneNumber = phoneNumber;
    }

    private String convertStringToPrettyText(String text) {
        String[] words = text.split(" ");
        for(int inc = 0; inc <= words.length; inc++) {
            words[inc] = words[inc].toLowerCase().substring(0,1).toUpperCase();
        }
        return new String(words.toString());
    }

    public String toString() {
        String wardInformation = "";
        wardInformation = wardInformation +
                convertStringToPrettyText(this.name) + "\n " +
                convertStringToPrettyText(this.alderman) + "\n" +
                this.phoneNumber;
        return wardInformation;
    }

}
