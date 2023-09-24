package ru.practicum.shareit.request.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryTest {
    private final EasyRandom generator = new EasyRandom();

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private User actualUser1;
    private User actualUser2;
    private Request actualRequest1;
    private Request actualRequest2;

    @BeforeEach
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void setUp() {
        actualUser1 = userRepository.save(generator.nextObject(User.class));
        actualUser2 = userRepository.save(generator.nextObject(User.class));
        Request request1 = generator.nextObject(Request.class);
        Request request2 = generator.nextObject(Request.class);
        request1.setOwnerId(actualUser1.getId());
        request2.setOwnerId(actualUser2.getId());
        actualRequest1 = requestRepository.save(request1);
        actualRequest2 = requestRepository.save(request2);
    }

    @Test
    void save() {
        Request request3 = generator.nextObject(Request.class);
        request3.setOwnerId(actualUser1.getId());
        Request actualRequest3 = requestRepository.save(request3);

        assertEquals(actualRequest3, requestRepository.findById(actualRequest3.getId()).get());
    }

    @Test
    void saveWhenUpdate() {
        actualRequest1.setDescription("Description1");
        requestRepository.save(actualRequest1);

        assertEquals("Description1", requestRepository.findById(actualRequest1.getId()).get().getDescription());
    }

    @Test
    void findById() {
        assertEquals(actualRequest1, requestRepository.findById(actualRequest1.getId()).get());
    }

    @Test
    void findByOwnerId() {
        List<Request> requests = requestRepository.findByOwnerId(actualUser1.getId()).get().collect(Collectors.toList());
        assertEquals(1, requests.size());
        assertEquals(actualRequest1, requests.get(0));
    }

    @Test
    void findAll() {
        assertEquals(
                List.of(actualRequest1, actualRequest2),
                requestRepository.findAll(Pageable.ofSize(10)).get().collect(Collectors.toList()));
    }
}