package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeRoleResponse {
    private Long id;
    private String username;
    private String motivation;
    private String status;
}
