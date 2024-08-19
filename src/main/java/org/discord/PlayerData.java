package org.discord;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;

public class PlayerData implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    public boolean hasPlayed = false;
    public Job job = Job.NONE;
    public int storyIndex = 0;

    public BigInteger level = BigInteger.ONE;

    public BigInteger hp = new BigInteger("5");
    public BigInteger atk = BigInteger.TWO;
    public BigInteger def = BigInteger.ONE;
    public BigInteger dex = BigInteger.TWO;
    public BigInteger luck = BigInteger.ZERO;
    public BigInteger currentHp = new BigInteger("5");

    public boolean isAlive() {
        return currentHp.compareTo(BigInteger.ZERO) > 0;
    }
}
