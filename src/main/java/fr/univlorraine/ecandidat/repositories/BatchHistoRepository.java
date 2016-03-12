package fr.univlorraine.ecandidat.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;

@Repository
public interface BatchHistoRepository extends JpaRepository<BatchHisto, Integer> {
	
	BatchHisto findByBatchCodBatchAndStateBatchHisto(String codBatch, String batchHisto);
	
	BatchHisto findFirst1ByBatchCodBatchOrderByIdBatchHistoDesc(String codBatch);
	
	List<BatchHisto> findFirst100ByBatchCodBatchOrderByIdBatchHistoDesc(String codBatch);

	List<BatchHisto> findByStateBatchHisto(String state);
	
	List<BatchHisto> findByDateDebBatchHistoLessThan(LocalDateTime dateTime);
}
