package org.discord;

import java.io.Serial;
import java.io.Serializable;

public class Data implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    public boolean hasPlayed = false;
    public Job job = Job.NONE;
    public int storyIndex = 0;

    public int level = 1;

    public int hp = 5;
    public int atk = 2;
    public int def = 1;
    public int dex = 2;
    public int luck = 0;
    public int currentHp = 5;
}
