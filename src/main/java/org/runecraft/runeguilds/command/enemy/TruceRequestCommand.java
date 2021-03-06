package org.runecraft.runeguilds.command.enemy;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.GuildsService;
import org.runecraft.runeguilds.service.InvitesService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class TruceRequestCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            return CommandResult.builder().build();
        }

        User user = User.by((Player) src).get();
        GuildsService guildService = RuneGuilds.provide(GuildsService.class);
        InvitesService inviteService = RuneGuilds.provide(InvitesService.class);

        if(!guildService.getPlayerGuild(user).isPresent()){
            src.sendMessage(Text.builder("Você não possui uma guilda. Utilize /guild create para criar uma").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild guild = guildService.getPlayerGuild(user).get();
        Optional<Guild> optTarget = guildService.getGuild((String) args.getOne("target").get());

        if(!guild.getOwner().equals(user)){
            src.sendMessage(Text.builder("Você precisar ser o líder da guild para fazer isso").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(!optTarget.isPresent()){
            src.sendMessage(Text.builder("Essa guild não existe.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild target = optTarget.get();

        if(!guild.getEnemys().contains(target)){
            src.sendMessage(Text.builder("Essa guild não é inimiga.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(!target.getOwner().getPlayer().isPresent()){
            src.sendMessage(Text.builder("O líder da outra guild precisa estar online.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        inviteService.addTruceRequest(guild, target);
        src.sendMessage(Text.builder("Proposta enviada.").color(TextColors.GREEN).build());
        return CommandResult.builder().build();
    }
}
