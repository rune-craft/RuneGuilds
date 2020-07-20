package org.runecraft.runeguilds.command;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class TesteCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Player p = (Player) src;

        Vector3i loc = p.getLocation().getChunkPosition();

        System.out.println(loc.getX() + ";" + loc.getY() + ";" + loc.getZ());
        return CommandResult.builder().build();
    }
}
