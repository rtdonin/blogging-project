/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
 */

package masteryddwa.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Post {

    private int id;
    
    @NotBlank
    @Size(max=255, message="Title can\'t be longer than 255 characters.")
    private String title;
    private boolean enabled;
    private boolean staticPost;
    
    @NotBlank(message = "Please include text.")
    private String body;
    
    @NotNull(message = "Start date must be filled in.")
    private LocalDate start;
    
    @NotNull(message = "End date must not be blank.")
    @Future(message = "End date must be in the future.")
    private LocalDate end;
    
    @NotEmpty(message = "Hashtags should be filled in.")
    private List<Hashtag> hashtags;
    
    private User user;

    public Post() {

    }

    public Post(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStaticPost() {
        return staticPost;
    }

    public void setStaticPost(boolean staticPost) {
        this.staticPost = staticPost;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.title);
        hash = 97 * hash + (this.enabled ? 1 : 0);
        hash = 97 * hash + (this.staticPost ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.body);
        hash = 97 * hash + Objects.hashCode(this.start);
        hash = 97 * hash + Objects.hashCode(this.end);
        hash = 97 * hash + Objects.hashCode(this.hashtags);
        hash = 97 * hash + Objects.hashCode(this.user);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Post other = (Post) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.staticPost != other.staticPost) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.body, other.body)) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.end, other.end)) {
            return false;
        }
        if (!Objects.equals(this.hashtags, other.hashtags)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", title=" + title + ", enabled=" + enabled + ", staticPost=" + staticPost + ", body=" + body + ", start=" + start + ", end=" + end + ", hashtags=" + hashtags + ", user=" + user + '}';
    }

}
