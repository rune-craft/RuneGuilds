package org.runecraft.runeguilds.listener;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.enums.Office;
import org.runecraft.runeguilds.event.GuildChatEvent;
import org.runecraft.runeguilds.service.GuildsService;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GuildTagListener implements EventListener<GuildChatEvent> {

    @Override
    public void handle(GuildChatEvent event) throws Exception {
        User user = User.by(event.getSender()).get();
        GuildsService guildService = RuneGuilds.provide(GuildsService.class);
        if(guildService.getPlayerGuild(user.getUUID()).isPresent()){
            Guild guild = guildService.getPlayerGuild(user.getUUID()).get();
            if(guild.getMemberOffice(user) == Office.OWNER){
                event.addTag("guildOffice", Text.builder("[LÍDER]").color(TextColors.YELLOW).build());
            }
            if(guild.getMemberOffice(user) == Office.SUB_LEADER){
                event.addTag("guildOffice", Text.builder("[SUB-LÍDER]").color(TextColors.YELLOW).build());
            }
        }
    }
}
