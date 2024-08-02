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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DiscordListener implements EventListener {
    public static HashMap<String, Data> data = new HashMap<>();
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        switch (event) {
            case SlashCommandInteractionEvent e -> {
                User u = e.getUser();
                if (e.getName().equals("trpg")) {
                    if (!data.getOrDefault(u.getId(), new Data()).hasPlayed) {
                        InputStream image = Main.class.getClassLoader().getResourceAsStream("startAdventure.png");
                        if (image == null) {
                            e.reply("당신의 신비한 모험이 지금 시작됩니다").queue();
                        } else {
                            e.replyFiles(FileUpload.fromData(image, "image.png")).queue();
                        }
                        Data dat = data.getOrDefault(u.getId(), new Data());
                        dat.hasPlayed = true;
                        data.put(u.getId(), dat);
                    } else {
                        Data playerData = data.get(u.getId());
                        ReplyCallbackAction messageAction;
                        InputStream image;
                        switch (playerData.storyIndex) {
                            case 0:
                                e.reply(STR."""
                                당신은 어두컴컴한 묘지에서 누군가의 가호를 받고 일어났습니다..
                                당신의 옷은 그을렸고 몸에는 신비로운 인장(\{u.getName()})이 박혀있는 듯 합니다.
                                """).queue();
                            case 1:
                                messageAction = e.reply("눈을 희미하게 뜬 당신은 수많은 과거의 기억들을 떠올립니다..");
                                image = Main.class.getClassLoader().getResourceAsStream("Ep1SelectionTime.png");
                                if (image == null) {
                                    messageAction.addContent("\n# Ep1.선택의 시간\n과거의 기억들 중 하나를 선택하세요");
                                } else {
                                    messageAction.addFiles(FileUpload.fromData(image, "image.png"));
                                    messageAction.addContent("\n과거의 기억들 중 하나를 선택하세요");
                                }
                                List<Job> jobs = Arrays.stream(Job.values()).skip(1).toList();
                                for (int i = 0; i < jobs.size(); i++) {
                                    List<Button> buttons = new ArrayList<>();
                                    for (int max = Math.min(i + 5, jobs.size()); i < max; i++) {
                                        buttons.add(new ButtonImpl(STR."jobSelection_\{u.getId()}_\{jobs.get(i).toString()}", jobs.get(i).getName(), ButtonStyle.SECONDARY, false, jobs.get(i).getEmoji()));
                                    }
                                    messageAction.addActionRow(buttons);
                                }

                                messageAction.queue();
                                playerData.storyIndex++;
                                break;
                            case 2:
                                e.reply("직업을 골라주세요").setEphemeral(true).queue();
                                break;
                            case 3:
                                e.reply(STR."""
                                \{u.getName()}은(는) \{playerData.job.getName()}의 기억을 떠올렸습니다..
                                하지만 과거의 일은 좀처럼 와닿지 않았습니다..
                                지끈 아파오는 머리를 뒤로하고\{u.getName()}은(는) 앞으로 나아갑니다
                                """).queue();
                                playerData.storyIndex++;
                                break;
                            case 4:
                                messageAction = e.reply("여러 묘지를 지난 당신은 그 가운데에 덩그러니 놓여있는 한 허름한 집을 보았습니다.");
                                image = Main.class.getClassLoader().getResourceAsStream("Ep2TheHouseKeeper.png");
                                if (image == null) {
                                    messageAction.addContent("\n# Ep2.무덤지기의 집\n허름해 보이는 집에는 아무도 살지 안을 것 같습니다..");
                                } else {
                                    messageAction.addFiles(FileUpload.fromData(image, "image.png"));
                                    messageAction.addContent("\n허름해 보이는 집에는 아무도 살지 안을 것 같습니다..");
                                }
                                messageAction.queue();
                                playerData.storyIndex++;
                                break;
                            case 5:
                                e.reply("허름한 집의 문을 열고 들어가려는 순간..").queue();
                                playerData.storyIndex++;
                                break;
                            case 6:
                                e.reply("""
                                    ```ansi
                                    [1;34m???[0;37m: 거 누구인가..?
                                    ```
                                    """).queue();
                                playerData.storyIndex++;
                                break;
                            case 7:
                                e.reply("낮선 누군가의 외침에 당신은 얼어붙었습니다..\n그 낮선 목소리는 숨어있던 그늘에서 나와 모습을 드러냅니다").queue();
                                playerData.storyIndex++;
                                break;
                            case 8:
                                messageAction = e.reply("인자하고 범접할 수 없는 아우라를 풍기는 노인이 당신 앞에 섭니다..");
                                image = Main.class.getClassLoader().getResourceAsStream("TheOldMan.png");
                                if (image == null) {
                                    messageAction.addContent("\n# 노인");
                                } else {
                                    messageAction.addFiles(FileUpload.fromData(image, "image.png"));
                                }
                                messageAction.queue();
                                playerData.storyIndex++;
                                break;
                            case 9:
                                e.reply("""
                                    ```ansi
                                    [1;34m노인[0;37m: 또 다른 불멸자구나..
                                         들어와서 차라도 한 잔 하지 않겠나?
                                    ```
                                    """).queue();
                                playerData.storyIndex++;
                                break;
                            case 10:
                                e.reply("노인이 당신에게 차를 권합니다.. 차를 마시게습니까?").addActionRow(
                                        new ButtonImpl(STR."teaSelection_\{u.getId()}_acceptTea", "차를 마신다", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("\uD83C\uDF75")),
                                        new ButtonImpl(STR."teaSelection_\{u.getId()}_talkOutside", "밖에서 이야기한다", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("\uD83C\uDFE0"))
                                ).queue();
                                playerData.storyIndex++;
                                break;
                            case 11:
                                e.reply("선택지를 골라주세요").setEphemeral(true).queue();
                                break;
                            case 12:
                                e.reply("""
                                        ```ansi
                                        [1;34m노인[0;37m: Story_NextLine
                                        ```
                                        """).queue();
                                playerData.storyIndex++;
                                break;
                            default:
                                e.reply("당신은 잠에서 깨어나 묵묵히 길을 나섭니다").queue();
                        }
                    }
                    return;
                }
                if (e.getName().equals("reset")) {
                    data.remove(u.getId());
                    e.reply("당신의 모험은 여기서 끝났습니다.").queue();
                    return;
                }
                if (e.getName().equals("status")) {
                    Data playerData;
                    OptionMapping option = e.getOption("유저명");
                    playerData = data.get((option != null ? (u = option.getAsUser()) : u).getId());
                    if (playerData == null) {
                        e.reply("아직 플레이 기록이 없습니다!").queue();
                        return;
                    }
                    e.replyEmbeds(new EmbedBuilder().setAuthor(STR."Lv.\{playerData.level} \{u.getName()} [\{playerData.job.getEmoji().getFormatted()} \{playerData.job.getName()}]", null, u.getEffectiveAvatarUrl()).setColor(playerData.job.getPersonalColor())
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
                Data playerData = data.get(e.getUser().getId());
                if (e.getButton().getId() == null) {
                    e.reply("없는 버튼입니다.").queue();
                    return;
                }
                if (playerData == null) {
                    e.reply("현재 데이터가 없습니다.").queue();
                    return;
                }
                if (e.getButton().getId().startsWith(STR."jobSelection_\{e.getUser().getId()}_")) {
                    if (!playerData.job.equals(Job.NONE)) {
                        e.reply("이미 직업을 가지고 있습니다.").setEphemeral(true).queue();
                        return;
                    }
                    playerData.job = Job.valueOf(e.getButton().getId().substring("jobSelection__".length() + e.getUser().getId().length()));
                    e.getChannel().sendMessage(STR."\{e.getUser().getAsMention()}님이 \{playerData.job.getName()} 직업으로 전직했습니다!").queue();
                    e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
                    playerData.storyIndex++;
                    return;
                }
                if (e.getButton().getId().startsWith(STR."teaSelection_\{e.getUser().getId()}_")) {
                    if (e.getButton().getId().substring("teaSelection__".length() + e.getUser().getId().length()).equals("acceptTea")) {
                        playerData.level++;
                        e.reply(STR."""
                                당신은 노인과 차를 마시며 대화하기로 했습니다.
                                차를 마시는 순간 이전의 기억이 돌아올 듯 말듯 하며 머리가 아파옵니다.
                                하지만 그 순간 번뜩이는 영감속에서 당신은 깨닳습니다..
                                \{playerData.job.getName()}의 기억의 일부를 말이죠
                                머리가 시원해지며 당신은 한층 강해집니다..
                                레벨이 1 상승했습니다.
                                """).addActionRow(getStatPointUseButtons(e.getUser())).queue();
                    } else {
                        e.reply("""
                                당신은 노인과 밖에서 대화하기로 했습니다.
                                ```ansi
                                [1;34m노인[0;37m: 아.. 아쉽구나 그러면 
                                     집 앞에서 이야기하는게 좋겠네
                                ```
                                """).queue();
                        playerData.storyIndex++;
                    }
                    e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
                    return;
                }
                if (e.getButton().getId().startsWith(STR."statup_\{e.getUser().getId()}_")) {
                    String selectedStat = e.getButton().getId().substring("statUp__".length() + e.getUser().getId().length());
                    switch (selectedStat) {
                        case "hp":
                            playerData.hp += 2;
                            playerData.currentHp += 2;
                            break;
                        case "atk":
                            playerData.atk += 2;
                            break;
                        case "def":
                            playerData.def += 2;
                            break;
                        case "dex":
                            playerData.dex += 2;
                            break;
                        case "luck":
                            playerData.luck += 2;
                            break;
                        default:
                    }
                    e.reply(STR."\{e.getButton().getLabel()} 스텟이 2 상승했습니다.").queue();
                    e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
                    playerData.storyIndex++;
                }
            }
            case MessageReceivedEvent e -> {
                if (Main.isBotOwner(e.getAuthor().getId())) {
                    if (e.getMessage().getContentRaw().equals("!끄다")) {
                        FileUtils.saveData(data);
                        e.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
                        e.getJDA().shutdown();
                    }
                }
            }
            default -> {
            }
        }
    }

    public static List<Button> getStatPointUseButtons(User u) {
        return new ArrayList<>(List.of(
                new ButtonImpl(STR."statup_\{u.getId()}_hp", "체력", ButtonStyle.SECONDARY, false, null),
                new ButtonImpl(STR."statup_\{u.getId()}_atk", "공격력", ButtonStyle.SECONDARY, false, null),
                new ButtonImpl(STR."statup_\{u.getId()}_def", "방어력", ButtonStyle.SECONDARY, false, null),
                new ButtonImpl(STR."statup_\{u.getId()}_dex", "민첩", ButtonStyle.SECONDARY, false, null),
                new ButtonImpl(STR."statup_\{u.getId()}_luck", "운", ButtonStyle.SECONDARY, false, null)
        ));
    }
}
