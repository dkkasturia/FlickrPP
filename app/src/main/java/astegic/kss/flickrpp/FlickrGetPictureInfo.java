package astegic.kss.flickrpp;

import android.os.AsyncTask;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Dhanesh on 11/12/2016.
 */
public class FlickrGetPictureInfo extends AsyncTask<String, Void, String> {

    private Exception exception;
    int responseCode;
    private final String USER_AGENT = "Mozilla/5.0";

    long photo_id;
    String farm, server, secret, originalformat, title;


    protected String doInBackground(String... urls) {
        try {
            String photo_id_str = urls[0];
            photo_id = Long.parseLong(photo_id_str);

            String https_url = "https://api.flickr.com/services/rest/?method=flickr.photos.getInfo&api_key=6074142999da709e665594b3f84f7759&photo_id=" + urls[0];
            // String https_url = "https://www.flickr.com/services/api/explore/flickr.photos.getInfo?format=rest&photo_id=" + urls[0];
            URL url = new URL(https_url);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            // optional default is GET
            // con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            // responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            // System.out.println(response.toString());


            return response.toString();
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    protected void onPostExecute(String resp) {
        // TODO: check this.exception
        // TODO: do something with the feed
        System.out.println(resp);
        String pict_info_xml = resp;

        String last_word = pict_info_xml.substring(pict_info_xml.lastIndexOf(" ")+1);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // Create a DOM-building parser
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Parse and return the content of the XML file into a Document instance


            Document document = builder.parse(new InputSource(new StringReader(pict_info_xml)));
            // Echo nodes starting from the root element
            echoNode(document.getDocumentElement());

            // get value of farm, server, secret attributes given in the 'photo' (root) tag -- Dhanesh
            Node root_node = document.getDocumentElement();

            String root_node_name = root_node.getNodeName();

            NodeList child_nodes = root_node.getChildNodes();

            for (int i=0; i < child_nodes.getLength(); i++) {

                String node_name = child_nodes.item(i).getNodeName();
                if (node_name.equals("photo")) {
                    Node photo_node = child_nodes.item(i);

                    // get attributes
                    NamedNodeMap attributeMap = photo_node.getAttributes();

                    for (int j = 0; j < attributeMap.getLength(); j++) {
                        Attr attr = (Attr) attributeMap.item(j);

                        String attr_name = attr.getName();
                        String attr_value = attr.getValue();

                        if (attr_name.equals("farm")) {
                            farm = attr_value;
                            print("farm :" + "=\"" + farm + "\" ");
                        } else if (attr_name.equals("server")) {
                            server = attr_value;
                            print("server :" + "=\"" + server + "\" ");
                        } else if (attr_name.equals("secret")) {
                            secret = attr_value;
                            print("secret :" + "=\"" + secret + "\" ");
                        } else if (attr_name.equals("originalformat")) {
                            originalformat = attr_value;
                            print("originalformat :" + "=\"" + originalformat + "\" ");
                        }
                    }

                    // get photo title

                    NodeList child_nodes_photo = photo_node.getChildNodes();
                    for (int k=0; k<child_nodes_photo.getLength(); k++){
                        String node_nm = child_nodes_photo.item(k).getNodeName();

                        if (node_nm.equals("title")){
                            title = child_nodes_photo.item(k).getTextContent();
                        }
                    }

                    // Create photo object
                    PhotoInfo photoInfo = new PhotoInfo(photo_id, Integer.valueOf(farm), Integer.valueOf(server), secret, originalformat);

                    photoInfo.setPicture_url();
                    photoInfo.setThumbnail_url();
                    photoInfo.setPicture_title(title);

                    MainActivity.downloadedPhotoInfo = photoInfo;
                }
            }

            // photo indexes must always be sequential
            MainActivity.photo_info_array[MainActivity.next_photo_to_download] = MainActivity.downloadedPhotoInfo;

            // get next picture info until 5 pictures are obtained, picture info is got sequentially

            int curr_photo_ind = MainActivity.next_photo_to_download++;

            if (curr_photo_ind < MainActivity.MAX_PHOTOS) {
                String photo_id = String.valueOf(MainActivity.photo_id_array[curr_photo_ind]);
                try {
                    new FlickrGetPictureInfo().execute(photo_id);
                } catch (Exception e) {
                    String msg = e.getMessage();
                    msg = e.getLocalizedMessage();
                }
            }

            if (curr_photo_ind == MainActivity.MAX_PHOTOS){
                MainActivity.main_activity_instance.displayPhotoList();
            }

        } catch (SAXException se) {
            // Error generated during parsing
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    /** Recursive implementation of traversing the DOM tree and printing nodes. */
    public static void echoNode(Node aNode) {
        short nodeType = aNode.getNodeType();

        if (nodeType == Node.ELEMENT_NODE) {
            // Element node: safe downcast and process
            Element element = (Element) aNode;

            // Always print <element
            // closing depends on children presence
            print("<" + element.getNodeName() + " ");

            // Print attributes if any
            NamedNodeMap attributeMap = element.getAttributes();
            for (int i = 0; i < attributeMap.getLength(); i++) {
                Attr attr = (Attr) attributeMap.item(i);

                // Print name="value" string
                print(attr.getName() + "=\"" + attr.getValue() + "\" ");
            }

            if (element.hasChildNodes()) {
                // Children: open element
                println(">");

                // Process children recursively
                for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                    echoNode(element.getChildNodes().item(i));
                }

                // Children: closing tag
                println("</" + element.getNodeName() + ">");
            } else {
                // No children: close element
                println("/>");
            }

        } else if (nodeType == Node.TEXT_NODE) {
            // Text node: just echo
            println(aNode.getNodeValue());
        } else {
            // ignore others like comments for now
        }

    }

    /** Basic print method to save typing. */
    public static void println(String s) {
        System.out.println(s);
    }

    /** Basic print method to save typing. */
    public static void print(String s) {
        System.out.print(s);
    }
}
