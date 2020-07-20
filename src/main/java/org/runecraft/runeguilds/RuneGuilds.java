package org.runecraft.runeguilds;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.runecraft.runechat.channel.manager.ChannelManager;
import org.runecraft.runeguilds.channel.GuildChannel;
import org.runecraft.runeguilds.channel.LeadershipChannel;
import org.runecraft.runeguilds.command.*;
import org.runecraft.runeguilds.command.ally.*;
import org.runecraft.runeguilds.command.enemy.TruceAcceptCommand;
import org.runecraft.runeguilds.command.enemy.TruceDeclineCommand;
import org.runecraft.runeguilds.command.enemy.TruceRequestCommand;
import org.runecraft.runeguilds.listener.GuildTagListener;
import org.runecraft.runeguilds.listener.PlayerWalkEvent;
import org.runecraft.runeguilds.service.GuildsService;
import org.runecraft.runeguilds.service.InvitesService;
import org.runecraft.runeguilds.service.impl.ChunksServiceImpl;
import org.runecraft.runeguilds.service.impl.GuildsServiceImpl;
import org.runecraft.runeguilds.service.ChunksService;
import org.runecraft.runeguilds.service.impl.InvitesServiceImpl;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Plugin(
        id = "runeguilds",
        name = "RuneGuilds",
        description = "RuneCraft guilds plugin",
        authors = {
                "Azure"
        }
)
public class RuneGuilds {

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File config;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    private CommentedConfigurationNode configNode;

    private static RuneGuilds instance;

    @Listener
    public void  onServerStart(GameStartedServerEvent event) {
        instance = this;
        this.registerCommands();
        Sponge.getEventManager().registerListeners(this, new GuildTagListener());
        Sponge.getEventManager().registerListeners(this, new PlayerWalkEvent());
        try{
            if(!config.exists()){
                config.createNewFile();
                configNode = loader.load();
                configNode.getNode("guildhomes", "default").setValue("world;0;100;0");
                loader.save(configNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onPosInit(GamePostInitializationEvent event){
        ChannelManager.registerChannel("guild", GuildChannel.class, true);
        ChannelManager.registerChannel("leadership", LeadershipChannel.class, true);
        Sponge.getServiceManager().setProvider(this, GuildsService.class, new GuildsServiceImpl());
        Sponge.getServiceManager().setProvider(this, InvitesService.class, new InvitesServiceImpl());
        Sponge.getServiceManager().setProvider(this, ChunksService.class, new ChunksServiceImpl());
    }

    private void registerCommands(){
        CommandSpec guildChat = CommandSpec.builder().executor(new GuildChatCommand())
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("message"))).build();

        CommandSpec guildCreate = CommandSpec.builder().executor(new CreateGuildCommand())
                .arguments(GenericArguments.string(Text.of("tag")),GenericArguments.remainingJoinedStrings(Text.of("nome"))).build();

        CommandSpec guildCommands = CommandSpec.builder().executor(new GuildCommandsCommand()).build();

        CommandSpec guildInvite = CommandSpec.builder().executor(new GuildInviteCommand())
                .arguments(GenericArguments.string(Text.of("player"))).build();

        CommandSpec inviteAccept = CommandSpec.builder().executor(new InviteAcceptCommand()).build();

        CommandSpec setGuildHome = CommandSpec.builder().executor(new GuildHomeSetCommand()).build();

        CommandSpec guildHome = CommandSpec.builder().executor(new GuildHomeCommand())
                .child(setGuildHome, "set", "setar").build();

        CommandSpec guildKick = CommandSpec.builder().executor(new GuildKickCommand())
                .arguments(GenericArguments.string(Text.of("player"))).build();

        CommandSpec guildClaim = CommandSpec.builder().executor(new ClaimChunkCommand()).build();

        CommandSpec allyAdd = CommandSpec.builder().executor(new AllyAddCommand())
                .arguments(GenericArguments.string(Text.of("target"))).build();

        CommandSpec allyRemove = CommandSpec.builder().executor(new AllyRemoveCommand())
                .arguments(GenericArguments.string(Text.of("target"))).build();

        CommandSpec allyDecline = CommandSpec.builder().executor(new AllyDeclineCommand())
                .arguments(GenericArguments.string(Text.of("target"))).build();

        CommandSpec allyAccept = CommandSpec.builder().executor(new AllyAcceptCommand())
                .arguments(GenericArguments.string(Text.of("target"))).build();

        CommandSpec guildAllies = CommandSpec.builder()
                .executor(new AllyCommand())
                .child(allyAdd, "add")
                .child(allyRemove, "remove")
                .child(allyDecline, "decline", "recusar")
                .child(allyAccept, "aceitar", "accept")
                .build();

        CommandSpec truceDecline = CommandSpec.builder().executor(new TruceDeclineCommand())
                .arguments(GenericArguments.string(Text.of("target"))).build();

        CommandSpec truceAccept = CommandSpec.builder().executor(new TruceAcceptCommand())
                .arguments(GenericArguments.string(Text.of("target"))).build();

        CommandSpec truceCommand = CommandSpec.builder()
                .executor(new TruceRequestCommand())
                .arguments(GenericArguments.string(Text.of("target")))
                .child(truceDecline, "recusar", "decline")
                .child(truceAccept, "aceitar", "accept")
                .build();

        CommandSpec guildCmds = CommandSpec.builder()
                .executor(new GuildCommand())
                .child(guildCreate, "create", "criar")
                .child(guildCommands, "commands", "cmd", "comandos")
                .child(guildInvite, "convidar", "invite")
                .child(inviteAccept, "aceitar", "accept")
                .child(guildKick, "kick", "kickar")
                .child(guildHome, "home")
                .child(guildClaim, "claim", "reivindicar", "claimar")
                .child(guildAllies, "ally")
                .child(truceCommand, "tregua", "truce")
                .build();

        CommandSpec teste = CommandSpec.builder().executor(new TesteCommand()).build();

        Sponge.getCommandManager().register(this, teste, "teste");
        Sponge.getCommandManager().register(this, guildCmds, "guild", "guilda");
        Sponge.getCommandManager().register(this, guildChat, ".", "gc");
    }

    public static RuneGuilds get() {
        return instance;
    }

    public CommentedConfigurationNode getConfig() {
        return configNode;
    }

    public Object getPlugin() {
        return Sponge.getPluginManager().fromInstance(this).get();
    }

    public void saveConfig(){
        try {
            loader.save(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T provide(Class<T> clazz){
        Optional<T> service = Sponge.getServiceManager().provide(clazz);
        if(!service.isPresent()){
            throw new RuntimeException("Service not present: " + clazz.getName());
        }
        return service.get();
    }
}
