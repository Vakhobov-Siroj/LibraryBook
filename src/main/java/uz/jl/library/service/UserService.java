package uz.jl.library.service;

import lombok.SneakyThrows;
import uz.jl.library.config.ApplicationContextHolder;
import uz.jl.library.config.Encoder;
import uz.jl.library.doa.Dao;
import uz.jl.library.doa.UserDao;
import uz.jl.library.domains.Book;
import uz.jl.library.domains.User;
import uz.jl.library.enums.Language;
import uz.jl.library.utils.SessionUser;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;

public class UserService implements Dao {
    private final UserDao userDao = ApplicationContextHolder.getBean(UserDao.class);
    public static UserService instance = ApplicationContextHolder.getBean(UserService.class);
    public static Encoder encoder = Encoder.instance;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/auth/login.jsp");
        dispatcher.forward(request, response);
    }

    public void signup(HttpServletRequest request, HttpServletResponse resp) {


        RequestDispatcher dispatcher = request.getRequestDispatcher("views/auth/signup.jsp");

        try {
            dispatcher.forward(request, resp);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void PostCreate(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String lastname = request.getParameter("lastname");
        String firstname = request.getParameter("firstname");
        String psswrd = request.getParameter("password");
        Integer age = Integer.valueOf(request.getParameter("age"));
        String password = encoder.encodePassword(psswrd);

        try {
            userDao.create(new User(encoder.encode(username), password, encoder.encode(email), encoder.encode(lastname), encoder.encode(firstname), age));

        } catch (Exception e) {
            request.setAttribute("email", email);
            request.setAttribute("username", username);
            request.setAttribute("lastname", lastname);
            request.setAttribute("firstname", firstname);
            request.setAttribute("password", password);
            request.setAttribute("age", age);
            signup(request, response);
        }

        HttpSession session = request.getSession();
        User user = new User(username, password, email, lastname, firstname, age);
        SessionUser sessionUser = new SessionUser(user, false);

        request.setAttribute("user", user);
        request.setAttribute("salom", "salomaa");
        session.setAttribute("sessionUser", sessionUser);
        request.getRequestDispatcher("views/MainMenu.jsp").forward(request, response);

    }


    public void PostLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = userDao.login(username, password);
        if (user == null) {
            request.setAttribute("notFound", true);
            request.setAttribute("username", username);
            request.setAttribute("password", password);
            login(request, response);
        } else {


            HttpSession session = request.getSession();

            SessionUser sessionUser = new SessionUser();
            sessionUser.setUsername(user.getUsername());
            sessionUser.setFirstname(user.getFirstname());
            sessionUser.setLastname(user.getLastname());
            sessionUser.setEmail(user.getEmail());
            sessionUser.setPassword(user.getPassword());
            sessionUser.setRole(user.getRole());
            sessionUser.setAge(user.getAge());

            session.setAttribute("sessionUser", sessionUser);


            response.sendRedirect("/main");
        }
    }

    @SneakyThrows
    public void MainMenu(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        SessionUser user = (SessionUser) session.getAttribute("sessionUser");
        SessionUser DisplayUser = new SessionUser();

        DisplayUser.setLastname(encoder.decode(user.getLastname()));
        DisplayUser.setFirstname(encoder.decode(user.getFirstname()));
        DisplayUser.setUsername(encoder.decode(user.getUsername()));
        DisplayUser.setEmail(encoder.decode(user.getEmail()));
        DisplayUser.setAge(user.getAge());


        request.setAttribute("user", DisplayUser);
//        request.setAttribute("img1", "/home/shohimardon/Pictures/images.jpeg");
        request.setAttribute("img2", "/home/shohimardon/Pictures/3Mcd61.webp");
//        request.setAttribute("img3", "/home/shohimardon/Pictures/images.png");

        Language[] languages = Language.values();
        Book.Genre[] genres = Book.Genre.values();

        session.setAttribute("sessionUser", user);
        session.setAttribute("languages", languages);
        session.setAttribute("genres", genres);

        RequestDispatcher dispatcher = request.getRequestDispatcher("views/MainMenu.jsp");
        dispatcher.forward(request, response);
    }

    public void Search(HttpServletRequest request, HttpServletResponse response) {






    }

    @SneakyThrows
    public void Logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.removeAttribute("sessionUser");
        response.sendRedirect("/");
    }

    /**
     * setting -> change Password only
     * settings -> update all user data without password
     */


    public void setting(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");

        String old = request.getParameter("password");
        String newPassword = request.getParameter("NPassword");
        String ConformPassword = request.getParameter("CPassword");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        if (!Objects.equals(username, sessionUser.getUsername()) && userDao.IsTakenUsername(username)) {
            request.setAttribute("isError", true);
            request.setAttribute("massage", "Bro this username already taken");
            return;
        }
        String lastname = request.getParameter("lastname");
        String firstname = request.getParameter("firstname");
        String parameter = request.getParameter("age");
        Integer age = 0;

        if (Objects.nonNull(parameter))
            age = Integer.valueOf(parameter);


        boolean b = Objects.nonNull(old) && Objects.nonNull(newPassword) && Objects.nonNull(ConformPassword);

        if (
                b && (!encoder.matchPassword(old, sessionUser.getPassword()) || !Objects.equals(newPassword, ConformPassword))) {


            setCookie(response, "ErrorMassageSetting", "1", 3);

            response.sendRedirect("/");
            return;
        }
        User user = userDao.login(encoder.decode(sessionUser.getUsername()), sessionUser.getPassword());
        if (b) {
            String newEncodePassword = encoder.encodePassword(newPassword);


            user.setPassword(newEncodePassword);
        }
        b = Objects.nonNull(email) && Objects.nonNull(firstname) && Objects.nonNull(lastname) && Objects.nonNull(username);
        if (b) {


            user.setEmail(encoder.encode(email));
            user.setFirstname(encoder.encode(firstname));
            user.setLastname(encoder.encode(lastname));
            user.setUsername(encoder.encode(username));
            user.setAge(age);
        }
        try {
            userDao.create(user);

            sessionUser = new SessionUser(user, true);
            session.setAttribute("sessionUser", sessionUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainMenu(request, response);
    }

    private static void setCookie(HttpServletResponse response, String nom, String valeur, int maxAge) throws IOException {
        Cookie cookie = new Cookie(nom, URLEncoder.encode(valeur, "UTF-8"));
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }


}
