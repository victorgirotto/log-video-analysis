/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modeling.and.analysis.tool;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 *
 * @author Elissa
 */
public class LogView {
    
    JTable logTable;
    DefaultTableModel logModel;
    String[] columnNames = {"Timestamp", "Action"};
    
    public LogView(File file, String path) throws FileNotFoundException
    {
        int x=0, length=0;
        
        //determine size of .csv file
        Scanner sizeScanner = new Scanner(file);
        Object temp;
        String line;
        sizeScanner.useDelimiter(",");
        while(sizeScanner.hasNextLine()) {
            temp = sizeScanner.nextLine();
            length++;
        }
        
        //create an array to store log data based on file size
        Object[][] logData = new Object[length][2];
        
        //read in log data from .csv file
        Scanner logScanner = new Scanner(file);
        while(logScanner.hasNextLine()) {
            line = logScanner.nextLine();
            logData[x] = line.split(","); 
            x++;
        }
        
        //load the log data into the table
        logModel = new DefaultTableModel(logData, columnNames);
    }
    
    public DefaultTableModel getTableModel()
    {
        return logModel;
    }
}
