package org.runecraft.runeguilds.command;

import com.flowpowered.math.vector.Vector2i;
import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.GuildsService;
import org.runecraft.runeguilds.service.ChunksService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class ClaimChunkCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }

        ChunksService chunksService = RuneGuilds.provide(ChunksService.class);
        GuildsService guildService = RuneGuilds.provide(GuildsService.class);

        User user = User.by((Player) src).get();
        Player userPlayer = user.getPlayer().get();
        Optional<Guild> optGuild = guildService.getPlayerGuild(user);

        if(!optGuild.isPresent()){
            userPlayer.sendMessage(Text.builder("Você não está em uma guild. Utilize /guild criar para criar uma.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Guild claimer = optGuild.get();

        if(!claimer.getOwner().equals(user)){
            src.sendMessage(Text.builder("Você precisar ser o líder da guild para fazer isso").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(!claimer.getOwner().equals(user)){
            userPlayer.sendMessage(Text.builder("Apenas o líder da guild pode claimar novas chunks.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        if(chunksService.isInCooldown(claimer)){
            userPlayer.sendMessage(Text.builder("Você só pode claimar uma chunk a cada 10 segundos.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        Location<World> loc = userPlayer.getLocation();
        Vector2i coords = new Vector2i(loc.getChunkPosition().getX(), loc.getChunkPosition().getZ());

        if(chunksService.isClaimed(coords)){
            if(chunksService.getOwner(coords).get().equals(claimer)){
                userPlayer.sendMessage(Text.builder("Essa chunk já está claimada por sua guild.").color(TextColors.RED).build());
                return CommandResult.builder().build();
            }
            Guild owner = chunksService.getOwner(coords).get();
            if(claimer.getPower() >= owner.getPower()*2){
                chunksService.addChunk(coords, claimer, true);
                userPlayer.sendMessage(Text.builder("Você claimou uma chunk de " + owner.getTag().toPlain() + " (-0.2 de poder)").color(TextColors.RED).build());
                claimer.decreasePower(0.2);
            }
        }

        if(claimer.getPower() < 3){
            userPlayer.sendMessage(Text.builder("Você precisa ter pelo menos 3 de poder para claimar uma chunk.").color(TextColors.RED).build());
            return CommandResult.builder().build();
        }

        chunksService.addChunk(coords,claimer,true);
        chunksService.addClaimCooldown(claimer);
        claimer.decreasePower(0.1);

        userPlayer.sendMessage(Text.builder("Essa chunk agora pertence a sua guild (-0.1 de poder).").color(TextColors.RED).build());
        return CommandResult.builder().build();
    }
}
