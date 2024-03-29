/*
Created by: Margaret Donin
Date created: 10/16/20
Date revised:
*/

package blog.dao;

import java.util.List;
import blog.dto.Comment;

public interface CommentDao {
    Comment addComment(Comment comment);
    Comment getCommmentById(int id);
    List<Comment> getAllComments();
    List<Comment> getAllEnabledCommentsByPostId(int postId);
    void editComment(Comment comment);
    void deleteCommentById(int id);
}
