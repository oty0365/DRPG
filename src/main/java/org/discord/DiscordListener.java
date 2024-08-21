package org.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

public class DiscordListener implements EventListener {
    public static HashMap<String, PlayerData> data = new HashMap<>();
    public static HashMap<UUID, MobData> mobs = new HashMap<>();
    public static Set<String> isCombating = new HashSet<>();
    public static List<String> script;
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        switch (event) {
            case SlashCommandInteractionEvent e -> {
                User u = e.getUser();
                if (e.getName().equals("trpg")) {
                    if (!data.getOrDefault(u.getId(), new PlayerData()).hasPlayed) {
                        InputStream image = Main.class.getClassLoader().getResourceAsStream("images/Ep1/startAdventure.png");
                        if (image == null) {
                            e.reply("당신의 신비한 모험이 지금 시작됩니다\n계속 진행하려면 이 명령어를 계속 입력해주세요.").queue();
                        } else {
                            e.replyFiles(FileUpload.fromData(image, "image.png")).addContent("계속 진행하려면 이 명령어를 계속 입력해주세요.").queue();
                        }
                        PlayerData dat = data.getOrDefault(u.getId(), new PlayerData());
                        dat.hasPlayed = true;
                        data.put(u.getId(), dat);
                    } else {
                        if (isCombating.contains(u.getId())) {
                            e.reply("싸우는 중에는 스토리를 진행할 수 없습니다!").queue();
                            return;
                        }
                        PlayerData playerData = data.get(u.getId());
                        ScriptProcesser.accept(script.get(playerData.storyIndex), e, u);
                    }
                    return;
                }
                if (e.getName().equals("reset")) {
                    data.remove(u.getId());
                    e.reply("당신의 모험은 여기서 끝났습니다.").queue();
                    return;
                }
                if (e.getName().equals("status")) {
                    PlayerData playerData;
                    OptionMapping option = e.getOption("유저명");
                    playerData = data.get((option != null ? (u = option.getAsUser()) : u).getId());
                    if (playerData == null) {
                        e.reply("아직 플레이 기록이 없습니다!").queue();
                        return;
                    }
                    String levelText = playerData.level.compareTo(new BigInteger("9".repeat(199))) > 0 ? STR."\{"9".repeat(198)}+" : playerData.level.toString();
                    e.replyEmbeds(new EmbedBuilder().setAuthor(STR."Lv.\{levelText} \{u.getEffectiveName()} [\{playerData.job.getEmoji().getFormatted()} \{playerData.job.getName()}]", null, u.getEffectiveAvatarUrl()).setColor(playerData.job.getPersonalColor())
                            .setDescription(STR."""
                            <스텟>
                            체력 : \{playerData.currentHp} / \{playerData.hp}
                            공격력 : \{playerData.atk}
                            방어력 : \{playerData.def}
                            민첩 : \{playerData.dex}
                            운 : \{playerData.luck}
                            """)
                            .build()).queue();
                    return;
                }
                e.reply("아직은 지원하지 않는 명령어입니다!").queue();
            }
            case ButtonInteractionEvent e -> {
                User u = e.getUser();
                PlayerData playerData = data.get(u.getId());
                if (e.getButton().getId() == null) {
                    e.reply("없는 버튼입니다.").queue();
                    return;
                }
                if (playerData == null) {
                    e.reply("현재 데이터가 없습니다.").queue();
                    return;
                }
                if (e.getButton().getId().startsWith(STR."jobSelection_\{u.getId()}_")) {
                    if (!playerData.job.equals(Job.NONE)) {
                        e.reply("이미 직업을 가지고 있습니다.").setEphemeral(true).queue();
                        return;
                    }
                    playerData.job = Job.valueOf(e.getButton().getId().substring("jobSelection__".length() + u.getId().length()));
                    e.getChannel().sendMessage(STR."\{u.getAsMention()}님이 \{playerData.job.getName()} 직업으로 전직했습니다!").queue();
                    e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
                    playerData.storyIndex++;
                    return;
                }
                if (e.getButton().getId().startsWith(STR."teaSelection_\{u.getId()}_")) {
                    if (e.getButton().getId().substring("teaSelection__".length() + u.getId().length()).equals("acceptTea")) {
                        playerData.level = playerData.level.add(BigInteger.ONE);
                        e.reply(STR."""
                                당신은 노인과 차를 마시며 대화하기로 했습니다.
                                차를 마시는 순간 이전의 기억이 돌아올 듯 말듯 하며 머리가 아파옵니다.

                                **

                                하지만 그 순간 번뜩이는 영감속에서 당신은 깨닳습니다..
                                \{playerData.job.getName()}의 기억의 일부를 말이죠
                                머리가 시원해지며 당신은 한층 강해집니다..

                                레벨이 1 상승했습니다.
                                """).addActionRow(getStatPointUseButtons(u)).queue();
                    } else {
                        e.reply("""
                                당신은 노인과 밖에서 대화하기로 했습니다.
                                ```ansi
                                [1;34m<노인>[0;37m
                                아.. 아쉽구나 그러면
                                집 앞에서 이야기하는게 좋겠네
                                ```
                                """).queue();
                        playerData.storyIndex++;
                    }
                    e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
                    return;
                }
                if (e.getButton().getId().startsWith(STR."statup_\{u.getId()}_")) {
                    String[] selectedStat = e.getButton().getId().substring("statUp__".length() + u.getId().length()).split("_");
                    BigInteger point = new BigInteger(selectedStat[1]);
                    switch (selectedStat[0]) {
                        case "hp":
                            playerData.hp = playerData.hp.add(point);
                            playerData.currentHp = playerData.currentHp.add(point);
                            break;
                        case "atk":
                            playerData.atk = playerData.atk.add(point);
                            break;
                        case "def":
                            playerData.def = playerData.def.add(point);
                            break;
                        case "dex":
                            playerData.dex = playerData.dex.add(point);
                            break;
                        case "luck":
                            playerData.luck = playerData.luck.add(point);
                            break;
                        default:
                    }
                    e.reply(STR."\{e.getButton().getLabel()} 스텟이 \{point} 상승했습니다.").queue();
                    e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
                    playerData.storyIndex++;
                }
                if (e.getButton().getId().startsWith(STR."mobSelection_\{u.getId()}_")) {
                    String decide = e.getButton().getId().substring("mobSelection__".length() + u.getId().length());
                    if (decide.startsWith("denyCombat_")){
                        isCombating.remove(u.getId());
                        e.reply(STR."\{getMobByUUID(UUID.fromString(decide.substring("denyCombat_".length()))).destroy().name}과(와)의 싸움에서 도망쳤습니다..").queue();
                    } else if (decide.startsWith("acceptCombat_"))
                        e.reply("싸움을 승낙했습니다!").addActionRow(getCombatProcessButtons(u, getMobByUUID(UUID.fromString(decide.substring("acceptCombat_".length()))), false)).queue();
                    e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
                }
                if (e.getButton().getId().startsWith(STR."mobProcess_\{u.getId()}_")) {
                    String[] decide = e.getButton().getId().substring("mobProcess__".length() + u.getId().length()).split("_");
                    MobData mob = getMobByUUID(UUID.fromString(decide[1]));
                    switch (decide[0]) {
                        case "attack":
                            mob.applyAction(STR."damage-\{playerData.atk}", playerData);
                            if (!mob.isAlive()) {
                                playerData.level = playerData.level.add(BigInteger.ONE);
                                isCombating.remove(u.getId());
                                e.reply(STR."당신의 공격!\n\{mob.name}(이)가 쓰러졌다!\n레벨이 1 올랐습니다.").addActionRow(getStatPointUseButtons(u)).queue();
                                break;
                            }
                            e.reply("당신의 공격!").addEmbeds(getMobInfoEmbed(mob).build()).addActionRow(getCombatProcessButtons(u, mob, true)).queue();
                            break;
                        case "guard":
                            mob.opponentGuarding = true;
                            e.reply("방어 태세 돌입!").addActionRow(getCombatProcessButtons(u, mob, true)).queue();
                            break;
                        case "process":
                            ReplyCallbackAction action;
                            if (mob.nextAction().startsWith("skill-")) {
                                mob.applyAction(mob.getNextSkill().getValue(), playerData);
                                action = e.reply(STR."""
                                \{mob.name}의 스킬 - \{mob.removeNextAction().substring("skill-".length())}!
                                효과가 적용되었다.
                                """).addEmbeds(getMobInfoEmbed(mob).build()).addActionRow(getCombatProcessButtons(u, mob, false));
                            } else {
                                BigInteger applyDamage = mob.damage;
                                if (mob.opponentGuarding) applyDamage = applyDamage.subtract(playerData.def);
                                mob.applyAction(STR."attack-\{applyDamage}", playerData);
                                playerData.currentHp = playerData.currentHp.subtract(applyDamage);
                                mob.removeNextAction();
                                action = e.reply(STR."\{mob.name}의 공격!\nhp \{applyDamage.negate()}").addEmbeds(getMobInfoEmbed(mob).build()).addActionRow(getCombatProcessButtons(u, mob, false));
                            }
                            if (!playerData.isAlive()) {
                                isCombating.remove(u.getId());
                                action.setComponents().addContent("\n당신은 사망하셨습니다.");
                            }
                            action.queue();
                            mob.opponentGuarding = false;
                            break;
                        default:
                            e.reply("오류 발생. 다시 시도해주세요.").queue();
                    }
                    e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
                }
            }
            case MessageReceivedEvent e -> {
                User u = e.getAuthor();
                if (Main.isBotOwner(u.getId())) {
                    if (e.getMessage().getContentRaw().equals("!끄다")) {
                        FileUtils.saveData(data);
                        e.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
                        e.getJDA().shutdown();
                    }
                    if (e.getMessage().getContentRaw().startsWith("!스토리진행도 ")) {
                        int index = Integer.parseInt(e.getMessage().getContentRaw().substring("!스토리진행도 ".length()));
                        PlayerData playerData = data.get(e.getAuthor().getId());
                        playerData.storyIndex = index;
                        e.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
                    }
                    if (e.getMessage().getContentRaw().startsWith("!레벨 ")) {
                        BigInteger level = new BigInteger(e.getMessage().getContentRaw().substring("!레벨 ".length()));
                        PlayerData playerData = data.get(e.getAuthor().getId());
                        playerData.level = level;
                        e.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
                    }
                    if (e.getMessage().getContentRaw().startsWith("!스탯 ")) {
                        BigInteger point = new BigInteger(e.getMessage().getContentRaw().substring("!스탯 ".length()));
                        e.getMessage().reply("올릴 스탯을 정해주세요.").addActionRow(getStatPointUseButtons(e.getAuthor(), point)).queue();
                        e.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
                    }
                    if (e.getMessage().getContentRaw().equals("!스탯")) {
                        e.getMessage().reply("올릴 스탯을 정해주세요.").addActionRow(getStatPointUseButtons(e.getAuthor())).queue();
                        e.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
                    }
                    if (e.getMessage().getContentRaw().equals("!몹테스트")) {
                        MobData mob = new MobData("슬라임", "기초적인 잡몹", new BigInteger("5"), new BigInteger("5"), new BigInteger("1"),
                                Map.entry("1|포식", "attack-0"),
                                Map.entry("4|찰박거리기", "heal-2")
                        );
                        e.getMessage().reply("몬스터가 출현했습니다!").addEmbeds(getMobInfoEmbed(mob).build()).addActionRow(getCombatAcceptButtons(u, mob)).queue();
                        isCombating.add(u.getId());
                        e.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
                    }
                }
            }
            default -> {
            }
        }
    }

    public static EmbedBuilder getMobInfoEmbed(MobData mob) {
        return new EmbedBuilder()
                .setTitle(mob.name)
                .setDescription(STR."""
                설명 : \{mob.description}
                체력 : \{mob.currentHp} / \{mob.maxHp}
                공격력 : \{mob.damage}
                """)
                .setFooter(STR."다음에 사용할 공격 : \{mob.nextAction().startsWith("skill-") ? STR."스킬 - \{mob.getNextSkill().getKey()[1]}" : "일반 공격"}");
    }

    public static Button[] getCombatProcessButtons(User u, MobData mob, boolean isMobTurn) {
        return isMobTurn
        ? new Button[] {
                new ButtonImpl(STR."mobProcess_\{u.getId()}_process_\{mob.uuid}", "진행", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("✅")),
        }
        : new Button[] {
                new ButtonImpl(STR."mobProcess_\{u.getId()}_attack_\{mob.uuid}", "공격", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("⚔️")),
                new ButtonImpl(STR."mobProcess_\{u.getId()}_guard_\{mob.uuid}", "방어", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("🛡️"))
        };
    }

    public static Button[] getCombatAcceptButtons(User u, MobData mob) {
        return new Button[] {
                new ButtonImpl(STR."mobSelection_\{u.getId()}_acceptCombat_\{mob.uuid}", "싸운다", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("⚔️")),
                new ButtonImpl(STR."mobSelection_\{u.getId()}_denyCombat_\{mob.uuid}", "도망친다", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("🔙"))
        };
    }

    public static List<Button> getStatPointUseButtons(User u) {
        return getStatPointUseButtons(u, BigInteger.TWO);
    }

    public static List<Button> getStatPointUseButtons(User u, BigInteger level) {
        level = level.min(new BigInteger("9".repeat(69)));
        return new ArrayList<>(List.of(
                new ButtonImpl(STR."statup_\{u.getId()}_hp_\{level}", "체력", ButtonStyle.SECONDARY, false, null),
                new ButtonImpl(STR."statup_\{u.getId()}_atk_\{level}", "공격력", ButtonStyle.SECONDARY, false, null),
                new ButtonImpl(STR."statup_\{u.getId()}_def_\{level}", "방어력", ButtonStyle.SECONDARY, false, null),
                new ButtonImpl(STR."statup_\{u.getId()}_dex_\{level}", "민첩", ButtonStyle.SECONDARY, false, null),
                new ButtonImpl(STR."statup_\{u.getId()}_luck_\{level}", "운", ButtonStyle.SECONDARY, false, null)
        ));
    }
    public static List<Button> getJobButtons(User u) {
        List<Job> jobs = Arrays.stream(Job.values()).skip(1).toList();
        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            for (int max = Math.min(i + 5, jobs.size()); i < max; i++) {
                buttons.add(new ButtonImpl(STR."jobSelection_\{u.getId()}_\{jobs.get(i).toString()}", jobs.get(i).getName(), ButtonStyle.SECONDARY, false, jobs.get(i).getEmoji()));
            }
        }
        return buttons;
    }

    public static MobData getMobByUUID(UUID uuid) {
        return mobs.get(uuid);
    }

    public static void registerMob(UUID uuid, MobData data) {
        mobs.put(uuid, data);
    }

    public static void unRegisterMob(UUID uuid) {
        mobs.remove(uuid);
    }
}
