package ru.practicum.shareit.item.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    private final EasyRandom generator = new EasyRandom();

    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User actualUser1;
    private User actualUser2;

    private Request actualRequest1;
    private Request actualRequest2;
    private Item actualItem1;
    private Item actualItem2;

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

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        item1.setOwner(actualUser1);
        item2.setOwner(actualUser2);
        item1.setRequestId(actualRequest1.getId());
        item2.setRequestId(actualRequest2.getId());
        actualItem1 = itemRepository.save(item1);
        actualItem2 = itemRepository.save(item2);
    }

    @Test
    void saveTest() {
        Item item3 = generator.nextObject(Item.class);
        item3.setOwner(actualUser1);
        item3.setRequestId(actualRequest1.getId());
        Item actualItem3 = itemRepository.save(item3);

        assertEquals(actualItem3, itemRepository.findById(actualItem3.getId()).get());
    }

    @Test
    void saveTestWhenUpdate() {
        actualItem1.setOwner(actualUser2);
        itemRepository.save(actualItem1);

        assertEquals(actualUser2, itemRepository.findById(actualItem1.getId()).get().getOwner());
    }

    @Test
    void findById() {
        assertEquals(actualItem1, itemRepository.findById(actualItem1.getId()).get());
    }

    @Test
    void findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue() {
        actualItem1.setName("Name1");
        actualItem1.setDescription("Description1");
        actualItem1.setAvailable(true);
        actualItem2.setAvailable(true);
        itemRepository.save(actualItem1);
        itemRepository.save(actualItem2);

        List<Item> items = itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                "Name1", "Description1", PageRequest.of(FROM, SIZE))
                .get().collect(Collectors.toList());

        assertEquals(1, items.size());
        assertEquals(actualItem1, items.get(0));
    }

    @Test
    void findByOwnerId() {
        List<Item> items = itemRepository.findByOwnerId(actualUser1.getId(), PageRequest.of(FROM, SIZE))
                .get().collect(Collectors.toList());
        assertEquals(1, items.size());
        assertEquals(actualItem1, items.get(0));
    }

    @Test
    void findByOwnerIdAndRequestId() {
        List<Item> items = itemRepository.findByOwnerIdAndRequestId(actualUser1.getId(), actualRequest1.getId())
                .get().collect(Collectors.toList());
        assertEquals(1, items.size());
        assertEquals(actualItem1, items.get(0));
    }

    @Test
    void findByRequestId() {
        List<Item> items = itemRepository.findByRequestId(actualRequest1.getId())
                .get().collect(Collectors.toList());
        assertEquals(1, items.size());
        assertEquals(actualItem1, items.get(0));
    }
}