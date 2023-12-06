package events;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.RowFilter.Entry;

import org.json.*;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Botevents extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Nbr of servers: " + event.getGuildTotalCount());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        System.out.println(message);

        if (message.equalsIgnoreCase("!status")) {
            //event.getChannel().sendMessage(test()).queue();
            //System.out.println("[INFO] - Printing data from Advent of code");
            printer(event, parse(getData()));
        }

        if (message.equals("Aye")) {
            //System.out.println(parse(test()));
            //printer(event, parse(getData()));
            event.getChannel().sendMessage("Ouuuuuu").queue();

        }

        if(message.equals("!members")) {
            ArrayList<JSONObject> objectList = parse(getData());

            String s = "";
            int i = 1;
            for(JSONObject o : objectList) {
                s = s + "Member " + i +": " + o.getString("name") + "\n";
                System.out.println(s);
                i++;
            }
            event.getChannel().sendMessage(s).queue();
        }

        if(message.startsWith("!info")) {
            //System.out.println("Test: user" + message.trim().split("!info")[1]);
            printInfo(event, parse(getData()), message.trim().split("!info")[1]);
        }
    }



    public String getData() {
        try {
            CookieHandler.setDefault(new CookieManager());

            HttpCookie sessionCookie = new HttpCookie("session",
                    "53616c7465645f5fa3de6a1f5396ae2add04d0507074b3eaca8279be88bb8092b7279b9f7ea9b5994fd24e74d9f33e6aaa54f5b7eadd6f2f390eb81d40cddbd2");
            sessionCookie.setPath("/");
            sessionCookie.setVersion(0);

            ((CookieManager) CookieHandler.getDefault()).getCookieStore().add(new URI("https://adventofcode.com"),
                    sessionCookie);

            HttpClient client = HttpClient.newBuilder()
                    .cookieHandler(CookieHandler.getDefault())
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("Your leaderboard URL")) // ---------------------------------- EDIT HERE ----------------------------------
                    .GET().build();

            String data = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
            return data;

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return "Error";
        }

    }

    private void printInfo(MessageReceivedEvent event, ArrayList<JSONObject> memberList, String member) {
        JSONObject memberObject = null;
        memberList.sort((o1,o2) -> Integer.compare(o2.getInt("local_score"), o1.getInt("local_score")));
        Boolean found = false;
        for(JSONObject o: memberList) { 
            if(o.getString("name").trim().equals(member.trim())) {
                memberObject = o;
                found = true;
            }
        }
        if(!found) {
            event.getChannel().sendMessage("User not found").queue();
            return;
        }
        String content = "";
        if(memberObject != null) {
            int place = memberList.indexOf(memberObject)+1;
            int localScore = memberObject.getInt("local_score");
            int stars = memberObject.getInt("stars");

            content = "\n**--- " + memberObject.getString("name") + " ---**" + "\n\n**Place:** " + place + "\n**Score:** " + localScore + "\n**Stars:** " + stars;

            JSONObject completionDayLevel = memberObject.getJSONObject("completion_day_level");
            TreeMap<Integer, JSONObject> days = new TreeMap<>();;
            int i = 1;
            for(String key : completionDayLevel.keySet()) {
                days.put(i,completionDayLevel.getJSONObject(key));
                i++;
            }
            
            for(Map.Entry<Integer, JSONObject> entry : days.entrySet()) {
                String starString = "\n**Day** " + entry.getKey() + "\n stars: * ";
                System.out.println("har part 1 för dag: " + entry.getKey());
                JSONObject day1 = entry.getValue().getJSONObject("1");
                Long time1 = day1.getLong("get_star_ts");
                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(time1), ZoneId.systemDefault());
                System.out.println(dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));

                String partTime = " | Part 1 completed " + dateTime.format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss"));
                if(entry.getValue().has("2")) {
                    System.out.println("har part 2 för dag: " + entry.getKey());
                    JSONObject day2 = entry.getValue().getJSONObject("2");
                    Long time2 = day2.getLong("get_star_ts");
                    dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(time2), ZoneId.systemDefault());
                    partTime = partTime + " | Part 2 completed " + dateTime.format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss"));
                    starString = starString + "*";
                }
                content = content + starString + partTime + "\n";
            }


            event.getChannel().sendMessage(content).queue();

        }
    }

    private void printer(MessageReceivedEvent event, ArrayList<JSONObject> memberList) {
        
        memberList.sort((o1,o2) -> Integer.compare(o2.getInt("local_score"), o1.getInt("local_score")));
        int k = 1;
        String content = "";
        for(JSONObject o: memberList) {
            if(k == 101) {
                break;
            }
            try {
                if(k % 10 == 0) {
                    event.getChannel().sendMessage(content).queue();
                    content = "";
                }

                double days = o.getInt("stars");
                
                String completion = "";
                if(days%2 == 0) {
                    completion = "Day "+ (int) days/2 + ", both part 1 & 2 complete.";
                }else{
                    completion = "Day "+ (int) ((days/2)+1) + ", part 1 complete.";
                }
                
                content = content + "\n**Place:** " + k++ + "\n **Member name:** " + o.getString("name")+ "\n **Score:** " + o.getInt("local_score") + "\n **Stars:** " + o.getInt("stars") + "\n **Status** " + completion + "\n *type !info "+o.getString("name") + " for more info.*"+"\n------------------------------------------------------";
            } catch (JSONException e) {

            }
            System.out.println("[INFO] Building string...");
        }
        event.getChannel().sendMessage(content).queue();
    }



    private ArrayList<JSONObject> parse(String s) {
        JSONObject json = new JSONObject(s);
        Set<String> jsonSet = json.getJSONObject("members").keySet();
        ArrayList<JSONObject> memberList = new ArrayList<>();

        for (String key : jsonSet) {
            memberList.add(json.getJSONObject("members").getJSONObject(key));
        }
        return memberList;
    }

}
