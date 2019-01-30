package com.csye6225.spring2019.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DAO {

    private static final Logger log = Logger.getAnonymousLogger();

    private static final ThreadLocal sessionThread = new ThreadLocal();
    private static final SessionFactory sessionFactory = new Configuration().configure("src/main/resources/hibernate.cfg.xml").buildSessionFactory();

    protected DAO() {
    }

    public static Session getSession()
    {
        System.out.println("into session 1");
        Session session = (Session) DAO.sessionThread.get();

        if (session == null)
        {

            session = sessionFactory.openSession();
            System.out.print("into session is null"+ session.toString());
            DAO.sessionThread.set(session);
        }
        return session;
    }

    protected void begin() {
        System.out.println("in begin");
        getSession().beginTransaction();
    }

    protected void commit() {
        System.out.println("in commit");
        getSession().getTransaction().commit();
    }

    protected void rollback() {
        try {
            System.out.println("in rollback");
            getSession().getTransaction().rollback();
        } catch (HibernateException e) {
            log.log(Level.WARNING, "Cannot rollback", e);
        }
        try {
            getSession().close();
        } catch (HibernateException e) {
            log.log(Level.WARNING, "Cannot close", e);
        }
        DAO.sessionThread.set(null);
    }

    public static void close() {
        getSession().close();
        DAO.sessionThread.set(null);
    }
}