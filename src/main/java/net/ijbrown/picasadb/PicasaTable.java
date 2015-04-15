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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A table in a Picasa database.
 */
public class PicasaTable
{
    private final Path root;
    private final String name;

    /**
     * Constructor.
     * @param root The filesystem root of the DB.
     */
    public PicasaTable(Path root, String name)
    {
        this.root = root;
        this.name = name;
    }

    // Maps a column name to a column object
    private Map<String, PicasaColumn> columns = new HashMap<>(64);

    public void readStructure() throws IOException
    {
        File[] files = collectFiles();
        for (File file : files){
            String columnName = file.getName().replace(name+"_", "").replace(".pmp", "");
            PicasaColumn column = new PicasaColumn(columnName, file);
            column.readStructure();
            columns.put(columnName, column);
        }
    }

    /**
     * Gets the set of column names for this table.
     * @return The column names for this table.
     */
    public Set<String> getColumnNames()
    {
        return columns.keySet();
    }

    /**
     * Gets the column with the given name.
     * @param columnName The name of the column to get.
     * @return The column with the given name.
     */
    public PicasaColumn getColumn(String columnName)
    {
        return columns.get(columnName);
    }

    /**
     * Find all the files for this table.
     * @return An array of all files used by this table.
     */
    private File[] collectFiles()
    {
        FilenameFilter filter = (dir, filename) -> filename.startsWith(name+"_") && filename.endsWith(".pmp");

        return root.toFile().listFiles(filter);
    }
}
