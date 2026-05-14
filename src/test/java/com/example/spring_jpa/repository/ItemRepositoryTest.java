package com.example.spring_jpa.repository;

import com.example.spring_jpa.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    public void isNew() {
        Item item = new Item("A");
        log.info("is new = {}", item.isNew());
        itemRepository.save(item);
    }
}