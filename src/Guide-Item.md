# Understanding Items and Callbacks

## What is an Item?

An `Item` is a consumable in your game. Each item has three parts:
1. **Name** - What it's called ("Health Potion")
2. **Icon** - The emoji to display ("🧪")
3. **Effect** - What happens when you use it (code you write!)

## The Magic of Callbacks

Here's the cool part: **You write the code when you CREATE the item, but the code doesn't RUN until you USE the item!**

### Example: Health Potion

```java
// Creating the item - the code inside () -> { } is written but NOT run yet!
Item potion = new Item("Health Potion", "🧪", () -> {
    playerHealth += 30;
    gui.displayMessage("Healed 30 HP!");
});

// The code above just CREATED the item. Nothing healed yet!

// Later in the game... player chooses to use item
potion.use();  // NOW the code runs! Player gets healed!
```

## Why is this useful?

1. **Separation of Concerns**: Each item knows what it does
2. **Cleaner Code**: No big if/else checking item names
3. **Easy to Add Items**: Just create a new Item with its behavior
4. **Real Programming Concept**: Callbacks are used everywhere in real software!

## Creating Different Items

### Basic Healing Item
```java
Item healthPotion = new Item("Health Potion", "🧪", () -> {
    playerHealth += 30;
    gui.displayMessage("Healed 30 HP!");
});
```

### Attack All Enemies
```java
Item bomb = new Item("Bomb", "💣", () -> {
    for (Monster m : monsters) {
        if (m.health() > 0) {
            m.takeDamage(25);
        }
    }
    gui.displayMessage("BOOM! 25 damage to all monsters!");
    gui.updateMonsters(monsters);  // Update the display
});
```

### Random Effect
```java
Item mysteryBox = new Item("Mystery Box", "🎁", () -> {
    double rand = Math.random();
    if (rand < 0.5) {
        playerHealth += 20;
        gui.displayMessage("Found a health boost! +20 HP");
    } else {
        Monster target = getNextMonster();
        target.takeDamage(40);
        gui.displayMessage("Lightning struck a monster! 40 damage!");
    }
});
```

### Buff Item
```java
Item strengthPotion = new Item("Strength Potion", "💪", () -> {
    playerDamage += 10;  // Increase player's damage
    gui.displayMessage("Feeling strong! +10 attack power!");
});
```

## The Pattern: Write Now, Run Later

```java
// STEP 1: Write the behavior (doesn't run yet!)
Item item = new Item(name, icon, () -> {
    // Your code here
    // This code is "saved" but not executed
});

// STEP 2: Add to inventory
inventory.add(item);

// ... time passes, player plays the game ...

// STEP 3: Player uses item (NOW the code runs!)
Item usedItem = inventory.remove(0);
usedItem.use();  // <-- Code from Step 1 executes here!
```

## What's That () -> { } Syntax?

This is called a **lambda expression** (or anonymous function). It's a way to write a function without naming it.

Think of it like this:
```java
// Normal method:
public void healPlayer() {
    playerHealth += 30;
}

// Lambda version (no name, stored in the Item):
() -> {
    playerHealth += 30;
}
```

The `()` means "no parameters needed"
The `->` means "when you run this, do the following:"
The `{ }` contains the code to run

## Common Mistakes

### ❌ Wrong: Trying to access variables that don't exist
```java
Item potion = new Item("Potion", "🧪", () -> {
    int heal = 30;  // This is okay
    playerHealth += heal;  // playerHealth must exist!
});
```

### ✅ Right: Use variables from your game
```java
// Make sure playerHealth exists in your main game code!
private static int playerHealth = 100;

Item potion = new Item("Potion", "🧪", () -> {
    playerHealth += 30;  // Works because playerHealth exists
});
```

### ❌ Wrong: Forgetting to update the GUI
```java
Item bomb = new Item("Bomb", "💣", () -> {
    for (Monster m : monsters) {
        m.takeDamage(20);
    }
    // Oops! Forgot to update the monster display
});
```

### ✅ Right: Always update displays after changes
```java
Item bomb = new Item("Bomb", "💣", () -> {
    for (Monster m : monsters) {
        m.takeDamage(20);
    }
    gui.updateMonsters(monsters);  // Don't forget this!
});
```

## Practice Ideas

Try creating these items:

1. **Revive Potion** - Fully restores health to 100
2. **Poison Bomb** - Damages monsters over multiple turns
3. **Shield Scroll** - Gives temporary defense boost
4. **Lucky Coin** - 50% chance of double effect
5. **Monster Bait** - Skips the monster's next turn
6. **Time Warp** - Take two actions this turn

## Key Takeaway

Items let you **package code with data**. When you create an item, you're saying:
> "Here's what this item is called, here's what it looks like, and here's what to do when someone uses it."

The computer remembers all three parts and executes the behavior when `item.use()` is called!

This is a powerful programming pattern used in game development, web applications, and many other areas of software engineering.