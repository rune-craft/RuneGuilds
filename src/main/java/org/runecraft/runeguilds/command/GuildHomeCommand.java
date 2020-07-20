package org.runecraft.runeguilds.command;

import com.flowpowered.math.vector.Vector3i;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.GuildsService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GuildHomeCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }

        User user = User.by((Player) src).get();
        GuildsService guildService = RuneGuilds.provide(GuildsService.class);

        if(!guildService.getPlayerGuild(user).isPresent()){
            src.sendMessage(Text.builder("Você não possui uma guilda. Utilize /guild create para criar uma").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild guild = guildService.getPlayerGuild(user).get();

        String locationStr = RuneGuilds.get().getConfig().getNode("guildhomes").getNode(guild.getTag().toPlain()).getString();

        if(locationStr == null){
            src.sendMessage(Text.builder("Sua guild não possui home definida.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        String[] locationArgs = locationStr.split(";");

        String world = locationArgs[0];
        int x = Integer.parseInt(locationArgs[1]);
        int y = Integer.parseInt(locationArgs[2]);
        int z = Integer.parseInt(locationArgs[3]);

        if(!Sponge.getServer().getWorld(world).isPresent()){
            try {
                throw new ObjectMappingException("Unknown work: " + world);
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        }

        user.getPlayer().get().setLocationSafely(Sponge.getServer().getWorld(world).get().getLocation(new Vector3i(x,y,z)));

        return CommandResult.builder().build();
    }
}
