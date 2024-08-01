package org.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
                        e.getChannel().sendMessage("당신은 어두컴컴한 묘지에서 누군가의 가호를 받고 일어났습니다..").queue();
                        e.getChannel().sendMessage("당신의 옷은 그을렸고 몸에는 신비로운 인장("+u.getName()+")이 박혀있는 듯 합니다.").queue();
                        var messageAction = e.getChannel().sendMessage("하나를 고르시오");
                        for (Job job : Arrays.stream(Job.values()).skip(1).toList()) messageAction.addActionRow(new ButtonImpl("jobSelection_" + u.getId() + "_" + job.toString(), job.getName(), ButtonStyle.SECONDARY, false, job.getEmoji()));
                        messageAction.queue();

                    } catch (FileNotFoundException ex) {
                        e.reply("당신의 신비한 모험이 지금 시작됩니다").queue();
                        e.getChannel().sendMessage("당신은 어두컴컴한 묘지에서 누군가의 가호를 받고 일어났습니다..").queue();
                        e.getChannel().sendMessage("당신의 옷은 그을렸고 몸에는 신비로운 인장("+u.getName()+")이 박혀있는 듯 합니다.").queue();
                    }
                    Data dat = data.getOrDefault(u.getId(), new Data());
                    dat.hasPlayed = true;
                    data.put(u.getId(), dat);
                } else {
                    e.reply("모험에 돌아오신 걸 환영합니다!").queue();
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
                e.getChannel().deleteMessageById(e.getMessageId()).queue();
                e.getChannel().sendMessage(e.getUser().getAsMention() + "님이 " + playerData.job.getName() + " 직업으로 전직했습니다!").setAllowedMentions(new ArrayList<>()).queue();
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
