package com.company;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    String name;
    BattleField field;
    List<Ship> shipList;
    List<AttackRecord> records;

    public Player(String name, BattleField field, List<Ship> ships) {
        this.name = name;
        this.field = field;
        this.shipList = ships;
        this.shipList.forEach(s -> this.field.placeShip(s));
        this.records = new ArrayList<>();
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
