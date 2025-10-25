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

// 4. Wait for player input
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

#### `updateInventory(ArrayList<String> items)`
Updates the inventory display with current items. Each item is displayed separately with its index, even if you have multiple items with the same name (items don't stack).
```java
ArrayList<String> items = new ArrayList<>();
items.add("Health Potion");  // Will show as [0] Health Potion
items.add("Health Potion");  // Will show as [1] Health Potion
items.add("Bomb");           // Will show as [2] Bomb
gui.updateInventory(items);
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
    while (hasLivingMonsters() && playerAlive) {
        // Player's turn
        gui.displayMessage("Your turn! Choose an action.");
        int action = gui.waitForAction();
        handlePlayerAction(action);
        gui.updateMonsters(monsters);
        
        // Monster's turn
        if (hasLivingMonsters()) {
            monsterAttack();
            gui.updateMonsters(monsters);
        }
        
        // Check win/loss conditions
        if (!hasLivingMonsters()) {
            gui.displayMessage("Victory!");
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
2. **Use displayMessage()** to give feedback to the player
3. **Run your game loop in a thread** so the GUI doesn't freeze
4. **Add small delays** (Thread.sleep) between actions for readability
5. **The GUI handles clicks for you** - just call waitForAction()

## Monster Class Requirements

Your Monster class needs these public methods:
- `int health()` - Returns current health
- `double damage()` - Returns damage stat
- `int speed()` - Returns speed stat  
- `String special()` - Returns special ability (or empty string)
- `void takeDamage(int dmg)` - Reduces health

See the provided Monster class for the correct implementation.

## See Also
- `BattleGameDemo.java` - Complete working example
- `Monster.java` - Required Monster class implementation