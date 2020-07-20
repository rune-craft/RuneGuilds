package org.runecraft.runeguilds.command;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.GuildsService;
import org.runecraft.runeguilds.service.impl.GuildsServiceImpl;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class GuildLeaveCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }

        GuildsService guildService = RuneGuilds.provide(GuildsService.class);

        User player = User.by((Player) src).get();
        Player leaverPlayer = User.by((Player) src).get().getPlayer().get();

        Optional<Guild> optGuild = guildService.getPlayerGuild(player);

        if(!optGuild.isPresent()){
            leaverPlayer.sendMessage(Text.builder("Você não está em uma guild. Utilize /guild criar para criar uma.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild leaverGuild = optGuild.get();

        leaverGuild.removeMember(player);

        leaverGuild.getOnlineMembers().forEach(
                e -> e.getKey().getPlayer().get().sendMessage(Text.builder(player.getName() + " saiu da guild.").color(TextColors.RED).build()));

        leaverGuild.getOnlineMembers().forEach(
                e -> e.getKey().getPlayer().get().playSound(SoundTypes.BLOCK_NOTE_BELL, e.getKey().getPlayer().get().getPosition(), 1d));

        leaverPlayer.sendMessage(Text.builder("Você saiu de sua guild.").color(TextColors.GREEN).build());
        return CommandResult.builder().build();
    }
}
