package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;

public class PartyMasterModel implements Serializable {

    private String id;
    private String party_Name;
    private String address;

    public PartyMasterModel(String id, String party_Name, String address) {
        this.id = id;
        this.party_Name = party_Name;
        this.address = address;
    }

    public PartyMasterModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParty_Name() {
        return party_Name;
    }

    public void setParty_Name(String party_Name) {
        this.party_Name = party_Name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Nonnull
    @Override
    public String toString() {
        return "PartyModel{" +
                "id='" + id + '\'' +
                ", party_Name='" + party_Name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
