package danil.blog.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/main")
public class MainController {

    private final PersonDetailsService personDetailsService;

	private final PeopleService peopleService;
	private final PostService postService;
	
	
	@Autowired
	public MainController(PeopleService peopleService, PostService postService, PersonDetailsService personDetailsService) {
		this.peopleService = peopleService;
		this.postService = postService;
		this.personDetailsService = personDetailsService;
	}
	
	@GetMapping("")
	public String mainPage(Model model)
	{
		model.addAttribute("posts", postService.findAll());
		return "main/main";
	}
	
	@GetMapping("/new_post")
	public String newPost(Model model)
	{
		model.addAttribute("post", new Post());
		return "main/newPost";
	}
	
	@PostMapping
	public String createPost(@ModelAttribute("post") Post post, BindingResult bindingResult, @RequestParam("file") MultipartFile file)
	{
		if (bindingResult.hasErrors())
		{
			return "main/newPost";
		}
		Post postModel = new Post();
		postModel.setPostText(post.getPostText());
		if (!file.isEmpty())
		{
			try
			{
				byte[] imageBytes = file.getBytes();
				postModel.setPostImage(imageBytes);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				bindingResult.addError(new ObjectError("file", "Ошибка загрузки файла"));
				return "main/newPost";
				
			}
		}
		else
		{
			postModel.setPostImage(null);
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
		postModel.setOwner(personDetails.getPerson());
		postService.save(postModel);
		return "redirect:/main";
	}
	
	@GetMapping("/my_posts")
	public String myPosts(Model model)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
		Person person = personDetails.getPerson();
		model.addAttribute("posts", postService.findAllByOwnerUsername(person.getUsername()));
		return "main/myPosts";
	}
	
	@GetMapping("/image/{postId}")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@PathVariable int postId)
	{
		Post post = postService.findOne(postId);
		if (post != null && post.getPostImage() != null)
		{
			return ResponseEntity.ok()
					.contentType(MediaType.IMAGE_JPEG)
					.contentType(MediaType.IMAGE_PNG)
					.body(post.getPostImage());
		}
		return ResponseEntity.notFound().build();
	}
	
	
	@GetMapping("/settings")
	public String settings(Model model, @AuthenticationPrincipal PersonDetails personDetails)
	{
		if (personDetails == null)
		{
			return "redirect:/auth/login";
		}
		Person person = personDetails.getPerson();
		model.addAttribute("person", peopleService.findOneByUsername(person.getUsername()));
		return "main/settings";
	}
	
	@PatchMapping("/settings/{username}")
	public String updatePerson(@ModelAttribute("person") Person person, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response)
	{
		if (bindingResult.hasErrors())
		{
			bindingResult.addError(new ObjectError("person", "Ошибка изменения данных"));
			return "main/settings";
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof PersonDetails)
		{
			PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
			Person updPerson = personDetails.getPerson();
			String username = updPerson.getUsername();
			
			peopleService.updateByUsername(person, username);
			new SecurityContextLogoutHandler().logout(request, response, authentication);
			return "redirect:/auth/login";
		}
		else
		{
			return "redirect:/auth/login";
		}
		
	}
	
	@DeleteMapping("/myPosts/{id}")
	public String delete(@PathVariable("id") int id)
	{
		postService.delete(id);
		return "redirect:/main/my_posts";
	}
}
