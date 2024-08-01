package org.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class Main {
    public static JDA jda;
    public static void main(String[] args) {
        DiscordListener.data = FileUtils.loadData();
        JDABuilder builder = JDABuilder.createDefault(FileUtils.readToken());
        builder.setAutoReconnect(true);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("TRPG"));
        builder.addEventListeners(new DiscordListener());
        builder.enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES
        );
        jda = builder.build();

        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                new CommandDataImpl("trpg", "즐거운 모험이 시작됩니다.")
        );
        commands.addCommands(
                new CommandDataImpl("reset", "지금까지 했던 모든 모험들을 초기화합니다.")
        );
        commands.addCommands(
                new CommandDataImpl("status","모든 모험가들이 이세계에 오면 처음 하는 말")
        );
        commands.queue();
    }
}
