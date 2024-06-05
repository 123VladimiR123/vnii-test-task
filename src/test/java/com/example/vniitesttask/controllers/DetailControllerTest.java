package com.example.vniitesttask.controllers;

import com.example.vniitesttask.dto.DetailDTO;
import com.example.vniitesttask.entities.DetailEntity;
import com.example.vniitesttask.entities.MasterEntity;
import com.example.vniitesttask.repositories.DetailRepository;
import com.example.vniitesttask.repositories.MasterRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DetailControllerTest {

    @Autowired
    private MasterRepository masterRepository;
    @Autowired
    private DetailRepository detailRepository;
    @Autowired
    private DetailController controller;

    @Test
    void getAllDetailsByMasterNumber() {
        masterRepository.delete(12345L);

        List<DetailEntity> list = new ArrayList<>();

        list.add(new DetailEntity(12345L, "New detail 1", 100L));
        list.add(new DetailEntity(12345L, "New detail 2", 100L));
        list.add(new DetailEntity(12345L, "New detail 3", 100L));

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "master document");

        detailRepository.insertNewDetail(12345L, "New detail 1", 100L);
        detailRepository.insertNewDetail(12345L, "New detail 2", 100L);
        detailRepository.insertNewDetail(12345L, "New detail 3", 100L);

        assertTrue(controller.getAllDetailsByMasterNumber(12345L).getBody().equals(list));

        masterRepository.delete(12345L);

    }

    @Test
    void getOneDetailByDetailName() {
        masterRepository.delete(12345L);

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "master document");

        detailRepository.insertNewDetail(12345L, "New detail 1", 100L);

        assertTrue(controller.getOneDetailByDetailName("New detail 1").getBody().getDetailName().equals("New detail 1"));
        assertTrue(controller.getOneDetailByDetailName("some detail").getStatusCode().equals(HttpStatusCode.valueOf(404)));

        masterRepository.delete(12345L);

        assertNull(detailRepository.findByDetailName("New detail 1"));
    }

    @Test
    void updateOneDetail() {
        masterRepository.delete(12345L);

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "master document");

        detailRepository.insertNewDetail(12345L, "New detail 1", 100L);

        controller.updateOneDetail(new DetailDTO(12345L, "New detail 1", 400L), 12345L);
        assertEquals(Long.valueOf(400), detailRepository.findByDetailName("New detail 1").getCostValue());

        assertTrue(controller.updateOneDetail(new DetailDTO(12345L, "some detail", 400L), 12345L)
                .getStatusCode()
                .equals(HttpStatusCode.valueOf(404)));

        masterRepository.delete(12345L);
    }

    @Test
    void createOneDetailByMasterId() {
        masterRepository.delete(12345L);

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "master document");

        assertEquals(controller.createOneDetailByMasterId(
                new DetailDTO(12345L, "New detail 1", 400L), 12345L).getBody(), 1);

        assertNull(controller.createOneDetailByMasterId(
                new DetailDTO(22245L, "some detail", 400L), 12345L).getBody());

        assertThrows(Exception.class, () -> controller.createOneDetailByMasterId(
                new DetailDTO(22245L, "some detail", 400L), 22245L));

        masterRepository.delete(12345L);
    }

    @Test
    void deleteOneDetailByDetailName() {
        masterRepository.delete(12345L);

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "master document");

        detailRepository.insertNewDetail(12345L, "New detail 1", 400L);

        controller.deleteOneDetailByDetailName("New detail 1");

        assertFalse(detailRepository.existsByDetailName("New detail 1"));

        masterRepository.delete(12345L);
    }

    @Test
    void deleteAllByMasterNumber() {
        masterRepository.delete(12345L);

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "master document");

        detailRepository.insertNewDetail(12345L, "New detail 1", 100L);
        detailRepository.insertNewDetail(12345L, "New detail 2", 100L);
        detailRepository.insertNewDetail(12345L, "New detail 3", 100L);

        assertEquals(detailRepository.countAllByMasterDocNumber(12345L), 3);

        controller.deleteAllByMasterNumber(12345L);

        assertEquals(detailRepository.countAllByMasterDocNumber(12345L), 0);
    }
}