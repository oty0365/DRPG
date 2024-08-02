package org.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DiscordListener implements EventListener {
    public static HashMap<String, Data> data = new HashMap<>();
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof SlashCommandInteractionEvent e) {
            User u = e.getUser();
            if (e.getName().equals("trpg")) {
                if (!data.getOrDefault(u.getId(), new Data()).hasPlayed) {
                    try {
                        e.replyFiles(FileUpload.fromData(new FileInputStream("startAdventure.png"), "image.png")).queue();
                    } catch (FileNotFoundException ex) {
                        e.reply("당신의 신비한 모험이 지금 시작됩니다").queue();
                    }
                    Data dat = data.getOrDefault(u.getId(), new Data());
                    dat.hasPlayed = true;
                    data.put(u.getId(), dat);
                } else {
                    Data playerData = data.get(u.getId());
                    switch (playerData.storyIndex) {
                        case 0:
                            e.reply("당신은 어두컴컴한 묘지에서 누군가의 가호를 받고 일어났습니다..\n당신의 옷은 그을렸고 몸에는 신비로운 인장("+u.getName()+")이 박혀있는 듯 합니다.").queue();
                        case 1:
                            var messageAction = e.reply("다음 직업 중 하나로 전직할 수 있습니다.");
                            List<Job> jobs = Arrays.stream(Job.values()).skip(1).toList();
                            for (int i = 0; i < jobs.size(); i++) {
                                List<Button> buttons = new ArrayList<>();
                                for (int max = Math.min(i + 5, jobs.size()); i < max; i++) {
                                    buttons.add(new ButtonImpl("jobSelection_" + u.getId() + "_" + jobs.get(i).toString(), jobs.get(i).getName(), ButtonStyle.SECONDARY, false, jobs.get(i).getEmoji()));
                                }
                                messageAction.addActionRow(buttons);
                            }
                            messageAction.queue();
                            playerData.storyIndex++;
                            break;
                        case 2:
                            if (playerData.job.equals(Job.NONE)) {
                                e.reply("직업을 골라주세요!").setEphemeral(true).queue();
                                return;
                            }
                            e.reply("Next_Story: 직업 고른 후 스토리").queue();
                            playerData.storyIndex++;
                            break;
                        case 3:
                            e.reply("Next_Story: 직업 고르고 진행 후 스토리").queue();
                            playerData.storyIndex++;
                            break;
                        default:
                            e.reply("모험에 돌아오신 걸 환영합니다!").queue();
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
                Data playerData = data.get(u.getId());
                if (playerData == null) {
                    e.reply("아직 플레이 기록이 없습니다!").queue();
                    return;
                }
                e.replyEmbeds(new EmbedBuilder().setTitle(u.getName() + "의 스테이터스").setColor(Color.green)
                        .setDescription("직업 : " + playerData.job.getEmoji().getFormatted() + " " + playerData.job.getName())
                        .build()).queue();
                return;
            }
            e.reply("아직은 지원하지 않는 명령어입니다!").queue();
        } else if (event instanceof ButtonInteractionEvent e) {
            Data playerData = data.get(e.getUser().getId());
            if (e.getButton().getId() == null) {
                e.reply("없는 버튼입니다.").queue();
                return;
            }
            if (playerData == null) {
                e.reply("현재 데이터가 없습니다.").queue();
                return;
            }
            if (e.getButton().getId().startsWith("jobSelection_" + e.getUser().getId() + "_")) {
                if (!playerData.job.equals(Job.NONE)) {
                    e.reply("이미 직업을 가지고 있습니다.").setEphemeral(true).queue();
                    return;
                }
                playerData.job = Job.valueOf(e.getButton().getId().substring("jobSelection__".length() + e.getUser().getId().length()));
                e.getChannel().editMessageById(e.getMessageId(), e.getUser().getAsMention() + "님이 " + playerData.job.getName() + " 직업으로 전직했습니다!").queue();
                e.getChannel().editMessageComponentsById(e.getMessageId()).queue();
            }
        } else if (event instanceof MessageReceivedEvent e) {
            if (Main.isBotOwner(e.getAuthor().getId())) {
                if (e.getMessage().getContentRaw().equals("!끄다")) {
                    FileUtils.saveData(data);
                    e.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
                    e.getJDA().shutdown();
                }
            }
        }
    }
}
