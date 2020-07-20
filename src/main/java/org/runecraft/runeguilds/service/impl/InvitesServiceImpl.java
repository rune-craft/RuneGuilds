package org.runecraft.runeguilds.service.impl;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.InvitesService;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InvitesServiceImpl implements InvitesService {

    private final Map<User, Guild> guildInvites = new HashMap<>();
    private final Map<Guild, Guild> allyRequests = new HashMap<>();
    private final Map<Guild, Guild> truceRequests = new HashMap<>();

    public Map<Guild, Guild> getAllyRequests() {
        return allyRequests;
    }

    public Map<User, Guild> getGuildInvites() {
        return guildInvites;
    }

    public void addGuildInvite(User invited, Guild inviter){
        guildInvites.remove(invited);
        guildInvites.put(invited, inviter);
        sendGuildInvite(invited, inviter);
        Task task = Task.builder().delay(1L, TimeUnit.MINUTES).execute(() -> {
            if(guildInvites.containsKey(invited)){
                inviter.getOwner().getPlayer().ifPresent(x -> x.sendMessage(Text.builder("O convite para " + invited.getName() + " expirou.").color(TextColors.RED).build()));
            }
            guildInvites.remove(invited);
        }).submit(RuneGuilds.get().getPlugin());

    }

    public void sendGuildInvite(User invited, Guild inviter){
        if(invited.getPlayer().isPresent()){
            Player invitedPlayer = invited.getPlayer().get();
            invitedPlayer.playSound(SoundTypes.BLOCK_NOTE_BELL, invitedPlayer.getPosition(), 1.5f);
            invitedPlayer.sendMessage(Text.NEW_LINE);
            invitedPlayer.sendMessage(Text.builder("A guild " + inviter.getName().toPlain() + "está te convidando!").color(TextColors.YELLOW).build());
            invitedPlayer.sendMessage(Text.builder("[ACEITAR] ").color(TextColors.GREEN).style(TextStyles.BOLD)
                    .onClick(TextActions.runCommand("guild accept"))
                    .append(Text.builder("[RECUSAR]").color(TextColors.RED).style(TextStyles.BOLD).onClick(TextActions.runCommand("guild decline")).build()).build());
            invitedPlayer.sendMessage(Text.NEW_LINE);
        }
    }

    public void addAllyRequest(Guild inviter, Guild invited){
        allyRequests.remove(inviter);
        allyRequests.put(inviter, invited);
        sendAllyRequest(inviter, invited);
        Task task = Task.builder().delay(1L, TimeUnit.MINUTES).execute(() -> {
            if(allyRequests.containsKey(inviter)){
                inviter.getOwner().getPlayer().ifPresent(x -> x.sendMessage(Text.builder("O pedido de aliança para " + invited.getName().toPlain() + " expirou.").color(TextColors.RED).build()));
            }
            allyRequests.remove(inviter);
        }).submit(RuneGuilds.get().getPlugin());
    }

    public void sendAllyRequest(Guild inviter, Guild invited){
        if(invited.getOwner().getPlayer().isPresent()){
            Player invitedOwner = invited.getOwner().getPlayer().get();
            invitedOwner.playSound(SoundTypes.BLOCK_NOTE_BELL, invitedOwner.getPosition(), 1.5f);
            invitedOwner.sendMessage(Text.NEW_LINE);
            invitedOwner.sendMessage(Text.builder("A guild " + inviter.getName().toPlain() + " está propondo uma aliança!").color(TextColors.YELLOW).build());
            invitedOwner.sendMessage(Text.builder("[ACEITAR] ").color(TextColors.GREEN).style(TextStyles.BOLD).onClick(TextActions.runCommand("guild ally accept" + invited.getTag().toPlain()))
                    .append(Text.builder("[RECUSAR]").color(TextColors.RED).style(TextStyles.BOLD).onClick(TextActions.runCommand("guild ally decline " + inviter.getTag().toPlain())).build()).build());
            invitedOwner.sendMessage(Text.NEW_LINE);
        }
    }

    public Map<Guild, Guild> getTruceRequests(){
        return truceRequests;
    }

    public void addTruceRequest(Guild requester, Guild requested){
        truceRequests.remove(requester);
        truceRequests.put(requester, requested);
        sendTruceRequest(requester, requested);
        Task task = Task.builder().delay(1L, TimeUnit.MINUTES).execute(() -> {
            if(allyRequests.containsKey(requester)){
                requester.getOwner().getPlayer().ifPresent(x -> x.sendMessage(Text.builder("O pedido de trégua para " + requested.getName().toPlain() + " expirou.").color(TextColors.RED).build()));
            }
            truceRequests.remove(requester);
        }).submit(RuneGuilds.get().getPlugin());
    }

    public void sendTruceRequest(Guild requester, Guild requested){
        if(requested.getOwner().getPlayer().isPresent()){
            Player invitedOwner = requested.getOwner().getPlayer().get();
            invitedOwner.playSound(SoundTypes.BLOCK_NOTE_BELL, invitedOwner.getPosition(), 1.5f);
            invitedOwner.sendMessage(Text.NEW_LINE);
            invitedOwner.sendMessage(Text.builder("A guild " + requester.getName().toPlain() + " está propondo uma trégua.").color(TextColors.YELLOW).build());
            invitedOwner.sendMessage(Text.builder("[ACEITAR] ").color(TextColors.GREEN).style(TextStyles.BOLD).onClick(TextActions.runCommand("guild tregua accept " + requester.getTag().toPlain()))
                    .append(Text.builder("[RECUSAR]").color(TextColors.RED).style(TextStyles.BOLD).onClick(TextActions.runCommand("guild tregua decline " + requester.getTag().toPlain())).build()).build());
            invitedOwner.sendMessage(Text.NEW_LINE);
        }
    }
}
