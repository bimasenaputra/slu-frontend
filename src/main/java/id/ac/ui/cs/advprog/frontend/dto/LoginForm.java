package id.ac.ui.cs.advprog.frontend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class LoginForm {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
