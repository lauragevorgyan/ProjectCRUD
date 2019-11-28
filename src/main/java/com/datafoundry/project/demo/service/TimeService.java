package com.datafoundry.project.demo.service;

import com.datafoundry.project.demo.repo.TimeRepository;
import com.datafoundry.project.demo.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TimeService{

    // -- Class Adapter

    @Autowired
    private TimeRepository timeRepo;

    public List<String> findAll(String userId) {
        List<Time> times = new ArrayList<>();
        List<String> usertime = new ArrayList<>();
        timeRepo.findAll().forEach(times::add);
        for(int i=0;i<times.size();i++){
            if(times.get(i).getUserId().equals(userId)){
                usertime.add(times.get(i).getTime());
            }
        }
        return usertime;
    }

    public Time save(String userId,String timee) throws Exception {
        Time time = new Time();
        time.setTime(timee);
        time.setUserId(userId);
        System.out.println("here");
        return timeRepo.save(time);
    }
}
