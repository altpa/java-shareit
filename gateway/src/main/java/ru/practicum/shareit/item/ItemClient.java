package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(ItemDto itemDto, long ownerId) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> getAllItemsByOwnerId(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, long ownerId, long itemId) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> getItemById(long itemId, long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> searchItems(String searchText) {
        return get("/search?text=" + searchText);
    }

    public ResponseEntity<Object> addComment(CommentDto comment, long ownerId, long itemId) {
        return post("/" + itemId + "/comment", ownerId,  comment);
    }
}
