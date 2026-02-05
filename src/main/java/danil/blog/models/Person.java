package danil.blog.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
	
	@Lob
	@Column(name="avatar", columnDefinition = "bytea")
	@JdbcTypeCode(SqlTypes.VARBINARY)
	private byte[] avatar;
	
	@OneToMany(mappedBy = "owner")
	private List<Post> posts;
	
	@ManyToMany
	@JoinTable(name = "friends", joinColumns = { @JoinColumn(name = "whom")},
	inverseJoinColumns = {@JoinColumn(name = "who")})
	private Set<Person> friends = new HashSet();
	
	@ManyToMany
	@JoinTable(name = "friends", joinColumns = { @JoinColumn(name = "who")},
	inverseJoinColumns = {@JoinColumn(name = "whom")})
	private Set<Person> users = new HashSet();
}
