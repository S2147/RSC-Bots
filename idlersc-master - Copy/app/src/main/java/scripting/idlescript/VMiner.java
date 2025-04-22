package scripting.idlescript;

import bot.Main;
import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A basic tin and copper mining script with banking for East Varrock.
 *
 * @author S2147
 */
public class VMiner extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.MINING, Category.IRONMAN_SUPPORTED},
          "S2147",
          "A basic tin and copper mining script with banking for East Varrock.");

  JFrame scriptFrame = null;
  boolean guiSetup = false;
  boolean scriptStarted = false;

  MiningObject target = null;
  int fightMode = 0;
  int eatingHealth = 0;

  final int[] oreIds = {150, 202, 151, 152, 153, 154, 155, 149, 157, 158, 159, 160, 383};

  final long startTimestamp = (System.currentTimeMillis() / 1000L);
  int oresMined = 0;
  int oresInBank = 0;

  static class MiningObject {
    final String name;
    final int rockId;

    public MiningObject(String _name, int _rockId) {
      name = _name;
      rockId = _rockId;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof MiningObject) {
        return ((MiningObject) o).name.equals(this.name);
      }

      return false;
    }
  }

  final ArrayList<MiningObject> objects =
      new ArrayList<MiningObject>() {
        {
          add(new MiningObject("Copper", 100));
          add(new MiningObject("Tin", 104));
          add(new MiningObject("Iron", 102));
          add(new MiningObject("Silver", 195));
          add(new MiningObject("Coal", 110));
          add(new MiningObject("Gold", 112));
          add(new MiningObject("Mithril", 106));
          add(new MiningObject("Adamantite", 108));
        }
      };
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
      controller.setStatus("@red@Waiting for start..");
    }

    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      scriptStart();
    }

    return 1000; // start() must return a int value now.
  }

  public void scriptStart() {
    while (controller.isRunning()) {
      if (controller.getInventoryItemCount() == 30) {
        walkToBank();
        bank();
        walkToMine();
      } else {

        while (controller.isBatching() && controller.getInventoryItemCount() < 30)
          controller.sleep(10);

        if (controller.getShouldSleep()) controller.sleepHandler(true);
        int[] objCoord = controller.getNearestObjectById(target.rockId);
        if (objCoord != null) {
          controller.setStatus("@red@Mining!");
          controller.atObject(objCoord[0], objCoord[1]);
        } else {
          controller.setStatus("@red@Waiting for spawn...");
        }

        controller.sleep(618);
      }
    }
  }

  public void openDoor() {
    controller.setStatus("@red@Opening bank door..");
    while (controller.getObjectAtCoord(102, 509) == 64) {
      controller.atObject(102, 509);
      controller.sleep(100);
    }
  }

  public void walkToBank() {
    controller.setStatus("@red@Walking to bank..");
    controller.walkTo(73, 548);
    controller.walkTo(75, 533);
    controller.walkTo(82, 518);
    controller.walkTo(91, 509);
    controller.walkTo(102, 509);

    openDoor();
  }

  public void walkToMine() {
    controller.setStatus("@red@Walking to mine..");

    openDoor();

    controller.walkTo(102, 509);
    controller.walkTo(91, 509);
    controller.walkTo(82, 518);
    controller.walkTo(75, 533);
    controller.walkTo(73, 548);
  }

  public void bank() {

    controller.setStatus("@red@Banking...");

    controller.openBank();

    for (int ore : this.oreIds) {
      if (controller.getInventoryItemCount(ore) > 0) {
        controller.depositItem(ore, controller.getInventoryItemCount(ore));
        controller.sleep(1000);
        this.oresInBank = controller.getBankItemCount(ore);
      }
    }
  }

  public void setupGUI() {
    JLabel headerLabel =
        new JLabel("Start in East Varrock mine with your pickaxe and sleeping bag!");
    JComboBox<String> targetField = new JComboBox<>();
    JButton startScriptButton = new JButton("Start");

    for (MiningObject obj : objects) {
      targetField.addItem(obj.name);
    }

    startScriptButton.addActionListener(
        e -> {
          target = objects.get(targetField.getSelectedIndex());
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;

          controller.displayMessage("@red@VMiner by S2147. Let's party like it's 2001!");
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(headerLabel);
    scriptFrame.add(targetField);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You manage to")) oresMined++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {

      int minedPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        minedPerHr = (int) (oresMined * scale);
      } catch (Exception e) {
        // divide by zero
      }

      controller.drawBoxAlpha(7, 7, 150, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@VMiner @whi@by @red@S2147", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Ores mined: @whi@"
              + String.format("%,d", this.oresMined)
              + " @red@(@whi@"
              + String.format("%,d", minedPerHr)
              + "@red@/@whi@hr@red@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      controller.drawString(
          "@red@Ores in bank: @whi@" + String.format("%,d", this.oresInBank),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
}
