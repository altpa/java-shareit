package ru.practicum.shareit.comment.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@DynamicUpdate
@DynamicInsert
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "text")
    String text;

    @ManyToOne
    @JoinColumn(name = "item")
    Item item;

    @Column(name = "author_name")
    String authorName;

    @Column(name = "created")
    LocalDateTime created;
}
