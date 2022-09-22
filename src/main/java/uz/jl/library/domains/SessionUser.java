package uz.jl.library.domains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.jl.library.config.Encoder;
import uz.jl.library.domains.User;
import uz.jl.library.enums.UserRole;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SessionUser {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private UserRole role;
    private String password;
    private Integer age;
    private String macAddress;
    private LocalDate loginDate;
    Encoder encoder = Encoder.instance;

    public SessionUser(User user, boolean b) {
        if (b) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.firstname = user.getFirstname();
            this.lastname = user.getLastname();
            this.email = user.getEmail();
            this.role = user.getRole();
            this.password = user.getPassword();
            this.age = user.getAge();
            return;
        }
        this.id = user.getId();
        this.username = encoder.encode(user.getUsername());
        this.firstname = encoder.encode(user.getFirstname());
        this.lastname = encoder.encode(user.getLastname());
        this.email = encoder.encode(user.getEmail());
        this.role = user.getRole();
        this.password = user.getPassword();
        this.age = user.getAge();
    }
}
