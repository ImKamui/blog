package danil.blog.controllers;

import danil.blog.dto.FriendDto;
import danil.blog.models.Person;
import danil.blog.services.FriendService;
import danil.blog.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/main/friends")
public class FriendController {

    private final PeopleService peopleService;
    private final FriendService friendService;

    @Autowired
    public FriendController(PeopleService peopleService, FriendService friendService) {
        this.peopleService = peopleService;
        this.friendService = friendService;
    }

    @GetMapping("")
    public String getAllUsers(Model model, Authentication auth) {
        Person user = peopleService.findOneByUsername(auth.getName());
        List<FriendDto> allUsers = friendService.getAllUsers(user.getId());
        model.addAttribute("users", allUsers);
        return "friends/friends";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model, Authentication auth) {
        if (query != null && !query.trim().isEmpty()) {
            List<FriendDto> searchResults = friendService.findByUsernameContainingIgnoreCase(query);
            model.addAttribute("searchResults", searchResults);
        } else {
            Person user = peopleService.findOneByUsername(auth.getName());
            List<FriendDto> allUsers = friendService.getAllUsers(user.getId());
            model.addAttribute("searchResults", allUsers);
        }
        return "friends/friends";
    }

    @PostMapping("/add")
    public String sendRequest(int friendId, Authentication auth) {
        Person user = peopleService.findOneByUsername(auth.getName());
        Person friend = peopleService.findOne(friendId);
        friendService.sendRequest(user, friend);
        return "redirect:/main/friends";
    }

    @PostMapping ("/accept")
    public String accept(int friendId, Authentication auth)
    {
        Person user = peopleService.findOneByUsername(auth.getName());
        friendService.acceptRequest(user.getId(), friendId);
        return "redirect:/main/friends";
    }

    @PostMapping("/reject")
    public String reject(int friendId, Authentication auth)
    {
        Person user = peopleService.findOneByUsername(auth.getName());
        friendService.declineRequest(user.getId(), friendId);
        return "redirect:/main/friends";
    }
}

