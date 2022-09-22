package uz.jl.library.doa;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import uz.jl.library.config.HibernateConfigurer;
import uz.jl.library.domains.Uploads;

public class UploadsDao implements Dao {
    public void save(Uploads uploads) {
        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();

        currentSession.merge(uploads);
        currentSession.getTransaction().commit();
        currentSession.close();
    }
}
