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
import org.spongepowered.api.text.format.TextStyles;

public class GuildCommand implements CommandExecutor {
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

        src.sendMessage(Text.NEW_LINE);

        src.sendMessage(Text.builder("[GUILD]").color(TextColors.LIGHT_PURPLE).style(TextStyles.BOLD)
                .append(Text.builder( " " + guild.getName().toPlain()).color(TextColors.GRAY).style(TextStyles.RESET).build())
                .append(Text.builder(" [" + guild.getTag().toPlain() + "]").color(TextColors.GRAY).style(TextStyles.RESET).build())
                .build());

        src.sendMessage(Text.NEW_LINE);

        src.sendMessage(Text.builder("[GUILD]").color(TextColors.LIGHT_PURPLE).style(TextStyles.BOLD)
                .append(Text.builder(" Líder: ").color(TextColors.AQUA).style(TextStyles.RESET).build())
                .append(Text.builder(guild.getOwner().getName()).color(TextColors.DARK_AQUA).style(TextStyles.RESET).build())
                .build());

        src.sendMessage(Text.builder("[GUILD]").color(TextColors.LIGHT_PURPLE).style(TextStyles.BOLD)
                .append(Text.builder(" Pontos: ").color(TextColors.AQUA).style(TextStyles.RESET).build())
                .append(Text.builder(""+guild.getPoints()).color(TextColors.DARK_AQUA).style(TextStyles.RESET).build())
                .build());

        src.sendMessage(Text.builder("[GUILD]").color(TextColors.LIGHT_PURPLE).style(TextStyles.BOLD)
                .append(Text.builder(" Poder: ").color(TextColors.AQUA).style(TextStyles.RESET).build())
                .append(Text.builder(""+guild.getPower() + "/5.0").color(TextColors.DARK_AQUA).style(TextStyles.RESET).build())
                .build());

        src.sendMessage(Text.NEW_LINE);

        src.sendMessage(Text.builder("[GUILD]").color(TextColors.LIGHT_PURPLE).style(TextStyles.BOLD)
                .append(Text.builder(" Comandos de guild: /guild comandos").color(TextColors.AQUA).style(TextStyles.RESET).build())
                .build());

        return CommandResult.builder().build();
    }
}
