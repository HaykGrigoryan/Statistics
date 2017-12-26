package com.constantlab.statistics.models;

import io.realm.RealmObject;

/**
 * Created by Sunny Kinger on 08-12-2017.
 */

public class Address extends RealmObject {
    private AddressStreet street;
    private StreetType streetType;
    private Kato kato;
    private String addressRu;
    private String addressKk;

    public AddressStreet getStreet() {
        return street;
    }

    public void setStreet(AddressStreet street) {
        this.street = street;
    }

    public StreetType getStreetType() {
        return streetType;
    }

    public void setStreetType(StreetType streetType) {
        this.streetType = streetType;
    }

    public Kato getKato() {
        return kato;
    }

    public void setKato(Kato kato) {
        this.kato = kato;
    }

    public String getAddressRu() {
        return addressRu;
    }

    public void setAddressRu(String addressRu) {
        this.addressRu = addressRu;
    }

    public String getAddressKk() {
        return addressKk;
    }

    public void setAddressKk(String addressKk) {
        this.addressKk = addressKk;
    }
}
