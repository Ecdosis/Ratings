/*
 * This file is part of Ratings.
 *
 *  Ratings is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Ratings is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ratings.  If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Desmond Schmidt 2016
 */
package ratings.handler.post;

import calliope.core.constants.Database;
import calliope.core.database.Connection;
import calliope.core.constants.JSONKeys;
import calliope.core.database.Connector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import ratings.exception.RatingsException;
import ratings.constants.Params;
import ratings.Base64;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

/**
 * Handle delete not supported by all browsers so it's a post
 * @author desmond
 */
public class RatingsDeleteHandler {
    public void handle( HttpServletRequest request,
        HttpServletResponse response, String urn ) throws RatingsException
    {
        String userdata = request.getParameter(Params.USERDATA);
        String docid = request.getParameter(Params.DOCID);
        System.out.println("docid="+docid+" userdata="+userdata);
        if ( userdata != null && docid != null )
        {
            String key = "I tell a settlers tale of the old times";
            int klen = key.length();
            char[] data = Base64.decode( userdata );
            StringBuilder sb = new StringBuilder();
            for ( int i=0;i<data.length;i++ )
                sb.append((char)(data[i]^key.charAt(i%klen)));
            String json = sb.toString();
            System.out.println("Delete: decoded json data="+json);
            JSONObject jObj = (JSONObject)JSONValue.parse(json);
            try
            {
                Connection conn = Connector.getConnection();
                if ( docid != null )
                {
                    String jStr = conn.getFromDb( Database.RATINGS, docid );
                    if ( jStr != null )
                    {
                        JSONObject entry = (JSONObject)JSONValue.parse( jStr );
                        JSONArray ratings = (JSONArray)entry.get(Params.RATINGS);
                        int item = -1;
                        for ( int i=0;i<ratings.size();i++ )
                        {
                            JSONObject obj = (JSONObject)ratings.get(i);
                            if ( obj.containsKey("user") 
                                && ((String)obj.get("user")).equals(jObj.get("name")) )
                            {
                                System.out.println("Delete: Found user "+jObj.get("name"));
                                item = i;
                                break;
                            }
                        }
                        if ( item != -1 )
                            ratings.remove(item);
                        else
                            System.out.println("Delete: didn't find user "+jObj.get("name"));
                        entry.remove(JSONKeys._ID);
                        entry.remove(JSONKeys.DOCID);
                        conn.putToDb(Database.RATINGS, docid, entry.toJSONString());
                    }
                }
            }
            catch ( Exception e )
            {
                throw new RatingsException(e);
            }
        }
    }
}
