package ru.practicum.shareit.comment.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    private final EasyRandom generator = new EasyRandom();

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item actualItem1;
    private Comment actualComment1;

    @BeforeEach
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void setUp() {
        User user = userRepository.save(generator.nextObject(User.class));
        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        item1.setOwner(user);
        item2.setOwner(user);
        actualItem1 = itemRepository.save(item1);
        Comment comment1 = generator.nextObject(Comment.class);
        comment1.setItem(actualItem1);
        actualComment1 = commentRepository.save(comment1);
    }

    @Test
    void saveTest() {
        Comment comment2 = generator.nextObject(Comment.class);
        comment2.setItem(actualItem1);
        Comment actualComment2 = commentRepository.save(comment2);
        assertEquals(2, commentRepository.findByItemId(actualItem1.getId())
                .get().collect(Collectors.toList()).get(1).getId());
    }

    @Test
    void saveTestWhenUpdate() {
        actualComment1.setAuthorName("AuthorName");
        commentRepository.save(actualComment1);
        assertEquals("AuthorName",
                commentRepository.findByItemId(actualItem1.getId())
                        .get().collect(Collectors.toList()).get(0).getAuthorName());
    }

    @Test
    void findByItemId() {
        List<Comment> comments = commentRepository.findByItemId(actualItem1.getId()).get().collect(Collectors.toList());

        assertEquals(1, comments.size());
        assertEquals(actualComment1, comments.get(0));
    }
}