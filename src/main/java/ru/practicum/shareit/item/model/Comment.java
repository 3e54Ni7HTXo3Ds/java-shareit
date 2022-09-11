package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;// уникальный идентификатор комментария;
    @Column(name = "comment_text")
    String text;// содержимое комментария;
    @Column(name = "item_id")
    Long item;// вещь, к которой относится комментарий;
    @Column(name = "author_id")
    Long author;// автор комментария;
    @Transient
    LocalDateTime created;// дата создания комментария

    public Comment(String text) {
        this.text = text;
    }
}
