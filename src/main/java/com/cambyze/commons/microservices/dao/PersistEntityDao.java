package com.cambyze.commons.microservices.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.cambyze.commons.microservices.model.PersistEntity;

/**
 * Interface for persistence entity DAO
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
@NoRepositoryBean
public interface PersistEntityDao<T extends PersistEntity> extends JpaRepository<T, Long> {

  /**
   * find a persistence entity with its reference
   * 
   * @param reference reference of the entity
   * @return an entity
   */
  public T findByReference(String reference);

}
