/**
 * 
 */
package fi.iki.pj.flickr;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.collections.Collection;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.tags.Tag;
import com.flickr4java.flickr.test.TestInterface;

/**
 * @author pj
 *
 */
public class Main {
	static String apiKey = "puppua";
	static String sharedSecret = "noareal";
	static HashSet<String> hs = new HashSet();
	static MariaConnection mc = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Flickr f = new Flickr(apiKey, sharedSecret, new REST());
		PeopleInterface pi  = f.getPeopleInterface();
		//TestInterface testInterface = f.getTestInterface();
		try {
			//PhotoList<Photo> pl = pi.getPublicPhotos("96854014@N02", 500, 2);
			mc = new MariaConnection();
			for (int i = 98 ; i < 100; i++) {
				PhotoList<Photo> pl = pi.getPublicPhotos("96854014@N02", hs, 500, i);
				if (null != pl && !pl.isEmpty())
					pl.forEach(p -> tulosta(p));
			}
		} catch (FlickrException e) {
			e.printStackTrace();
		}
	}

	static void tulosta(Photo p) {
		GeoData gd = p.getGeoData();
		double lat = MariaConnection.NOLLA;
		double lon = MariaConnection.NOLLA;
		int tarkkuus = MariaConnection.EI;
		if (null != gd ) {
			lat = gd.getLatitude();
			lon = gd.getLongitude();
			tarkkuus = gd.getAccuracy();
			System.out.print(gd.getLatitude()+":"+gd.getLongitude()+":"+gd.getAccuracy());
		}
		java.util.Collection<Tag> ct = p.getTags();
		List<Integer> intList = null;
		if (null != ct && !ct.isEmpty())
			intList = ct.stream().map(t -> mc.insertTag(t.getValue())).collect(Collectors.toList());
		System.out.println(p.getId()+": "+ p.getTitle()+":"+  p.getDescription() +":"+
				p.getViews() + ":" + p.getDateTaken()); 
		long id  = Long.parseLong(p.getId());
		mc.insertFlikr(id, p.getTitle(), lat, lon, tarkkuus,
				p.getViews(), p.getDateTaken());
		if (null != intList)
			intList.forEach(i -> mc.insertflicktags(id, i));
	}
	
	static {
		hs.add("tags");
		hs.add("machine_tags");
		hs.add("geo");
		//hs.add("o_dims");
		hs.add("views");
		hs.add("description");
		hs.add("date_taken");
		
	}
	
}
