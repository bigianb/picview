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

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads a picasa 3 database.
 */
public class PicasaDB
{
    private static final Logger logger = Logger.getLogger(PicasaDB.class);

    private Path dbRoot;

    private Map<String, PicasaTable> tables = new HashMap<>(16);

    /**
     * Constructor.
     * @param dbRoot The root of the picasa DB. Generally %LOCALAPPDATA%/Google/Picasa2/db3 under windows.
     */
    public PicasaDB(Path dbRoot)
    {
        this.dbRoot = dbRoot;
    }

    public void readStructure() throws IOException
    {
        readTableStructure("albumdata");
        readTableStructure("catdata");
        readTableStructure("imagedata");
    }

    public void readTableStructure(String tablename) throws IOException
    {
        logger.info("Reading table: " + tablename);
        PicasaTable table = new PicasaTable(dbRoot, tablename);
        table.readStructure();
        tables.put(tablename, table);
    }

    /**
     * Gets the table with the given name.
     * @param tableName The name of the table to get.
     * @return The table with the given name.
     */
    public PicasaTable getTable(String tableName)
    {
        return tables.get(tableName);
    }
}
