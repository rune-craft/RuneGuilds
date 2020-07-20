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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class GuildKickCommand implements CommandExecutor {
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }

        GuildsService guildService = RuneGuilds.provide(GuildsService.class);


        User kicker = User.by((Player) src).get();
        Player kickerPlayer = kicker.getPlayer().get();
        Optional<User> optKicked = User.by((String)args.getOne("player").get());

        Optional<Guild> optGuild = guildService.getPlayerGuild(kicker);

        if(!optGuild.isPresent()){
            kickerPlayer.sendMessage(Text.builder("Você não está em uma guild. Utilize /guild criar para criar uma.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild kickerGuild = optGuild.get();

        if(!optKicked.isPresent()){
            kickerPlayer.sendMessage(Text.builder("Esse jogador nunca entrou no servidor.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        User kicked = optKicked.get();

        if(!guildService.getPlayerGuild(kicked).isPresent()
            || !guildService.getPlayerGuild(kicked).equals(guildService.getPlayerGuild(kicker))){
            kickerPlayer.sendMessage(Text.builder("Esse jogador não está na sua guild.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(kickerGuild.getMemberOffice(kicker).getId() >= kickerGuild.getMemberOffice(kicked).getId()){
            kickerPlayer.sendMessage(Text.builder("Você não pode kickar esse jogador.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        kickerGuild.removeMember(kicked);
        kicked.getPlayer().ifPresent(p -> p.sendMessage(Text.builder("Você foi kickado de sua guild").color(TextColors.RED).build()));
        kickerPlayer.sendMessage(Text.builder("Jogador kickado com sucesso.").color(TextColors.GREEN).build());

        return CommandResult.builder().build();
    }
}
