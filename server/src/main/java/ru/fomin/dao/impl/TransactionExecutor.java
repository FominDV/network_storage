package ru.fomin.dao.impl;

import org.hibernate.Session;
import ru.fomin.factory.SessionFactory;

import java.util.function.Function;

/**
 * Class for opening hibernate session and executing transaction.
 */
public class TransactionExecutor {

    private Session openSessionAndTransaction() {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        return session;
    }

    private void closeSessionAndTransaction(Session session) {
        session.getTransaction().commit();
        session.close();
    }

    public <T> T executeTransaction(Function<Session, T> function) {
        Session session = openSessionAndTransaction();
        T result = function.apply(session);
        closeSessionAndTransaction(session);
        return result;
    }

}
