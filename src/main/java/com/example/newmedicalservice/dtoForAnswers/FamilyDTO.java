package com.example.newmedicalservice.dtoForAnswers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyDTO {

    private Integer id;
    private List<ClientDTO> clientList;
    private String familyName;
    private String FamilyHead;
    private String familyMobile;
    private String description;

}
