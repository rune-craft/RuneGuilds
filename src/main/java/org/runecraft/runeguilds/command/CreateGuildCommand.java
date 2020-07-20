package org.runecraft.runeguilds.command;

import org.runecraft.runecore.User;
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

public class CreateGuildCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }

        Player p = (Player) src;

        GuildsService guildsService = RuneGuilds.provide(GuildsService.class);

        if(guildsService.getPlayerGuild(p.getUniqueId()).isPresent()){
            p.sendMessage(Text.builder("Você já está em uma guilda. Utilize /guild sair para sair.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        String name = (String) args.getOne("nome").get();
        String tag = (String) args.getOne("tag").get();

        if(name.length() > 32){
            p.sendMessage(Text.builder("O nome da guilda deve ter no máximo 32 caracteres.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(tag.length()>10){
            p.sendMessage(Text.builder("A tag da guilda deve ter no máximo 10 caracteres.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        guildsService.createGuild(User.by(p).get(), Text.of(tag), Text.of(name));

        return CommandResult.builder().build();
    }
}
