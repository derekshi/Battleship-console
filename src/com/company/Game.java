package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {

    private Player human;
    private Player computer;
    private boolean endGame = false;

    private static final int totalRounds = 5;
    private static Scanner input = new Scanner(System.in);

    private static final int BattleFieldCol = 5;
    private static final int BattleFieldRow = 5;
    private static final int[] ShipSizes = {2, 3, 4, 5};

    Game() {
        initPlayers();
        printInstructions();
    }

    void initPlayers() {
        computer = new ComputerPlayer();
        computer.setBattleField(new BattleField(BattleFieldCol, BattleFieldRow));
        computer.addShips(generateShips());
        computer.field.displayField();

        System.out.println("Please enter your name to start game: ");
        //Ask for user name
        String playerName = input.nextLine();
        human = new HumanPlayer(playerName);
        human.setBattleField(new BattleField(BattleFieldCol, BattleFieldRow));
        human.addShips(generateShips());

        System.out.println(String.format("\nWelcome %s, here is your battle field: ", playerName));
        human.field.displayField();
        System.out.println("\nNOTE: [number] represents ship pieces. ~ represents water.\n");
    }

    List<Ship> generateShips() {
        List<Ship> ships = new ArrayList<>(ShipSizes.length);
        for(int i = ShipSizes.length - 1; i >= 0; i --) {
            ships.add(new Ship(ShipSizes[i], String.format("Size-%d Ship", ShipSizes[i])));
        }

        return ships;
    }

    void printInstructions() {
        System.out.println("Your opponent's battle field has the same grid coordinates as yours. " +
                "\nTo attack, simply type in [Column][Row], such as F8. " +
                "You and your opponent (computer) will take turns to attack each other.");
        System.out.println(String.format("Your mission is to sink all your opponent ships in no more than %d attacks before " +
                "your opponent sinks all your ships.", totalRounds));
    }

    void start() {
        System.out.println("\nPlease any key to start:");
        input.nextLine();

        int count = 0;
        while (count < totalRounds && !endGame) {
            count++;
            System.out.println(String.format("\n===== ROUND %d ======", count));

            endGame = human.attack(computer);
            if (!endGame) {
                endGame = computer.attack(human);
            }
        }

        if (count >= totalRounds) {
            System.out.println("\n@@@ Sorry, your mission failed. @@@");
        }
    }

    void stop() {
        this.printStatics();
        System.out.println(String.format("\nThanks for playing, %s. GOOD-BYE!", human.name));
    }

    void printStatics() {
        System.out.println(String.format("\nYour total attack:  %d", human.totalAttacks()));
        System.out.println(String.format("Your total hits:  %d", human.totalHits()));
        System.out.println("\nThis is how your opponent battlefield look like now: (X represents ship got hit.)");
        computer.field.displayField();

        System.out.println(String.format("\nComputer total attack:  %d", computer.totalAttacks()));
        System.out.println(String.format("Computer total hits:  %d", computer.totalHits()));
        System.out.println("\nThis is how your own battlefield look like now: (X represents ship got hit.)");
        human.field.displayField();
    }
}
