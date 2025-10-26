import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * MonsterBattleGUI - A student-friendly API for displaying monster battles
 * 
 * This class provides a graphical interface for the Monster Battle game.
 * Students interact with this API to display monsters, handle player actions,
 * and show inventory items.
 * 
 * STUDENT API METHODS:
 * - updateMonsters(ArrayList<Monster>) - Refresh the monster display
 * - setActionButtons(String[]) - Set custom button labels (4 buttons)
 * - waitForAction() - Returns 0-3 based on which button is clicked
 * - updateInventory(ArrayList<String>) - Update inventory display
 * - displayMessage(String) - Show a message to the player
 * - highlightMonster(int) - Highlight a specific monster
 */
public class MonsterBattleGUI extends JFrame {
    
    // GUI Components
    private MonsterDisplayPanel monsterPanel;
    private ActionButtonPanel buttonPanel;
    private InventoryPanel inventoryPanel;
    private MessagePanel messagePanel;
    
    // State tracking
    private int selectedAction = -1;
    private boolean waitingForInput = false;
    private final Object actionLock = new Object();
    
    // Layout constants
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    
    /**
     * Constructor - Creates the GUI with default settings
     */
    public MonsterBattleGUI() {
        this("Monster Battle Arena");
    }
    
    /**
     * Constructor with custom title
     * @param title The window title
     */
    public MonsterBattleGUI(String title) {
        super(title);
        initializeComponents();
        layoutComponents();
        finalizeWindow();
    }
    
    /**
     * Initialize all GUI components
     */
    private void initializeComponents() {
        monsterPanel = new MonsterDisplayPanel();
        buttonPanel = new ActionButtonPanel(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleButtonClick(e);
            }
        });
        inventoryPanel = new InventoryPanel();
        messagePanel = new MessagePanel();
    }
    
    /**
     * Layout all components in the window
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Top: Monster Display (largest area)
        add(monsterPanel, BorderLayout.CENTER);
        
        // Bottom: Buttons and Messages
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(messagePanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Right: Inventory
        add(inventoryPanel, BorderLayout.EAST);
        
        // Add padding around everything
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    /**
     * Finalize window settings
     */
    private void finalizeWindow() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
    }
    
    // ==================== STUDENT API METHODS ====================
    
    /**
     * Update the monster display
     * @param monsters ArrayList of monsters to display
     */
    public void updateMonsters(ArrayList<Monster> monsters) {
        SwingUtilities.invokeLater(() -> {
            monsterPanel.setMonsters(monsters);
        });
    }
    
    /**
     * Set custom labels for the action buttons
     * @param labels Array of 4 button labels
     */
    public void setActionButtons(String[] labels) {
        if (labels.length != 4) {
            throw new IllegalArgumentException("Must provide exactly 4 button labels");
        }
        SwingUtilities.invokeLater(() -> {
            buttonPanel.setButtonLabels(labels);
        });
    }
    
    /**
     * Wait for the player to click an action button
     * This method blocks until a button is clicked
     * @return The button index (0-3) that was clicked
     */
    public int waitForAction() {
        selectedAction = -1;
        waitingForInput = true;
        buttonPanel.setEnabled(true);
        
        // Block until a button is clicked
        synchronized(actionLock) {
            while (selectedAction == -1) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return -1;
                }
            }
        }
        
        waitingForInput = false;
        return selectedAction;
    }
    
    /**
     * Update the inventory display
     * @param items ArrayList of item names to display
     */
    public void updateInventory(ArrayList<Item> items) {
        SwingUtilities.invokeLater(() -> {
            inventoryPanel.setItems(items);
        });
    }
    
    /**
     * Display a message to the player
     * @param message The message to display
     */
    public void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messagePanel.setMessage(message);
        });
    }
    
    /**
     * Highlight a specific monster (for targeting)
     * @param index The index of the monster to highlight (-1 for none)
     */
    public void highlightMonster(int index) {
        SwingUtilities.invokeLater(() -> {
            monsterPanel.highlightMonster(index);
        });
    }
    
    /**
     * Enable or disable the action buttons
     * @param enabled true to enable, false to disable
     */
    public void setButtonsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            buttonPanel.setEnabled(enabled);
        });
    }
    
    // ==================== INTERNAL METHODS ====================
    
    /**
     * Handle button click events
     */
    private void handleButtonClick(ActionEvent e) {
        if (!waitingForInput) return;
        
        String command = e.getActionCommand();
        selectedAction = Integer.parseInt(command);
        
        synchronized(actionLock) {
            actionLock.notifyAll();
        }
        
        buttonPanel.setEnabled(false);
    }
    
    /**
     * Display the GUI (make it visible)
     */
    public void display() {
        setVisible(true);
    }
}