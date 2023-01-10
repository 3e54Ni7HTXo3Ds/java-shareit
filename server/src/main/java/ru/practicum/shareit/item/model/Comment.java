package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

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
    private Long id;// уникальный идентификатор комментария;
    @Column(name = "comment_text")
    private String text;// содержимое комментария;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;// вещь, к которой относится комментарий;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;// автор комментария;
    @Transient
    private LocalDateTime created;// дата создания комментария

    public Comment(String text) {
        this.text = text;
    }
}
