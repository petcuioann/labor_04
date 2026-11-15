package com.example.library.repository;

import com.example.library.model.ReadableItem;
import org.springframework.stereotype.Repository;

@Repository
public class ReadableItemRepository extends InFileRepository<ReadableItem> {
    public ReadableItemRepository() {
        super("src/main/resources/data/readable_items.json", ReadableItem.class);
    }
}
