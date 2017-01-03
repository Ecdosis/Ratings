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

package notes.handler.get.annotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import notes.exception.NotesException;
import notes.handler.NotesHandler;
import calliope.core.constants.JSONKeys;
import calliope.core.database.Connection;
import calliope.core.database.Connector;
import calliope.core.constants.Database;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
/**
 *
 * @author desmond
 */
public class AnnotationsGetHandler extends NotesHandler
{
    public void handle(HttpServletRequest request,
            HttpServletResponse response, String urn) throws NotesException 
    {
        try 
        {
            docid = request.getParameter(JSONKeys.DOCID);
            Connection conn = Connector.getConnection();
            String[] docids = conn.listDocuments( Database.ANNOTATIONS, 
                docid+"/.*", JSONKeys.DOCID );
            JSONArray jArr = new JSONArray();
            for ( int i=0;i<docids.length;i++ )
            {
                String jStr = conn.getFromDb(Database.ANNOTATIONS, docids[i]);
                JSONObject jObj = (JSONObject)JSONValue.parse(jStr);
                jObj.remove(JSONKeys._ID);
                jArr.add(jObj);
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(jArr.toJSONString().replaceAll("\\\\/","/"));
        } 
        catch (Exception e) 
        {
            throw new NotesException(e);
        }
    }   
}
