package danil.blog.services;

import danil.blog.dto.FriendDto;
import danil.blog.models.Friend;
import danil.blog.models.FriendStatus;
import danil.blog.models.Person;
import danil.blog.repositories.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    private final PeopleService peopleService;

    private final FriendRepository friendRepository;

    @Autowired
    public FriendService(PeopleService peopleService,
                         FriendRepository friendRepository) {
        this.peopleService = peopleService;
        this.friendRepository = friendRepository;
    }

    public List<Friend> findAll()
    {
        return friendRepository.findAll();
    }

    public Friend findOne(int id)
    {
        Optional<Friend> foundOne = friendRepository.findById(id);
        return foundOne.orElse(null);
    }

    public void save(Friend friend)
    {
        friendRepository.save(friend);
    }

    public void update(Friend updFriend, Person person)
    {
        Friend friend = friendRepository.findByUserId(person.getId()).orElse(new Friend());
        friend.setStatus(updFriend.getStatus());
        friendRepository.save(friend);
    }

    public void delete(int id)
    {
        friendRepository.deleteById(id);
    }

    public void deleteByUser(Friend user)
    {
        friendRepository.delete(user);
    }

    public List<FriendDto> getAllUsers(int userId)
    {
        List<Person> persons = peopleService.findAll();
        List<FriendDto> result = new ArrayList<>();
        for (Person person : persons)
        {
            if (person.getId() == userId)
            {
                continue;
            }
            Optional<Friend> friendshipOpt = friendRepository.findInvites(userId, person.getId());
            FriendStatus sentStatus = null;
            FriendStatus receivedStatus = null;

            if (friendshipOpt.isPresent())
            {
                Friend friendship = friendshipOpt.get();

                if (friendship.getUser().getId() == userId)
                {
                    sentStatus = friendship.getStatus();
                }
                else if (friendship.getFriend().getId() == userId)
                {
                    receivedStatus = friendship.getStatus();
                }
            }
            System.out.println("[" + userId + "] -> " + person.getUsername() +
                    " | sent=" + sentStatus + ", received=" + receivedStatus);
            result.add(new FriendDto(person.getId(), person.getUsername(), person.getAvatar(), sentStatus, receivedStatus));
        }

        return result;
    }

    public void sendRequest(Person user, Person friend)
    {
//        Person pers_user = user.getId() < friend.getId() ? user : friend;
//        Person pers_friend = user.getId() < friend.getId() ? friend : user;

        Friend friends = new Friend();
        friends.setUser(user);
        friends.setFriend(friend);
        friends.setStatus(FriendStatus.PENDING);
        friendRepository.save(friends);

    }

    public List<FriendDto> findByUsernameContainingIgnoreCase(String username, Person currentUser)
    {
        List<Person> allWhoContains = peopleService.findByUsernameContainingIgnoreCase(username);
        List<FriendDto> result = new ArrayList<>();
        for (Person user : allWhoContains)
        {
            if (user.getUsername().equalsIgnoreCase(username)) continue;

            Optional<Friend> friendshipOpt = friendRepository.findInvites(user.getId(), currentUser.getId());
            FriendStatus sentStatus = null;
            FriendStatus receivedStatus = null;
            if (friendshipOpt.isPresent())
            {
                Friend friendship = friendshipOpt.get();

                if (friendship.getUser().getId() == currentUser.getId())
                {
                    sentStatus = friendship.getStatus();
                }
                else if(friendship.getFriend().getId() == currentUser.getId())
                {
                    receivedStatus = friendship.getStatus();
                }
            }
            result.add(new FriendDto(user.getId(), user.getUsername(), user.getAvatar(), sentStatus, receivedStatus));
        }
        return result;
    }

    public void acceptRequest(int currentUserId, int friendId)
    {
        Optional<Friend> friendOpt = friendRepository.findInvites(currentUserId, friendId);
        if (friendOpt.isPresent())
        {
            Friend friendship = friendOpt.get();
            if (friendship.getFriend().getId() == currentUserId && friendship.getUser().getId() == friendId && friendship.getStatus() == FriendStatus.PENDING)
            {
                friendship.setStatus(FriendStatus.ACCEPTED);
                friendRepository.save(friendship);
            }
        }
    }

    public void declineRequest(int currentUserId, int friendId)
    {
        Optional<Friend> friendOpt = friendRepository.findInvites(currentUserId, friendId);
        if (friendOpt.isPresent())
        {
            Friend friendship = friendOpt.get();
            if (friendship.getFriend().getId() == currentUserId && friendship.getUser().getId() == friendId && friendship.getStatus() == FriendStatus.PENDING)
            {
                friendRepository.delete(friendship);
            }
        }
    }

}
