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

import calliope.core.Utils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ratings.exception.RatingsException;
import ratings.handler.RatingsHandler;
import ratings.constants.Params;
import ratings.constants.Service;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import calliope.core.constants.JSONKeys;
import calliope.core.database.Connector;
import calliope.core.database.Connection;
import calliope.core.constants.Database;
import calliope.core.constants.Formats;


/**
 * Handle posting or saving of project data
 * @author desmond
 */
public class RatingsPostHandler extends RatingsHandler
{
    int score;
    String review;
    String user;
    String format;
    public RatingsPostHandler()
    {
        score = -1;
        format = Formats.MIME_MARKDOWN;
    }
    String checkFormat( String f )
    {
        if ( f.equals(Formats.MIME_MARKDOWN) )
            return Formats.MIME_MARKDOWN;
        else if ( f.equals(Formats.MIME_HTML) )
            return Formats.MIME_HTML;
        else if ( f.equals(Formats.MIME_TEXT) )
            return Formats.MIME_TEXT;
        else
            return Formats.MIME_MARKDOWN;
    }
    public void handle( HttpServletRequest request,
        HttpServletResponse response, String urn ) throws RatingsException
    {
        try
        {
            String service = Utils.first(urn);
            // DELETE not supported by all browser, use POST
            if ( service.equals(Service.DELETE) )
            {
                urn = Utils.pop(urn);
                System.out.println("calling delete handler");
                new RatingsDeleteHandler().handle(request,response, Utils.pop(urn));
            }
            else
            {
                Map<String,String[]> params = request.getParameterMap();
                Set<String> keys = params.keySet();
                Iterator<String> iter = keys.iterator();
                while ( iter.hasNext() )
                {
                    String key = iter.next();
                    String[] values = params.get( key );
                    if ( values.length > 0 )
                    {
                        if ( key.equals(Params.SCORE) )
                            score = Integer.parseInt(values[0]);
                        else if ( key.equals(Params.REVIEW) )
                            review = values[0];
                        else if ( key.equals(Params.DOCID) )
                            docid = values[0];
                        else if ( key.equals(Params.USER) )
                            user = values[0];
                        else if ( key.equals(Params.FORMAT) )
                            format = checkFormat(values[0]);
                    }
                }
                if ( docid != null && user != null && score != -1 )
                {
                    Connection conn = Connector.getConnection();
                    String jStr = (String)conn.getFromDb(Database.RATINGS,docid);
                    JSONObject jObj;
                    if ( jStr != null )
                        jObj = (JSONObject)JSONValue.parse(jStr);
                    else
                    {
                        jObj = new JSONObject();
                        jObj.put( Params.RATINGS, new JSONArray() );
                    }
                    JSONArray jArr = (JSONArray)jObj.get(Params.RATINGS);
                    boolean found = false;
                    for ( int i=0;i<jArr.size();i++ )
                    {
                        JSONObject r = (JSONObject) jArr.get(i);
                        String userName = (String)r.get(JSONKeys.USER);
                        if ( userName != null && userName.equals(user) )
                        {
                            // update existing review and score
                            r.put(Params.SCORE, score);
                            if ( review != null )
                                r.put(Params.REVIEW, review);
                            found = true;
                            break;
                        }
                    }
                    if ( !found )
                    {
                        // enter a new score for that user
                        JSONObject rating = new JSONObject();
                        rating.put(Params.SCORE, score);
                        rating.put(JSONKeys.USER, user);
                        if ( review != null )
                            rating.put(Params.REVIEW, review);
                        rating.put(JSONKeys.FORMAT, format);
                        jArr.add( rating );
                    }
                    conn.putToDb(Database.RATINGS, docid, jObj.toJSONString());
                }
            }
        }
        catch ( Exception e )
        {
            throw new RatingsException(e);
        }
    }
}
