package org.runecraft.runeguilds;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;

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

    @Listener
    public void  onServerStart(GameStartedServerEvent event) {
    }
}
