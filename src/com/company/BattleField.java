package com.company;
import java.util.*;

public class BattleField {
    int col;
    int row;
    private int startArea;
    private int endArea;

    private List<String> colLabel;
    private List<Integer> rowLabel;
    private List<String> fieldArea;
    private Map<Integer, Ship> occupied = new HashMap<>();
    private Set<Integer> randomed = new HashSet<>();

    public BattleField(int col, int row) {
        this.col = col;
        this.row = row;

        this.colLabel = new ArrayList<>(col);
        this.rowLabel = new ArrayList<>(row);

        int area = col * row;
        this.fieldArea = new ArrayList<>(area);

        this.startArea = 0;
        this.endArea = col * row - 1;

        // initialize row and column labels
        int charA = (int) 'A';
        for(int i = 0; i < row; i++) {
            rowLabel.add(i + 1);
        }

        for(int i = 0; i< col; i++) {
            colLabel.add(Character.toString((char)(charA + i)));
        }

        // initialize field area for printing
        for(int i = 0; i < area; i++) {
            this.fieldArea.add("~"); // ~ represents water
        }
    }

    String getRandomAreaLabel() {
        if (randomed.size() >= this.endArea) {
            throw new RuntimeException("No more random labels can be generated. Please start over.");
        }

        Random random = new Random();
        int area = random.nextInt(this.endArea + 1);
        while (randomed.contains(area)) {
            area = random.nextInt(this.endArea + 1);
        }

        randomed.add(area);
        System.out.println(String.format("random area: %d", area));
        return area2Label(area);
    }


    int label2Area(String label) {
        String colChar = label.toUpperCase().substring(0, 1);
        String rowNum = label.toUpperCase().substring(1);

        try {
            int colIndex = colLabel.indexOf(colChar);
            int rowIndex = rowLabel.indexOf(Integer.parseInt(rowNum));
            if (colIndex < 0 || rowIndex < 0) {
                throw new RuntimeException(String.format("Invalid label %s", label));
            }
            return rowIndex * this.col + colIndex;
        } catch (NumberFormatException ex) {
            throw new RuntimeException(String.format("Invalid label %s", label));
        }
    }

    String area2Label(int area) {
        if (area < startArea || area > endArea ) {
            System.out.println(String.format("Area %d is out of bounds of battle field", area));
            return null;
        }

        int colIndex = area % this.col;
        int rowIndex = (int) Math.floor(area / this.col);
        return colLabel.get(colIndex) + rowLabel.get(rowIndex);
    }

    Ship isHit(String label) {
        try {
            int area = this.label2Area(label);

            if (this.occupied.containsKey(area)) {
                Ship shipHit = this.occupied.get(area);
                this.fieldArea.set(area, "X"); // X means ship hit;
                shipHit.hit(area);
                return shipHit;
            }
        } catch (RuntimeException ex) {
            System.out.println(ex.getLocalizedMessage());
        }

        return null;
    }

    boolean placeShip(Ship ship) {
        int size = ship.size;

        if (size < 1 || size > Math.max(this.col, this.row)) {
            throw new RuntimeException("Invalid ship size.");
        }

        if (this.endArea + 1 - this.occupied.size() < size) {
            System.out.println("Not enough area to place the ship.");
            return false;
        }

        List<Integer> allocated;
        Random random = new Random();
        int maxTry = 100; // avoid infinite loop
        int count = 0;

        boolean found = false;
        while(!found && count <= maxTry) {
            count++;
            int rn = random.nextInt(this.endArea + 1);

            while(this.occupied.containsKey(rn)) {
                // random starting point and make sure not the area occupied
                rn = random.nextInt(this.endArea + 1);
            }

            // place horizontally or vertically
            int vorh = random.nextInt(2);
            // if horizontal, go right or left first; if vertical, go down or up first
            int direction = random.nextInt(2);

            if (vorh == 0) {
                // place horizontally first
                allocated = allocHorizontal(size, rn, direction);
                if (allocated == null || allocated.size() == 0) {
                    allocated = allocVertical(size, rn, direction);
                }
            } else {
                // place vertically first
                allocated = allocVertical(size, rn, direction);
                if (allocated == null || allocated.size() == 0) {
                    allocated = allocHorizontal(size, rn, direction);
                }
            }

            if (allocated != null && allocated.size() > 0) {
                // add to occupied list
                allocated.forEach( area -> {
                    this.occupied.put(area, ship);
                    this.fieldArea.set(area, String.valueOf(ship.size)); // $ represents ship piece
                    ship.occupy(area);
                });

                found = true;
            }
        }

        return found;
    }

    private List<Integer> allocHorizontal(int size, int rn, int direction) {
        List<Integer> allocated = new ArrayList<>(size);

        if (direction == 0) {
            // go to right first
            allocated.add(rn);
            boolean found = true;
            int rest = 0;
            for (int i = 1; i < size; i++) {
                if (this.occupied.containsKey(rn + i) || rn % this.col + i >= this.col) {
                    // change direction to left
                    rest = size - allocated.size();
                    break;
                } else {
                    allocated.add(rn + i);
                }
            }

            if (rest > 0 && rn % this.col - rest >= 0) {
                for (int j = 1; j <= rest; j ++) {
                    if (this.occupied.containsKey(rn - j)) {
                        allocated.clear();
                        found = false;
                        break;
                    } else {
                        allocated.add(rn - j);
                    }
                }
            } else {
                found = false;
            }

            if (found) {
                return allocated;
            }
        }

        if (direction == 1) {
            // go to left
            allocated.add(rn);
            int rest = 0;
            boolean found = true;
            for (int i = 1; i < size; i++) {
                if (this.occupied.containsKey(rn - i) || rn % this.col - i < 0) {
                    rest = size - allocated.size();
                    break;
                } else {
                    allocated.add(rn - i);
                }
            }

            if (rest > 0 && rn % this.col + rest < this.col) {
                for (int j = 1; j <= rest; j ++) {
                    if (this.occupied.containsKey(rn + j)) {
                        allocated.clear();
                        found = false;
                        break;
                    } else {
                        allocated.add(rn + j);
                    }
                }
            } else {
                found = false;
            }

            if (found) {
                return allocated;
            }
        }

        return null;
    }

    private List<Integer> allocVertical(int size, int rn, int direction) {
        List<Integer> allocated = new ArrayList<>(size);

        if (direction == 0) {
            // go straight down
            allocated.add(rn);

            int rest = 0;
            boolean found = true;
            for (int i = 1; i < size; i++) {
                if (this.occupied.containsKey(rn + i * this.col) || rn + i * this.col > this.endArea) {
                    rest = size - allocated.size();
                    break;
                } else {
                    allocated.add(rn + i * this.col);
                }
            }

            if (rest > 0 && rn - rest * this.col >= 0) {
                // change direction
                for (int j = 1; j <= rest; j ++) {
                    if (this.occupied.containsKey(rn - j * this.col)) {
                        allocated.clear();
                        found = false;
                        break;
                    } else {
                        allocated.add(rn - j * this.col);
                    }
                }
            } else {
                found = false;
            }

            if (found) {
                return allocated;
            }
        }

        if (direction == 1) {
            // go straight up
            allocated.add(rn);

            int rest = 0;
            boolean found = true;
            for (int i = 1; i < size; i++) {
                if (this.occupied.containsKey(rn - i * this.col) || rn - i * this.col < 0) {
                    rest = size - allocated.size();
                    break;
                } else {
                    allocated.add(rn - i * this.col);
                }
            }

            if (rest > 0 && rn + rest * this.col <= this.endArea) {
                for (int j = 1; j <= rest; j ++) {
                    if (this.occupied.containsKey(rn + j * this.col)) {
                        allocated.clear();
                        found = false;
                        break;
                    } else {
                        allocated.add(rn + j * this.col);
                    }
                }
            } else {
                found = false;
            }

            if (found) {
                return allocated;
            }
        }

        return null;
    }

    void displayField() {
        System.out.println("----- Battle Field Layout -----");
        // System.out.println(this.fieldArea);
        // start with -1 as index so that we can print out column and row labels
        for(int i = -1; i < row; i++) {
            for(int j = -1; j < col; j++) {
                if (i < 0 && j < 0) {
                   System.out.print("     ");
                } else if (i < 0 && j >= 0) {
                    System.out.print(this.colLabel.get(j) + "  ");
                } else if (i >=0 && j < 0) {
                    System.out.print(String.format("%2d   ", this.rowLabel.get(i)));
                } else {
                    System.out.print(this.fieldArea.get(i * col + j) + "  ");
                }
            }
            System.out.print("\n");
        }
    }

}
