package ru.practicum.shareit.user.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    private final EasyRandom generator = new EasyRandom();

    @Autowired
    private UserRepository userRepository;

    private User actualUser1;
    private User actualUser2;

    @BeforeEach
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void setUp() {
        actualUser1 = userRepository.save(generator.nextObject(User.class));
        actualUser2 = userRepository.save(generator.nextObject(User.class));
    }

    @Test
    void saveTest() {
        User actualUser3 = userRepository.save(generator.nextObject(User.class));

        assertEquals(actualUser3, userRepository.findById(actualUser3.getId()).get());
    }

    @Test
    void saveTestWhenUpdate() {
        actualUser1.setName("Name1");
        userRepository.save(actualUser1);

        assertEquals("Name1", userRepository.findById(actualUser1.getId()).get().getName());
    }

    @Test
    void findById() {
        assertEquals(actualUser1, userRepository.findById(actualUser1.getId()).get());
    }

    @Test
    void findAll() {
        assertEquals(List.of(actualUser1, actualUser2), userRepository.findAll());
    }

    @Test
    void deleteById() {
        userRepository.deleteById(actualUser1.getId());
        List<User> users = StreamSupport.stream(userRepository.findAll().spliterator(), false).collect(toList());

        assertEquals(1, users.size());
    }

    @Test
    void existsById() {
        userRepository.deleteById(actualUser1.getId());
        assertFalse(userRepository.existsById(actualUser1.getId()));
        assertTrue(userRepository.existsById(actualUser2.getId()));
    }
}