package danil.blog.services;

import java.util.List;
import java.util.Optional;

import danil.blog.dto.PersonDto;
import danil.blog.dto.mapper.PersonMapping;
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
	private final PersonMapping personMapping;
	
	@Autowired
	public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, PersonMapping personMapping) {
		this.peopleRepository = peopleRepository;
		this.passwordEncoder = passwordEncoder;
        this.personMapping = personMapping;
    }
	
	public List<PersonDto> findAll()
	{
		return peopleRepository.findAll().stream().map(personMapping::toDto).toList();
	}
	
	public PersonDto findOne(int id)
	{
		Optional<PersonDto> foundPerson = peopleRepository.findById(id).map(personMapping::toDto);
		return foundPerson.isPresent() ?  (foundPerson.map(p -> p)).get(): null;
	}
	
	public PersonDto findOneByUsername(String username)
	{
		Optional<PersonDto> foundPerson = peopleRepository.findByUsername(username).map(personMapping::toDto);
		return foundPerson.orElseGet(()->null);
	}
	
	public List<PersonDto> findByUsernameContainingIgnoreCase(String username)
	{
        return peopleRepository.findByUsernameContainingIgnoreCase(username).stream().map(personMapping::toDto).toList();
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
		person.setAvatar(updatedPerson.getAvatar());
		peopleRepository.save(person);
	}
	
	@Transactional
	public void updateByUsername(Person updatedPerson, String username)
	{
		Person person = peopleRepository.findByUsername(username).orElse(null);
		person.setUsername(updatedPerson.getUsername());
		person.setEmail(updatedPerson.getEmail());
		person.setPassword(updatedPerson.getPassword());
		person.setAvatar(updatedPerson.getAvatar());
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

	public Person findOneEntityById(int id)
	{
		return peopleRepository.findById(id).orElseThrow(IllegalArgumentException :: new);
	}
	
}
