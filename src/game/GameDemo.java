package game;
import java.util.ArrayList;

import gui.MonsterBattleGUI;

/**
 * GameDemo - Complete working example of a monster battle game
 * 
 * This shows you how to use the MonsterBattleGUI to make a game.
 * Look at this code to understand the pattern, then build your own in Game.java!
 * 
 * Pattern:
 * 1. Create GUI
 * 2. Setup game (monsters, items, health)
 * 3. Game loop: get action → do action → check win/loss
 */
public class GameDemo {
    
    // The GUI (use this to display everything)
    private MonsterBattleGUI gui;
    
    // Game state
    private ArrayList<Monster> monsters;
    private ArrayList<Item> inventory;
    private int playerHealth;
    private int maxHealth;
    
    /**
     * Main method - start the game!
     */
    public static void main(String[] args) {
        GameDemo game = new GameDemo();
        game.play();
    }
    
    /**
     * Play the game!
     */
    public void play() {
        setupGame();
        gameLoop();
    }
    
    /**
     * Setup - create the GUI and game state
     */
    private void setupGame() {
        // Create the GUI
        gui = new MonsterBattleGUI("Monster Battle - DEMO");
        
        // Setup player health
        maxHealth = 100;
        playerHealth = 100;
        gui.setPlayerMaxHealth(maxHealth);
        gui.updatePlayerHealth(playerHealth);
        
        // Create monsters
        monsters = new ArrayList<>();
        monsters.add(new Monster());
        monsters.add(new Monster());
        monsters.add(new Monster());
        gui.updateMonsters(monsters);
        
        // Create items
        inventory = new ArrayList<>();
        addHealthPotion(30);
        addHealthPotion(30);
        addBomb(20);
        gui.updateInventory(inventory);
        
        // Setup buttons
        String[] buttons = {"Attack", "Defend", "Heal", "Use Item"};
        gui.setActionButtons(buttons);
        
        // Welcome message
        gui.displayMessage("Battle Start! Choose your action.");
    }
    
    /**
     * Main game loop
     */
    private void gameLoop() {
        // Keep playing while monsters alive and player alive
        while (countLivingMonsters() > 0 && playerHealth > 0) {
            
            // PLAYER'S TURN
            gui.displayMessage("Your turn! HP: " + playerHealth);
            int action = gui.waitForAction();  // Wait for button click
            handlePlayerAction(action);
            gui.updateMonsters(monsters);
            gui.pause(500);
            
            // MONSTER'S TURN (if any alive)
            if (countLivingMonsters() > 0 && playerHealth > 0) {
                monsterAttack();
                gui.updateMonsters(monsters);
                gui.pause(500);
            }
        }
        
        // Game over!
        if (playerHealth <= 0) {
            gui.displayMessage("💀 DEFEAT! You have been defeated...");
        } else {
            gui.displayMessage("🎉 VICTORY! You defeated all monsters!");
        }
    }
    
    /**
     * Handle player's action choice
     */
    private void handlePlayerAction(int action) {
        switch (action) {
            case 0: attackMonster(); break;
            case 1: defend(); break;
            case 2: heal(); break;
            case 3: useItem(); break;
        }
    }
    
    /**
     * Attack a random monster
     */
    private void attackMonster() {
        Monster target = getRandomLivingMonster();
        if (target != null) {
            int damage = (int)(Math.random() * 30) + 10;
            target.takeDamage(damage);
            gui.displayMessage("💥 You hit for " + damage + " damage!");
            
            // Show which one we hit
            int index = monsters.indexOf(target);
            gui.highlightMonster(index);
            gui.pause(300);
            gui.highlightMonster(-1);
        }
    }
    
    /**
     * Defend (reduce next attack)
     */
    private void defend() {
        gui.displayMessage("🛡️ You brace for impact! Defense up!");
    }
    
    /**
     * Heal yourself
     */
    private void heal() {
        int healAmount = (int)(Math.random() * 20) + 10;
        playerHealth = Math.min(maxHealth, playerHealth + healAmount);
        gui.updatePlayerHealth(playerHealth);
        gui.displayMessage("💚 You healed for " + healAmount + " HP!");
    }
    
    /**
     * Use an item from inventory
     */
    private void useItem() {
        if (inventory.isEmpty()) {
            gui.displayMessage("No items in inventory!");
            return;
        }
        
        // Use first item
        Item item = inventory.remove(0);
        gui.updateInventory(inventory);
        item.use();  // The item knows what to do!
    }
    
    /**
     * Monster attacks player
     */
    private void monsterAttack() {
        Monster attacker = getRandomLivingMonster();
        if (attacker != null) {
            int damage = (int)(Math.random() * attacker.damage());
            playerHealth -= damage;
            gui.updatePlayerHealth(playerHealth);
            gui.displayMessage("👹 Monster attacks! You take " + damage + " damage!");
        }
    }
    
    /**
     * Count how many monsters are still alive
     */
    private int countLivingMonsters() {
        int count = 0;
        for (Monster m : monsters) {
            if (m.health() > 0) count++;
        }
        return count;
    }
    
    /**
     * Get a random living monster
     */
    private Monster getRandomLivingMonster() {
        ArrayList<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.health() > 0) alive.add(m);
        }
        if (alive.isEmpty()) return null;
        return alive.get((int)(Math.random() * alive.size()));
    }
    
    // ==================== ITEM CREATION HELPERS ====================
    
    /**
     * Add a health potion to inventory
     */
    private void addHealthPotion(int healAmount) {
        inventory.add(new Item("Health Potion", "🧪", () -> {
            playerHealth = Math.min(maxHealth, playerHealth + healAmount);
            gui.updatePlayerHealth(playerHealth);
            gui.displayMessage("💚 Used Health Potion! Healed " + healAmount + " HP!");
        }));
    }
    
    /**
     * Add a bomb to inventory (damages all monsters)
     */
    private void addBomb(int damage) {
        inventory.add(new Item("Bomb", "💣", () -> {
            for (Monster m : monsters) {
                if (m.health() > 0) {
                    m.takeDamage(damage);
                }
            }
            gui.displayMessage("💣 BOOM! All monsters take " + damage + " damage!");
            gui.updateMonsters(monsters);
        }));
    }
}