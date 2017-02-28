package edu.gatech.adamlabstudio.sortinghat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by somee on 2/5/17.
 */

public class UserSession {

    private Map<Integer,Integer> scores = new HashMap<>();
    private int replyNum = 0;


    public void reset() {
        scores.clear();
        scores.put(ConversationEngine.GRYFFINDOR,0);
        scores.put(ConversationEngine.RAVENCLAW,0);
        scores.put(ConversationEngine.SLYTHERIN,0);
        scores.put(ConversationEngine.HUFFLEPUFF,0);
        replyNum = 0;
    }

    public int incrementReplyNum() {
        return ++replyNum;
    }

    public void updateScores(int house, int score2add) {
        int cur_score = scores.get(house);
        scores.put(house,cur_score+score2add);
    }

    public int getMaxScoringHouse() {
        int maxValueInMap=(Collections.max(scores.values()));  // This will return max value in the Hashmap
        for (Map.Entry<Integer, Integer> entry : scores.entrySet()) {  // Itrate through hashmap
            if (entry.getValue()==maxValueInMap) {
                return entry.getKey();    // Print the key with max value
            }
        }
        Random rand = new Random();
        int random_win = rand.nextInt(4)+1;
        return random_win;
    }
}
