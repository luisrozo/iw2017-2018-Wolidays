package es.uca.wolidays.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.uca.wolidays.backend.entities.IVA;

@Repository
public interface IVARepository extends CrudRepository<IVA, Integer>{
	
	public IVA findByPais(String pais);

}
