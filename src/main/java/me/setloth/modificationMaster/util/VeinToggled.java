package me.setloth.modificationMaster.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class VeinToggled {

  static HashMap<UUID, Boolean> veinToggled = new HashMap<>();

  public static void toggleVeinPlayer(Player p) {
    toggleVeinPlayer(p.getUniqueId());
  }

  public static void toggleVeinPlayer(UUID uuid) {
    if (!veinToggled.containsKey(uuid)) {
      veinToggled.put(uuid, true);
    }
    veinToggled.compute(uuid, (k, b) -> Boolean.FALSE.equals(b));
  }

  public static boolean isVeinToggled(Player p) {
    return isVeinToggled(p.getUniqueId());
  }
  public static boolean isVeinToggled(UUID u) {
    if (!veinToggled.containsKey(u)) {
      veinToggled.put(u, true);
    }
    return veinToggled.get(u);
  }

  public static void statusMessage(Player p) {
    boolean state = VeinToggled.isVeinToggled(p);

    Component c = Component.text("Vein mining is now")
            .appendSpace()
                    .append(
                            (state ?
                                    Component.text("enabled").color(TextColor.color(115, 189, 121))
                                    :
                                    Component.text("disabled").color(TextColor.color(188, 63, 60))
                            )
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.runCommand("/vt"))
                                    .hoverEvent(HoverEvent.showText(Component.text("Click To " +
                                            "Toggle "+(state ? "Off" : "On"))))
                    );

    p.sendMessage(c);
  }

}
