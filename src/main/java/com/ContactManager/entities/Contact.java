package com.ContactManager.entities;

import javax.persistence.*;

@Entity
@Table(name = "CONTACT")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int contactId;
    private String firstName;
    private String secondName;
    private String work;
    private String email;
    private String phone;
    private String image;
    @Column(length = 1000)
    private String description;

    @ManyToOne
    private User user;


    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUsers() {
        return user;
    }

    public void setUsers(User users) {
        this.user = users;
    }

//    @Override
//    public String toString() {
//        return "Contact{" +
//                "customerId=" + customerId +
//                ", firstName='" + firstName + '\'' +
//                ", secondName='" + secondName + '\'' +
//                ", work='" + work + '\'' +
//                ", email='" + email + '\'' +
//                ", phone='" + phone + '\'' +
//                ", image='" + image + '\'' +
//                ", description='" + description + '\'' +
//                ", user=" + user +
//                '}';
//    }
}
