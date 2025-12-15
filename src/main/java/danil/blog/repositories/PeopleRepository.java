package danil.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import danil.blog.models.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer>{

	Optional<Person> findByUsername(String username);
	
}
