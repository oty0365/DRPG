package org.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class Main {
    public static JDA jda;
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] ignoredArgs) {
        DiscordListener.data = FileUtils.loadData();
        DiscordListener.script = FileUtils.loadScript();
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
                new CommandDataImpl("trpg", "즐거운 모험이 시작됩니다."),
                new CommandDataImpl("reset", "지금까지 했던 모든 모험들을 초기화합니다."),
                new CommandDataImpl("status","모든 모험가들이 이세계에 오면 처음 하는 말")
                        .addOption(OptionType.USER, "유저명", "조금은 숙련된 모험가들이 이세계에서 가장 많이 하는 말", false)
        );
        commands.queue();
    }

    public static boolean isBotOwner(String id) {
        return id.equals("682471100726509589") || id.equals("419137051670347777");
    }
}
