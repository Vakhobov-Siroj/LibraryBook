package uz.jl.library.doa;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import uz.jl.library.config.Encoder;
import uz.jl.library.config.HibernateConfigurer;
import uz.jl.library.domains.User;

import java.util.Objects;

public class UserDao implements Dao {

    Encoder encoder = Encoder.instance;
    public static UserDao instance = getInstance();

    public static UserDao getInstance() {

        if (instance == null) {
            instance = new UserDao();
        }
        return instance;

    }


    public User login(String username, String password) {

        username = encoder.encode(username);
        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();
        Query<User> query = currentSession.createQuery("select t from User t where t.username = :username ", User.class);


        User user = query.setParameter("username", username).getSingleResultOrNull();
        currentSession.getTransaction().commit();
        currentSession.close();
        if (!Objects.isNull(user) && !user.getPassword().equals(password) && !encoder.matchPassword(password, user.getPassword())) {

            return null;
        }
        return user;
    }

    public void create(User user) {
        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();

        currentSession.merge(user);
        currentSession.getTransaction().commit();
        currentSession.close();
    }

    public void delete(User user) {
        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();

        currentSession.delete(user);
        currentSession.getTransaction().commit();
        currentSession.close();
    }


    public boolean IsTakenUsername(String username) {

        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();
        Query<User> query = currentSession.createQuery("select t from User t where t.username = :username ", User.class);


        User user = query.setParameter("username", username).getSingleResultOrNull();

        currentSession.getTransaction().commit();

        if (Objects.isNull(user)) {
            return false;
        }
        return true;
    }
}
