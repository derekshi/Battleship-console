package com.company;

public class AttackRecord {

    String attackArea;
    boolean hit;

    AttackRecord(String label, boolean hit) {
        this.attackArea = label;
        this.hit = hit;
    }
}
