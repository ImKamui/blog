package danil.blog.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import danil.blog.models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>{
	List<Post> findAllByOwnerUsername(String username);
}
