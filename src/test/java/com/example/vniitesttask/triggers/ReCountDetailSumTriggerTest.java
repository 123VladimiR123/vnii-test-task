package com.example.vniitesttask.triggers;

import com.example.vniitesttask.repositories.DetailRepository;
import com.example.vniitesttask.repositories.MasterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReCountDetailSumTriggerTest {
    @Autowired
    private MasterRepository masterRepository;
    @Autowired
    private DetailRepository detailRepository;

    @Test
    void AutoUpdateCostTest() {
        masterRepository.delete(12345L);
        masterRepository.insertNewMasterDocument(12345L, LocalDateTime.now(), "Some description");

        assertEquals(0L, masterRepository.findById(12345L).get().getCost());

        detailRepository.insertNewDetail(12345L, "detail name 1", 100L);
        detailRepository.insertNewDetail(12345L, "detail name 2", 200L);
        detailRepository.insertNewDetail(12345L, "detail name 3", 300L);

        assertEquals(600L, masterRepository.findById(12345L).get().getCost());

        detailRepository.updateByDetailName(12345L, 600L, "detail name 1");

        assertEquals(1100L, masterRepository.findById(12345L).get().getCost());

        detailRepository.delete("detail name 2");

        assertEquals(900L, masterRepository.findById(12345L).get().getCost());

        masterRepository.updateById("new beautiful description", 12345L);

        assertEquals(900L, masterRepository.findById(12345L).get().getCost());
    }
}