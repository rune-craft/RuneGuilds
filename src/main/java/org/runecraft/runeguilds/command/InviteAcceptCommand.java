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
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class InviteAcceptCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }

        User invited = User.by((Player) src).get();
        Player invitedPlayer = invited.getPlayer().get();
        GuildsService guildService = RuneGuilds.provide(GuildsService.class);
        InvitesService inviteService = RuneGuilds.provide(InvitesService.class);

        if(!inviteService.getGuildInvites().containsKey(invited)){
            invitedPlayer.sendMessage(Text.builder("Ninguem está te convidando.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(guildService.getPlayerGuild(invited).isPresent()){
            invitedPlayer.sendMessage(Text.builder("Você já está em uma guilda. Você precisa sair dela para entrar em outra.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild inviter = inviteService.getGuildInvites().get(invited);

        inviter.registerMember(invited, Office.MEMBER);
        invitedPlayer.sendMessage(Text.builder("Você agora é um membro da guilda " + inviter.getName().toPlain()).color(TextColors.GREEN).build());
        inviter.getOnlineMembers().forEach(m -> {
            Player p = m.getKey().getPlayer().get();
            p.sendMessage(Text.builder("*" + invited.getName() + " se juntou a guilda!").color(TextColors.YELLOW).build());
            p.playSound(SoundTypes.BLOCK_NOTE_BELL, p.getPosition(), 1.5f);
        });
        RuneGuilds.provide(InvitesService.class).getGuildInvites().remove(invited);

        return CommandResult.builder().build();
    }
}
