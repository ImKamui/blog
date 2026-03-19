package danil.blog.models;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
	
	@OneToMany(mappedBy = "owner", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Friend> users;

    @OneToMany(mappedBy = "friend", fetch=FetchType.LAZY)
    private List<Friend> friends;
}
