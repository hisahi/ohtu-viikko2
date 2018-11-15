package ohtu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.apache.http.client.fluent.Request;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String studentNr = null;
        if (args.length>0) {
            studentNr = args[0];
        } else {
            System.out.println("Opiskelijanumero?");
            studentNr = sc.nextLine();
            System.out.println();
        }

        String url = "https://studies.cs.helsinki.fi/courses/students/"+studentNr+"/submissions";
        String bodyText = Request.Get(url).execute().returnContent().asString();
        
        String coursesUrl = "https://studies.cs.helsinki.fi/courses/courseinfo";
        String coursesText = Request.Get(coursesUrl).execute().returnContent().asString();

        System.out.println("Opiskelijanumero " + studentNr);
        System.out.println();

        Gson mapper = new Gson();
        Submission[] subs = mapper.fromJson(bodyText, Submission[].class);
        Course[] courses = mapper.fromJson(coursesText, Course[].class);
        
        JsonParser parser = new JsonParser();
        
        for (Course c: courses) {
            boolean courseHeader = false;
            int kurssiTehty = 0;
            int kurssiYht = 0;
            int kurssiTunnit = 0;
            
            for (Submission s: subs) {
                if (s.getCourse().equals(c.getName())) {
                    if (!courseHeader) {
                        System.out.println(c.getFullName() + " " + c.getTerm() + " " + c.getYear());
                        System.out.println(); 
                        courseHeader = true;
                    }
                    
                    System.out.println("viikko " + s.getWeek() + ":");
                    int tehty = s.getExercises().size();
                    int yht = c.getExercises().get(s.getWeek());
                    int tunnit = s.getHours();
                    
                    System.out.println(" tehtyjä tehtäviä " + tehty + "/" + yht
                                        + " aikaa kului " + tunnit + " tehdyt tehtävät: " + 
                                        String.join(", ", s.getExercises().stream().map(Object::toString).collect(Collectors.toList())));
                    
                    kurssiTehty += tehty;
                    kurssiYht += yht;
                    kurssiTunnit += tunnit;
                }
            }
            
            if (courseHeader) {
                System.out.println();
                System.out.println("yhteensä: " + kurssiTehty + "/" + kurssiYht + " tehtävää " + kurssiTunnit + " tuntia");
                System.out.println();
                
                String courseStatsUrl = "https://studies.cs.helsinki.fi/courses/" + c.getName() + "/stats";
                String courseStatsText = Request.Get(courseStatsUrl).execute().returnContent().asString();
                JsonObject parsittuData = parser.parse(courseStatsText).getAsJsonObject();
                
                int yhtPalautukset = 0;
                int yhtPalautusTeht = 0;
                int yhtPalautusTunnit = 0;
                
                for (String k: parsittuData.keySet()) {
                    JsonObject o = parsittuData.getAsJsonObject(k);
                    yhtPalautukset += o.get("students").getAsInt();
                    yhtPalautusTeht += o.get("exercise_total").getAsInt();
                    yhtPalautusTunnit += o.get("hour_total").getAsInt();
                }
                
                System.out.println("kurssilla yhteensä " + yhtPalautukset + " palautusta, palautettuja tehtäviä " + yhtPalautusTeht + " kpl, aikaa käytetty yhteensä " + yhtPalautusTunnit + " tuntia");
                
                System.out.println();
            }
        }
    }
}
