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
package notes.handler;
import notes.exception.NotesException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handle a PUT request (used for update)
 * @author desmond
 */
public class NotesPutHandler extends NotesHandler
{
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws NotesException
    {
        System.out.println("PUT");
    }
}
