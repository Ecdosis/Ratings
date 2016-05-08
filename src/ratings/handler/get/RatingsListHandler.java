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
package ratings.handler.get;

import calliope.core.constants.Database;
import calliope.core.constants.JSONKeys;
import calliope.core.database.Connection;
import calliope.core.exception.DbException;
import calliope.core.database.Connector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ratings.constants.Params;
import ratings.exception.RatingsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.json.simple.*;

/**
 * Answer a request for a list of rated works
 * @author desmond
 */
public class RatingsListHandler extends RatingsGetHandler
{
    /**
     * Compute the work's rating rounded to the nearest half star value
     * @param docid the work's docid
     * @return its average rating
     * @throws DbException 
     */
    float computeRating( String docid ) throws DbException
    {
        Connection conn = Connector.getConnection();
        String json = conn.getFromDb( Database.RATINGS, docid );
        JSONObject jObj = (JSONObject)JSONValue.parse( json );
        JSONArray ratings = (JSONArray)jObj.get("ratings");
        int total = 0;
        for ( int i=0;i<ratings.size();i++ )
        {
            JSONObject jRating = (JSONObject)ratings.get(i);
            Number num = (Number)jRating.get("score");
            total += num.intValue();
        }
        float twice = total * 2;
        twice /= ratings.size();
        float half = (float)Math.round(twice)/2.0f;
        return half;
    }
    /**
     * Get the title of a cortex document
     * @param docid the document identifier
     * @return the work's title or null
     * @throws DbException 
     */
    String getTitleForDocid( String docid ) throws DbException
    {
        Connection conn = Connector.getConnection();
        if ( conn != null )
        {
            String json = conn.getFromDb( Database.CORTEX, docid );
            if ( json != null )
            {
                JSONObject jObj = (JSONObject) JSONValue.parse(json);
                if ( jObj.containsKey(JSONKeys.TITLE) )
                    return (String)jObj.get(JSONKeys.TITLE);
            }
        }
        return null;
    }
    /**
     * Get a list of document-titles and their ratings by decreasing score
     * @param request the http request object
     * @param response the response we will write to
     * @param urn the residual urn (empty and unused)
     * @throws RatingsException 
     */
    public void handle(HttpServletRequest request,
            HttpServletResponse response, String urn) throws RatingsException 
    {
        docid = request.getParameter(Params.DOCID);
        try
        {
            Connection conn = Connector.getConnection();
            if ( docid != null )
            {
                // 1. get list of docids in the collection
                String[] docids = conn.listDocuments( Database.CORTEX, 
                    docid+"/.*", JSONKeys.DOCID );
                // 2. get list of rated docids in that set 
                String[] ratings = conn.listDocuments( Database.RATINGS, 
                    docid+"/.*", JSONKeys.DOCID );
                // 3. assign a rating of 0 to all unrated works
                HashMap<String,Float> map = new HashMap<String,Float>();
                for ( int i=0;i<ratings.length;i++ )
                    map.put( ratings[i], computeRating(ratings[i]));
                ArrayList list = new ArrayList();
                for ( int i=0;i<docids.length;i++ )
                {
                    if ( map.containsKey(docids[i]) )
                        list.add( new RatingEntry(docids[i],
                            map.get(docids[i]),getTitleForDocid(docids[i])));
                    else
                        list.add( new RatingEntry(docids[i],0.0f,
                            getTitleForDocid(docids[i])));
                }
                // 4. sort the rated works by decreasing value, 
                // then alphabetically increasing by title
                RatingEntry[] array = new RatingEntry[list.size()];
                list.toArray(array);
                Arrays.sort( array, new RatingComparator() );
                // 5. format into JSON for reply
                JSONArray table = new JSONArray();
                for ( int i=0;i<array.length;i++ )
                {
                    JSONObject entry = new JSONObject();
                    entry.put("score", array[i].rating );
                    entry.put("docid", array[i].docid );
                    entry.put("title", array[i].title );
                    table.add( entry );
                }
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().print(table.toJSONString());
            }
        }
        catch ( Exception e )
        {
            throw new RatingsException(e);
        }
    }
}
