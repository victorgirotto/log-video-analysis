/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modeling.and.analysis.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Elissa
 */
public class FileWriter {
  
    public boolean writeToCSV(int rows, int columns, DefaultTableModel data, String dateString, File outputFile) throws FileNotFoundException, IOException
    {
        boolean success = true;
        String newLine = "";
        FileOutputStream is = new FileOutputStream(outputFile);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        try (Writer w = new BufferedWriter(osw)) {
            for(int i=0; i<data.getRowCount(); i++)
            {
                for(int j=0; j<data.getColumnCount(); j++)
                {
                    if(j==data.getColumnCount()-1)
                    {
                        //don't add comma to last item on line
                        newLine = newLine + data.getValueAt(i, j) + "\n";
                    }
                    else if(j==1)
                    {
                        //insert extraneous timestamp info into log file
                        newLine = newLine + data.getValueAt(i, j) + dateString + ",";
                    }
                    else
                    {
                        //format each row and write to output file
                        newLine = newLine + data.getValueAt(i, j) + ",";
                    }
                }
                //write line to .csv file
                w.write(newLine);
                newLine = "";
            }
        }
        
        return success;
    }
    
}
