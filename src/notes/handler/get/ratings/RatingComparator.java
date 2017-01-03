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

import java.util.Comparator;
import java.util.HashSet;

/**
 * Compare two ratings for sorting
 * @author desmond
 */
public class RatingComparator implements Comparator<RatingEntry> 
{
    static HashSet<String> skips;
    static
    {
        skips = new HashSet<String>();
        skips.add("The");
        skips.add("A");
        skips.add("An");
        skips.add("the");
        skips.add("a");
        skips.add("an");
    }
    /**
     * Truncate the leading part of the title for sorting
     * @param title the raw title
     * @return the possibly truncated title
     */
    String truncate( String title )
    {
        if ( title != null )
        {
            String[] parts = title.split(" ");
            StringBuilder sb = new StringBuilder();
            for ( int i=0;i<parts.length;i++ )
            {
                if ( sb.length()==0 )
                {
                    while ( parts[i].length()>0 
                        && !Character.isLetter(parts[i].charAt(0)) )
                        parts[i] = parts[i].substring(1);
                    if ( !skips.contains(parts[i]) )
                        sb.append(parts[i]);
                }
                else
                {
                    sb.append(" ");
                    sb.append(parts[i]);
                }
            }
            return sb.toString();
        }
        else
            return "";
    }
    public int compare( RatingEntry r1, RatingEntry r2 )
    {
        if (r1.rating > r2.rating)
            return -1;
        else if (r1.rating< r2.rating)
            return 1;
        else
        {
            String t1 = truncate(r1.title);
            String t2 = truncate(r2.title);
            return t1.compareTo(t2);
        }
    }
    public boolean equals( Object obj )
    {
        return this.equals(obj);
    }
}
