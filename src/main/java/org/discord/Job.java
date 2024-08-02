package org.discord;

import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;

public enum Job {
    NONE("가지지 못한 자", Emoji.fromUnicode("🔰"), Color.WHITE),
    GUARDIAN("가디언", Emoji.fromUnicode("🛡️"), Color.BLUE),
    BERSERK("버서커", Emoji.fromUnicode("🔪"), Color.RED),
    MAGE("메이지", Emoji.fromUnicode("🪄"), Color.YELLOW),
    ASSASSIN("어쎄신", Emoji.fromUnicode("🥷"), Color.BLACK),
    CHRIST("크리스트", Emoji.fromUnicode("✝️"), Color.GREEN);

    public final String name;
    public final Emoji emoji;

    public final Color personalColor;

    Job(String name, Emoji emoji, Color personalColor) {
        this.name = name;
        this.emoji = emoji;
        this.personalColor = personalColor;
    }

    public String getName() {
        return name;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public Color getPersonalColor() {
        return personalColor;
    }
}
