package danil.blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import danil.blog.models.Person;
import danil.blog.models.Post;
import danil.blog.security.PersonDetails;
import danil.blog.services.PeopleService;
import danil.blog.services.PersonDetailsService;
import danil.blog.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/main/settings")
public class SettingsController {
	private final PersonDetailsService personDetailsService;

	private final PeopleService peopleService;
	private final PostService postService;
	
	
	@Autowired
	public SettingsController(PeopleService peopleService, PostService postService, PersonDetailsService personDetailsService) {
		this.peopleService = peopleService;
		this.postService = postService;
		this.personDetailsService = personDetailsService;
	}
	
	@GetMapping("")
	public String settings(Model model, @AuthenticationPrincipal PersonDetails personDetails)
	{
		if (personDetails == null)
		{
			return "redirect:/auth/login";
		}
		Person person = personDetails.getPerson();
		model.addAttribute("person", peopleService.findOneByUsername(person.getUsername()));
		return "settings/settings";
	}
	
	@PatchMapping("/{username}")
	public String updatePerson(@ModelAttribute("person") Person person, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "file", required = false) MultipartFile file)
	{
		if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("person", "Ошибка изменения данных"));
            return "settings/settings";
        }
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof PersonDetails)
		{
			PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
			Person updPerson = personDetails.getPerson();
            if (file != null && !file.isEmpty() && !file.getOriginalFilename().isEmpty())
            {
                try
                {
                    person.setAvatar(file.getBytes());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    bindingResult.addError(new ObjectError("file", "Ошибка загрузки файла"));
                    return "settings/settings";
                }
            }
            else
            {
                person.setAvatar(updPerson.getAvatar());
            }
			peopleService.updateByUsername(person, updPerson.getUsername());
			new SecurityContextLogoutHandler().logout(request, response, authentication);
			return "redirect:/auth/login";
		}
		else
		{
			return "redirect:/auth/login";
		}
		
	}
	@GetMapping("/avatar/{id}")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@PathVariable int id)
	{
		Person person = peopleService.findOne(id);
		if (person != null && person.getAvatar() != null)
		{
			return ResponseEntity.ok()
					.contentType(MediaType.IMAGE_JPEG)
					.contentType(MediaType.IMAGE_PNG)
					.body(person.getAvatar());
		}
		return ResponseEntity.notFound().build();
	}
	
	
}
