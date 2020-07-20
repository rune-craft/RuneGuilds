package org.runecraft.runeguilds.listener;

import com.flowpowered.math.vector.Vector2i;
import javafx.scene.control.TitledPane;
import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.ChunksService;
import org.runecraft.runeguilds.service.GuildsService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import java.util.Optional;

public class PlayerWalkEvent implements EventListener<MoveEntityEvent> {

    @Override
    public void handle(MoveEntityEvent event) throws Exception {
        if(event.getTargetEntity() instanceof Player){
            Player player = (Player) event.getTargetEntity();
            if(event.getFromTransform().getLocation().getChunkPosition().compareTo(event.getToTransform().getLocation().getChunkPosition()) != 0){
                Vector2i from = new Vector2i(event.getFromTransform().getLocation().getChunkPosition().getX(), event.getFromTransform().getLocation().getChunkPosition().getZ());
                Vector2i to = new Vector2i(event.getToTransform().getLocation().getChunkPosition().getX(), event.getToTransform().getLocation().getChunkPosition().getZ());

                ChunksService chunksService = RuneGuilds.provide(ChunksService.class);

                Optional<Guild> playerGuild = RuneGuilds.provide(GuildsService.class).getPlayerGuild(User.by(player).get());

                Text titleText = null;
                Text subtitle = null;

                if(chunksService.isClaimed(to)){
                    if((chunksService.isClaimed(from) && !chunksService.getOwner(from).equals(chunksService.getOwner(to)))
                            || !(chunksService.isClaimed(from))){
                            Guild toGuild = chunksService.getOwner(to).get();
                            if (playerGuild.isPresent()){
                                if(playerGuild.get().getEnemys().contains(toGuild)){
                                    titleText = Text.builder("Território Inimigo").color(TextColors.RED).build();
                                    subtitle = Text.builder(toGuild.getName().toPlain()).color(TextColors.GRAY).build();
                                }else if(playerGuild.get().getAllies().contains(toGuild)){
                                    titleText = Text.builder("Território Aliado").color(TextColors.BLUE).build();
                                    subtitle = Text.builder(toGuild.getName().toPlain()).color(TextColors.GRAY).build();
                                }else{
                                    titleText = Text.builder("Território Neutro").color(TextColors.GRAY).build();
                                    subtitle = Text.builder(toGuild.getName().toPlain()).color(TextColors.GRAY).build();
                                }
                            }else{
                                titleText = Text.builder("Território Neutro").color(TextColors.GRAY).build();
                                subtitle = Text.builder(toGuild.getName().toPlain()).color(TextColors.GRAY).build();
                            }
                    }
                }else{
                    if(chunksService.isClaimed(from)){
                        titleText = Text.builder("Zona Livre").color(TextColors.DARK_GREEN).build();
                        subtitle = Text.builder("Território não reevindicado.").color(TextColors.GRAY).build();
                    }
                }

                Title title = Title.builder().title(titleText).subtitle(subtitle).fadeIn(1).fadeOut(1).build();
                player.sendTitle(title);
            }
        }

    }
}
