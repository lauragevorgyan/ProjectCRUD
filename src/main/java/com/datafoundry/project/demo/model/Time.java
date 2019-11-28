package com.datafoundry.project.demo.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

    @Entity
    @Table(name = "Time", indexes = @Index(columnList = "userId"))
    @Getter
    @Setter
    @EqualsAndHashCode(of = {"userId"}) @ToString
    public class Time implements Serializable {

        // -- Time class used for save userId and time where user was log in

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @NotNull
        private String userId;

        @NotNull
        private String time;


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
