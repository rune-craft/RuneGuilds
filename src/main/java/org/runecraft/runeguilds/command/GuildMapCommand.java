package org.runecraft.runeguilds.command;

import com.flowpowered.math.vector.Vector2i;
import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.ChunksService;
import org.runecraft.runeguilds.service.GuildsService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class GuildMapCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }

        User user = User.by((Player) src).get();

        Player p = (Player) src;
        Location<World> loc = p.getLocation();

        Text.Builder builder = Text.builder();

        Optional[][] map = RuneGuilds.provide(ChunksService.class).getGuildsMap(new Vector2i(loc.getChunkPosition().getX(), loc.getChunkPosition().getZ()));

        for(int x = -7; x<7; x++){
            for(int y = -7; y<7; y++){
                Optional<Guild> opt = (Optional<Guild>) map[x][y];
                if(opt.isPresent()){
                    Guild guild = opt.get();
                    if(RuneGuilds.provide(GuildsService.class).getPlayerGuild(user).isPresent()){
                        Guild playerGuild = RuneGuilds.provide(GuildsService.class).getPlayerGuild(user).get();
                        TextColor color = TextColors.GRAY;

                        if(playerGuild.getAllies().contains(guild)){
                            color = TextColors.BLUE;
                        }

                        else if(playerGuild.getEnemys().contains(guild)){
                            color = TextColors.RED;
                        }

                        if(playerGuild.equals(guild)){
                            color = TextColors.GREEN;
                        }
                        builder.append(Text.builder("⬛")
                                .color(TextColors.RESET).color(TextColors.DARK_GRAY)
                                .onHover(TextActions.showText(Text.builder("Chunk de " + guild.getName().toPlain() + "(" + guild.getTag().toPlain() + ")")
                                        .color(color)
                                        .append(Text.NEW_LINE)
                                        .append(Text.builder("(X: " + x*16 + ", Z: " + y*16 + ")").color(color).build()).build())).build());

                    }
                }else{
                    builder.append(Text.builder("⬛")
                            .color(TextColors.RESET).color(TextColors.DARK_GRAY)
                            .onHover(TextActions.showText(Text.builder("Chunk não claimada.")
                                    .color(TextColors.GRAY)
                                    .append(Text.NEW_LINE)
                                    .append(Text.builder("(X: " + x*16 + ", Z: " + y*16 + ")").color(TextColors.GRAY).build()).build())).build());
                }
            }
            builder.append(Text.NEW_LINE);
        }

        p.sendMessage(Text.builder("⬛: SUA GUILD").color(TextColors.GREEN).build());
        p.sendMessage(Text.builder("⬛: GUILD ALIDA").color(TextColors.BLUE).build());
        p.sendMessage(Text.builder("⬛: GUILD INIMIGA").color(TextColors.RED).build());
        p.sendMessage(Text.builder("⬛: GUILD NEUTRA").color(TextColors.GRAY).build());
        p.sendMessage(Text.builder("⬛: CHUNK NÃO CLAIMADA").color(TextColors.DARK_GRAY).build());



        return CommandResult.builder().build();
    }
}
