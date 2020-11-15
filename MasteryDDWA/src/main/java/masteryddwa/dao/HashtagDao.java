/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
*/

package masteryddwa.dao;

import java.util.List;
import masteryddwa.dto.Hashtag;

public interface HashtagDao {
    /**
     * Add a hashtag to the database
     * 
     * @param hashtag
     * @return 
     */
    Hashtag addHashtag(Hashtag hashtag);
    Hashtag getHashtagById(int id);
    List<Hashtag> getAllHashtags();
    List<Hashtag> getAllHashtagsByPostId(int postId);
    Hashtag getAllHashtagByTag(String tag);
    void editHashtag(Hashtag hashtag);
    void deleteHashtagById(int id);
}
