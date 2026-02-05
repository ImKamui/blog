package danil.blog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import danil.blog.models.Person;
import danil.blog.security.PersonDetails;
import danil.blog.services.PeopleService;
import danil.blog.services.PersonDetailsService;
import danil.blog.services.PostService;

@Controller
@RequestMapping("/main/admin")
public class AdminController {

	private final PersonDetailsService personDetailsService;

	private final PeopleService peopleService;
	private final PostService postService;
	
	
	@Autowired
	public AdminController(PeopleService peopleService, PostService postService, PersonDetailsService personDetailsService) {
		this.peopleService = peopleService;
		this.postService = postService;
		this.personDetailsService = personDetailsService;
	}
	
	
	@GetMapping("")
	public String admin(Model model)
	{
		model.addAttribute("people", peopleService.findAll());
		return "admin/admin";
	}
	
	@GetMapping("/search")
	public String search(@RequestParam("query") String query, Model model)
	{
		if (query != null && !query.trim().isEmpty())
		{
			List<Person> searchResults = peopleService.findByUsernameContainingIgnoreCase(query);
			model.addAttribute("searchResults", searchResults);
		}
		else
		{
			model.addAttribute("searchResults", peopleService.findAll());
		}
		return "admin/admin";
	}
	
	@DeleteMapping("user/{id}")
	public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails)authentication.getPrincipal();
		Person person = personDetails.getPerson();
		if (id == person.getId())
		{
			redirectAttributes.addFlashAttribute("errorMessage", "Ошибка удаления. Вы не можете удалить себя.");
			return "redirect:/main/admin";
		}
		Person userToDelete = peopleService.findOne(id);
		if ("ADMIN".equals(userToDelete.getRole()) || "KING_ADMIN".equals(userToDelete.getRole()))
		{
			redirectAttributes.addFlashAttribute("errorMessage", "Ошибка удаления. Пользователь является администратором или высшим администратором.");
			return "redirect:/main/admin";
		}
		
		try
		{
			peopleService.delete(id);
		}
		catch(Exception e)
		{
			redirectAttributes.addFlashAttribute("errorMessage", "Ошибка удаления.");
		}
		return "redirect:/main/admin";
	}
	
	@DeleteMapping("post/{id}")
	public String deleteAnyPost(@PathVariable("id") int id, RedirectAttributes redirectAttributes)
	{
		postService.delete(id);
		return "redirect:/main";
	}
	
	@PatchMapping("/setAdmin/{id}")
	public String setAdmin(@PathVariable("id") int id)
	{
		peopleService.updateRoleById(id, "ADMIN");
		return "redirect:/main/admin";
	}
	
	@PatchMapping("/setUser/{id}")
	public String setUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal PersonDetails personDetails)
	{
		Person thisPerson = personDetails.getPerson();
		if (id == thisPerson.getId())
		{
			redirectAttributes.addFlashAttribute("errorMessage", "Ошибка. Вы не можете снять себя с поста администратора.");
			return "redirect:/main/admin";
		}
		if ("KING_ADMIN".equals(peopleService.findOne(id).getRole()))
		{
			redirectAttributes.addFlashAttribute("errorMessage", "Ошибка. Вы не можете снять с поста высшего администратора.");
			return "redirect:/main/admin";
		}
		peopleService.updateRoleById(id, "USER");
		return "redirect:/main/admin";
	}
}
