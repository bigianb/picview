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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Writes PicasaDB tables to csv files.
 */
public class CsvWriter
{
    private final PicasaDB db;

    public CsvWriter(PicasaDB db)
    {
        this.db = db;
    }

    public void write(String filename, String tableName) throws IOException
    {
        PicasaTable table = db.getTable(tableName);
        Set<String> columnNames = table.getColumnNames();
        List<String> orderedColumns = new ArrayList<>(columnNames);
        orderedColumns.sort(null);
        List<PicasaColumn> columns = new ArrayList<>(orderedColumns.size());
        StringBuilder sb = new StringBuilder(0x10000);

        int numRows = 0;
        final int numCols = orderedColumns.size();
        for (int c = 0; c < numCols; ++c) {
            String columnName = orderedColumns.get(c);
            PicasaColumn column = table.getColumn(columnName);
            if (column.getNumRows() > numRows) {
                numRows = column.getNumRows();
            }
            columns.add(table.getColumn(columnName));
            if (c != 0) {
                sb.append(',');
            }
            sb.append(columnName);
        }

        for (int row = 0; row < numRows; ++row) {
            for (int col = 0; col < numCols; ++col) {
                List<String> colData = columns.get(col).getStringData();
                if (col != 0) {
                    sb.append(',');
                }
                if (colData.size() > row) {
                    String val = colData.get(row);
                    if (val.contains(",")){
                        sb.append('"');
                        sb.append(colData.get(row));
                        sb.append('"');
                    } else {
                        sb.append(colData.get(row));
                    }
                }
            }
            sb.append('\n');
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(sb.toString());
        }
    }
}
