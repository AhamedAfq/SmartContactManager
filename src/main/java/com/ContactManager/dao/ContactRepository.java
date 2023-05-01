package com.ContactManager.dao;

import com.ContactManager.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

//    @Query("from Contact as c where c.user.id =:userId")
//    public List<Contact> findContactsByUser(@Param("userId")int userId);

    @Query("from Contact as c where c.user.id =:userId")
//    Current page and Contact per page are the params need to be passed to  Pageable object
    public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pageable);
}
