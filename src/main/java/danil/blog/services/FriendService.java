package danil.blog.services;

import danil.blog.dto.FriendDto;
import danil.blog.dto.PersonDto;
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

    public void deleteByFriendIdAndUserId(int friendId, int userId)
    {
        friendRepository.deleteByFriendIdAndUserId(friendId, userId);
    }

    public void deleteByUser(Friend user)
    {
        friendRepository.delete(user);
    }

    public List<FriendDto> getAllUsers(int userId)
    {
        List<PersonDto> persons = peopleService.findAll();
        List<FriendDto> result = new ArrayList<>();
        for (PersonDto person : persons)
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

    public void sendRequest(PersonDto user, PersonDto friend)
    {
//        Person pers_user = user.getId() < friend.getId() ? user : friend;
//        Person pers_friend = user.getId() < friend.getId() ? friend : user;
        Person person_user = peopleService.findOneEntityById(user.getId());
        Person person_frnd= peopleService.findOneEntityById(friend.getId());
        Friend friends = new Friend();
        friends.setUser(person_user);
        friends.setFriend(person_frnd);
        friends.setStatus(FriendStatus.PENDING);
        friendRepository.save(friends);

    }

    public List<FriendDto> findByUsernameContainingIgnoreCase(String username, PersonDto currentUser)
    {
        List<PersonDto> allWhoContains = peopleService.findByUsernameContainingIgnoreCase(username);
        List<FriendDto> result = new ArrayList<>();
        for (PersonDto user : allWhoContains)
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

    public List<FriendDto> findUserFriends(int userId)
    {
        List<Friend> friendsList = friendRepository.findUserFriends(userId);
        List<FriendDto> result = new ArrayList<>();

        for (Friend friend : friendsList)
        {
            Person friendPerson;
            if (friend.getUser().getId() == userId)
            {
                friendPerson = friend.getFriend();
            }
            else
            {
                friendPerson = friend.getUser();
            }
            result.add(new FriendDto(friendPerson.getId(), friendPerson.getUsername(), friendPerson.getAvatar(), null ,null));
        }
        return result;
    }



}
