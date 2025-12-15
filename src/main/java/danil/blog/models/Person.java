package danil.blog.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Entity
@Table(name = "person")
@Data
public class Person {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "username")
	@NotEmpty(message = "Имя не должно быть пустым")
	@Size(min = 2, max = 100, message = "Имя не должно быть меньше 2 и больше 100 символов")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "email")
	@Email
	@NotEmpty(message = "Поле не должно быть пустым")
	private String email;
	
	@Column(name = "role")
	private String role;
	
	@OneToMany(mappedBy = "owner")
	private List<Post> posts;
}
