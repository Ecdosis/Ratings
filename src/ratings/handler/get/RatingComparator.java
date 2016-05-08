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

import java.util.Comparator;

/**
 * Compare two ratings for sorting
 * @author desmond
 */
public class RatingComparator implements Comparator<RatingEntry> 
{
    public int compare( RatingEntry r1, RatingEntry r2 )
    {
        return (r1.rating > r2.rating)?-1:(r1.rating==r2.rating)?0:1;
    }
    public boolean equals( Object obj )
    {
        return this.equals(obj);
    }
}
