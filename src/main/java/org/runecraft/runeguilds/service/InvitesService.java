package org.runecraft.runeguilds.service;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.Guild;

import java.util.Map;

public interface InvitesService {
    Map<Guild, Guild> getTruceRequests();

    void addTruceRequest(Guild requester, Guild target);

    void sendTruceRequest(Guild requester, Guild target);

    Map<User, Guild> getGuildInvites();

    void addGuildInvite(User invited, Guild inviter);

    void sendGuildInvite(User user, Guild inviter);

    void addAllyRequest(Guild inviter, Guild invited);

    void sendAllyRequest(Guild inviter, Guild invited);

    Map<Guild, Guild> getAllyRequests();
}
