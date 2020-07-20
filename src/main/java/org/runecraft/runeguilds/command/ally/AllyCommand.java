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
import org.spongepowered.api.text.format.TextStyles;

public class AllyCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            return CommandResult.builder().build();
        }

        User user = User.by((Player) src).get();
        GuildsService guildService = RuneGuilds.provide(GuildsService.class);

        if(!guildService.getPlayerGuild(user).isPresent()){
            src.sendMessage(Text.builder("Você não possui uma guilda. Utilize /guild create para criar uma").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild guild = guildService.getPlayerGuild(user).get();

        if(guild.getAllies().isEmpty()){
            src.sendMessage(Text.builder("[ALLY] ").color(TextColors.GOLD).style(TextStyles.BOLD).append(Text.builder("Sua guild não possui nenhum aliado.").color(TextColors.YELLOW).build()).build());
            return CommandResult.builder().build();
        }

        src.sendMessage(Text.builder("[ALLY] ").color(TextColors.GOLD).style(TextStyles.BOLD).append(Text.builder("Suas guilds aliadas:").color(TextColors.YELLOW).build()).build());
        guild.getAllies().forEach(g -> {
            src.sendMessage(Text.builder("  - ").color(TextColors.GOLD).style(TextStyles.BOLD).append(Text.builder(g.getName().toPlain() + " (" + g.getTag().toPlain() + ")").color(TextColors.YELLOW).build()).build());
        });

        return CommandResult.builder().build();
    }
}
