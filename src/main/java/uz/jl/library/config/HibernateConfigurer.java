package uz.jl.library.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.sql.Update;
import org.reflections.Reflections;
import uz.jl.library.domains.Book;
import uz.jl.library.domains.Uploads;
import uz.jl.library.domains.User;


import java.util.HashMap;
import java.util.Map;

public class HibernateConfigurer {

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder =
                        new StandardServiceRegistryBuilder();

                Map<String, Object> settings = new HashMap<>();
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, "jdbc:postgresql://localhost:5432/servlet");
                settings.put(Environment.USER, "postgres");
                settings.put(Environment.PASS, "1");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.SHOW_SQL, true);
                settings.put(Environment.FORMAT_SQL, true);
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
//                settings.put(Environment.Conne)


                settings.put("hibernate.hikari.connectionTimeout", "20000");

                settings.put("hibernate.hikari.minimumIdle", "10");

                settings.put("hibernate.hikari.maximumPoolSize", "20");
                settings.put("hibernate.hikari.idleTimeout", "300000");

                registryBuilder.applySettings(settings);

                registry = registryBuilder.build();
                MetadataSources sources = new MetadataSources(registry).addAnnotatedClasses(User.class);
                sources.addAnnotatedClass(Uploads.class);
                sources.addAnnotatedClass(Book.class);


//                Reflections reflections = new Reflections("src/main/java/uz/jl/library/domains");
//                reflections.getTypesAnnotatedWith(Entity.class)
//                        .forEach(clazz -> sources.addAnnotatedClassName(clazz.getName()));


                    Metadata metadata = sources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }

                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);

        }
    }
}