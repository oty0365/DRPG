package org.discord;

import java.math.BigInteger;
import java.util.*;

public class MobData {
    public String name, description;
    public Queue<Map.Entry<String[], String>> skills;
    public BigInteger currentHp, maxHp, damage;
    public int turn = 0;
    public UUID uuid;
    public boolean opponentGuarding = false;

    @SafeVarargs
    public MobData(String name, String description, BigInteger currentHp, BigInteger maxHp, BigInteger damage, Map.Entry<String, String>... skillActions) {
        this.name = name;
        this.description = description;
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.damage = damage;
        skills = new LinkedList<>(Arrays.stream(skillActions).map(s -> Map.entry(s.getKey().split("\\|"), s.getValue())).toList());
        do uuid = UUID.randomUUID(); while (DiscordListener.getMobByUUID(uuid) != null);
        DiscordListener.registerMob(uuid, this);
    }

    public Map.Entry<String[], String> getNextSkill() {
        return skills.peek();
    }

    public String nextAction() {
        if (!skills.isEmpty() && skills.peek().getKey()[0].equals(STR."\{turn}")) return STR."skill-\{skills.peek().getKey()[1]}";
        return STR."attack-\{damage}";
    }

    public String removeNextAction() {
        String action = nextAction();
        if (action.startsWith("skill-")) skills.poll();
        turn++;
        return action;
    }

    public void applyAction(String action) {
        if (action.startsWith("damage-")) {
            currentHp = currentHp.subtract(new BigInteger(action.substring("damage-".length())));
            if (currentHp.compareTo(BigInteger.ZERO) <= 0) DiscordListener.unRegisterMob(uuid);
        } else if (action.startsWith("heal-"))
            currentHp = currentHp.add(new BigInteger(action.substring("heal-".length()))).min(maxHp);
    }

    public MobData destroy() {
        currentHp = BigInteger.ZERO;
        DiscordListener.unRegisterMob(uuid);
        return this;
    }

    public boolean isAlive() {
        return currentHp.compareTo(BigInteger.ZERO) > 0;
    }
}
