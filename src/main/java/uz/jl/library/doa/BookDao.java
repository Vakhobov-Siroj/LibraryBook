package uz.jl.library.doa;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import uz.jl.library.config.HibernateConfigurer;
import uz.jl.library.domains.Book;

import java.util.ArrayList;
import java.util.List;

public class BookDao implements Dao {

    static BookDao instance;

    public static BookDao getInstance() {

        if (instance == null) {

            instance = new BookDao();
        }
        return instance;
    }

    public void save(Book addedBook) {
        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();

        currentSession.merge(addedBook);
        currentSession.getTransaction().commit();
        currentSession.close();
    }

    public List<Book> getAllBook(boolean checker) {
        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();
        Query<Book> query;
        if (!checker) {

            query = currentSession.createQuery("select t from Book t where t.deleted=false and  t.isConform = false ", Book.class);
        } else {
            query = currentSession.createQuery("select t from Book t where t.deleted=false and t.isConform = true", Book.class);

        }


        List<Book> bookList = query.getResultList();
        currentSession.getTransaction().commit();
        currentSession.close();
        return bookList;
    }

    public Book getById(Long id) {
        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();
        Query<Book> query = currentSession.createQuery("select t from Book t where t.id=:id", Book.class);
        Book result = query.setParameter("id", id).getSingleResult();


        currentSession.getTransaction().commit();
        currentSession.close();
        return result;
    }

    public List<Book> getAnyWhere(String search) {
        List<Book> allBook = getAllBook(true);

        List<Book> bookList = new ArrayList<>();
        for (Book book : allBook) {
            if (book.getName().toLowerCase().contains(search) || book.getAuthor().toLowerCase().contains(search) || book.getDescription().toLowerCase().contains(search) || book.getGenre().getKey().toLowerCase().contains(search) || book.getLanguage().name().toLowerCase().contains(search)) {
                bookList.add(book);
            }
        }
        return bookList;
    }

    public List<Book> getRecords(int start, int total) {

        SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.getTransaction().begin();
        Query<Book> query;


        query = currentSession.createQuery("select t from Book t where t.deleted=false and  t.isConform = false  " + (start - 1) + "," + total, Book.class);
        query.setFirstResult(start);
        query.setMaxResults(total);

        List<Book> bookList = query.getResultList();
        currentSession.getTransaction().commit();
        currentSession.close();
        return bookList;
    }
//    @Query("SELECT r FROM RunEntity r WHERE r.testName = :testname AND r.testNumber = :testnumber AND r.environment = :environment AND r.source = :source")
//    public List<Book> findByNameNumberEnvironmentSource(
//            @Param("testname") String testname,
//            @Param("testnumber") int testnumber,
//            @Param("environment") String environment,
//            @Param("source") String source);
}
