package edu.gatech.adamlabstudio.sortinghat;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by somee on 2/5/17.
 */

public class ConversationEngine {

//    public static final int WHICH_HOUSE = 12;
    public static final int WHICH_HOUSE_NO = 5;
    public static final int WHICH_HOUSE_YES = 6;
    public static final int WHY_HOUSE = 7;
    public static final int HELP_MEMORY = 8;
//    public static final int RIGHT_THEN = 9;

    //Topics
    public static final int GRYFFINDOR = 1;
    public static final int RAVENCLAW = 2;
    public static final int SLYTHERIN = 3;
    public static final int HUFFLEPUFF = 4;
    public static final int YES = 10;
    public static final int NO = 11;

    static Map<String,Integer> house_names = new HashMap<>();
    static Map<String,Integer> yes_response = new HashMap<>();
    static Map<String,Integer> no_response = new HashMap<>();
    static Map<String,Integer> house_quals = new HashMap<>();
    static {
        house_names.put("gryffindor",GRYFFINDOR);
        house_names.put("griffin",GRYFFINDOR);
        house_names.put("hufflepuff",HUFFLEPUFF);
        house_names.put("huffle",HUFFLEPUFF);
        house_names.put("hustle",HUFFLEPUFF);
        house_names.put("ravenclaw",RAVENCLAW);
        house_names.put("raven",RAVENCLAW);
        house_names.put("claw",RAVENCLAW);
        house_names.put("slytherin",SLYTHERIN);
        house_names.put("slither",SLYTHERIN);
        house_names.put("slithering",SLYTHERIN);

        yes_response.put("yes",YES);
        yes_response.put("yeah",YES);
        yes_response.put("yep",YES);

        no_response.put("no",NO);
        no_response.put("don't",NO);
        no_response.put("i don't know",NO);
        no_response.put("dont",NO);
        no_response.put("nah",NO);
        no_response.put("nope",NO);

        house_quals.put("brave",GRYFFINDOR);
        house_quals.put("rescue",GRYFFINDOR);
        house_quals.put("save",GRYFFINDOR);
        house_quals.put("courage",GRYFFINDOR);
        house_quals.put("fearless",GRYFFINDOR);
        house_quals.put("no fear",GRYFFINDOR);
        house_quals.put("not afraid",GRYFFINDOR);

        house_quals.put("loyal",HUFFLEPUFF);
        house_quals.put("friend",HUFFLEPUFF);
        house_quals.put("hardworking",HUFFLEPUFF);
        house_quals.put("plants",HUFFLEPUFF);
        house_quals.put("kind",HUFFLEPUFF);
        house_quals.put("help",HUFFLEPUFF);

        house_quals.put("intellect",RAVENCLAW);
        house_quals.put("read",RAVENCLAW);
        house_quals.put("math",RAVENCLAW);
        house_quals.put("studious",RAVENCLAW);
        house_quals.put("intelligent",RAVENCLAW);
        house_quals.put("genius",RAVENCLAW);
        house_quals.put("smart",RAVENCLAW);
        house_quals.put("research",RAVENCLAW);
        house_quals.put("know-it-all",RAVENCLAW);
        house_quals.put("knowledgeable",RAVENCLAW);
        house_quals.put("topped",RAVENCLAW);

        house_quals.put("win",SLYTHERIN);
        house_quals.put("won",SLYTHERIN);
        house_quals.put("ambition",SLYTHERIN);
        house_quals.put("cunning",SLYTHERIN);
        house_quals.put("streetsmart",SLYTHERIN);
        house_quals.put("shrewd",SLYTHERIN);
        house_quals.put("resourceful",SLYTHERIN);
        house_quals.put("elite",SLYTHERIN);
    }

    public int getCategory(String result, UserSession session) {
        int category = -1;
        boolean is_yes = false;
        boolean is_no = false;
        boolean is_house = false;
        boolean is_qual = false;
        int house = 0;

        result = result.toLowerCase();
        String [] words = result.split("\\s");

        int replyNum = session.incrementReplyNum();

        if (replyNum == 1) {
            Log.v("SortingHat","reply no. 1");
            for (String word : words) {
                if (yes_response.containsKey(word)) {
                    Log.v("SortingHat","is_yes");
                    is_yes = true;
                } else if (no_response.containsKey(word)) {
                    Log.v("SortingHat","is_no");
                    is_no = true;
                } else if (house_names.containsKey(word)) {
                    house = house_names.get(word);
                    Log.v("SortingHat","is_house: "+house);
                }
            }
            if (is_yes) {
                if (house > 0) {
                    session.updateScores(house, 1);
                    Log.v("SortingHat", "returning WHY_HOUSE category");
                    return WHY_HOUSE;
                } else {
                    Log.v("SortingHat", "returning WHICH_HOUSE_YES category");
                    return WHICH_HOUSE_YES;
                }
            } else if (house>0) {
                session.updateScores(house, 1);
                Log.v("SortingHat", "returning WHY_HOUSE category");
                return WHY_HOUSE;
            } else if (is_no) {
                Log.v("SortingHat","returning HELP_MEMORY category for not knowing");
                return HELP_MEMORY;
            }
        } else if (replyNum > 1) {
            for (String word : words) {
                if (house_names.containsKey(word)) {
                    is_house = true;
                    int name = house_names.get(word);
                    session.updateScores(name,1);
                    Log.v("SortingHat","added score for house_name to: "+ name);
                }
                else if (house_quals.containsKey(word)) {
                    is_qual = true;
                    int name = house_quals.get(word);
                    session.updateScores(name,1);
                    Log.v("SortingHat","added score for house quality to: "+ name);
                }
            }
            Log.v("SortingHat","is_house: "+is_house);
            Log.v("SortingHat","is_qual: "+is_qual);
            if(is_house) {
                if (is_qual) {
                    int win_house = session.getMaxScoringHouse();
                    Log.v("SortingHat","returning winning house category: "+ win_house);
                    return win_house;
                } else {
                    Log.v("SortingHat","returning WHY_HOUSE category");
                    return WHY_HOUSE;
                }
            } else if (is_qual) {
                int win_house = session.getMaxScoringHouse();
                Log.v("SortingHat","returning winning house category: "+ win_house);
                return win_house;
            }
        } //else if (replyNum >= 4){
//            int win_house = session.getMaxScoringHouse();
//            Log.v("SortingHat","returning random winning house category: "+ win_house);
//            return win_house;
//        }

        Log.v("SortingHat","returning default category");
        return category;
    }
}
