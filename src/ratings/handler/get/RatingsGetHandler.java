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

import calliope.core.exception.DbException;
import calliope.core.database.Connector;
import ratings.exception.*;
import ratings.constants.*;
import ratings.handler.RatingsHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import calliope.core.database.*;
import calliope.core.constants.Database;
import ratings.constants.Params;
import org.json.simple.*;

/**
 * Get a project document from the database
 * @author desmond
 */
public class RatingsGetHandler extends RatingsHandler
{
    int calcScore( JSONArray ratings )
    {
        int total = 0;
        for ( int i=0;i<ratings.size();i++ )
        {
            JSONObject jObj = (JSONObject)ratings.get(i);
            total += ((Number)jObj.get(Params.SCORE)).intValue();
        }
        float average = (float)total/(float)ratings.size();
        return Math.round( average);
    }
    public void handle(HttpServletRequest request,
            HttpServletResponse response, String urn) throws RatingsException 
    {
        try 
        {
            docid = request.getParameter(Params.DOCID);
            Connection conn = Connector.getConnection();
            if ( docid != null )
            {
                String jStr = conn.getFromDb( Database.RATINGS, docid );
                if ( jStr != null )
                {
                    JSONObject jObj = (JSONObject)JSONValue.parse( jStr );
                    JSONArray ratings = (JSONArray)jObj.get(Params.RATINGS);
                    int avScore = calcScore( ratings );
                    jObj.put( Params.SCORE, avScore );
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().println(jObj.toJSONString());
                }
                else
                    throw new DbException(docid+" not found");
            }
            else
                throw new DbException("You must specify a docid");
        } 
        catch (Exception e) 
        {
            try
            {
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(e.getMessage());
            }
            catch ( Exception ex )
            {
                throw new RatingsException(ex);
            }
        }
    }   
}
