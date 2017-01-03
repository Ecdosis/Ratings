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

package notes.handler.get;

import notes.exception.NotesException;
import calliope.core.Utils;
import notes.handler.get.ratings.RatingsGetHandler;
import notes.handler.get.annotations.AnnotationsGetHandler;
import notes.handler.NotesHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import notes.constants.Service;

/**
 * Get a note (rating or annotation) from the database
 * @author desmond
 */
public class NotesGetHandler extends NotesHandler
{
    public void handle(HttpServletRequest request,
            HttpServletResponse response, String urn) throws NotesException 
    {
        try 
        {
            String first = Utils.first(urn);
            if ( first.equals(Service.RATINGS) )
                new RatingsGetHandler().handle(request,response,Utils.pop(urn));
            else if ( first.equals(Service.ANNOTATIONS) )
                new AnnotationsGetHandler().handle(request,response,Utils.pop(urn));
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
