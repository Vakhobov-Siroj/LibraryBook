package uz.jl.library.config;


import uz.jl.library.doa.BookDao;
import uz.jl.library.doa.Dao;
import uz.jl.library.doa.UserDao;
import uz.jl.library.domains.Book;
import uz.jl.library.service.UserService;


import java.util.List;

public class ApplicationContextHolder {
    public static Book editedBook;
    public static String searchBook;
    public static List<Book> books;
    public static int MaPX;

    public static <T> T getBean(Class<? extends Dao> clazz) {
        return switch (clazz.getSimpleName()) {
            case "UserDao" -> (T) UserDao.getInstance();
            case "BookDao" -> (T) BookDao.getInstance();
            case "UserService" -> (T) UserService.getInstance();


            default -> throw new BeanNotFoundException("Bean not found");
        };
    }

    public static class BeanNotFoundException extends RuntimeException {
        public BeanNotFoundException(String message) {
            super(message);
        }
    }

    public static Book book = new Book();
}

