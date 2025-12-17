package danil.blog.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import danil.blog.models.Person;
import jakarta.transaction.Transactional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer>{

	Optional<Person> findByUsername(String username);
	List<Person> findByUsernameContainingIgnoreCase(String username);
	
	@Transactional
	@Modifying
	@Query("UPDATE Person p SET p.role = :role WHERE p.id = :id")
	void updateRoleById(@Param("id") int id, @Param("role") String role);
	
}
