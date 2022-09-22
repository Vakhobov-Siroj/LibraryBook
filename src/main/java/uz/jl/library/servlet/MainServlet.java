package uz.jl.library.servlet;


import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import uz.jl.library.config.ApplicationContextHolder;
import uz.jl.library.config.HibernateConfigurer;
import uz.jl.library.domains.SessionUser;
import uz.jl.library.service.BookService;
import uz.jl.library.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@WebServlet(value = "/")
@MultipartConfig(


        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class MainServlet extends HttpServlet {
    BookService bookService = BookService.instance;
    SessionFactory sessionFactory = HibernateConfigurer.getSessionFactory();
    static UserService userService = ApplicationContextHolder.getBean(UserService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {


            HttpSession session = req.getSession();
            SessionUser user = (SessionUser) session.getAttribute("sessionUser");
            String uri = req.getRequestURI();
            List<Integer> list = new ArrayList<>();
            List<Integer> listw = new ArrayList<>();
            listw.addAll(list);
            if (Objects.nonNull(user)) {
                if (uri.contains("/mergeImage/")) {
                    bookService.mergeImage(uri.split("[+]")[1], req, resp);
                    return;
                } else if (uri.contains("/download")) {
                    bookService.Download(req, Long.valueOf(uri.split("/")[2]), resp);
                    return;
                } else if (Objects.nonNull(req.getParameter("str"))) {
                    bookService.showList(req, req.getParameter("str"), resp);
                    return;
                } else if (uri.contains("/conform/") || uri.contains("/delete/")) {
                    bookService.ConformOrDelete(req, Long.valueOf(uri.split("/")[2]), resp);
                    return;
                }


                switch (uri) {
                    case "/search" -> bookService.Search(req, resp);
                    case "/logout" -> userService.Logout(req, resp);
                    case "/setting" -> userService.setting(req, resp);
                    case "/showList", "/conform" -> bookService.showList(req, "0", resp);
                    case "/selectPhoto" -> bookService.selectPhoto(req, resp);

                    default -> userService.MainMenu(req, resp);

                }
            } else {
                if ("/signup".equals(uri)) {
                    userService.signup(req, resp);
                } else {
                    userService.login(req, resp);
                }
            }
        } catch (Exception e) {
            resp.sendRedirect("/main");
        }
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {


            SessionUser user = (SessionUser) req.getSession().getAttribute("sessionUser");

            String uri = req.getRequestURI();
            if (Objects.nonNull(user)) {
                switch (uri) {
                    case "/login" -> userService.PostLogin(req, resp);
                    case "/signup" -> userService.PostCreate(req, resp);
                    case "/book/add" -> bookService.AddBook(req, resp);
                    case "/showList" -> bookService.showList(req, uri,resp);
                    case "/setting" -> userService.setting(req, resp);
                    case "/main" -> userService.MainMenu(req, resp);
                    case "/editBook" -> bookService.editBook(req, resp);
                    case "/download" -> bookService.Download(req, Long.valueOf(uri.split("/")[1]), resp);
                    case "/searchImg" -> bookService.downloadFile(req, resp);

                }
            } else {
                switch (uri) {

                    case "/login" -> userService.PostLogin(req, resp);
                    case "/signup" -> userService.PostCreate(req, resp);
                    default -> userService.login(req, resp);
                }

            }
        } catch (Exception e) {
            throw new Exception();
        }

    }
}
