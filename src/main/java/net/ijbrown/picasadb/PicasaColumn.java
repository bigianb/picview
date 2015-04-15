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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A Column in a picasaDB table.
 */
@SuppressWarnings("FieldCanBeLocal")
public class PicasaColumn
{
    private static final Logger logger = Logger.getLogger(PicasaColumn.class);

    private final String name;
    private final File file;

    /**
     * Constructor.
     *
     * @param name The name of the column.
     * @param file The file storing the data of this column.
     */
    public PicasaColumn(String name, File file)
    {
        this.name = name;
        this.file = file;
    }

    // The data stored in the header
    private long magic;
    private int type;
    private int magic2;     // expect 0x1332
    private long magic3;     // expect 0x02
    private int type2;      // expect to be the same as type
    private int magic4;     // expect 0x1332
    private int numRows;

    /**
     * Reads the structure of this column.
     */
    public void readStructure() throws IOException
    {
        try (DataStreamLE in = new DataStreamLE(new BufferedInputStream(new FileInputStream(file)))) {
            magic = in.readUInt32();
            type = in.readUInt16();
            magic2 = in.readUInt16();
            magic3 = in.readUInt32();
            type2 = in.readUInt16();
            magic4 = in.readUInt16();
            numRows = (int)in.readUInt32();

            if (magic2 != 0x1332 || magic3 != 0x02 || type != type2 || magic4 != 0x1332){
                throw new IOException("Unexpected magic numbers in column header");
            }
            logger.info("Column "+name+" (type "+type +") has "+numRows+" rows.");
        }
    }

    private List<String> stringData = null;

    public List<String> getStringData() throws IOException
    {
        if (stringData == null){
            readStringData();
        }
        return stringData;
    }

    private final Object readLock = new Object();

    public void readStringData() throws IOException
    {
        synchronized (readLock)
        {
            List<String> data = new ArrayList<>(numRows);
            try (DataStreamLE in = new DataStreamLE(new BufferedInputStream(new FileInputStream(file)))) {
                int bytesToSkip=20;
                while(bytesToSkip>0) {
                    bytesToSkip -= in.skip(20);
                }
                for (int row=0; row<numRows; ++row) {
                    Object obj = readEntry(in);
                    if (obj == null){
                        data.add("");
                    } else if (obj instanceof String){
                        data.add((String)obj);
                    } else if (obj instanceof Long){
                        data.add(formatLong((Long)obj));
                    } else {
                        data.add(obj.toString());
                    }
                }

            }
            stringData = data;
        }
    }

    private final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private String formatLong(Long lng)
    {
        final long l = lng;
        StringBuilder sb = new StringBuilder(32);
        sb.append("0x");
        int shift=56;
        while(shift >=0){
            sb.append(hex[(int)(l >> shift) & 0x0f]);
            shift -= 4;
        }
        return sb.toString();
    }

    private Object readEntry(DataStreamLE in) throws IOException
    {
        Object obj=null;
        switch(type)
        {
            case 0:
                obj = in.readString();
                break;

            case 4:
                obj = in.readInt64();
                break;

            case 6:
                // Actually a list of strings I think
                obj = in.readString();
                break;
        }
        return obj;
    }

    public int getNumRows()
    {
        return numRows;
    }
}
