package com.example.vniitesttask.controllers;

import com.example.vniitesttask.dto.DetailDTO;
import com.example.vniitesttask.entities.DetailEntity;
import com.example.vniitesttask.mappers.Mapper;
import com.example.vniitesttask.repositories.DetailRepository;
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

@AllArgsConstructor
@RestController
@RequestMapping("/detail")
@Slf4j
public class DetailController {
    private final DetailRepository detailRepository;
    private final Mapper mapper;


    /*
        path + http get method for getting all detail entities for current master

        requires - master number id in path
        returns - list of details for selected master or null if master id not found
     */
    @GetMapping("/all/{id}")
    public ResponseEntity<List<DetailEntity>> getAllDetailsByMasterNumber(@PathVariable(name = "id") @NotNull Long masterNumber) {
        List<DetailEntity> entities = detailRepository
                .findAllByMasterDocNumber(masterNumber, PageRequest.of(0, 200))
                .getContent();

        return ResponseEntity.ok(entities);
    }


    /*
        path + http get method for getting one detail by its name

        requires - detail name in path
        returns - detail + 200 if found, 404 if not
     */
    @GetMapping("/one/{id}")
    public ResponseEntity<DetailEntity> getOneDetailByDetailName(@PathVariable(name = "id") @NotNull String detailName) {
        DetailEntity entity = detailRepository.findByDetailName(detailName);

        if (entity == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(entity);
    }


    /*
        path + http patch method for update existing detail

        requires -master id in path and DTO object in body
        returns - count of updated (1)
     */
    @PatchMapping("/one/{id}")
    public ResponseEntity<Integer> updateOneDetail(@ModelAttribute @Valid DetailDTO dto,
                                                   @PathVariable(name = "id") Long masterNumber) {
        if (!masterNumber.equals(dto.getMasterDocNumber()))
            return ResponseEntity.notFound().build();

        DetailEntity entity = mapper.detailDTOToEntity(dto);

        int entitySaved = detailRepository.updateByDetailName(entity.getMasterDocNumber(), entity.getCostValue(), entity.getDetailName());

        if (entitySaved == 0)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(entitySaved);
    }


    /*
        path + http post method for create detail for existing master, if
            master not found returns 404

        requires -master id in path and DTO object in body
        returns - count of created (1)
     */
    @PostMapping("/one/{id}")
    public ResponseEntity<Integer> createOneDetailByMasterId(@ModelAttribute @Valid DetailDTO dto,
                                                             @PathVariable(name = "id") Long masterNumber) {
        if (!masterNumber.equals(dto.getMasterDocNumber()))
            return ResponseEntity.notFound().build();

        int entitySaved = detailRepository.insertNewDetail(masterNumber, dto.getDetailName(), dto.getCostValue());

        return ResponseEntity.ok(entitySaved);
    }


    /*
        path + http delete method for delete existing detail

        requires -DETAIL NAME as id
        returns - 200 if entity found and deleted, 404 if not found, 403 if deleting wasn't accepted
     */
    @DeleteMapping("/one/{id}")
    public ResponseEntity deleteOneDetailByDetailName(@PathVariable(name = "id") String detailName) {
        if (!detailRepository.existsByDetailName(detailName))
            return ResponseEntity.notFound().build();

        if (!validateDeleting())
            return ResponseEntity.status(403).build();
        
        detailRepository.delete(detailName);
        log.info("Validates for deleting detail named " + detailName);
        
        return ResponseEntity.ok().build();
    }


    /*
        path + http delete method for delete all existing details for current master

        requires - MASTER NUMBER as id
        returns - 200 if any number of entities found and deleted, 404 if not found, 403 if deleting wasn't accepted
     */
    @DeleteMapping("/all/{id}")
    public ResponseEntity deleteAllByMasterNumber(@PathVariable(name = "id") Long masterNumber) {
        if (detailRepository.countAllByMasterDocNumber(masterNumber) == 0)
            return ResponseEntity.notFound().build();

        if (!validateDeleting())
            return ResponseEntity.status(403).build();
        log.info("Validates for deleting all details #" + masterNumber);

        detailRepository.deleteAllByMasterDocNumber(masterNumber);

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
