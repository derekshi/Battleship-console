package com.company;

public class ComputerPlayer extends Player {

    ComputerPlayer() {
        super("Computer");
    }

    @Override
    boolean attack(Player defender) {
        String attackLabel = defender.field.getRandomAreaLabel();

        Ship shipHit = defender.field.isHit(attackLabel);
        recordAttack(attackLabel, shipHit != null);

        if (shipHit != null) {
            System.out.println(String.format("ATTENTION: Computer just attacked you at %s and your %s got hit!",
                    attackLabel, shipHit.name));

            if (shipHit.isSunk()) {
                System.out.println(String.format("@@@ Sorry, you just lost %s! @@@", shipHit.name));

                if (defender.isLost()) {
                    System.out.println(
                            "You lost! Mission unaccomplished.");
                    return true;
                }
            }
        } else {
            System.out.println(
                    String.format("Computer attached at %s but missed!", attackLabel));
        }

        return false;
    }
}
