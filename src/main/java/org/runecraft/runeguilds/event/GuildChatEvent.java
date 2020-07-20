package org.runecraft.runeguilds.event;

import org.runecraft.runechat.channel.TextChannel;
import org.runecraft.runechat.event.AbstractRuneChatEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GuildChatEvent extends AbstractRuneChatEvent {

    public GuildChatEvent(TextChannel channel, Player sender, Text message) {
        super(channel, sender, message, TextColors.GREEN);
        addTag("channel", Text.builder("[CHAT DA GUILD]").color(TextColors.GRAY).build());
    }

    @Override
    public Set<Player> getViewers() {
        Set<Player> viewers = new HashSet<>();

        Sponge.getServer().getOnlinePlayers().forEach(p -> {
            if(channel.canView(p, Collections.singletonList(new Context("sender", sender.getName())))) viewers.add(p);
        });

        return viewers;
    }
}
