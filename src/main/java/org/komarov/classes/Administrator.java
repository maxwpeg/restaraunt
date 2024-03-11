package org.komarov.classes;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Administrator implements Serializable {

    private static final long serialVersionUID = 1L;

    private String login;

    private String password;

}
