/*
    Copyright (C) 2015 Ian Brown

    This file is part of Picview.

    Picview is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.ijbrown.picasadb;

import org.apache.log4j.Logger;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Little Endian data stream.
 */
public class DataStreamLE extends FilterInputStream
{
    private static final Logger logger = Logger.getLogger(DataStreamLE.class);

    public DataStreamLE(InputStream in)
    {
        super(in);
    }

    public byte readByte() throws IOException
    {
        int ch = in.read();
        if (ch < 0){
            throw new EOFException();
        }
        return (byte)ch;
    }

    public short readInt16() throws IOException
    {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short)((ch2 << 8) | ch1);
    }

    public long readUInt32() throws IOException
    {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        long ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) | (ch3 << 16) | (ch2 << 8) | ch1;
    }

    public int readInt32() throws IOException
    {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) | (ch3 << 16) | (ch2 << 8) | ch1;
    }

    public long readInt64() throws IOException
    {
        long long1 = readUInt32();
        long long2 = readUInt32();
        return (long2 << 32) | long1;
    }

    public int readUInt16() throws IOException
    {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2 ) < 0) {
            throw new EOFException();
        }
        return (ch2 << 8) | ch1;
    }


    public String readString() throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int c;
        while((c = in.read()) != 0) {
            sb.append((char)c);
        }

        return sb.toString();
    }

    public double readDouble() throws IOException
    {
        long l = readInt64();
        return Double.longBitsToDouble(l);
    }

    private static double MILLIS_PER_DAY = 1000 * 60 * 60 * 24;

    public Date readDate() throws IOException
    {
        double d = readDouble();    // days since 1900
        if (d < 0){
            logger.info("-ve d: " + d);
        }
        d -= 25569.0;       // days since Jan 1, 1970
        long millis = Math.round(d*MILLIS_PER_DAY);
        return new Date(millis);
    }
}
