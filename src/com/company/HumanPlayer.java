package com.company;

import java.util.List;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private static Scanner input = new Scanner(System.in);

    HumanPlayer(String name, BattleField field, List<Ship> ships) {
        super(name, field, ships);
    }

    @Override
    boolean attack(Player defender) {
        System.out.println("Your turn to attack, please enter grid to attack:");
        String attackLabel = input.next();

        Ship shipHit = defender.field.isHit(attackLabel);
        recordAttack(attackLabel, shipHit != null);

        if (shipHit != null) {
            System.out.println("Nice!!! You hit an opponent's ship!");

            if (shipHit.isSunk()) {
                System.out.println(String.format("*** WOW, you have sunk one of opponents ships (%s)! ***", shipHit.name));

                if (defender.isLost()) {
                    System.out.println("*** CONGRATULATIONS, Mission accomplished! YOU WIN ! ***");
                    return true;
                }
            }
        } else {
            System.out.println("Mmm, you didn't hit any ships." );
        }

        return false;
    }
}
