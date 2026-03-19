package danil.blog.controllers;

import java.io.IOException;

import danil.blog.dto.PersonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	
	@DeleteMapping("/myPosts/{id}")
	public String delete(@PathVariable("id") int id)
	{
		postService.delete(id);
		return "redirect:/main/my_posts";
	}
	
	
	@GetMapping("/avatar/{id}")
	@ResponseBody
	public ResponseEntity<byte[]> getAvatar(@PathVariable int id)
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
