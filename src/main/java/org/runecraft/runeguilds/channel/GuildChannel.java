package org.runecraft.runeguilds.channel;

import org.runecraft.runechat.channel.TextChannel;
import org.runecraft.runechat.util.Utils;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.GuildsService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;

import java.util.List;
import java.util.Optional;

public class GuildChannel extends TextChannel {

    public GuildChannel(String id, boolean showTags) {
        super(id, showTags);
    }

    @Override
    public boolean canView(Player player, List<Context> contexts) {
        Player sender = Sponge.getServer().getPlayer(contexts.iterator().next().getValue()).get();
        Optional<Guild> viewerGuild = RuneGuilds.provide(GuildsService.class).getPlayerGuild(player.getUniqueId());
        Guild senderGuild = RuneGuilds.provide(GuildsService.class).getPlayerGuild(sender.getUniqueId()).get();
        return viewerGuild.isPresent() && senderGuild.equals(viewerGuild.get()) && !Utils.isIgnored(player, sender);
    }
}
