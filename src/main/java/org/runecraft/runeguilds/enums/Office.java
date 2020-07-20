package org.runecraft.runeguilds.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Office {
    OWNER(1),SUB_LEADER(2),MEMBER(3);

    private final int id;

    Office(int id){
        this.id = id;
    }

    public static Optional<Office> by(int id){
        return Arrays.asList(values()).stream().filter(x -> x.getId() == id).findFirst();
    }

    public int getId() { return id; }
}
