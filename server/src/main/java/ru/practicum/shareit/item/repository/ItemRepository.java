package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface ItemRepository extends Repository<Item, Long> {
    Item save(Item item);

    Optional<Item> findById(Long primaryKey);

    Page<Item>
    findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String searchName,
                                                                     String searchDescription,
                                                                     Pageable pageable);

    Page<Item> findByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);

    Streamable<Item> findByOwnerIdAndRequestId(Long ownerId, Long requestId);

    Streamable<Item> findByRequestId(Long requestId);
}