package danil.blog.repositories;

import danil.blog.models.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

    @Query("SELECT f FROM Friend f WHERE" +
            "(f.user.id = :userId AND f.friend.id = :friendId) OR (f.user.id = :friendId AND f.friend.id = :userId)")
    Optional<Friend> findInvites(int userId, int friendId);

    @Query("SELECT f FROM Friend f WHERE ((f.user.id = :userId AND f.friend.id <> :userId) OR (f.friend.id = :userId AND f.user.id <> :userId)) AND f.status = 'ACCEPTED'")
    List<Friend> findUserFriends(int userId);


    @Modifying
    @Transactional
    @Query("DELETE FROM Friend f WHERE (f.friend.id = :friendId AND f.user.id = :userId) OR (f.friend.id = :userId AND f.user.id = :friendId)")
    void deleteByFriendIdAndUserId(int friendId, int userId);

    //List<Friend> findByUser(Friend user);

    Optional<Friend> findByUserId(int id);
    //Optional<Friend> findOneByUser(Person person);
}