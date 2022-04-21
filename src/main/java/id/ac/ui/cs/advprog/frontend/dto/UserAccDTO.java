package id.ac.ui.cs.advprog.frontend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.*;

@Getter
@Setter
@NoArgsConstructor
public class UserAccDTO {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String password;
}
