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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * A toy harness to exercise the code during development.
 */
public class TestBed
{
    public static void main(String[] args) throws IOException
    {
        String picasaDBLocation = "/var/picasaBackup/Picasa2/db3";

        Path picasaRoot = FileSystems.getDefault().getPath(picasaDBLocation);
        PicasaDB picasaDB = new PicasaDB(picasaRoot);
        picasaDB.readStructure();
        CsvWriter csvWriter= new CsvWriter(picasaDB);
        csvWriter.write("/var/picasaExport/albumdata.csv", "albumdata");
        csvWriter.write("/var/picasaExport/catdata.csv", "catdata");
        csvWriter.write("/var/picasaExport/imagedata.csv", "imagedata");
    }
}
