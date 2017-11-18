package com.company;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    String name;
    BattleField field;
    List<Ship> shipList;
    List<AttackRecord> records;

    public Player(String name) {
        this.name = name;
        this.records = new ArrayList<>();
    }

    void setBattleField(BattleField field) {
        this.field = field;
    }

    void addShips(List<Ship> ships) {
        if (this.field == null) {
            throw new RuntimeException("Please set BattleField before adding ships.");
        }

        this.shipList = ships;
        this.shipList.forEach(s -> this.field.placeShip(s));
    }

    boolean attack(Player defender) {
        return false;
    }

    void recordAttack(String label, boolean result) {
        this.records.add(new AttackRecord(label, result));
    }

    int totalAttacks() {
        return records.size();
    }

    int totalHits() {
        return this.records.stream().mapToInt(r ->  r.hit ? 1 : 0 ).sum();
    }

    boolean isLost() {
        for (Ship ship: shipList) {
            if (!ship.isSunk()) {
                return false;
            }
        }

        return true;
    }

}
