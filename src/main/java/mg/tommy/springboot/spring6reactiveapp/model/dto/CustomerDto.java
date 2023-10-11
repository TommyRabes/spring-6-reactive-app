package mg.tommy.springboot.spring6reactiveapp.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CustomerDto {
    private String id;
    @Size(min = 3, max = 255)
    private String firstName;
    @Size(min = 3, max = 255)
    private String lastName;
    @Size(min = 3, max = 320)
    @Email
    private String email;
    private LocalDate birthdate;
}
