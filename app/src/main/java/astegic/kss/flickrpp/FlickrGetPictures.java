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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FlickrGetPictures extends AsyncTask<String, Void, String> {

    private Exception exception;
    int responseCode;
    private final String USER_AGENT = "Mozilla/5.0";


    protected String doInBackground(String... urls) {
        try {

            String https_url = "https://api.flickr.com/services/feeds/photos_public.gne?tags=" + urls[0];

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
        // System.out.println(resp);
        String picts_xml = resp;

        // String last_word = picts_xml.substring(picts_xml.lastIndexOf(" ")+1);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // Create a DOM-building parser
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Parse and return the content of the XML file into a Document instance


            Document document = builder.parse(new InputSource(new StringReader(picts_xml)));
            // Echo nodes starting from the root element
            echoNode(document.getDocumentElement());

            // get value of 'id' in all 'entry' tags -- Dhanesh
            Node root_node = document.getDocumentElement();

            NodeList entry_list = root_node.getChildNodes();

            int photo_id_ind = 0;

            for (int i=0; i < entry_list.getLength(); i++){

                String node_name = entry_list.item(i).getNodeName();
                println("child element :" + node_name);
                if (node_name.equals("entry")){
                    Node this_node = entry_list.item(i);

                    NodeList entry_children = this_node.getChildNodes();

                    for (int j=0; j<entry_children.getLength(); j++){
                        String node_nm = entry_children.item(j).getNodeName();
                        if (node_nm != null) {
                            if (node_nm.equals("id")){
                                String entry_id = entry_children.item(j).getTextContent();
                                println("id :" + entry_id);
                                long photo_id = 0;
                                String photo_id_str = entry_id.substring(entry_id.lastIndexOf("/")+1);
                                photo_id = Long.valueOf(photo_id_str);
                                if (photo_id_ind <10){
                                    MainActivity.photo_id_array[photo_id_ind] = photo_id;
                                    photo_id_ind++;
                                }
                            }
                        }
                    }
                }
            }

            // but get photo info thread must only run after completion of get pictures thread
            // for each photo-id, get photo info which is necessary to download the photo and its thumbnail
            // get first photo info next_photo_to_download is 0, initialize for the next run

            MainActivity.next_photo_to_download = 0;
            String photo_id = String.valueOf(MainActivity.photo_id_array[MainActivity.next_photo_to_download]);
            try {
                new FlickrGetPictureInfo().execute(photo_id);
            } catch (Exception e) {
                String msg = e.getMessage();
                msg = e.getLocalizedMessage();
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
