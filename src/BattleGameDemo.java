import java.util.ArrayList;

/**
 * BattleGameDemo - Example of how to use the MonsterBattleGUI API
 * 
 * This demonstrates the student-facing API for the battle system.
 * Students write their game logic here and interact with the GUI
 * through simple method calls.
 */
public class BattleGameDemo {
    
    // The GUI instance (students interact with this)
    private static MonsterBattleGUI gui;
    
    // Game state
    private static ArrayList<Monster> monsters;
    private static ArrayList<Item> inventory;  // Changed from String to Item
    private static int playerHealth = 100;
    
    public static void main(String[] args) {
        // Initialize the GUI
        gui = new MonsterBattleGUI("Monster Battle Demo");
        gui.display();
        
        // Setup the game
        setupGame();
        
        // Run game loop in a separate thread so GUI stays responsive
        new Thread(() -> gameLoop()).start();
    }
    
    /**
     * Setup initial game state
     */
    private static void setupGame() {
        // Create some monsters
        monsters = new ArrayList<>();
        monsters.add(new Monster());
        monsters.add(new Monster());
        monsters.add(new Monster());
        
        // Create inventory with Item objects
        // Each item has a name, icon, and FUNCTION that runs when used
        inventory = new ArrayList<>();
        
        // Health Potion - heals 30 HP
        inventory.add(new Item("Health Potion", "🧪", () -> {
            int healAmount = 30;
            playerHealth = Math.min(100, playerHealth + healAmount);
            gui.displayMessage("💚 Used Health Potion! Healed " + healAmount + " HP!");
        }));
        
        // Another Health Potion (same function)
        inventory.add(new Item("Health Potion", "🧪", () -> {
            int healAmount = 30;
            playerHealth = Math.min(100, playerHealth + healAmount);
            gui.displayMessage("💚 Used Health Potion! Healed " + healAmount + " HP!");
        }));
        
        // Mega Potion - heals 50 HP
        inventory.add(new Item("Mega Potion", "🧪", () -> {
            int healAmount = 50;
            playerHealth = Math.min(100, playerHealth + healAmount);
            gui.displayMessage("💚 Used Mega Potion! Healed " + healAmount + " HP!");
        }));
        
        // Bomb - damages all monsters
        inventory.add(new Item("Bomb", "💣", () -> {
            int damage = 20;
            for (Monster m : monsters) {
                if (m.health() > 0) {
                    m.takeDamage(damage);
                }
            }
            gui.displayMessage("💣 Used Bomb! All monsters take " + damage + " damage!");
            gui.updateMonsters(monsters);
        }));
        
        // Magic Scroll - random effect
        inventory.add(new Item("Magic Scroll", "✨", () -> {
            double rand = Math.random();
            if (rand < 0.5) {
                // Heal player
                playerHealth = Math.min(100, playerHealth + 25);
                gui.displayMessage("✨ Magic healed you for 25 HP!");
            } else {
                // Damage random monster
                Monster target = getNextMonster();
                if (target != null) {
                    target.takeDamage(30);
                    gui.displayMessage("✨ Magic struck a monster for 30 damage!");
                    gui.updateMonsters(monsters);
                }
            }
        }));
        
        // Update the GUI
        gui.updateMonsters(monsters);
        gui.updateInventory(inventory);
        
        // Set custom button labels
        String[] buttonLabels = {"Attack", "Defend", "Heal", "Use Item"};
        gui.setActionButtons(buttonLabels);
    }
    
    /**
     * Main game loop
     */
    private static void gameLoop() {
        gui.displayMessage("Battle Start! Choose your action.");
        
        // Game runs while there are alive monsters
        while (monsterCount() > 0 && playerHealth > 0) {
            // Get player action
            gui.displayMessage("Your turn! Health: " + playerHealth);
            int action = gui.waitForAction();
            
            // Process the action
            handlePlayerAction(action);
            
            // Small delay for readability
            sleep(500);
            
            // Monster's turn
            if (monsterCount() > 0) {
                monsterTurn();
            }
            
            // Update display
            gui.updateMonsters(monsters);
            
            // Check win/loss
            if (monsterCount() == 0) {
                gui.displayMessage("🎉 VICTORY! You defeated all monsters!");
                break;
            }
            if (playerHealth <= 0) {
                gui.displayMessage("💀 DEFEAT! You have been defeated...");
                break;
            }
        }
    }
    
    /**
     * Handle the player's chosen action
     */
    private static void handlePlayerAction(int action) {
        switch (action) {
            case 0: // Attack
                attackMonster();
                break;
            case 1: // Defend
                gui.displayMessage("You brace for impact! Shield up!");
                break;
            case 2: // Heal
                healPlayer();
                break;
            case 3: // Use Item
                useItem();
                break;
        }
    }
    
    /**
     * Attack a random living monster
     */
    private static void attackMonster() {
        Monster target = getNextMonster();
        if (target != null) {
            int damage = (int)(Math.random() * 30) + 10;
            target.takeDamage(damage);
            gui.displayMessage("💥 You hit the monster for " + damage + " damage!");
            
            // Highlight the target briefly
            int index = monsters.indexOf(target);
            gui.highlightMonster(index);
            sleep(300);
            gui.highlightMonster(-1);
        }
    }
    
    /**
     * Heal the player
     */
    private static void healPlayer() {
        int healAmount = (int)(Math.random() * 20) + 10;
        playerHealth = Math.min(100, playerHealth + healAmount);
        gui.displayMessage("💚 You healed for " + healAmount + " HP! Health: " + playerHealth);
    }
    
    /**
     * Use an item from inventory
     */
    private static void useItem() {
        if (inventory.isEmpty()) {
            gui.displayMessage("No items in inventory!");
            return;
        }
        
        // Remove and use the first item
        Item item = inventory.remove(0);
        gui.updateInventory(inventory);
        
        // Call the item's use() function - THIS IS THE KEY!
        // The function was defined when the item was created,
        // but it doesn't run until we call .use() here
        item.use();
    }
    
    /**
     * Monster's turn to attack
     */
    private static void monsterTurn() {
        Monster attacker = getNextMonster();
        if (attacker != null) {
            int damage = (int)(Math.random() * attacker.damage());
            playerHealth -= damage;
            gui.displayMessage("👹 Monster attacks! You take " + damage + " damage. Health: " + playerHealth);
        }
    }
    
    /**
     * Get the next living monster
     */
    private static Monster getNextMonster() {
        for (Monster m : monsters) {
            if (m.health() > 0) {
                return m;
            }
        }
        return null;
    }
    
    /**
     * Count living monsters
     */
    private static int monsterCount() {
        int count = 0;
        for (Monster m : monsters) {
            if (m.health() > 0) count++;
        }
        return count;
    }
    
    /**
     * Helper method for delays
     */
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}