package com.codegym.demo.dao;

import com.codegym.demo.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDAO {
    private SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public void save(User user) {
        getCurrentSession().save(user);
    }

    @Transactional
    public User findById(Long id) {
        return getCurrentSession().get(User.class, id);
    }

    @Transactional
    public void update(User user) {
        getCurrentSession().update(user);
    }

    @Transactional
    public void delete(User user) {
        getCurrentSession().delete(user);
    }

    @Transactional
    public List<User> findAll() {
        return getCurrentSession().createQuery("from User", User.class).list();
    }
}
