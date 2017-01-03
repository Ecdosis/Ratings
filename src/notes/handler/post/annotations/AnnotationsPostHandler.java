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

package notes.handler.post.annotations;

import calliope.core.Utils;
import calliope.core.constants.JSONKeys;
import calliope.core.database.Connection;
import calliope.core.database.Connector;
import calliope.core.constants.Database;
import notes.constants.Params;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import notes.constants.Service;
import notes.exception.NotesException;
import notes.handler.NotesHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;


/**
 * Handle posting or saving of project data
 * @author desmond
 */
public class AnnotationsPostHandler extends NotesHandler
{
    JSONArray annotations;
    /**
     * Parse the import params from the request
     * @param request the http request
     */
    void parseImportParams( HttpServletRequest request ) throws NotesException
    {
        try
        {
            Map tbl = request.getParameterMap();
            Set<String> keys = tbl.keySet();
            Iterator<String> iter = keys.iterator();
            while ( iter.hasNext() )
            {
                String key = iter.next();
                if ( key.equals(Params.DATA) )
                {
                    String[] values = (String[])tbl.get(key);
                    if ( values.length==1 )
                        annotations = (JSONArray)JSONValue.parse(values[0]);
                }
            }
        }
        catch ( Exception e )
        {
            throw new NotesException( e );
        }
    }
    public void handle( HttpServletRequest request,
        HttpServletResponse response, String urn ) throws NotesException
    {
        try
        {
            String service = Utils.first(urn);
            parseImportParams(request);
            if ( annotations != null )
            {
                Connection conn = Connector.getConnection();
                for ( int i=0;i<annotations.size();i++ )
                {
                    JSONObject jObj = (JSONObject)annotations.get(i);
                    String docid = (String)jObj.get(JSONKeys.DOCID);
                    docid += "/"+(String)jObj.get("id");
                    jObj.remove(JSONKeys.DOCID);
                    jObj.remove("id");
                    conn.putToDb(Database.ANNOTATIONS, docid, 
                        jObj.toJSONString().replaceAll("\\\\/","/"));
                }
            }
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        }
        catch ( Exception e )
        {
            throw new NotesException(e);
        }
    }
}
