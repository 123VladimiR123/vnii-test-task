package com.example.vniitesttask.repositories;

import com.example.vniitesttask.entities.MasterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(isolation = Isolation.READ_COMMITTED)
@Repository
public interface MasterRepository extends CrudRepository<MasterEntity, Long> {

    Page<MasterEntity> findAll(Pageable pageable);

    @Transactional(propagation = Propagation.REQUIRES_NEW,
        isolation = Isolation.SERIALIZABLE)
    @Modifying
    @Query(value = "UPDATE master SET " +
            " description = :description WHERE doc_number = :masterDocNumber",
            nativeQuery = true)
    int updateById(@NonNull String description, @NonNull Long masterDocNumber);

    // didn't find ways to unpack MasterEntity to its fields without  default implementation
    @Transactional(propagation = Propagation.REQUIRES_NEW,
        isolation = Isolation.SERIALIZABLE)
    @Modifying
    @Query(value = "INSERT INTO master VALUES (:id, :creationDate, 0, :description)",nativeQuery = true)
    int insertNewMasterDocument(@NonNull Long id,
                                         @NonNull LocalDateTime creationDate,
                                         @NonNull String description);

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.SERIALIZABLE)
    @Modifying
    @Query(value = "DELETE FROM master WHERE doc_number = :id", nativeQuery = true)
    void delete(@NonNull Long id);

    // save methods provide insert-or-update logic, so we're unable to check existing entities by one transaction
    @Override
    default <S extends MasterEntity> S save(@NonNull S entity) {
        throw new IllegalArgumentException("This method is forbidden");
    }

    @Override
    default <S extends MasterEntity> Iterable<S> saveAll(@NonNull Iterable<S> entities) {
        throw new IllegalArgumentException("This method is forbidden");
    }
}
