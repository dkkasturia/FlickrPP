package astegic.kss.flickrpp;

/**
 * Created by Dhanesh on 11/13/2016.
 */
public class PhotoInfo {
    public long photo_id;
    public int farm;
    public int server;
    public String secret;
    public String original_format;
    public String thumbnail_url;
    public String picture_url;
    public String picture_title;

    PhotoInfo(long photo_id, int farm, int server, String secret, String original_format){
        this.photo_id = photo_id;
        this.farm = farm;
        this.server = server;
        this.secret = secret;
        this.original_format = original_format;
    }

    public long getPhotoID(){
        return photo_id;
    }

    public int getFarm(){
        return farm;
    }

    public int getServer(){
        return server;
    }

    public String getSecret(){
        return secret;
    }

    public String getOriginalFormat(){
        return original_format;
    }

    public String getThumbnail_url(){
        return thumbnail_url;
    }

    public String getPicture_url(){
        return picture_url;
    }

    public String getPicture_title() {return picture_title;}

    public void setThumbnail_url() {
        thumbnail_url = "https://farm" + String.valueOf(farm) +"." + "staticflickr.com/" + String.valueOf(server) + "/"
                + String.valueOf(photo_id) + "_" + String.valueOf(secret) + "_s"+ "." + String.valueOf(original_format);
    }

    public void setPicture_url() {
        picture_url = "https://farm" + String.valueOf(farm) + "." + "staticflickr.com/" + String.valueOf(server) + "/"
                + String.valueOf(photo_id) + "_" + String.valueOf(secret) + "." + String.valueOf(original_format);
    }

    public void setPicture_title(String title) {
        this.picture_title = title;
    }


}
