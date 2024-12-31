package com.supcom.cot.smartirrigation.entities;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.json.bind.annotation.JsonbVisibility;
import com.supcom.cot.smartirrigation.util.Identity;

import java.io.Serializable;
import java.util.Objects;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class User implements Serializable, Identity { // User entity for database
    @Id
    private String mail; //email address
    @Column
    private String userName;
    @Column
    private String password;
    @Column
    private Long permissionLevel;


    public User() {
    }

    public User(String mail, String userName, String password, Long permissionLevel) {
        this.mail=mail;
        this.userName = userName;
        this.password = password;
        this.permissionLevel=permissionLevel;
    }



    public String getmail() {
        return mail;
    }
    public String getuserName() {
        return userName;
    }
    public String getpassword() {
        return password;
    }
    public Long getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(Long permissionLevel) {
        this.permissionLevel = permissionLevel;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        User user = (User) o;
        return Objects.equals(mail, user.mail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mail);
    }
    @Override
    public String getName() {
        return getmail();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + mail + '\'' +
                ", fullname=" + userName +

                '}';
    }

}