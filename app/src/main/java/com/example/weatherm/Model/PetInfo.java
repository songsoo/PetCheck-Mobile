package com.example.weatherm.Model;

import java.io.Serializable;

public class PetInfo  implements Serializable {

    public String masterUid;
    public String petName;
    public String petType;
    public String age;
    public String weight;
    public String character;
    public String gender;

    public PetInfo(String masterUid, String petName, String petType, String age, String weight, String character, String gender) {
        this.masterUid = masterUid;
        this.petName = petName;
        this.petType = petType;
        this.age = age;
        this.weight = weight;
        this.character = character;
        this.gender = gender;
    }

    public String getMasterUid() {
        return masterUid;
    }

    public void setMasterUid(String masterUid) {
        this.masterUid = masterUid;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
