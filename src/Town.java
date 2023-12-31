/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean isSearched;
    private boolean mode;
    private boolean dugged;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, boolean mode) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        this.mode = mode;

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        isSearched = false;
        dugged = false;
    }

    public String getLatestNews() {return printMessage;}

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt be pretty rough around here, so watch yerself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "Ye used your " + item + " to cross the " + terrain.getTerrainName() + Colors.CYAN + "." + Colors.RESET;
            if (mode == false) {
                if (checkItemBreak()) {
                    hunter.removeItemFromKit(item);
                    printMessage += "\nUnfortunately, ye done lost your " + item + ".";
                }
            }

            return true;
        }

        printMessage = "Ye can't leave town, " + hunter.getHunterName() + ". Ye not be having a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        int goldDiff = (int) (Math.random() * 10) + 1;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else if (mode) {
            noTroubleChance = 0.165;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "Ye couldn't find any trouble";
        }
        else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
            if (hunter.hasItemInKit("cannon")){
                hunter.changeGold(goldDiff);
                printMessage += ("It be you, the legendary Pirate. Here, take all me gold.");
            }else if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                printMessage += "\nYe won the brawl and'll receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYe lost the brawl and'll pay " + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public void huntForTreasure(){
        int findTreasure = (int) (Math.random() * 8) + 1;
        if(isSearched){
            System.out.println("Ye already searched fer' treasure here, find a new town!");
        }else{
            setSearchTrue();
            if(findTreasure == 1 && !hunter.hasTreasure("Crown")){
                System.out.println("Ye' found a golden Crown! Fitting only for a king");
               hunter.addTreasure("Crown");
            } else if (findTreasure == 2 && !hunter.hasTreasure("Trophy")) {
                System.out.println("Ye' found a trophy! I wonder what competition it be for?");
                hunter.addTreasure("Trophy");
            } else if (findTreasure == 3 && !hunter.hasTreasure("Gem")) {
                System.out.println("Ye' found a gem! Rick Harrison would probably only give ye' 3 dabloons fer' it.");
                hunter.addTreasure("Gem");
            }else{
                System.out.println("Ye' found dust, unfortunate.");
            }
        }
    }

    public void digForTreasure() {
        int digGold = (int) (Math.random() * 2) + 1;
        if (dugged) {
            System.out.println("Ye already dug fer' gold here, find a new town!");
        } else if (!hunter.hasItemInKit("shovel")) {
            System.out.println("Ye' cannot dig without a shovel fool!");
        } else {
            if (digGold == 1) {
                int goldDug = (int) (Math.random()*20) + 1;
                System.out.println("Ye' dug up " + goldDug + " gold! Har Har Har!");
                hunter.changeGold(goldDug);
                dugged = true;
            } else if (digGold == 2) {
                System.out.println("Ye' dug but only found dirt");
                dugged = true;
            }
        }
    }
    public String toString() {
        return "This be a nice little town surrounded by " + Colors.CYAN + terrain.getTerrainName() + "." + Colors.RESET;
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = (int) (Math.random() * 6) + 1;
        if (rnd == 1) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 2) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 3) {
            return new Terrain("Plains", "Horse");
        } else if (rnd == 4) {
            return new Terrain("Desert", "Water");
        }else if (rnd == 5){
            return new Terrain("Marsh","Boots");
        } else {
            return new Terrain("Jungle", "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }

    public void resetNews() {
        printMessage = "Ye' left the shop";
    }
    private void setSearchTrue(){
        isSearched = true;
    }
}