/*
 * This file is part of Notes.
 *
 *  Notes is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Notes is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Notes.  If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Desmond Schmidt 2016
 */

package notes.handler.get.ratings;

import notes.exception.NotesException;
import calliope.core.Utils;
import calliope.core.exception.DbException;
import calliope.core.database.Connector;
import notes.handler.NotesHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import calliope.core.database.*;
import calliope.core.constants.Database;
import notes.constants.Params;
import notes.constants.Service;
import org.json.simple.*;

/**
 * Get a project document from the database
 * @author desmond
 */
public class RatingsGetHandler extends NotesHandler
{
    double calcScore( JSONArray ratings )
    {
        double total = 0;
        for ( int i=0;i<ratings.size();i++ )
        {
            JSONObject jObj = (JSONObject)ratings.get(i);
            total += ((Number)jObj.get(Params.SCORE)).intValue();
        }
        double average = total/ratings.size();
        return average;
    }
    public void handle(HttpServletRequest request,
            HttpServletResponse response, String urn) throws NotesException 
    {
        try 
        {
            String first = Utils.first(urn);
            urn = Utils.pop(urn);
            if ( first.equals(Service.LIST) )
                new RatingsListHandler().handle(request,response,urn);
            else
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
                        double avScore = calcScore( ratings );
                        jObj.put( Params.SCORE, avScore );
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().println(jObj.toJSONString());
                    }
                    else
                        throw new DbException(
                            "{\"score\":0,\"ratings\":[],\"message\":\""
                            +docid+" not found"+"\"}");
                }
                else
                    throw new DbException("You must specify a docid");
            }
        } 
        catch (Exception e) 
        {
            try
            {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().write(e.getMessage());
            }
            catch ( Exception ex )
            {
                throw new NotesException(ex);
            }
        }
    }   
}
