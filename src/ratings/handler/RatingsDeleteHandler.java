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
package ratings.handler;
import ratings.exception.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import calliope.core.database.*;
import calliope.core.constants.JSONKeys;
import calliope.core.constants.Database;
import calliope.core.image.Corpix;
import calliope.core.Utils;
import ratings.RatingsWebApp;

/**
 * Handle a DELETE request
 * @author desmond
 */
public class RatingsDeleteHandler extends RatingsHandler
{
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws RatingsException
    {
        try
        {
            Connection conn = Connector.getConnection();
            String jDoc = conn.getFromDb(Database.PROJECTS,urn);
            if ( jDoc != null )
            {
                String shortId = Utils.shortDocID(urn);
                String imgId = shortId + "/project/icon";
                if ( !imgId.equals("english/anonymous/project/icon") )
                    Corpix.deleteImage(RatingsWebApp.webRoot, imgId );
                // remove all events starting with the project short id
                conn.removeFromDbByExpr(Database.EVENTS, JSONKeys.DOCID, 
                    shortId+".*");
                conn.removeFromDb(Database.PROJECTS, urn);
            }
        }
        catch ( Exception e )
        {
            throw new RatingsException(e);
        }
    }
}
