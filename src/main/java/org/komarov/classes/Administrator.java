package org.komarov.classes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Administrator {

    private String login;

    private String password;
}
