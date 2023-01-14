package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // — уникальный идентификатор пользователя;
    @Column(name = "user_name")
    private String name;// — имя или логин пользователя;
    private String email;/* — адрес электронной почты (учтите, что два пользователя не могут
            иметь одинаковый адрес электронной почты).*/

}
