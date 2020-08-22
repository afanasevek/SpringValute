package main.Repo;

import main.model.Valute;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ValuteRepository extends CrudRepository<Valute, Integer> {
}
