package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id; // — уникальный идентификатор пользователя;
    private String name;// — имя или логин пользователя;
    @Email
    private String email;/* — адрес электронной почты (учтите, что два пользователя не могут
            иметь одинаковый адрес электронной почты).*/
}
