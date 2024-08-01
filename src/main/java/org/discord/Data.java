package org.discord;

import java.io.Serializable;

public class Data implements Serializable {
    public boolean hasPlayed;
    public Job job = Job.NONE;
    public int storyIndex;
}
