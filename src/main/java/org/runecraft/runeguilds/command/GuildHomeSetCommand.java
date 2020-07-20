package org.runecraft.runeguilds.command;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.GuildsService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class GuildHomeSetCommand implements CommandExecutor {

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

        if(!guild.getOwner().equals(user)){
            src.sendMessage(Text.builder("Apenas o líder da guild pode marcar sua home.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Player p = (Player) src;

        Location<World> pLocation = p.getLocation();

        String locStr = pLocation.getExtent().getName()+";"+pLocation.getBlockX()+";"+pLocation.getBlockY()+";"+pLocation.getBlockZ();

        RuneGuilds.get().getConfig().getNode("guildhomes").getNode(guild.getTag().toPlain()).setValue(locStr);
        RuneGuilds.get().saveConfig();

        return CommandResult.builder().build();
    }
}
