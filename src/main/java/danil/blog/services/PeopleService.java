package danil.blog.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import danil.blog.models.Person;
import danil.blog.repositories.PeopleRepository;

@Service
@Transactional(readOnly = true)
public class PeopleService {

	private final PeopleRepository peopleRepository;
	private final PasswordEncoder passwordEncoder;
	
	
	@Autowired
	public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
		this.peopleRepository = peopleRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public List<Person> findAll()
	{
		return peopleRepository.findAll();
	}
	
	public Person findOne(int id)
	{
		Optional<Person> foundPerson = peopleRepository.findById(id);
		return foundPerson.orElse(null);
	}
	
	public Person findOneByUsername(String username)
	{
		Optional<Person> foundPerson = peopleRepository.findByUsername(username);
		return foundPerson.orElse(null);
	}
	
	public List<Person> findByUsernameContainingIgnoreCase(String username) 
	{
        return peopleRepository.findByUsernameContainingIgnoreCase(username);
    }
	
	@Transactional
	public void save(Person person)
	{
		peopleRepository.save(person);
	}
	
	@Transactional
	public void update(Person updatedPerson, int id)
	{
		Person person = peopleRepository.findById(id).orElse(null);
		person.setUsername(updatedPerson.getUsername());
		person.setEmail(updatedPerson.getEmail());
		person.setPassword(passwordEncoder.encode(updatedPerson.getPassword()));
		peopleRepository.save(person);
	}
	
	@Transactional
	public void updateByUsername(Person updatedPerson, String username)
	{
		Person person = peopleRepository.findByUsername(username).orElse(null);
		person.setUsername(updatedPerson.getUsername());
		person.setEmail(updatedPerson.getEmail());
		person.setPassword(passwordEncoder.encode(updatedPerson.getPassword()));
		peopleRepository.save(person);
	}
	
	@Transactional
	public void updateRoleById(int id, String role)
	{
		peopleRepository.updateRoleById(id, role);
	}
	
	@Transactional
	public void delete(int id)
	{
		peopleRepository.deleteById(id);
	}
	
}
