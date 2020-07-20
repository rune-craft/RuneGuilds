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
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class EnemyAddCommand implements CommandExecutor {

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

        Optional<Guild> optTarget = guildService.getGuild((String) args.getOne("target").get());

        if(!guild.getOwner().equals(user)){
            src.sendMessage(Text.builder("Você precisar ser o líder da guild para fazer isso").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(!optTarget.isPresent()){
            userPlayer.sendMessage(Text.builder("Essa guild não existe.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild target = optTarget.get();

        if(guild.getAllies().contains(target)){
            userPlayer.sendMessage(Text.builder("Você não pode ser inimiga de guilds aliadas.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(guild.getEnemys().contains(target)){
            userPlayer.sendMessage(Text.builder("Essa guild já é inimiga.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        guild.addEnemy(target, true);
        target.addEnemy(guild, true);
        target.getOnlineMembers().forEach(m->{
            Player p = m.getKey().getPlayer().get();
            p.sendMessage(Text.NEW_LINE);
            p.sendMessage(Text.builder("*" + guild.getName().toPlain() + " declarou sua guild como inimiga.").color(TextColors.RED).build());
            p.sendMessage(Text.NEW_LINE);
            p.playSound(SoundTypes.BLOCK_NOTE_BASS, p.getPosition(), 1.5f);
        });
        guild.getOnlineMembers().forEach(m->{
            Player p = m.getKey().getPlayer().get();
            p.sendMessage(Text.NEW_LINE);
            p.sendMessage(Text.builder("*" + target.getName().toPlain() + " agora é uma guild inimiga.").color(TextColors.YELLOW).build());
            p.sendMessage(Text.NEW_LINE);
            p.playSound(SoundTypes.BLOCK_NOTE_BASS, p.getPosition(), 1.5f);
        });

        return CommandResult.builder().build();
    }
}
