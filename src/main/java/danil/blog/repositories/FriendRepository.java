package danil.blog.repositories;

import danil.blog.dto.FriendDto;
import danil.blog.models.Friend;
import danil.blog.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

    @Query("SELECT f FROM Friend f WHERE" +
            "(f.user.id = :userId AND f.friend.id = :friendId) OR (f.user.id = :friendId AND f.friend.id = :userId)")
    Optional<Friend> findInvites(int userId, int friendId);

    List<Friend> findByUser(Friend user);

    Optional<Friend> findByUserId(int id);
    Optional<Friend> findOneByUser(Person person);
}