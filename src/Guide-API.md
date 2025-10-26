# Monster Battle GUI - Student API Guide

## Overview
The MonsterBattleGUI provides a visual interface for your monster battle game. You don't need to understand how the GUI works internally - just use these simple methods to display your game!

## Quick Start

```java
// 1. Create the GUI
MonsterBattleGUI gui = new MonsterBattleGUI("My Battle Game");
gui.display();

// 2. Setup your monsters
ArrayList<Monster> monsters = new ArrayList<>();
monsters.add(new Monster());
monsters.add(new Monster());

// 3. Display them
gui.updateMonsters(monsters);

// 4. Setup player health
gui.setPlayerMaxHealth(100);
gui.updatePlayerHealth(100);

// 5. Wait for player input
int choice = gui.waitForAction(); // Returns 0-3 based on button clicked
```

## Main API Methods

### Display Methods

#### `updateMonsters(ArrayList<Monster> monsters)`
Updates the monster display to show the current state of all monsters.
```java
gui.updateMonsters(monsters); // Refreshes the display
```

#### `displayMessage(String message)`
Shows a message to the player in the message area. The previous message is kept visible above the current one in a dimmed color.
```java
gui.displayMessage("You defeated the monster!");
gui.displayMessage("Player health: " + health);
// Both messages will be visible - "You defeated..." above (dimmed) and "Player health..." below (bright)
```

#### `updateInventory(ArrayList<Item> items)`
Updates the inventory display with current items. Each item is displayed separately with its index, even if you have multiple items with the same name (items don't stack).
```java
ArrayList<Item> items = new ArrayList<>();
items.add(new Item("Health Potion", "🧪", () -> { /* healing code */ }));
items.add(new Item("Health Potion", "🧪", () -> { /* healing code */ }));
items.add(new Item("Bomb", "💣", () -> { /* bomb code */ }));
gui.updateInventory(items);
```

#### `updatePlayerHealth(int health)` ⭐ NEW!
Updates the player's health bar display. Call this whenever the player's health changes!
```java
playerHealth -= damage;
gui.updatePlayerHealth(playerHealth);  // Updates the health bar
```

#### `setPlayerMaxHealth(int maxHealth)` ⭐ NEW!
Sets the maximum health for the player. Usually called once during setup.
```java
gui.setPlayerMaxHealth(100);  // Player starts with max 100 HP
```

### Button Methods

#### `setActionButtons(String[] labels)`
Customize the four action button labels.
```java
String[] labels = {"Attack", "Defend", "Heal", "Run"};
gui.setActionButtons(labels);
```

#### `waitForAction()`
**IMPORTANT**: This method blocks until the player clicks a button!
Returns the button index (0-3) that was clicked.

```java
int action = gui.waitForAction();
// Now 'action' contains 0, 1, 2, or 3
switch(action) {
    case 0: // Attack was clicked
        attackMonster();
        break;
    case 1: // Defend was clicked
        defend();
        break;
    // etc...
}
```

### Advanced Methods

#### `highlightMonster(int index)`
Highlights a specific monster (useful for targeting).
```java
gui.highlightMonster(2); // Highlight monster at index 2
// ... show effect ...
gui.highlightMonster(-1); // Clear highlight
```

#### `setButtonsEnabled(boolean enabled)`
Enable or disable all buttons.
```java
gui.setButtonsEnabled(false); // Disable during monster turn
gui.setButtonsEnabled(true);  // Enable for player turn
```

## Example Game Loop Pattern

```java
public static void gameLoop() {
    while (hasLivingMonsters() && playerHealth > 0) {
        // Player's turn
        gui.displayMessage("Your turn! Choose an action.");
        int action = gui.waitForAction();
        handlePlayerAction(action);
        gui.updateMonsters(monsters);
        
        // Monster's turn
        if (hasLivingMonsters()) {
            monsterAttack();
            gui.updatePlayerHealth(playerHealth);  // Update health bar!
            gui.updateMonsters(monsters);
        }
        
        // Check win/loss conditions
        if (!hasLivingMonsters()) {
            gui.displayMessage("Victory!");
        }
        if (playerHealth <= 0) {
            gui.displayMessage("Defeat!");
        }
    }
}
```

## Threading Note

The GUI updates should happen on the Event Dispatch Thread, but the API handles this for you! Just call the methods normally.

Your **game loop should run in a separate thread** so the GUI stays responsive:

```java
public static void main(String[] args) {
    gui = new MonsterBattleGUI("Battle!");
    gui.display();
    
    // Run game loop in background
    new Thread(() -> gameLoop()).start();
}
```

## Common Patterns

### Character Selection
Use the same buttons for selection:
```java
String[] selectLabels = {"Fighter", "Tank", "Healer", "Ninja"};
gui.setActionButtons(selectLabels);
gui.displayMessage("Choose your class!");
int classChoice = gui.waitForAction();

// Set max health based on class
if (classChoice == 1) { // Tank
    gui.setPlayerMaxHealth(150);
    playerHealth = 150;
} else {
    gui.setPlayerMaxHealth(100);
    playerHealth = 100;
}
gui.updatePlayerHealth(playerHealth);
```

### Taking Damage
Always update the health bar when damage is taken!
```java
public static void takeDamage(int damage) {
    playerHealth -= damage;
    gui.updatePlayerHealth(playerHealth);  // Don't forget this!
    gui.displayMessage("Took " + damage + " damage! HP: " + playerHealth);
}
```

### Healing
Update the health bar when healing too!
```java
public static void healPlayer(int amount) {
    playerHealth = Math.min(maxHealth, playerHealth + amount);
    gui.updatePlayerHealth(playerHealth);  // Don't forget this!
    gui.displayMessage("Healed " + amount + " HP! HP: " + playerHealth);
}
```

### Multi-Target Attack
```java
// Highlight each monster as you damage it
for (int i = 0; i < monsters.size(); i++) {
    if (monsters.get(i).health() > 0) {
        gui.highlightMonster(i);
        monsters.get(i).takeDamage(damage);
        gui.updateMonsters(monsters);
        Thread.sleep(300); // Brief pause
    }
}
gui.highlightMonster(-1); // Clear highlight
```

## Tips for Success

1. **Always update the display** after changing monster health or inventory
2. **Always update player health** after the player takes damage or heals
3. **Use displayMessage()** to give feedback to the player
4. **Run your game loop in a thread** so the GUI doesn't freeze
5. **Add small delays** (Thread.sleep) between actions for readability
6. **The GUI handles clicks for you** - just call waitForAction()

## Monster Class Requirements

Your Monster class needs these public methods:
- `int health()` - Returns current health
- `double damage()` - Returns damage stat
- `int speed()` - Returns speed stat  
- `String special()` - Returns special ability (or empty string)
- `void takeDamage(int dmg)` - Reduces health

See the provided Monster class for the correct implementation.

## Item Class Requirements

Your Item class needs:
- Constructor: `Item(String name, String icon, Runnable onUse)`
- Methods: `use()`, `getName()`, `getIcon()`

See the provided Item class and Guide-Item.md for details on creating items with effects!

## Visual Features

The player health bar:
- **Green** when health > 50%
- **Orange** when health is 25-50%
- **Red** when health < 25%
- Shows percentage and exact HP values
- Automatically updates with smooth colors

## See Also
- `BattleGameDemo.java` - Complete working example with player health bar
- `Monster.java` - Required Monster class implementation
- `Item.java` - Item class for consumables
- `Guide-Item.md` - Guide to creating items with effects