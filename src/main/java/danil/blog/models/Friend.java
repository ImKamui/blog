package danil.blog.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "friendship")
@Data
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    private Person user;

    @ManyToOne
    private Person friend;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FriendStatus status;
}
