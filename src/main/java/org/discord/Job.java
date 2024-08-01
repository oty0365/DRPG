package org.discord;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum Job {
    NONE("가지지 못한 자", Emoji.fromUnicode("🔰")),
    GUARDIAN("가디언", Emoji.fromUnicode("🛡️")),
    BERSERK("버서커", Emoji.fromUnicode("🪓")),
    MAGE("메이지", Emoji.fromUnicode("🪄")),
    ASSASSIN("어쎄신", Emoji.fromUnicode("🥷")),
    CHRIST("크리스트", Emoji.fromUnicode("✝️"));

    public final String name;
    public final Emoji emoji;

    Job(String name, Emoji emoji) {
        this.name = name;
        this.emoji = emoji;
    }

    public String getName() {
        return name;
    }

    public Emoji getEmoji() {
        return emoji;
    }
}
