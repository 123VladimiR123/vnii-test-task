package com.example.vniitesttask.controllers;

import com.example.vniitesttask.dto.MasterDTO;
import com.example.vniitesttask.entities.MasterEntity;
import com.example.vniitesttask.mappers.Mapper;
import com.example.vniitesttask.repositories.MasterRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/master")
public class MasterController {
    private final MasterRepository masterRepository;
    private final Mapper mapper;


    /*
        path + get for getting one master by its id

        requires - masterNumber
        returns - 404 if not found, else 200 + entity
     */
    @GetMapping("/one/{id}")
    public ResponseEntity<MasterEntity> getOneMasterById(@PathVariable(name = "id") @NotNull Long masterId) {
        MasterEntity entity = masterRepository.findById(masterId).orElse(null);

        if (entity == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(entity);
    }


    /*
        path + http get for get all masters

        requires - nothing, but could be @requestparam like query, page size
        returns - list of masters
     */
    @GetMapping("/all")
    public ResponseEntity<List<MasterEntity>> getAllMasterEntities() {
        List<MasterEntity> list = masterRepository.findAll(PageRequest.of(0, 200)).getContent();

        log.info("request for all masters accepted");

        return ResponseEntity.ok(list);
    }


    /*
        path + http patch for update existing master

        requires - masterDTO
        returns - 1 for updated rows or 404 if ids are not same
     */
    @PatchMapping("/one/{id}")
    public ResponseEntity<Integer> updateMasterById(@ModelAttribute @Valid MasterDTO masterDTO,
                                                         @PathVariable(name = "id") @NotNull Long masterId) {
        if (!masterId.equals(masterDTO.getId()))
            return ResponseEntity.notFound().build();

        MasterEntity mapped = mapper.masterDTOToEntity(masterDTO);
        int saved = masterRepository.updateById(mapped.getDescription(), masterId);

        if (saved == 0)
            return ResponseEntity.notFound().build();

        log.info("master #" + masterDTO.getId() + " updated");

        return ResponseEntity.ok(saved);
    }


    /*
        path + http post method for create new master
        unique constrains guarantees non-repeatable id

        requires - master DTO
        returns - 1 for created
     */
    @PostMapping("/one")
    public ResponseEntity<Integer> createMaster(@ModelAttribute @Valid MasterDTO masterDTO) {
        MasterEntity mapped = mapper.masterDTOToEntity(masterDTO);
        int saved = masterRepository.insertNewMasterDocument(mapped.getId(), mapped.getCreationDate(), mapped.getDescription());

        log.info("master #" + masterDTO.getId() + " created");

        return ResponseEntity.ok(saved);
    }


    /*
        path + http delete method for delete all existing masters and details by cascade deleting

        requires - master number
        returns - 200 if deleted, 404 if not found, 403 if deleting wasn't accepted
     */
    @DeleteMapping("/one/{id}")
    public ResponseEntity deleteOneMasterById(@PathVariable(name = "id") @NotNull Long masterNumber) {
        if (!masterRepository.existsById(masterNumber))
            return ResponseEntity.notFound().build();

        log.warn("attempt to delete master #" + masterNumber);

        if (!validateDeleting())
            return ResponseEntity.status(403).build();

        masterRepository.delete(masterNumber);
        log.warn("master #" + masterNumber + " deleted");

        return ResponseEntity.ok().build();
    }


    /*
        path + http delete method for delete all masters

        returns - 200 if any number of entities found and deleted, 404 if not found, 403 if deleting wasn't accepted
     */
    @DeleteMapping("/all")
    public ResponseEntity deleteAllMasters() {
        if (masterRepository.count() == 0)
            return ResponseEntity.notFound().build();

        log.warn("attempt to clear db");

        if (!validateDeleting())
            return ResponseEntity.status(403).build();

        masterRepository.deleteAll();
        log.warn("database was cleared, deleting confirmed");

        return ResponseEntity.ok().build();
    }


    /*
        empty-body method to accept deleting as dangerous request
     */
    private boolean validateDeleting() {
        return true;
    }


    /*
        In case of broken data when end user is trying update non-existing entity or the same
     */
    @ExceptionHandler({DataIntegrityViolationException.class, ValidationException.class})
    public ResponseEntity someErrorOccured(Exception exception) {
        log.error(exception.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
