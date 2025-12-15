package danil.blog.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import danil.blog.models.Post;
import danil.blog.repositories.PeopleRepository;
import danil.blog.repositories.PostRepository;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PeopleRepository peopleRepository;

	private final PostRepository postRepository;
	
	@Autowired
	public PostService(PostRepository postRepository, PeopleRepository peopleRepository) {
		this.postRepository = postRepository;
		this.peopleRepository = peopleRepository;
	}
	
	public List<Post> findAllByOwnerUsername(String username)
	{
		return postRepository.findAllByOwnerUsername(username);
	}
	
	public List<Post> findAll()
	{
		return postRepository.findAll();
	}
	
	public Post findOne(int id)
	{
		Optional<Post> post = postRepository.findById(id);
		return post.orElse(null);
	}
	
	@Transactional
	public void save(Post post)
	{
		postRepository.save(post);
	}
	
	@Transactional
	public void update(Post updatedPost, int id)
	{
		Post post = postRepository.findById(id).orElse(null);
		post.setPostText(updatedPost.getPostText());
		post.setPostImage(updatedPost.getPostImage());
		postRepository.save(post);
	}
	
	@Transactional
	public void delete(int id)
	{
		postRepository.deleteById(id);
	}
}
