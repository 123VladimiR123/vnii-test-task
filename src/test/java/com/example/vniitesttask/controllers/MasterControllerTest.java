package com.example.vniitesttask.controllers;

import com.example.vniitesttask.dto.MasterDTO;
import com.example.vniitesttask.entities.MasterEntity;
import com.example.vniitesttask.repositories.MasterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MasterControllerTest {
    @Autowired
    private MasterRepository masterRepository;
    @Autowired
    private MasterController controller;

    @Test
    @Transactional
    void getOneMasterById() {
        masterRepository.delete(12345L);

        assertNull(controller.getOneMasterById(12345L).getBody());

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "new master doc");

        MasterEntity entity = masterRepository.findById(12345L).get();
            entity.getDetailEntityList();

        MasterEntity controllerEntity = controller.getOneMasterById(12345L).getBody();
            controllerEntity.getDetailEntityList();

        assertEquals(entity, controllerEntity);

        masterRepository.delete(12345L);
    }

    @Test
    void getAllMasterEntities() {
        masterRepository.delete(12345L);
        masterRepository.delete(12346L);
        masterRepository.delete(12347L);

        HashSet<Long> set = new HashSet<>(3);
        set.add(12345L);
        set.add(12346L);
        set.add(12347L);

        List<MasterEntity> list = controller.getAllMasterEntities().getBody();
        assertEquals(0, list.size());

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "new master doc");
        masterRepository.insertNewMasterDocument(12346L, LocalDateTime.now(), "new master doc");
        masterRepository.insertNewMasterDocument(12347L, LocalDateTime.now(), "new master doc");

        list = controller.getAllMasterEntities().getBody();
        assertEquals(3, list.size());

        list.forEach(e -> assertTrue(set.contains(e.getId())));

        masterRepository.delete(12345L);
        masterRepository.delete(12346L);
        masterRepository.delete(12347L);
    }

    @Test
    @Transactional
    void updateMasterById() {
        masterRepository.delete(12345L);

        assertEquals(HttpStatusCode.valueOf(404),
                controller.updateMasterById(new MasterDTO(12345L, "some description"), 12345L).getStatusCode());

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "new master desc");
        MasterEntity saved = masterRepository.findById(12345L).get();

        saved.setDescription("new some description");

        controller.updateMasterById(new MasterDTO(12345L, "new some description"), 12345L);

        assertEquals(saved, masterRepository.findById(12345L).get());

        masterRepository.delete(12345L);
    }

    @Test
    void createMaster() {
        masterRepository.delete(12345L);
        assertFalse(masterRepository.existsById(12345L));

        controller.createMaster(new MasterDTO(12345L, "new desc"));
        assertTrue(masterRepository.existsById(12345L));

        assertThrows(DataIntegrityViolationException.class,
                () -> controller.createMaster(new MasterDTO(12345L, "new desc")));

        masterRepository.delete(12345L);
    }

    @Test
    void deleteOneMasterById() {
        masterRepository.delete(12345L);
        assertFalse(masterRepository.existsById(12345L));

        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "some desc");
        assertTrue(masterRepository.existsById(12345L));

        assertEquals(HttpStatusCode.valueOf(200), controller.deleteOneMasterById(12345L).getStatusCode());
        assertEquals(HttpStatusCode.valueOf(404), controller.deleteOneMasterById(12345L).getStatusCode());
    }
}