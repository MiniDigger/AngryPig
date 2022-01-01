package dev.benndorf.angrypig;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public final class AngryPig extends JavaPlugin {

  @Override
  public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
    if (command.getName().equalsIgnoreCase("angrypig")) {
      if (sender instanceof Player player) {
        this.spawn(player);
      } else {
        sender.sendMessage("This command is only available in-game.");
      }
      return true;
    }
    return false;
  }

  private void spawn(final Player player) {
    final Block target = player.getTargetBlock(50);
    if (target == null) {
      player.sendMessage("You need to target a block for this to work");
      return;
    }
    final Pig pig = (Pig) player.getWorld().spawnEntity(target.getLocation(), EntityType.PIG);
    pig.registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
    pig.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10000);
    pig.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
    pig.setGlowing(true);
    Bukkit.getMobGoals().removeAllGoals(pig);
    Bukkit.getMobGoals().addGoal(pig, 1, new Goal<>() {
      @Override
      public boolean shouldActivate() {
        return true;
      }

      @Override
      public void tick() {
        pig.setTarget(player);
        pig.lookAt(player);
        pig.getPathfinder().moveTo(player);
        if (pig.getLocation().distanceSquared(player.getLocation()) < 3) {
          pig.attack(player);
        }
      }

      @Override
      public @NotNull GoalKey<Pig> getKey() {
        return GoalKey.of(Pig.class, new NamespacedKey(AngryPig.this, "goal"));
      }

      @Override
      public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET, GoalType.MOVE);
      }
    });
  }
}
