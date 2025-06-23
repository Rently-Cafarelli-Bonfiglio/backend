package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
}
