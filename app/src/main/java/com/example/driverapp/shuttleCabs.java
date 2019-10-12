package com.example.driverapp;

public class shuttleCabs {
    String cabID;
    double cabx;
    double caby;
    int cabpax;
    int full;

    public  shuttleCabs(){

    }

    public shuttleCabs(String cabID, double cabx, double caby, int cabpax, int full) {
        this.cabID = cabID;
        this.cabx = cabx;
        this.caby = caby;
        this.cabpax = cabpax;
        this.full = full;
    }

    public String getCabID() {
        return cabID;
    }

    public double getCabx() {
        return cabx;
    }

    public int getFull(){
        return full;
    }
    public double getCaby() {
        return caby;
    }

    public int getCabpax() {
        return cabpax;
    }
}
