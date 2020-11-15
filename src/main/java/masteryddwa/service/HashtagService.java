/*
Created by: Margaret Donin
Date created:
Date revised:
 */
package masteryddwa.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import masteryddwa.dao.HashtagDao;
import masteryddwa.dto.Hashtag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class HashtagService {

    @Autowired
    private HashtagDao hashtagDao;

    public List<Hashtag> getHashtags(String hashtags) {
        String[] tags = hashtags.split(" ");
        List<Hashtag> hash = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\S+");

        for (String s : tags) {
            s = s.replace("#", " ").replace(",", " ");
            s = s.trim();

            Matcher matcher = pattern.matcher(s);

            if (matcher.matches()) {
                Hashtag h = new Hashtag();
                h.setTag(s);
                try {
                    h = hashtagDao.addHashtag(h);
                } catch (DuplicateKeyException ex) {
                    h = hashtagDao.getAllHashtagByTag(s);
                }

                hash.add(h);
            }
        }

        return hash;
    }

    public String stringifyHashtags(List<Hashtag> hashtags) {
        String tagString = "";

        Iterator iterator = hashtags.listIterator();

        while (iterator.hasNext()) {
            Hashtag h = (Hashtag) iterator.next();
            tagString += h.getTag() + " ";
        }
        return tagString;
    }

}
