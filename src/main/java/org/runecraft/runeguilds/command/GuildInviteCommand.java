package org.runecraft.runeguilds.command;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.enums.Office;
import org.runecraft.runeguilds.service.GuildsService;
import org.runecraft.runeguilds.service.InvitesService;
import org.runecraft.runeguilds.service.impl.InvitesServiceImpl;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class GuildInviteCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }

        User inviter = User.by((Player) src).get();
        Player playerInviter = inviter.getPlayer().get();
        GuildsService guildService = RuneGuilds.provide(GuildsService.class);
        InvitesService inviteService = RuneGuilds.provide(InvitesService.class);

        Optional<Guild> optGuild = guildService.getPlayerGuild(inviter);

        if(!optGuild.isPresent()){
            playerInviter.sendMessage(Text.builder("Você não possui uma guilda. Utilize /guild criar para criar uma.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild guild = optGuild.get();

        if(guild.getMemberOffice(inviter) == Office.MEMBER){
            playerInviter.sendMessage(Text.builder("Você precisa ser líder ou sub-líder de sua guilda para convidar alguém.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Optional<User> optInvited = User.by((String) args.getOne("player").get());
        if(!optInvited.isPresent()){
            playerInviter.sendMessage(Text.builder("Esse jogador não está online.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        User invited = optInvited.get();

        if(guild.getMembers().size() >= 15){
            playerInviter.sendMessage(Text.builder("A sua guilda ja atingiu o limite máximo de jogadores (15).").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(guildService.getPlayerGuild(invited).isPresent()){
            playerInviter.sendMessage(Text.builder("O jogador convidado já está em uma guilda.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(guild.getPower() <= 2.5){
            playerInviter.sendMessage(Text.builder("Sua guilda precisa ter mais de 2.5 de poder para convidar novos membros.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(inviteService.getGuildInvites().entrySet().stream().anyMatch(e -> e.getValue().equals(guild))){
            playerInviter.sendMessage(Text.builder("Sua guilda só pode convidar um jogador por vez.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        RuneGuilds.provide(InvitesService.class).addGuildInvite(invited, guild);
        playerInviter.sendMessage(Text.builder("Convite enviado.").color(TextColors.GREEN).build());

        return CommandResult.builder().build();
    }
}
