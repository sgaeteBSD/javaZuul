import java.util.ArrayList;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0 (February 2002)
 */

class Game 
{
    private Parser parser;
    private Room currentRoom;
    int coffeeCheck;
    Room boss, cubicles1, conference, break1, reception, bridge, lobby, cubicles2, vending, office, empty, IT, storage, kitchen, break2;
    ArrayList<Item> inventory = new ArrayList<Item>();
    
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }
    
    public static void main(String[] args) {
    	Game mygame = new Game();
    	mygame.play();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        // create the rooms
        boss = new Room("in your boss' office");
        cubicles1 = new Room("in an average sea of cubicles. Exciting");
        conference = new Room("in a conference room. There's a meeting going on, so you should probably leave");
        break1 = new Room("in a break room. You usually use the coffee machine here, but it's broken. There's another one further north");
        reception = new Room("near the receptionist's desk. There's a can of ground espresso on the counter");
        bridge = new Room("on a bridge between rooms. For whatever reason, there's a frother on the ground");
        lobby = new Room("in the lobby. Leaving through the front door is tempting, but there's work to be done");
        cubicles2 = new Room("in just another room of cubicles. Just as exciting as last time");
        vending = new Room("in an elevator room, with some vending machines. There's water bottles in the machine");
        office = new Room("in a random office. The boss here is out, and there's a really nice mug on his desk");
        empty = new Room("in a completely empty room. Still better than the cubicles");
        IT = new Room("in the IT department's office. Maybe someone here can fix that south coffee machine later");
        storage = new Room("in the storage room. Surprisingly, there's nothing useful");
        kitchen = new Room("in the kitchen. There's milk in the fridge");
        break2 = new Room("in the north break room, with a working coffee machine. Use the command \"brew\" to use the machine");
        
        // initialize room exits
        boss.setExit("north", cubicles1);

        cubicles1.setExit("south", boss);
        cubicles1.setExit("west", break1);
        cubicles1.setExit("east", conference);

        break1.setExit("east", cubicles1);
        break1.setExit("north", reception);
        
        conference.setExit("west", cubicles1);
        conference.setExit("north", bridge);
        
        reception.setExit("south", break1);
        reception.setExit("north", lobby);
        
        bridge.setExit("south", conference);
        bridge.setExit("north", cubicles2);
        
        lobby.setExit("south", reception);
        lobby.setExit("east", vending);
        
        cubicles2.setExit("south", bridge);
        cubicles2.setExit("west", vending);
        cubicles2.setExit("east", office);
        
        vending.setExit("west", lobby);
        vending.setExit("north", IT);
        vending.setExit("east", cubicles2);
        
        IT.setExit("south", vending);
        IT.setExit("north", kitchen);
        
        office.setExit("west", cubicles2);
        office.setExit("north", empty);
        
        empty.setExit("south", office);
        empty.setExit("north", storage);
        
        kitchen.setExit("south", IT);
        kitchen.setExit("east", break2);
        
        storage.setExit("south", empty);
        storage.setExit("west", break2);
        
        break2.setExit("west", kitchen);
        break2.setExit("east", storage);
        
        // initialize items
        reception.setItem(new Item("espresso"));
        bridge.setItem(new Item("frother"));
        vending.setItem(new Item("water"));
        office.setItem(new Item("mug"));
        kitchen.setItem(new Item("milk"));

        currentRoom = boss;  // start game at boss
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to Adventure!");
        System.out.println("Adventure is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println("Your boss wants a cup of coffee. Find the five ingredients and a coffee machine, then bring the finished latte back.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            wantToQuit = goRoom(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        else if (commandWord.equals("inventory")) {
        	printInventory();
        }
        else if (commandWord.equals("get")) {
        	getItem(command);
        }
        else if (commandWord.equals("drop")) {
        	dropItem(command);
        }
        else if (commandWord.equals("brew")) {
        	brewCoffee();
        }
        return wantToQuit;
    }

    private void brewCoffee() { //
    	if (currentRoom == break2) { //if there's a coffee machine
    		for (int i = 0; i < 5; i++) { //run through inventory, check if all 5 are present
    			try {
    				if (!inventory.get(i).getDescription().equals(null)) { //if there is an item in slot
    				coffeeCheck += 1; //add one to total for later checking
    				}
    			}
    			catch(Exception ex) { //try catch in case of missing ingredients
    				System.out.println("You're missing ingredients!");
    		    	coffeeCheck = 0;
    		    	break;
    			}
    		}
        	if (coffeeCheck == 5) { //check if all 5 were present
    			for (int i = 0; i < 5; i++) { //remove all 5 now
    				inventory.remove(0); 
    			}
        		inventory.add(new Item("coffee")); //add coffee
        		System.out.println("You've brewed the coffee! Bring it back to your boss.");
    		}
    	}
    	else if (currentRoom == break1) {
    		System.out.println("The coffee machine here is broken. Too bad.");
    	}
    	else {
    		System.out.println("There's no coffee machine here!");
    	}
    }

	private void dropItem(Command command) {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what to drop...
            System.out.println("Drop what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to drop requested item.
        Item newItem = null;
        int index = 0;
        for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).getDescription().equals(item)) {
				newItem = inventory.get(i);
				index = i;
			}
		}
        
        if (newItem == null) {
            System.out.println("That item is not in your inventory!");
        }
        else {
            inventory.remove(index);
            currentRoom.setItem(new Item(item));
            System.out.println("Dropped: " + item);
        }
	}

	private void getItem(Command command) {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what to pick up...
            System.out.println("Get what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to get requested item.
        Item newItem = currentRoom.getItem(item);

        if (newItem == null) {
            System.out.println("That item is not here!");
        }
        else {
            inventory.add(newItem);
            currentRoom.removeItem(item);
            System.out.println("Picked up: " + item);
        }
    }

	private void printInventory() {
    	String output = "";
    	for (int i = 0; i < inventory.size(); i++) {
			output += inventory.get(i).getDescription() + " ";
		}
    	System.out.println("You are carrying:");
    	System.out.println(output);
	}

	// implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You need to find five coffee ingredients and brew a latte for your boss at the northern coffee machine,");
        System.out.println("and bring it back to their office once you're done.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private boolean goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return false;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null)
            System.out.println("There is no door!");
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
            //win condition(s)! step into boss' room with coffee in inventory
            if (currentRoom == boss && inventory.get(0).getDescription().equals("coffee")) { 
            	System.out.println("You win! Your boss is very thankful. Now get back to work!");
            	return true;
            }
        }
        return false;
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game. Return true, if this command
     * quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else
            return true;  // signal that we want to quit
    }
}
