package uz.jl.library.service;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.darkprograms.speech.translator.GoogleTranslate;
import com.itextpdf.text.pdf.PdfReader;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import uz.jl.library.config.ApplicationContextHolder;
import uz.jl.library.doa.BookDao;
import uz.jl.library.doa.UploadsDao;
import uz.jl.library.domains.Book;
import uz.jl.library.domains.Uploads;

import static uz.jl.library.config.ApplicationContextHolder.MaPX;
import static uz.jl.library.config.ApplicationContextHolder.searchBook;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,
        maxFileSize = 1024 * 1024 * 50,
        maxRequestSize = 1024 * 1024 * 100)

public class BookService {
    public static BookService instance = getInstance();
    static BookDao bookDao = ApplicationContextHolder.getBean(BookDao.class);
    UploadsDao uploadsDao = new UploadsDao();

    private static BookService getInstance() {
        if (instance == null) {
            return new BookService();
        }
        return instance;
    }

    @SneakyThrows
    public void AddBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String author = request.getParameter("author");
        String genre = request.getParameter("genre");
        String language = request.getParameter("language");
        String pageCount = request.getParameter("pageCount");
        String description = request.getParameter("description");
        Part part = request.getPart("cover");
        Part file = request.getPart("file");


        long size = 0;

        Uploads image;


        Uploads book = uploads(file.getSize(), "/home/shohimardon/Documents/library/file/", file);

        Integer pages = calculatePage(pageCount, book.getPath());
        language = getLanguage(language, book.getPath());
        Book addedBook = new Book(name, description, author, Book.Genre.valueOf(genre), Language.convertor(language), pages, 0, null, book);
        if (part != null) {
            size = part.getSize();

            image = uploads(size, "/home/shohimardon/Documents/library/file/", part);
            addedBook.setCover(image);

//

            bookDao.save(addedBook);

            response.sendRedirect("/");
        } else {

//            getImage(size, book.getOriginalName(), book.getPath());
//            image = getImage(0, "Haryy Potter", "/home/shohimardon/Documents/library/file/harry_potter_annd_the_sorcerers_stone (3).pdf");
//            App.getImge();
            ApplicationContextHolder.book = addedBook;

//                URL url = new URL("http://localhost:9091/image/name=" + book.getOriginalName().split(".pdf")[0]);
//                URLConnection connection = url.openConnection();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

//            getImage(0, book.getOriginalName().split(".pdf")[0] + "book", book.getPath());
//                String d;
//                while ((d = reader.readLine()) != null) {
//                    System.out.println(d);
//                }

//            InputStream inputStream = connection.getInputStream();

//            request.getRequestDispatcher("views/selectImage.jsp").forward(request, response);
            response.sendRedirect("/selectPhoto");
        }

    }

    @SneakyThrows
    public Uploads getImage(long size, String fileName, String path) {
        File file = new File("/home/shohimardon/Desktop/images/");
        for (File listFile : file.listFiles()) {
            listFile.delete();
        }
        DownloadImage(fileName);
        getFirsPageAsImage(path);

        return null;
    }

    public Integer calculatePage(String pageCount, String fullAddress) {
        if (Objects.nonNull(pageCount) && !(pageCount.trim().equals(""))) {
            return Integer.valueOf(pageCount);
        }
        int totalPages = 0;
        try {


            PdfReader reader = new PdfReader(fullAddress);
            totalPages = reader.getNumberOfPages();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalPages;
    }

    public String getLanguage(String language, String fullAddress) {
        if (Objects.nonNull(language) && !language.trim().equals("")) {
            return language;
        }
        try {
            File file = new File(fullAddress);
            String text = getText(file);
            return GoogleTranslate.
                    getDisplayLanguage(
                            GoogleTranslate.detectLanguage(
                                    Objects.requireNonNull(text))
                    );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getText(File file) throws IOException {

        StringBuilder builder = new StringBuilder();
        PDDocument pdfDocument = PDDocument.load(file);


        PDFTextStripper pdfStripper = new PDFTextStripper();


        String textPdf = pdfStripper.getText(pdfDocument);


        pdfDocument.close();
        String[] split = textPdf.split("\n");
        int counter = 0;
        for (String s : split) {
            builder.append(s + "\n");
            if (counter == 10) {
                break;
            }
            counter++;
        }


        return builder.toString();
    }

    public Uploads uploads(long size, String path, Part part) throws IOException {
        String fullAddress = null;
        String name = null;
        File file = new File(path);
        file.mkdirs();
        part.write(path + File.separator + part.getSubmittedFileName());
//

        fullAddress = path + part.getSubmittedFileName();
        name = part.getSubmittedFileName();
        return !fullAddress.trim().equals("") ? new Uploads(fullAddress, name, String.valueOf(UUID.randomUUID()), size, "pdf") : null;
//        return null;
    }


    private String download(Set<String> list) throws IOException {
        ExecutorService pl = Executors.newFixedThreadPool(3);
        String path1 = "";
        int i = 0;
        List<String> names = new ArrayList<>();
        for (String s : list) {


            File file = new File("/home/shohimardon/Desktop/images//");
            String[] list1 = file.list();

            Runnable tasks = new Image(s);


            pl.execute(tasks);

            if (Objects.nonNull(list1) && list1.length > 5) {
                for (String s1 : list1) {
                    File file1 = new File("/home/shohimardon/Desktop/images//" + s1);
                    if (file1.isDirectory()) {
                        file1.deleteOnExit();
                        break;
                    }

                    Path path = Paths.get("/home/shohimardon/Desktop/images//" + s1);
                    long size = Files.size(path);

                    if (size < 100000L) {
                        file1.deleteOnExit();

                    }

                }
                file = new File("/home/shohimardon/Desktop/images//");
                list1 = file.list();
                if (list1.length > 70) {

                    break;
                }
            }
        }
//        pl.shutdown();

        return path1;

    }

    public void DownloadImage(String name) {

        name = name.trim();

        name = name.replace(",", "");
        String path = "";
        String[] splits = name.split(" ");
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < splits.length - 1; i++) {
            String split = splits[i];
            split = split.replace("\t", "");
            split = split.replace("\n", "");
            nameBuilder.append(split).append("%20");
        }
        nameBuilder.append(splits[splits.length - 1]);

        name = nameBuilder.toString();


        {
            try {
                String str = "https://yandex.ru/images/search?text=" + name + "&pos=0&img_url=https%3A%2F%2Ftelegra.ph%2Ffile%2Fef54f6fc4b58cd61bc66e.jpg&rpt=simage&lr=10335";
                str = str.trim();


                URL url = new URL(str);

                URLConnection connection = url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String d;
                while ((d = reader.readLine()) != null) {
                    sb.append(d);
                }
                Set<String> list = new TreeSet<>();

                String s = sb.toString();
                String[] split = s.split("url\":\"");
                lap:
                for (String s1 : split) {

                    if (s1.startsWith("https://") && s1.contains(".jpg")) {

                        String[] split1 = s1.split(".jpg");
                        for (String sq : split1) {
                            sq += ".jpg";
                            sq = sq.trim();
                            if (sq.startsWith("https://") && sq.trim().endsWith(".jpg")) {
                                list.add(sq);
                                if (list.size() == 30)
                                    break lap;

                            }
                        }
                    }

                }
                try {

                    path = download(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File file = new File("/home/shohimardon/Desktop/images/");
        File[] files = file.listFiles();
        for (File file1 : files) {
            boolean directory = file1.isDirectory();
            System.out.println(file1.getName() + " -> " + directory
            );
            if (directory) {
                boolean delete = file1.delete();
                System.out.println("delete = " + delete);
            }

        }

    }

    public void getFirsPageAsImage(String name) throws IOException {
        PdfDocument doc = new PdfDocument();


        doc.loadFromFile(name);


        int index = 0;


        for (Object page : doc.getPages()) {

            PdfPageBase pageBase = (PdfPageBase) page;


            for (BufferedImage image : pageBase.extractImages()) {


                File output = new File("/home/shohimardon/Desktop/images//" + UUID.randomUUID() + "/");


                ImageIO.write(image, "PNG", output);
            }
            break;
        }
    }

    @SneakyThrows
    public void selectPhoto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        File file = new File("/home/shohimardon/Desktop/images/");
        List<String> images = new ArrayList<>();
        for (File listFile : file.listFiles()) {
            if (!listFile.isDirectory()) {
                images.add(listFile.getAbsolutePath());
            }
        }
        System.out.println("files.length = " + images.size());

        request.setAttribute("images", images);
        request.setAttribute("name", ApplicationContextHolder.book.getName());
        System.out.println(images);
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/selectImage.jsp");
        dispatcher.forward(request, response);
    }

    public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part part = request.getPart("file");
        Uploads uploads = uploads(part.getSize(), "/home/shohimardon/Documents/library/file/", part);
        getImage(0, part.getSubmittedFileName(), uploads.getPath());
        var file = new File("/home/shohimardon/Desktop/images/");
        List<String> images = new ArrayList<>();
        for (File listFile : file.listFiles()) {
            if (!listFile.isDirectory()) {
                images.add(listFile.getAbsolutePath());
            }
        }
        System.out.println("files.length = " + images.size());
        request.setAttribute("images", images);
        request.getRequestDispatcher("views/MainMenu.jsp").forward(request, response);

    }

    public void mergeImage(String imagePath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("imagePath = " + imagePath);
        File file = new File(imagePath);
        String generenedName = imagePath.split("/")[imagePath.split("/").length - 1];
        String fullPath = "/home/shohimardon/Documents/library/image/" + ApplicationContextHolder.book.getName() + imagePath.split("/")[imagePath.split("/").length - 1];


        try {


            Path temp = Files.move
                    (
                            Paths.get(file.getAbsolutePath()),
                            Paths.get(fullPath)
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
        file = new File(fullPath);

        Uploads image = new Uploads(fullPath, ApplicationContextHolder.book.getName(), generenedName, file.length(), "img/jpg");
        ApplicationContextHolder.book.setCover(image);
        HttpSession session = request.getSession();
        SessionUser user = (SessionUser) session.getAttribute("sessionUser");
        ApplicationContextHolder.book.setCreatedBy(user.getId());
        bookDao.save(ApplicationContextHolder.book);
        file = new File("/home/shohimardon/Desktop/images");

        ApplicationContextHolder.book = null;
        try {


            for (File listFile : file.listFiles()) {

                listFile.delete();

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        response.sendRedirect("/main");
    }

    public void showList(HttpServletRequest request, String page, HttpServletResponse response) throws ServletException, IOException {

        List<Book> book;

        Long id = 0L;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("page")) {
                id = Long.valueOf(cookie.getValue());
            }
        }
        try {
        } catch (Exception e) {
            if (page.contains("-")) {
                id -= 1;
            } else {
                id += 1;
            }
        }

        if (Objects.isNull(searchBook)) {
            ApplicationContextHolder.book = null;
        }
        if (request.getRequestURI().contains("/conform")) {
            request.setAttribute("conformPage", "true");
            book = bookDao.getAllBook(false);
        } else {
            book = bookDao.getAllBook(true);
        }

        String str = request.getParameter("str");
        List<Book> pageWithBook = new ArrayList<>();
        SessionUser user = (SessionUser) request.getSession().getAttribute("sessionUser");

        if (str != null) {
            if (str.contains("-")) {
                id--;
                str = String.valueOf(id);

            } else if (str.contains("+")) {
                id++;
                str = String.valueOf(id);

            }

            Cookie cookie = new Cookie("page", str);
            response.addCookie(cookie);
            request.setAttribute("books", pageWithBook);
            request.setAttribute("user", user);
            request.getRequestDispatcher("views/showList.jsp").forward(request, response);
            return;
        }
//        else {

//            pageWithBook = bookDao.getAllBook(true);

        pageWithBook = getPageWithBook(book, id);

        Cookie cookie = new Cookie("currentPage", String.valueOf(id));
        response.addCookie(cookie);


        request.setAttribute("books", pageWithBook);
        request.setAttribute("user", user);
        request.setAttribute("curPg", id);
        request.setAttribute("MaxPge", (MaPX/10)+1);

        request.getRequestDispatcher("views/showList.jsp").forward(request, response);
//        response.sendRedirect("/");
    }

    private List<Book> getPageWithBook(List<Book> book, Long currentPage) {
        List<Book> bookList = new ArrayList<>();

        if (Objects.nonNull(ApplicationContextHolder.book)) {
            bookList = ApplicationContextHolder.books;
        } else {
            bookList = book;
        }

        if (currentPage == 0 || currentPage == 1 || currentPage > 0 && currentPage > bookList.size()) {
            currentPage = 0L;
        } else {

            currentPage = (currentPage * 10) - 10;


        }
        List<Book> result = new ArrayList<>();

        for (int i = Math.toIntExact(currentPage); i < bookList.size(); i++) {
            result.add(bookList.get(i));
            if (result.size() == 10) {
                break;
            }
        }

        ApplicationContextHolder.MaPX = bookList.size() - 1;

        return result;
    }

    public static String zipFile(List<String> srcFiles, String zipName) throws IOException {


        FileOutputStream fos = new FileOutputStream("/home/shohimardon/Documents/library/zipFiles/" + zipName + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
        return "/home/shohimardon/Documents/library/zipFiles/" + zipName + ".zip";
    }

    public void Download(HttpServletRequest request, Long id, HttpServletResponse response) throws ServletException, IOException {

        Book book = bookDao.getById(id);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String filename = book.getName();
        String filepath = zipFile(new ArrayList<>(Arrays.asList(book.getFile().getPath(), book.getCover().getPath())), book.getName());
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        FileInputStream fileInputStream = new FileInputStream(filepath);

        int i;
        while ((i = fileInputStream.read()) != -1) {
            out.write(i);
        }
        fileInputStream.close();
        out.close();


    }

    public void ConformOrDelete(HttpServletRequest request, Long id, HttpServletResponse response) throws IOException {
        Book book = bookDao.getById(id);

        if (request.getRequestURI().contains("/conform")) {

            book.setConform(true);
        } else {
            book.setDeleted(true);
        }
        bookDao.save(book);
        response.sendRedirect("/conform");
    }

    public void editBook(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        Book book = bookDao.getById(Long.valueOf(request.getParameter("id")));
        ApplicationContextHolder.editedBook = book;
        request.setAttribute("book", book);
        request.getRequestDispatcher("views/showList.jsp").forward(request, response);

    }

    public void Search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = request.getParameter("search");
        List<Book> books = bookDao.getAnyWhere(search);
        searchBook = search;
        ApplicationContextHolder.books = books;

        SessionUser user = (SessionUser) request.getSession().getAttribute("sessionUser");
        request.setAttribute("books", books);
        request.setAttribute("user", user);
        request.getRequestDispatcher("views/showList.jsp").forward(request, response);


    }
}

