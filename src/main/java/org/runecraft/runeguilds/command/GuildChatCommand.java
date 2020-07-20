package org.runecraft.runeguilds.command;

import org.runecraft.runechat.channel.TextChannel;
import org.runecraft.runechat.channel.manager.ChannelManager;
import org.runecraft.runeguilds.event.GuildChatEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.print.attribute.TextSyntax;

public class GuildChatCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.builder().build();
        }
        Player sender = (Player) src;
        Text message = Text.of((String) args.getOne("message").get());

        GuildChatEvent event = new GuildChatEvent(ChannelManager.getChannelInstance("guild").get(), sender, message);
        Sponge.getEventManager().post(event);
        if(!event.isCancelled()){
            event.send();
        }

        return CommandResult.builder().build();
    }
}
