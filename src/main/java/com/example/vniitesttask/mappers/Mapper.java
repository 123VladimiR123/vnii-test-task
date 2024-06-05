package com.example.vniitesttask.mappers;

import com.example.vniitesttask.dto.DetailDTO;
import com.example.vniitesttask.dto.MasterDTO;
import com.example.vniitesttask.entities.DetailEntity;
import com.example.vniitesttask.entities.MasterEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class Mapper {

    /* simple mapper for master entity from dto

        returns - MasterEntity
        parameter - MasterDTO

        sets id, description after validation,
        creationDate sets to now,
        cost sets to 0 because no details exists without master doc
     */
    public MasterEntity masterDTOToEntity(MasterDTO dto) {
        return MasterEntity.builder()
                .id(dto.getId())
                .creationDate(LocalDateTime.now())
                .description(dto.getDescription())
                .cost(0L)
                .build();
    }


    /* simple mapper for detail entity from dto

        returns - DetailEntity
        requires - validated DetailDTO

        sets detailName, masterDocNumber, costValue after validation (and check)
     */
    public DetailEntity detailDTOToEntity(DetailDTO dto) {
        return DetailEntity.builder()
                .detailName(dto.getDetailName())
                .masterDocNumber(dto.getMasterDocNumber())
                .costValue(dto.getCostValue())
                .build();
    }
}
