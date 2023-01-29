package com.driver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WhatsappRepository {

    HashMap<String,User> userHashMap = new HashMap<>();
    HashMap<Group,List<User>> groupListHashMap = new HashMap<>();

    HashMap<Group,List<SenderMessage>> senderMessage = new HashMap<>();

    ArrayList<String> groupnames = new ArrayList<>();

    ArrayList<Message> messages = new ArrayList<>();
    public  String createUser(String name,String mobile)throws Exception{
        if(userHashMap.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        User user = new User(name,mobile);
        userHashMap.put(mobile,user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> user){
        Group group = new Group();
        if(user.size()==2){
            group.setName(user.get(1).getName());
            group.setNumberOfParticipants(2);
            groupnames.add(group.getName());
            groupListHashMap.put(group,user);
        }
        else if(user.size()>2){

            int groupNo = 0;
            for(Group group1 :groupListHashMap.keySet()){
                if(group1.getNumberOfParticipants()>2)
                    groupNo++;
            }
            group.setName("Group "+groupNo);
            group.setNumberOfParticipants(user.size());
            groupnames.add(group.getName());
            groupListHashMap.put(group,user);
        }
        return group;
    }



    public int CreateMessage(String content){
        int id = messages.size()+1;
        Message message = new Message();
        message.setId(id);
        message.setContent(content);
        message.setTimestamp(new Date());
        messages.add(message);
        return id;
    }


    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupListHashMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!groupListHashMap.get(group).contains(sender)){
            throw new Exception("You are not allowed to send message");
        }
        SenderMessage senderMessage1 = new SenderMessage();
        senderMessage1.setMessage(message);
        senderMessage1.setUser(sender);
        if(senderMessage.containsKey(group)){
            senderMessage.get(group).add(senderMessage1);
        }
        else{
            ArrayList<SenderMessage> list = new ArrayList<>();
            list.add(senderMessage1);
            senderMessage.put(group,list);
        }
        if(!messages.contains(message)){
            messages.add(message);
        }
        return senderMessage.get(group).size();
    }


    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if (!groupListHashMap.containsKey(group)) {

            throw new Exception("Group does not exist");
        }
        if(groupListHashMap.get(group).get(0) != approver){
            throw new Exception("Approver does not have rights");
        }

        List<User> userList = groupListHashMap.get(group);
        if(!userList.contains(user)){
            throw new Exception("User is not a participant");
        }
        userList.remove(user);
        userList.add(0,user);
        groupListHashMap.put(group,userList);
        return "SUCCESS";
    }


    public int removeUser(User user) throws Exception{

        Group group1 = null;
        boolean isExists = false;
        for(Group group :groupListHashMap.keySet()){
            List<User> userList = groupListHashMap.get(group);
            if(userList.contains(user)){
                if(userList.get(0)==user){
                    throw new Exception("Cannot remove admin");
                }
                else  {
                    isExists = true;
                    group1 = group;
                    break;
                }
            }

        }
        if(!isExists){
            throw new Exception("User not found");
        }
        List<User> userList = groupListHashMap.get(group1);
        userList.remove(user);
        group1.setNumberOfParticipants(userList.size());
        groupListHashMap.put(group1,userList);

        int groupMessages = 0;
        if(senderMessage.containsKey(group1)){
            List<SenderMessage> senderMessagesList = senderMessage.get(group1);
            for(SenderMessage senderMessage1 :senderMessagesList){
                if(senderMessage1.getUser() == user){
                    senderMessagesList.remove(senderMessage1);
                    messages.remove(senderMessage1.getMessage());
                }
            }
            groupMessages = senderMessagesList.size();
        }
        return userList.size()+groupMessages+messages.size();

    }

    public String findMessage(Date start, Date end, int K) throws Exception{
        int numberOfMessages = 0;
        for(Message message:messages){
            if(message.getTimestamp().compareTo(start)>0 && message.getTimestamp().compareTo(end)<0){
                numberOfMessages++;
            }
        }
        if(numberOfMessages<K){
            throw new Exception("K is greater than the number of messages");
        }
        numberOfMessages-=K;
        int ref = 0;
        String message1  = "";
        for(Message message:messages){
            if(message.getTimestamp().compareTo(start)>0 && message.getTimestamp().compareTo(end)<0){
                ref++;
                if(ref == numberOfMessages)
                {
                    message1 = message.getContent();
                    break;
                }
            }
        }
        return message1;
    }

}
