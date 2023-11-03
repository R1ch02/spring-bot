package com.application.springbot.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "userDataTable")
@Data
public class User {

    @Id
    private Long chatId;

    @Override
    public String toString() {
        return
                "chatId=" + chatId + '\n' +
                "firstName='" + firstName + '\n' +
                "lastName='" + lastName + '\n' +
                "userName='" + userName + '\n' +
                "registeredAt=" + registeredAt;
    }

    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp registeredAt;


}
