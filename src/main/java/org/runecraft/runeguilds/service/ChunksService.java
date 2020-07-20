package org.runecraft.runeguilds.service;

import com.flowpowered.math.vector.Vector2i;
import org.runecraft.runeguilds.Guild;

import java.util.Optional;

public interface ChunksService{
    void downloadChunks();

    void addClaimCooldown(Guild claimer);

    boolean isInCooldown(Guild guild);

    boolean isClaimed(Vector2i coords);

    Optional<Guild> getOwner(Vector2i coords);

    void removeChunk(Vector2i coords, boolean fromDb);

    void addChunk(Vector2i coords, Guild owner, boolean onDb);

    Optional[][] getGuildsMap(Vector2i center);
}
