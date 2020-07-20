package org.runecraft.runeguilds.command.ally;

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

public class AllyAddCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            return CommandResult.builder().build();
        }
        GuildsService guildService = RuneGuilds.provide(GuildsService.class);

        User user = User.by((Player) src).get();
        Player userPlayer = user.getPlayer().get();
        Optional<Guild> optGuild = guildService.getPlayerGuild(user);

        if(!optGuild.isPresent()){
            userPlayer.sendMessage(Text.builder("Você não está em uma guild. Utilize /guild criar para criar uma.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild guild = optGuild.get();

        if(!guild.getOwner().equals(user)){
            src.sendMessage(Text.builder("Você precisar ser o líder da guild para fazer isso").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Optional<Guild> optTarget = guildService.getGuild((String) args.getOne("target").get());

        if(!optTarget.isPresent()){
            userPlayer.sendMessage(Text.builder("Essa guild não existe.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild target = optTarget.get();

        if(guild.getEnemys().contains(target)){
            userPlayer.sendMessage(Text.builder("Você não pode mandar um pedido de aliança para uma guild inimiga.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(guild.getAllies().contains(target)){
            userPlayer.sendMessage(Text.builder("Essa guild já é sua aliada.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(!target.getOwner().getPlayer().isPresent()){
            userPlayer.sendMessage(Text.builder("O líder da outra guild precisa estar online.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        RuneGuilds.provide(InvitesService.class).addAllyRequest(guild, target);
        userPlayer.sendMessage(Text.builder("Proposta enviada.").color(TextColors.GREEN).build());

        return CommandResult.builder().build();
    }
}
