package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ship {
    int size;
    String name = "unnamed";

    private List<Integer> occupies;
    private Set<Integer> areaHit;

    public Ship(int size) {
        this.size = size;
        this.occupies = new ArrayList<>(size);
        this.areaHit = new HashSet<>(size);
    }

    public Ship(int size, String name) {
        this(size);
        this.name = name;
    }

    void occupy(int area) {
        this.occupies.add(area);
    }

    public boolean isSunk() {
        return this.areaHit.size() == this.size;
    }

    int hit(int area) {
        if (this.occupies.contains(area)) {
            this.areaHit.add(area);
        }

        return this.areaHit.size();
    }

    boolean isPartOfMe(int area) {
        return this.occupies.contains(area);
    }

}
