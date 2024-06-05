package com.example.vniitesttask.repositories;

import com.example.vniitesttask.entities.DetailEntity;
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


@Transactional(isolation = Isolation.READ_COMMITTED)
@Repository
public interface DetailRepository extends CrudRepository<DetailEntity, String> {

    // transaction params set above repository annotation
    DetailEntity findByDetailName(@NonNull String detailName);

    Page<DetailEntity> findAllByMasterDocNumber(@NonNull Long id, @NonNull Pageable pageable);

    Boolean existsByDetailName(@NonNull String name);

    long countAllByMasterDocNumber(@NonNull Long masterDocNumber);

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.SERIALIZABLE)
    @Modifying
    @Query(value = "UPDATE detail SET master_doc_number = :masterNumber, cost_value = :costValue WHERE detail_name = :detailName", nativeQuery = true)
    int updateByDetailName(@NonNull Long masterNumber, @NonNull Long costValue, @NonNull String detailName);


    // didn't find ways to unpack DetailEntity to it's fields without default implementation
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.SERIALIZABLE)
    @Modifying
    @Query(value = "INSERT INTO detail VALUES (:masterDocNumber, :detailName, :costValue)",nativeQuery = true)
    int insertNewDetail(@NonNull Long masterDocNumber,
                                         @NonNull String detailName,
                                         @NonNull Long costValue);

    // deletes for different situations
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.SERIALIZABLE)
    void deleteAllByMasterDocNumber(@NonNull Long masterDocNumber);

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.SERIALIZABLE)
    @Modifying
    @Query(value = "DELETE FROM detail WHERE detail_name = :name", nativeQuery = true)
    void delete(@NonNull String name);

    // save methods provide insert-or-update logic, so we're unable to check existing entities by one transaction
    @Override
    default <S extends DetailEntity> S save(S entity) {
        throw new IllegalArgumentException("This method is forbidden");
    };

    @Override
    default <S extends DetailEntity> Iterable<S> saveAll(Iterable<S> entities) {
        throw new IllegalArgumentException("This method is forbidden");
    };
}
