package com.er;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        solution(new String());
    }

    public static int solution(String S) {
        // write your code in Java SE 8
        S = "Tue 00:00-03:00\nTue 20:00-24:00\nSun 10:00-20:00\nFri 05:00-10:00\nFri 16:30-23:50\nSat 10:00-24:00\nSun 01:00-04:00\nSat 02:00-06:00\nTue 03:30-18:15\nTue 19:00-20:00\nWed 04:25-15:14\nWed 15:14-22:40\nThu 00:00-23:59\nMon 05:00-13:00\nMon 15:00-21:00";

        String lines[] = S.split("\\n");

        List<Meeting> meetings = new ArrayList<>();
        for(String eachLine: lines){
            String cleanedMeeting = eachLine.replaceAll("\"", "");
            System.out.println(cleanedMeeting+"\n");

            Integer day = 0;
            String[] meetingParts = cleanedMeeting.split(" ");
            String strDay = meetingParts[0];
            switch (strDay){
                case "Mon":
                    day = 1;
                    break;
                case "Tue":
                    day = 2;
                    break;
                case "Wed":
                    day = 3;
                    break;
                case "Thu":
                    day = 4;
                    break;
                case "Fri":
                    day = 5;
                    break;
                case "Sat":
                    day = 6;
                    break;
                case "Sun":
                    day = 7;
                    break;
                default:
                    break;
            }

            Meeting meeting = new Meeting(day, meetingParts[1].split("-")[0], meetingParts[1].split("-")[1]);
            meetings.add(meeting);
        }

        Collections.sort(meetings);

        return calculateLongerSleepingTime(meetings);
    }

    public static Integer calculateLongerSleepingTime(List<Meeting> meetings){
        Integer longestTime = 0;

        //Calculate time from week start to first meeting
        Meeting firstMeeting = meetings.get(0);
        String[] firstMeetingHourParts = firstMeeting.getFrom().split("\\:");
        Integer firstMeetingHours = Integer.parseInt(firstMeetingHourParts[0]);
        Integer firstMeetingMinutes = Integer.parseInt(firstMeetingHourParts[1]);
        Integer minutesDaySkipped = (firstMeeting.getDay() > 1) ? firstMeeting.getDay()-1 * 60 : 0;
        Integer minutesTillFirstMeeting = firstMeetingHours * 60 + firstMeetingMinutes + minutesDaySkipped;
        if(minutesTillFirstMeeting > longestTime) longestTime = minutesTillFirstMeeting;

        //Calculate time between meetings
        for(int i = 0; i < meetings.size()-1; i++){
            Meeting previousMeeting = meetings.get(i);
            Meeting nextMeeting = meetings.get(i+1);

            String[] previousHourParts = previousMeeting.getTo().split("\\:");
            Integer previousHours = Integer.parseInt(previousHourParts[0]);
            Integer previousMinutes = Integer.parseInt(previousHourParts[1]);

            String[] nextHourParts = nextMeeting.getFrom().split("\\:");
            Integer nextHours = Integer.parseInt(nextHourParts[0]);
            Integer nextMinutes = Integer.parseInt(nextHourParts[1]);

            //If both meetings are on same day, simple substraction
            if(previousMeeting.getDay() == nextMeeting.getDay()){
                Integer diffHours = nextHours - previousHours;
                Integer diffMinutes = nextMinutes - previousMinutes;

                Integer totalMinutes = diffHours * 60 + diffMinutes;
                if(totalMinutes > longestTime){
                    longestTime = totalMinutes;
                }
            } else {
                //Calculate time from previous meeting until end day
                Integer minutesFromLastMeeting = (((24 - previousHours) * 60) - previousMinutes);
                //Calculate time from start of day until first meeting of day
                Integer minutesUntilFirstMeetingOfDay = nextHours * 60 + nextMinutes;

                Integer totalMinutes = minutesFromLastMeeting + minutesUntilFirstMeetingOfDay;
                if(totalMinutes > longestTime){
                    longestTime = totalMinutes;
                }
            }
        }

        //Calculate time from last meeting to week ending
        Meeting lastMeeting = meetings.get(meetings.size()-1);
        String[] lastMeetingHourParts = lastMeeting.getTo().split("\\:");
        Integer lastMeetingHours = Integer.parseInt(lastMeetingHourParts[0]);
        Integer lastMeetingMinutes = Integer.parseInt(lastMeetingHourParts[1]);
        Integer minutesWithoutMeetings = (lastMeeting.getDay() < 7) ? 7 - lastMeeting.getDay() * 60 : 0;
        Integer minutesFromLastMeeting = (((24 - lastMeetingHours) * 60) - lastMeetingMinutes) + minutesWithoutMeetings;
        if(minutesFromLastMeeting > longestTime) longestTime = minutesFromLastMeeting;

        return longestTime;
    }

    public static class Meeting implements Comparable<Meeting>{
        private Integer day;
        private String from;
        private String to;

        public Meeting(Integer day, String from, String to){
            this.day = day;
            this.from = from;
            this.to = to;
        }

        @Override
        public int compareTo(Meeting o) {
            if(this.getDay() != o.getDay()){
                return this.getDay() - o.getDay();
            } else{
                String[] hourParts = this.getFrom().split("\\:");
                Integer hours = Integer.parseInt(hourParts[0]);
                Integer minutes = Integer.parseInt(hourParts[1]);
                LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hours, minutes));

                String[] otherMeetingHourParts = o.getFrom().split("\\:");
                Integer otherHours = Integer.parseInt(otherMeetingHourParts[0]);
                Integer otherMinutes = Integer.parseInt(otherMeetingHourParts[1]);
                LocalDateTime otherLocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(otherHours, otherMinutes));

                if(hours != otherHours){
                    return hours - otherHours;
                } else return minutes - otherMinutes;
            }
        }

        public Integer getDay() {
            return day;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }

}
