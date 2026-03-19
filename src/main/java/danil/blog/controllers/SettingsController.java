package danil.blog.controllers;

import danil.blog.dto.PersonDto;
import danil.blog.dto.ProfileUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import java.util.Objects;

@Controller
@RequestMapping("/main/settings")
public class SettingsController {
	private final PersonDetailsService personDetailsService;

	private final PeopleService peopleService;
	private final PostService postService;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public SettingsController(PeopleService peopleService, PostService postService, PersonDetailsService personDetailsService,
							  PasswordEncoder passwordEncoder) {
		this.peopleService = peopleService;
		this.postService = postService;
		this.personDetailsService = personDetailsService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping("")
	public String settings(Model model, @AuthenticationPrincipal PersonDetails personDetails)
	{
		if (personDetails == null)
		{
			return "redirect:/auth/login";
		}
		Person person = personDetails.getPerson();
		PersonDto personDto = peopleService.findOneByUsername(person.getUsername());
		ProfileUpdateForm form = new ProfileUpdateForm();
		form.setId(personDto.getId());
		form.setUsername(personDto.getUsername());
		form.setEmail(personDto.getEmail());
		form.setAvatar(personDto.getAvatar());
		model.addAttribute("form", form);
		return "settings/settings";
	}
	
	@PatchMapping("/{username}")
	public String updatePerson(@ModelAttribute("form") ProfileUpdateForm form, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "file", required = false) MultipartFile file)
	{
		if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("person", "Ошибка изменения данных"));
            return "settings/settings";
        }
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication.getPrincipal() instanceof PersonDetails personDetails)) {
			return "redirect:/auth/login";
		}

		Person currentPerson = personDetails.getPerson();

		Person updPerson = new Person();
		updPerson.setId(currentPerson.getId());
		updPerson.setUsername(!form.getUsername().isEmpty() ? form.getUsername() : currentPerson.getUsername());
		updPerson.setEmail(!form.getEmail().isEmpty() ? form.getEmail():currentPerson.getEmail());
		updPerson.setRole(currentPerson.getRole());
		updPerson.setAvatar(form.getAvatar());
		if (file != null && !file.isEmpty()) {
			try {
				updPerson.setAvatar(file.getBytes());
			} catch (Exception ex){
				bindingResult.addError(new ObjectError("file","Не удалось загрузить файл"));
				return "settings/settings";
			}
		}
		else {
			updPerson.setAvatar(currentPerson.getAvatar());
		}
		if (form.getPassword() != null && !form.getPassword().isEmpty()) {
			updPerson.setPassword(passwordEncoder.encode(form.getPassword()));
		} else {
			updPerson.setPassword(currentPerson.getPassword());
		}
		peopleService.updateByUsername(updPerson, currentPerson.getUsername());
		if (!Objects.equals(updPerson.getPassword(), currentPerson.getPassword())) {
			new SecurityContextLogoutHandler().logout(request,response ,SecurityContextHolder.getContext().getAuthentication());
			return "redirect:/auth/login";
		}
		return "redirect:/main/settings";
//		if (authentication != null && authentication.getPrincipal() instanceof PersonDetails personDetails)
//		{
//            Person updPerson = personDetails.getPerson();
//            if (file != null && !file.isEmpty() && !file.getOriginalFilename().isEmpty())
//            {
//                try
//                {
//                    form.setAvatar(file.getBytes());
//                }
//                catch(Exception e)
//                {
//                    e.printStackTrace();
//                    bindingResult.addError(new ObjectError("file", "Ошибка загрузки файла"));
//                    return "settings/settings";
//                }
//            }
//            else
//            {
//                form.setAvatar(updPerson.getAvatar());
//            }
//			if (form.getUsername().isEmpty())
//			{
//				form.setUsername(updPerson.getUsername());
//			}
//			if (form.getEmail().isEmpty())
//			{
//				form.setEmail(updPerson.getEmail());
//			}
//			if (form.getPassword().isEmpty() || form.getPassword() == null)
//			{
//                form.setPassword(updPerson.getPassword());
//			}
//            else
//            {
//                if (passwordEncoder.matches(form.getPassword(), updPerson.getPassword()))
//                {
//					form.setPassword(updPerson.getPassword());
//                }
//                else
//                {
//					form.setPassword(passwordEncoder.encode(updPerson.getPassword()));
//                }
//            }
//			peopleService.updateByUsername(form, updPerson.getUsername());
//			new SecurityContextLogoutHandler().logout(request, response, authentication);
//			return "redirect:/auth/login";
//		}
//		else
//		{
//			return "redirect:/auth/login";
//		}
		
	}
	@GetMapping("/avatar/{id}")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@PathVariable int id)
	{
		PersonDto person = peopleService.findOne(id);
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
