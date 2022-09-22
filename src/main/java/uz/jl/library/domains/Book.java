package uz.jl.library.domains;


import jakarta.persistence.Convert;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import uz.jl.library.enums.Language;


import jakarta.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@ToString
@Entity

public class Book extends Auditable {
//    @Id
//    @Column(name = "id", nullable = false)
//    private Long id;

    private String name;
    private String description;
    private String author;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Enumerated(EnumType.STRING)
    private Language language;
    private int pageCount;
    private int downloadCount;

    @OneToOne
    @Cascade(CascadeType.ALL)

    private Uploads cover;
    @Column(columnDefinition = "smallint default 0")
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    private boolean isConform = false;
    @OneToOne

    @Cascade(CascadeType.ALL)
    private Uploads file;

    public Book(String name, String description, String author, Genre genre, Language language, int pageCount, int downloadCount, Uploads cover, Uploads file) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.genre = genre;
        this.language = language;
        this.pageCount = pageCount;
        this.downloadCount = downloadCount;
        this.cover = cover;
        this.isConform = false;
        this.file = file;
    }

    @AllArgsConstructor
    @Getter
    public enum Genre {
        CRIME("Crime"),
        HORROR("Horror"),
        CI_FI("Ci-fi"),
        DRAMA("Drama"),
        ROMANCE("Romance"),
        ROMANCE_DRAMA("Romance Drama"),
        FANTASY("Fantasy");
        private final String key;

        public String getKey() {
            return key;
        }
    }
}
