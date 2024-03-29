/*
Created by: Margaret Donin
Date created:
Date revised:
*/

package blog.dao;

import java.util.List;
import blog.dto.Post;

public interface PostDao {
    Post addPost(Post post);
    Post getPostById(int id);
    List<Post> getAllPosts();
    List<Post> getAllEnabledBlogs();
    List<Post> getAllEnabledStatics();
    List<Post> getAllPostsByUserId(int userId);
    List<Post> getAllPostsByHashtagId(int hashtagId);
    void editPost(Post post);
    void deletePost(int postId);
    void deleteExpiredPosts();
}
