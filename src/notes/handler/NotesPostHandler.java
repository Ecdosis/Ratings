/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notes.handler;

import calliope.core.Utils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import notes.constants.Service;
import notes.exception.NotesException;
import notes.handler.post.annotations.AnnotationsPostHandler;
import notes.handler.post.ratings.RatingsPostHandler;

/**
 *
 * @author desmond
 */
public class NotesPostHandler extends NotesHandler
{
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws NotesException
    {
        String service = Utils.first(urn);
        urn = Utils.pop(urn);
        if ( service.equals(Service.RATINGS))
            new RatingsPostHandler().handle(request,response,urn);
        else if ( service.equals(Service.ANNOTATIONS) )
            new AnnotationsPostHandler().handle(request,response,urn);
    }
}
